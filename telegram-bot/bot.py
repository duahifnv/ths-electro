import os
import logging
import requests
import wsmanager
import json

from sender import AsyncMessageSender
from dotenv import load_dotenv
from telegram import Update, ReplyKeyboardMarkup
from telegram.ext import (
    ApplicationBuilder,
    CommandHandler,
    MessageHandler,
    filters,
    ContextTypes,
    ConversationHandler,
)

# Загрузка переменных окружения
load_dotenv()

# Настройка логирования
logging.basicConfig(
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    level=logging.INFO
)
logger = logging.getLogger(__name__)

# Константы из .env
TELEGRAM_BOT_TOKEN = os.getenv("TELEGRAM_BOT_TOKEN")
AUTH_URL = os.getenv("AUTH_URL")
WEBSOCKET_URL = os.getenv("WEBSOCKET_URL")

# Глобальные переменные
application = None
ws_manager: wsmanager.WebSocketManager = None
ws_connection = None
ws_thread = None
users_usernames = {}

# Переменная числа ждущих пользователей и обработчик ее обновления
waiting_count = 0

sender = AsyncMessageSender()

def message_all(message):
    for user_id, _ in users_usernames.items():
        sender.send(application.bot, user_id, message)

def message_username(username, message):
    user_id = next((k for k, v in users_usernames.items() if v == username), None)
    if not user_id:
        logger.error(f"Отсутствует пользователь с именем {username}")
        return
    sender.send(application.bot, user_id, message)

LOGIN, PASSWORD = range(2)

# Клавиатуры
MAIN_KEYBOARD = [["Авторизоваться в системе"]]
AFTER_AUTH_KEYBOARD = [["Ожидающие пользователи", "Найти пользователя"], ["Выйти из системы"]]

# Обработчик команды /start
async def start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await update.message.reply_text(
        "Добро пожаловать! Пожалуйста, авторизуйтесь.",
        reply_markup=ReplyKeyboardMarkup(MAIN_KEYBOARD, resize_keyboard=True),
    )

# Начало авторизации
async def authorize(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await update.message.reply_text("Введите логин:")
    return LOGIN

# Получение логина
async def get_login(update: Update, context: ContextTypes.DEFAULT_TYPE):
    context.user_data["username"] = update.message.text
    await update.message.reply_text("Введите пароль:")
    return PASSWORD

# Получение пароля и отправка на бэкэнд
async def get_password(update: Update, context: ContextTypes.DEFAULT_TYPE):
    global users_usernames
    password = update.message.text
    username = context.user_data.get("username")

    try:
        auth_response = requests.post(
            AUTH_URL,
            json={"username": username, "password": password}
        )
        if auth_response.status_code == 200:
            jwt_token = auth_response.json().get("token")
            user_id = update.effective_user.id
            users_usernames[user_id] = username
            # Инициализация WebSocket менеджера
            global ws_manager
            ws_manager = initialize_ws_manager(username, jwt_token)
            # Подключаемся к WebSocket
            if ws_manager.connect():
                await update.message.reply_text(
                    "Авторизация успешна!",
                    reply_markup=ReplyKeyboardMarkup(AFTER_AUTH_KEYBOARD, resize_keyboard=True),
                )
            else:
                await update.message.reply_text(
                    "Авторизация успешна, но не удалось подключиться к WebSocket",
                    reply_markup=ReplyKeyboardMarkup(MAIN_KEYBOARD, resize_keyboard=True),
                )
            return ConversationHandler.END

        elif auth_response.status_code == 400 or auth_response.status_code == 401:
            await update.message.reply_text("Неверный пароль. Повторите ввод данных.")
            return LOGIN

        elif auth_response.status_code == 404:
            await update.message.reply_text(f"Не найден помощник с логином: {username}")
            return LOGIN

        else:
            logger.info(f"Ошибка авторизации. Статус {auth_response.status_code}")
            await update.message.reply_text("Ошибка на сервере. Попробуйте позже.")
            return ConversationHandler.END

    except Exception as e:
        logger.error(f"Ошибка авторизации: {e}")
        await update.message.reply_text("Ошибка соединения с сервером.")
        return ConversationHandler.END

def update_waiting_count(new_count):
    global waiting_count
    waiting_count = new_count

def handle_stomp_message(destination: str, body):
    logger.info(f"Получено сообщение (destination: {destination}): {body}")
    try:
        data = json.loads(body.rstrip('\x00'))

        if destination.startswith("/queue"):
            username = data.get("username")

            if destination.startswith("/queue/errors"):
                error = data.get("error")
                message_username(username, f"Ошибка: {error}")

            if destination.startswith("/queue/dialogs"):
                request_count = data.get("size", 0)
                message_username(username, f"Число ожидающих пользователей: {request_count}")

        if destination.startswith("/topic"):
            if destination == "/topic/dialogs":
                # Если сообщение с топика - значит запрашивали не мы, т.е. число обновилось
                global waiting_count
                request_count = data.get("size", 0)
                waiting_count = request_count
                message_all(f"Обновлено число ожидающих пользователей: {waiting_count}")

    except json.JSONDecodeError:
        logger.warning(f"Не удалось спарсить сообщение: {body}")
    except Exception as e:
        logger.error(f"Ошибка обработки STOMP сообщения: {e}")

def initialize_ws_manager(username, token):
    return wsmanager.WebSocketManager(
        subscribe_topics={
            "sub-1": "/user/queue/errors",
            "sub-2": "/topic/dialogs",
            "sub-3": "/user/queue/dialogs",
        },
        send_apis_on_connect=["/app/waiting.size"],
        message_handler=handle_stomp_message,
        ws_url=f"{WEBSOCKET_URL}?token={token}&username={username}"
    )

# Обработка кнопок
async def handle_buttons(update: Update, context: ContextTypes.DEFAULT_TYPE):
    text = update.message.text
    global ws_manager

    try:
        private_sub_id = "sub-4"
        if text == "Ожидающие пользователи":
            if ws_manager is None:
                raise ConnectionError("Websocket не подключен")
            ws_manager.send("/app/waiting.size", "")

        elif text == "Найти пользователя":
            if ws_manager is None:
                raise ConnectionError("Websocket не подключен")
            if waiting_count > 0:
                ws_manager.subscribe(private_sub_id, "/user/queue/private")
                ws_manager.unsubscribe("sub-2")  # Отписываемся от /topic/dialogs
                await update.message.reply_text(
                    "Подключено к пользователю.",
                    reply_markup=ReplyKeyboardMarkup([["Завершить диалог"]], resize_keyboard=True))
            else:
                await update.message.reply_text("Нет ожидающих пользователей.")

        elif text == "Завершить диалог":
            if ws_manager is None:
                raise ConnectionError("Websocket не подключен")
            ws_manager.unsubscribe(private_sub_id) # Отписываемся
            ws_manager.subscribe("sub-2", "/topic/dialogs")
            await update.message.reply_text(
                "Диалог завершен.",
                reply_markup=ReplyKeyboardMarkup(AFTER_AUTH_KEYBOARD, resize_keyboard=True))

        elif text == "Выйти из системы":
            if ws_manager is None:
                raise ConnectionError("Websocket не подключен")
            ws_manager.disconnect()
            await update.message.reply_text(
                "Вы вышли из системы.",
                reply_markup=ReplyKeyboardMarkup(MAIN_KEYBOARD, resize_keyboard=True))
    except ConnectionError as e:
        logger.error(e)
        await update.message.reply_text(
            "Вы не авторизованы.",
            reply_markup=ReplyKeyboardMarkup(MAIN_KEYBOARD, resize_keyboard=True))
    except Exception as e:
        logger.error(f"Ошибка обработки команды: {e}")
        await update.message.reply_text("Произошла ошибка. Попробуйте позже.")

# Основная функция
def main():
    global application
    application = ApplicationBuilder().token(TELEGRAM_BOT_TOKEN).build()
    conv_handler = ConversationHandler(
        entry_points=[MessageHandler(filters.Regex("^Авторизоваться в системе$"), authorize)],
        states={
            LOGIN: [MessageHandler(filters.TEXT & ~filters.COMMAND, get_login)],
            PASSWORD: [MessageHandler(filters.TEXT & ~filters.COMMAND, get_password)],
        },
        fallbacks=[],
    )
    application.add_handler(conv_handler)
    application.add_handler(MessageHandler(
        filters.Regex("^(Ожидающие пользователи|Найти пользователя|Завершить диалог|Выйти из системы)$"),
        handle_buttons
    ))
    application.add_handler(CommandHandler("start", start))

    application.run_polling()

if __name__ == "__main__":
    main()