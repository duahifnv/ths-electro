from telegram import (
    Update,
    ReplyKeyboardMarkup,
    KeyboardButton,
    ReplyKeyboardRemove
)
from telegram.ext import (
    Application,
    CommandHandler,
    ContextTypes,
    MessageHandler,
    filters,
    ConversationHandler
)
from collections import deque
import asyncio
import aiohttp

import logging

# Настройка логирования
logging.basicConfig(
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
    level=logging.INFO
)
logger = logging.getLogger(__name__)

GET_SERVER_KEY = 1

ADMIN_ID = 996188029
active_dialogs = {}
waiting_queue = deque()
work_started = False
notification_task = None
# server_key = "g6s75rjhWc6cWxsYf7KSPdl0rO6Rc0RQ"
server_key = None

COMMANDS = ["Начать работу", "Завершить работу", "Следующий диалог", "Завершить диалог"]

async def notify_admin(context: ContextTypes.DEFAULT_TYPE):
    global notification_task
    while work_started:
        if waiting_queue and ADMIN_ID not in active_dialogs:
            count = len(waiting_queue)
            await context.bot.send_message(
                chat_id=ADMIN_ID,
                text=f"🔔 Новый диалог в очереди! Всего ожидает: {count}",
                reply_markup=await get_admin_keyboard()
            )
        await asyncio.sleep(60)

async def get_admin_keyboard():
    if work_started:
        if ADMIN_ID in active_dialogs:
            return ReplyKeyboardMarkup(
                [[KeyboardButton("Завершить диалог")]],
                resize_keyboard=True
            )
        else:
            return ReplyKeyboardMarkup(
                [
                    [KeyboardButton("Следующий диалог")],
                    [KeyboardButton("Завершить работу")]
                ],
                resize_keyboard=True
            )
    else:
        return ReplyKeyboardMarkup(
            [[KeyboardButton("Начать работу")]],
            resize_keyboard=True
        )

async def start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    global server_key

    if update.effective_user.id == ADMIN_ID:
        if server_key is None:
            await update.message.reply_text(
                "🔒 Пожалуйста, введите секретный ключ для доступа к сервису:",
                reply_markup=ReplyKeyboardRemove()
            )
            return GET_SERVER_KEY
        else:
            await update.message.reply_text(
                "Админ-панель готова к работе:",
                reply_markup=await get_admin_keyboard()
            )
            return ConversationHandler.END
    else:
        await update.message.reply_text(
            "Привет! Отправьте ваше сообщение, и оператор с вами свяжется.",
            reply_markup=ReplyKeyboardRemove()
        )
        return ConversationHandler.END

async def get_server_key(update: Update, context: ContextTypes.DEFAULT_TYPE):
    global server_key

    # Получение secretKey из сообщения пользователя
    server_key = update.message.text.strip()

    async with aiohttp.ClientSession() as session:
        try:
            # Отправка запроса для проверки ключа
            logger.info("Отправка POST-запроса к http://helper-service:8082/api/helper/chat/se")
            logger.debug(f"Параметры запроса: tgId={ADMIN_ID}, secretKey={server_key}")

            async with session.post(
                'http://helper-service:8082/api/helper/chat/se',
                params={
                    'tgId': str(ADMIN_ID),
                    'secretKey': server_key
                }
            ) as response:
                if response.status == 200:
                    data = await response.json()

                    # Если ключ принят, отправляем сообщение об успехе
                    await update.message.reply_text(
                        "✅ Ключ принят! Админ-панель готова к работе:",
                        reply_markup=await get_admin_keyboard()
                    )

                    # Вызов функции для получения размера очереди
                    queue_size = await get_queue_size()
                    if queue_size is not None:
                        await update.message.reply_text(
                            f"ℹ Текущий размер очереди: {queue_size}"
                        )

                    return ConversationHandler.END
                else:
                    server_key = None
                    error_message = await response.text()
                    logger.error(f"Неверный ключ. Статус: {response.status}, Сообщение: {error_message}")

                    await update.message.reply_text(
                        "❌ Неверный ключ. Пожалуйста, введите правильный секретный ключ:"
                    )
                    return GET_SERVER_KEY
        except Exception as e:
            server_key = None
            logger.error(f"Ошибка подключения к серверу: {e}")

            await update.message.reply_text(
                f"❌ Ошибка подключения к серверу: {e}. Пожалуйста, попробуйте снова:"
            )
            return GET_SERVER_KEY

async def cancel(update: Update, context: ContextTypes.DEFAULT_TYPE):
    await update.message.reply_text(
        "Действие отменено.",
        reply_markup=await get_admin_keyboard()
    )
    return ConversationHandler.END

async def get_queue_size():
    # Проверка наличия server_key
    if not server_key:
        logger.error("Server key не настроен. Запрос отменен.")
        return None

    # Создание сессии aiohttp
    async with aiohttp.ClientSession() as session:
        try:
            # Логирование отправки запроса
            logger.info("Отправка POST-запроса к http://helper-service:8082/api/helper/chat/queue")
            logger.debug(f"Параметры запроса: tgId={ADMIN_ID}, secretKey={server_key}")

            # Выполнение запроса
            async with session.post(
                'http://helper-service:8082/api/helper/chat/queue',
                params={
                    'tgId': str(ADMIN_ID),
                    'secretKey': server_key
                }
            ) as response:
                # Логирование статуса ответа
                logger.info(f"Получен ответ от сервера. Статус: {response.status}")

                # Проверка успешного статуса
                if response.status == 200:
                    data = await response.json()
                    logger.debug(f"Данные ответа: {data}")

                    # Извлечение размера очереди
                    queue_size = data.get('queue_size', 0)
                    logger.info(f"Размер очереди: {queue_size}")
                    return queue_size
                else:
                    # Логирование ошибочного статуса
                    error_message = await response.text()
                    logger.error(f"Ошибка при запросе. Статус: {response.status}, Сообщение: {error_message}")
                    return None
        except Exception as e:
            # Логирование исключений
            logger.error(f"Произошла ошибка при выполнении запроса: {e}")
            return None

async def handle_message(update: Update, context: ContextTypes.DEFAULT_TYPE):
    global work_started, notification_task

    if update.effective_user.id == ADMIN_ID:
        text = update.message.text

        if text == "Начать работу":
            work_started = True
            queue_size = await get_queue_size()
            count = queue_size if queue_size is not None else len(waiting_queue)

            notification_task = asyncio.create_task(notify_admin(context))
            await update.message.reply_text(
                f"ℹ Ожидают ответа: {count} пользователей\n"
                "Работа начата. Используйте 'Следующий диалог' для начала общения.",
                reply_markup=await get_admin_keyboard()
            )

        elif text == "Завершить работу":
            work_started = False
            if ADMIN_ID in active_dialogs:
                user_id, chat_id = active_dialogs.pop(ADMIN_ID)
                await context.bot.send_message(
                    chat_id=chat_id,
                    text="❌ Оператор завершил работу. Диалог прерван."
                )
            if notification_task:
                notification_task.cancel()
            await update.message.reply_text(
                "❌ Работа завершена. Новые диалоги не принимаются.",
                reply_markup=await get_admin_keyboard()
            )

        elif text == "Следующий диалог":
            if not work_started:
                await update.message.reply_text("⚠ Сначала начните работу!", reply_markup=await get_admin_keyboard())
                return

            if ADMIN_ID in active_dialogs:
                await update.message.reply_text(
                    "⚠ У вас уже есть активный диалог. Завершите его сначала.",
                    reply_markup=await get_admin_keyboard()
                )
            else:
                if waiting_queue:
                    user_id, chat_id, first_message, first_message_id = waiting_queue.popleft()
                    active_dialogs[ADMIN_ID] = (user_id, chat_id)

                    await context.bot.send_message(
                        chat_id=chat_id,
                        text="✅ Оператор подключился к диалогу. Можете общаться."
                    )

                    await update.message.reply_text(
                        f"🔄 Новый диалог с пользователем ID: {user_id}\n"
                        f"Первое сообщение:\n\n{first_message}\n\n"
                        "Отправляйте сообщения - они будут пересылаться пользователю. "
                        "Используйте 'Завершить диалог' для окончания.",
                        reply_markup=await get_admin_keyboard()
                    )
                else:
                    await update.message.reply_text(
                        "ℹ Нет ожидающих диалогов.",
                        reply_markup=await get_admin_keyboard()
                    )

        elif text == "Завершить диалог":
            if ADMIN_ID not in active_dialogs:
                await update.message.reply_text(
                    "⚠ Нет активного диалога для завершения.",
                    reply_markup=await get_admin_keyboard()
                )
            else:
                user_id, chat_id = active_dialogs.pop(ADMIN_ID)
                await context.bot.send_message(
                    chat_id=chat_id,
                    text="❌ Оператор завершил диалог. Спасибо за обращение!"
                )
                await update.message.reply_text(
                    "❌ Диалог завершён.",
                    reply_markup=await get_admin_keyboard()
                )

        elif ADMIN_ID in active_dialogs and text not in COMMANDS:
            user_id, chat_id = active_dialogs[ADMIN_ID]
            try:
                await context.bot.send_message(
                    chat_id=chat_id,
                    text=f"Оператор: {update.message.text}"
                )
            except Exception as e:
                await update.message.reply_text(
                    f"❌ Ошибка отправки: {e}",
                    reply_markup=await get_admin_keyboard()
                )

    else:
        user_id = update.effective_user.id
        chat_id = update.effective_chat.id

        if work_started:
            if ADMIN_ID in active_dialogs:
                active_user_id, _ = active_dialogs[ADMIN_ID]
                if user_id == active_user_id:
                    await context.bot.send_message(
                        chat_id=ADMIN_ID,
                        text=f"Пользователь {user_id}:\n\n{update.message.text}"
                    )
                else:
                    await update.message.reply_text(
                        "⏳ Оператор занят другим диалогом. Ваше сообщение будет обработано позже.",
                        reply_markup=ReplyKeyboardRemove()
                    )
                    if not any(q[0] == user_id for q in waiting_queue):
                        waiting_queue.append((
                            user_id,
                            chat_id,
                            update.message.text,
                            update.message.message_id
                        ))
            else:
                if not any(q[0] == user_id for q in waiting_queue):
                    waiting_queue.append((
                        user_id,
                        chat_id,
                        update.message.text,
                        update.message.message_id
                    ))
                    if work_started:
                        try:
                            await context.bot.send_message(
                                chat_id=ADMIN_ID,
                                text=f"🔔 Новый диалог от пользователя ID: {user_id}! Всего в очереди: {len(waiting_queue)}",
                                reply_markup=await get_admin_keyboard()
                            )
                        except:
                            pass

                await update.message.reply_text(
                    "✔ Ваше сообщение получено. Ожидайте подключения оператора.",
                    reply_markup=ReplyKeyboardRemove()
                )
        else:
            await update.message.reply_text(
                "❌ В настоящее время оператор не принимает сообщения. Попробуйте позже.",
                reply_markup=ReplyKeyboardRemove()
            )

def main():
    application = Application.builder() \
        .token("8170772601:AAGitKaxTm_LVKE4BtaqmsU4iR9RFtzZCYM") \
        .build()

    conv_handler = ConversationHandler(
        entry_points=[CommandHandler("start", start)],
        states={
            GET_SERVER_KEY: [MessageHandler(filters.TEXT & ~filters.COMMAND, get_server_key)],
        },
        fallbacks=[CommandHandler("cancel", cancel)],
    )

    application.add_handlers([
        conv_handler,
        MessageHandler(filters.TEXT & ~filters.COMMAND, handle_message)
    ])

    application.run_polling()

if __name__ == "__main__":
    main()