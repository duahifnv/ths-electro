import os
import logging
import requests
from requests import Response

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
USER_ROLES_URL = os.getenv("USER_ROLES_URL")
WEBSOCKET_URL = os.getenv("WEBSOCKET_URL")

# Константы состояния авторизации
LOGIN, PASSWORD = range(2)

# Клавиатуры
MAIN_KEYBOARD = [["Авторизоваться в системе"]]
AFTER_AUTH_KEYBOARD = [["Ожидающие пользователи", "Найти пользователя"], ["Выйти из системы"]]

# Константы для id подписок на топики и очереди
ERROR_QUEUE_SUB_ID = "sub-1"
DIALOGS_TOPIC_SUB_ID = "sub-2"
DIALOGS_QUEUE_SUB_ID = "sub-3"
PRIVATE_CHAT_SUB_ID = "sub-4"
DIALOG_END_QUEUE_SUB_ID = "sub-5"

# Глобальные переменные
application = None
ws_manager: wsmanager.WebSocketManager = None
ws_connection = None
ws_thread = None
users_usernames = {}

# Переменная числа ждущих пользователей и обработчик ее обновления
waiting_count = 0
# Переменная состояния диалога с пользователем
is_dialog_open = False

sender = AsyncMessageSender()

def message_all(message):
    for user_id, _ in users_usernames.items():
        sender.send(application.bot, user_id, message)

def message_send(receiver, message):
    user_id = next((k for k, v in users_usernames.items() if v == receiver), None)
    if not user_id:
        logger.error(f"Отсутствует пользователь с именем {receiver}")
        return
    sender.send(application.bot, user_id, message)

# Обработчик команды /start
async def start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await update.message.reply_text(
        "🌐 Добро пожаловать! Пожалуйста, авторизуйтесь в системе.",
        reply_markup=ReplyKeyboardMarkup(MAIN_KEYBOARD, resize_keyboard=True),
    )

# Начало авторизации
async def authorize(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await update.message.reply_text("👤 Введите <b>логин</b>:",
                                    parse_mode="HTML")
    return LOGIN

# Получение логина
async def get_login(update: Update, context: ContextTypes.DEFAULT_TYPE):
    context.user_data["username"] = update.message.text
    await update.message.reply_text("🔑 Введите <b>пароль</b>:",
                                    parse_mode="HTML")
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
            roles_response = requests.get(
                USER_ROLES_URL,
                headers={"Authorization": f"Bearer {jwt_token}"}
            )
            if not has_helper_role(roles_response):
                await update.message.reply_text(
                    "🚫 Нет доступа к сервису.",
                    reply_markup=ReplyKeyboardMarkup(MAIN_KEYBOARD, resize_keyboard=True),
                )
                return ConversationHandler.END
            if username in users_usernames.values():
                await update.message.reply_text(
                    "⚠️ Данный пользователь находится в системе с другого устройства. Попробуйте позже",
                    reply_markup=ReplyKeyboardMarkup(MAIN_KEYBOARD, resize_keyboard=True),
                )
                return ConversationHandler.END
            user_id = update.effective_user.id
            users_usernames[user_id] = username
            # Инициализация WebSocket менеджера
            global ws_manager
            ws_manager = initialize_ws_manager(username, jwt_token)
            # Подключаемся к WebSocket
            if ws_manager.connect():
                await update.message.reply_text(
                    "🔐 Авторизация успешна!",
                    reply_markup=ReplyKeyboardMarkup(AFTER_AUTH_KEYBOARD, resize_keyboard=True),
                )
            else:
                await update.message.reply_text(
                    "⚠️ Ошибка на сервере. Попробуйте позже.",
                    reply_markup=ReplyKeyboardMarkup(MAIN_KEYBOARD, resize_keyboard=True),
                )
            return ConversationHandler.END

        elif auth_response.status_code == 400 or auth_response.status_code == 401:
            await update.message.reply_text("🚫 Неверный пароль. Повторите ввод данных.")
            return LOGIN

        elif auth_response.status_code == 404:
            await update.message.reply_text(f"⚠️ Не найден помощник с логином: <b>{username}</b>",
                                            parse_mode="HTML")
            return ConversationHandler.END

        else:
            logger.info(f"Ошибка авторизации. Статус {auth_response.status_code}")
            await update.message.reply_text("⚠️ Ошибка на сервере. Попробуйте позже.")
            return ConversationHandler.END

    except Exception as e:
        logger.error(f"Ошибка авторизации: {e}")
        await update.message.reply_text("⚠️ Ошибка соединения с сервером.")
        return ConversationHandler.END

def has_helper_role(roles_response: Response) -> bool:
    try:
        body = roles_response.text
        data = json.loads(body)
        role_names = [item['roleName'] for item in data]
        return "helper" in role_names
    except Exception as e:
        logger.warning(f"Не удалось спарсить сообщение: {roles_response.text}\n{e}")
        return False

def update_waiting_count(new_count):
    global waiting_count
    waiting_count = new_count

def handle_stomp_message(destination: str, body):
    logger.info(f"Получено сообщение (destination: {destination}): {body}")
    try:
        data = json.loads(body.rstrip('\x00'))

        if destination.startswith("/queue"):
            receiver = data.get("receiver")

            if destination.startswith("/queue/errors"):
                error = data.get("error")
                message = f"❌ Ошибка: {error}"
            elif destination.startswith("/queue/dialogs"):
                request_count = data.get("size", 0)
                update_waiting_count(request_count)
                message = f"▶️ Число ожидающих пользователей: <b>{request_count}</b>"
            elif destination.startswith("/queue/private"):
                sender_username = data.get("sender")
                sender_message = data.get("message")
                message = f"💬 Сообщение от пользователя <b>{sender_username}</b>:\n<i>{sender_message}</i>"
            elif destination.startswith("/queue/dialog.end"):
                if not is_dialog_open:
                    raise Exception("Диалог еще не начат")
                initiator = data.get("initiator")
                message = f"✅ Пользователь <b>{initiator}</b> закончил с вами диалог"
                ws_manager.unsubscribe(PRIVATE_CHAT_SUB_ID)
            else:
                raise Exception(f"Отсутствует обработчик для {destination}")

            message_send(receiver, message)

        if destination.startswith("/topic"):
            if destination == "/topic/dialogs":
                # Если сообщение с топика - значит запрашивали не мы, т.е. число обновилось
                request_count = data.get("size", 0)
                update_waiting_count(request_count)
                message_all(f"🔄 Обновлено число ожидающих пользователей: <b>{request_count}</b>")

    except json.JSONDecodeError:
        logger.warning(f"Не удалось спарсить сообщение: {body}")
    except Exception as e:
        logger.error(f"Ошибка обработки STOMP сообщения: {e}")

def initialize_ws_manager(username, token):
    return wsmanager.WebSocketManager(
        subscribe_topics={
            ERROR_QUEUE_SUB_ID: "/user/queue/errors",
            DIALOGS_TOPIC_SUB_ID: "/topic/dialogs",
            DIALOGS_QUEUE_SUB_ID: "/user/queue/dialogs"
        },
        send_apis_on_connect=["/app/waiting.size"],
        message_handler=handle_stomp_message,
        ws_url=f"{WEBSOCKET_URL}?token={token}&username={username}"
    )

# Обработка кнопок
async def handle_buttons(update: Update, context: ContextTypes.DEFAULT_TYPE):
    text = update.message.text
    global ws_manager, waiting_count, is_dialog_open

    try:
        if text == "Ожидающие пользователи":
            if ws_manager is None:
                raise ConnectionError("Websocket не подключен")
            ws_manager.send("/app/waiting.size", "")

        elif text == "Найти пользователя":
            if ws_manager is None:
                raise ConnectionError("Websocket не подключен")

            if int(waiting_count) > 0:
                ws_manager.unsubscribe(DIALOGS_TOPIC_SUB_ID)
                ws_manager.subscribe(PRIVATE_CHAT_SUB_ID, "/user/queue/private")
                ws_manager.subscribe(DIALOG_END_QUEUE_SUB_ID, "/user/queue/dialog.end")
                is_dialog_open = True
                await update.message.reply_text(
                    "ℹ️ Вы начали диалог с пользователем.",
                    reply_markup=ReplyKeyboardMarkup([["Завершить диалог"]], resize_keyboard=True))
            else:
                await update.message.reply_text("⚠️ Нет ожидающих пользователей.")

        elif text == "Завершить диалог":
            if ws_manager is None:
                raise ConnectionError("Websocket не подключен")
            ws_manager.unsubscribe(PRIVATE_CHAT_SUB_ID)
            ws_manager.unsubscribe(DIALOG_END_QUEUE_SUB_ID)
            ws_manager.subscribe(DIALOGS_TOPIC_SUB_ID, "/topic/dialogs")
            is_dialog_open = False
            
            await update.message.reply_text(
                "ℹ️ Диалог с пользователем завершен.",
                reply_markup=ReplyKeyboardMarkup(AFTER_AUTH_KEYBOARD, resize_keyboard=True))

            ws_manager.send("/app/waiting.size", "")

        elif text == "Выйти из системы":
            if ws_manager is None:
                raise ConnectionError("Websocket не подключен")
            ws_manager.disconnect()
            user_id = update.effective_user.id
            del users_usernames[user_id]
            await update.message.reply_text(
                "🔓 Вы вышли из системы. До свидания!",
                reply_markup=ReplyKeyboardMarkup(MAIN_KEYBOARD, resize_keyboard=True))

        elif is_dialog_open:
            ws_manager.send("/app/chat", text)

    except ConnectionError as e:
        logger.error(e)
        await update.message.reply_text(
            "⚠️ Вы не авторизованы.",
            reply_markup=ReplyKeyboardMarkup(MAIN_KEYBOARD, resize_keyboard=True))
    except Exception as e:
        logger.error(f"Ошибка обработки команды: {e}")
        # await update.message.reply_text("Произошла ошибка. Попробуйте позже.")

# Основная функция
def main():
    global application
    application = ApplicationBuilder().token(TELEGRAM_BOT_TOKEN).build()
    application.add_handler(CommandHandler("start", start))
    application.add_handler(ConversationHandler(
        entry_points=[MessageHandler(filters.Regex("^Авторизоваться в системе$"), authorize)],
        states={
            LOGIN: [MessageHandler(filters.TEXT & ~filters.COMMAND, get_login)],
            PASSWORD: [MessageHandler(filters.TEXT & ~filters.COMMAND, get_password)],
        },
        fallbacks=[],
    ))
    application.add_handler(MessageHandler(
        None,
        # filters.Regex("^(Ожидающие пользователи|Найти пользователя|Завершить диалог|Выйти из системы)$"),
        callback=handle_buttons
    ))
    application.run_polling()

if __name__ == "__main__":
    main()