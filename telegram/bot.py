from dotenv import load_dotenv
import os

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
    ConversationHandler,
    filters
)
import asyncio
import aiohttp
import logging

# Загрузка переменных окружения
load_dotenv()
TELEGRAM_TOKEN = os.getenv("TELEGRAM_API_KEY")

# Настройка логирования
logging.basicConfig(
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
    level=logging.INFO
)
logger = logging.getLogger(__name__)

# Конфигурация
ADMIN_ID = 996188029
active_dialogs = set()  
work_started = False
notification_task = None
secret_key = None
WAITING_FOR_SECONDS = 1
COMMANDS = ["Начать работу", "Завершить работу", "Следующий диалог", "Завершить диалог", "Получить сообщения"]
BACKEND_URL = "http://helper-service:8082/api/helper/chat"

async def verify_key(key: str) -> bool:
    """Проверяет ключ на бекенде"""
    try:
        async with aiohttp.ClientSession() as session:
            async with session.post(
                f"{BACKEND_URL}/key",
                params={'key': key}
            ) as response:
                return response.status == 200
    except Exception as e:
        logger.error(f"Ошибка проверки ключа: {e}")
        return False

async def get_queue_size() -> int:
    """Получает размер очереди с бекенда"""
    global secret_key
    if not secret_key:
        return 0

    try:
        async with aiohttp.ClientSession() as session:
            async with session.get(
                f"{BACKEND_URL}/queue",
                params={'tgId': ADMIN_ID, 'key': secret_key},
                timeout=5
            ) as response:
                if response.status == 200:
                    return int(await response.text())
                return 0
    except Exception as e:
        logger.error(f"Ошибка получения очереди: {e}")
        return 0

async def get_next_dialog() -> str | None:
    """Получаем следующее сообщение из очереди"""
    global secret_key
    try:
        async with aiohttp.ClientSession() as session:
            async with session.post(
                f"{BACKEND_URL}/link",
                params={'tgId': ADMIN_ID, 'key': secret_key},
                timeout=5
            ) as response:
                if response.status == 200:
                    return await response.text()
                return None
    except Exception as e:
        logger.error(f"Ошибка получения диалога: {e}")
        return None

async def send_to_user(message: str) -> bool:
    """Отправляет сообщение пользователю через бекенд"""
    global secret_key
    try:
        async with aiohttp.ClientSession() as session:
            async with session.post(
                f"{BACKEND_URL}/message",
                params={
                    'tgId': ADMIN_ID,
                    'key': secret_key
                },
                json={
                    'msg': message
                }
            ) as response:
                return response.status == 200
    except Exception as e:
        logger.error(f"Ошибка отправки сообщения: {e}")
        return False
    
async def get_messages_by_time(seconds: int) -> list[str] | None:
    """Получает сообщения за указанное количество секунд"""
    global secret_key
    if not secret_key:
        return None

    try:
        async with aiohttp.ClientSession() as session:
            async with session.get(
                f"{BACKEND_URL}",  
                params={
                    'tgId': ADMIN_ID,
                    'key': secret_key
                },
                json={
                    'duration': seconds
                },
                timeout=5
            ) as response:
                if response.status == 200:
                    data = await response.json()
                    if isinstance(data, list) and all(isinstance(item, str) for item in data):
                        return data
                    logger.warning("Некорректный формат данных в ответе")
                return None
    except Exception as e:
        logger.error(f"Ошибка получения сообщений по времени: {e}")
        return None

async def close_dialog() -> bool:
    """Закрывает текущий диалог"""
    global secret_key
    try:
        async with aiohttp.ClientSession() as session:
            async with session.post(
                f"{BACKEND_URL}/close",
                params={'tgId': ADMIN_ID, 'key': secret_key},
                timeout=5
            ) as response:
                if response.status == 200:
                    return True
                elif response.status == 400:
                    logger.warning("Попытка закрыть несуществующий диалог")
                    return False
                return False
    except Exception as e:
        logger.error(f"Ошибка закрытия диалога: {e}")
        return False

async def notify_admin(context: ContextTypes.DEFAULT_TYPE):
    """Периодически уведомляет админа о новых сообщениях"""
    global work_started
    while work_started:
        if ADMIN_ID not in active_dialogs:
            queue_size = await get_queue_size()
            if queue_size > 0:
                await context.bot.send_message(
                    chat_id=ADMIN_ID,
                    text=f"🔔 Ожидают ответа: {queue_size}",
                    reply_markup=await get_admin_keyboard()
                )
        await asyncio.sleep(60)

async def get_admin_keyboard():
    """Генерирует клавиатуру для админа"""
    if not work_started:
        return ReplyKeyboardMarkup([[KeyboardButton("Начать работу")]], resize_keyboard=True)
    
    if ADMIN_ID in active_dialogs:
        return ReplyKeyboardMarkup([[KeyboardButton("Завершить диалог")]], resize_keyboard=True)
    else:
        return ReplyKeyboardMarkup(
            [
                [KeyboardButton("Следующий диалог")],
                [KeyboardButton("Получить сообщения")],
                [KeyboardButton("Завершить работу")]
            ],
            resize_keyboard=True
        )

async def start(update: Update, context: ContextTypes.DEFAULT_TYPE):
    """Обработчик команды /start"""
    global secret_key
    if update.effective_user.id == ADMIN_ID:
        if secret_key is None:
            await update.message.reply_text(
                "🔒 Введите секретный ключ:",
                reply_markup=ReplyKeyboardRemove()
            )
        else:
            await update.message.reply_text(
                "Админ-панель готова:",
                reply_markup=await get_admin_keyboard()
            )
    else:
        await update.message.reply_text(
            "Привет! Оставьте сообщение, и оператор вам ответит.",
            reply_markup=ReplyKeyboardRemove()
        )

async def handle_seconds_input(update: Update, context: ContextTypes.DEFAULT_TYPE):
    """Обрабатывает ввод количества секунд"""
    user_id = update.effective_user.id
    if user_id != ADMIN_ID:
        return ConversationHandler.END
    
    try:
        seconds = int(update.message.text)
        if seconds <= 0:
            raise ValueError
    except ValueError:
        await update.message.reply_text("⚠️ Пожалуйста, введите положительное целое число")
        return WAITING_FOR_SECONDS
    
    messages = await get_messages_by_time(seconds)
    
    if messages:
        response = f"📨 Сообщения за последние {seconds} секунд:\n\n" + "\n\n".join(messages)
        for i in range(0, len(response), 4096):
            await update.message.reply_text(response[i:i+4096])
    else:
        await update.message.reply_text(f"ℹ️ Нет сообщений за последние {seconds} секунд")
    
    await update.message.reply_text(
        "Админ-панель:",
        reply_markup=await get_admin_keyboard()
    )
    return ConversationHandler.END

async def handle_message(update: Update, context: ContextTypes.DEFAULT_TYPE):
    """Обработчик всех текстовых сообщений"""
    global work_started, notification_task, secret_key
    user_id = update.effective_user.id
    text = update.message.text

    if user_id == ADMIN_ID:
        if secret_key is None:
            if await verify_key(text.strip()):
                secret_key = text.strip()
                await update.message.reply_text(
                    "✅ Доступ разрешён",
                    reply_markup=await get_admin_keyboard()
                )
            else:
                await update.message.reply_text("❌ Неверный ключ")
            return ConversationHandler.END

        if text == "Начать работу":
            work_started = True
            count = await get_queue_size()
            notification_task = asyncio.create_task(notify_admin(context))
            await update.message.reply_text(
                f"ℹ️ Ожидают: {count}\nРабота начата",
                reply_markup=await get_admin_keyboard()
            )
            return ConversationHandler.END

        elif text == "Завершить работу":
            work_started = False
            if ADMIN_ID in active_dialogs:
                await close_dialog()
                active_dialogs.remove(ADMIN_ID)
            if notification_task:
                notification_task.cancel()
            await update.message.reply_text(
                "❌ Работа завершена",
                reply_markup=await get_admin_keyboard()
            )
            return ConversationHandler.END

        elif text == "Следующий диалог":
            if not work_started:
                await update.message.reply_text("⚠️ Сначала начните работу!")
                return ConversationHandler.END
                
            if ADMIN_ID in active_dialogs:
                await update.message.reply_text("⚠️ Завершите текущий диалог!")
                return ConversationHandler.END
                
            user_msg = await get_next_dialog()
            if user_msg:
                active_dialogs.add(ADMIN_ID)
                await update.message.reply_text(
                    f"🔄 Новый диалог:\n{user_msg}",
                    reply_markup=await get_admin_keyboard()
                )
            else:
                await update.message.reply_text(
                    "ℹ️ Нет ожидающих сообщений",
                    reply_markup=await get_admin_keyboard()
                )
            return ConversationHandler.END
        
        elif text == "Получить сообщения":
            if not work_started:
                await update.message.reply_text("⚠️ Сначала начните работу!")
                return ConversationHandler.END
                
            await update.message.reply_text(
                "⏳ Введите количество секунд для получения сообщений:",
                reply_markup=ReplyKeyboardRemove()
            )
            return WAITING_FOR_SECONDS

        elif text == "Завершить диалог":
            if ADMIN_ID in active_dialogs:
                success = await close_dialog()
                if success:
                    active_dialogs.remove(ADMIN_ID)
                    await update.message.reply_text(
                        "✅ Диалог завершён",
                        reply_markup=await get_admin_keyboard()
                    )
                else:
                    await update.message.reply_text(
                        "⚠️ Не удалось завершить диалог",
                        reply_markup=await get_admin_keyboard()
                    )
            else:
                await update.message.reply_text(
                    "⚠️ Нет активного диалога",
                    reply_markup=await get_admin_keyboard()
                )
            return ConversationHandler.END

        elif ADMIN_ID in active_dialogs and text not in COMMANDS:
            if await send_to_user(text):
                logger.info("Сообщение оператора отправлено")
            else:
                await update.message.reply_text("⚠️ Ошибка отправки")
            return ConversationHandler.END

    else:
        if work_started:
            if ADMIN_ID in active_dialogs:
                await context.bot.send_message(
                    chat_id=ADMIN_ID,
                    text=f"Пользователь:\n{text}"
                )
            else:
                await update.message.reply_text("⌛ Ожидайте ответа оператора")
        else:
            await update.message.reply_text("❌ Оператор недоступен")
        return ConversationHandler.END

def main():
    application = Application.builder().token(TELEGRAM_TOKEN).build()
    
    conv_handler = ConversationHandler(
        entry_points=[CommandHandler("start", start)],
        states={
            WAITING_FOR_SECONDS: [
                MessageHandler(filters.TEXT & ~filters.COMMAND, handle_seconds_input)
            ],
        },
        fallbacks=[MessageHandler(filters.TEXT & ~filters.COMMAND, handle_message)],
    )
    
    application.add_handler(conv_handler)
    application.add_handler(MessageHandler(filters.TEXT & ~filters.COMMAND, handle_message))
    
    logger.info("Бот запущен")
    application.run_polling()

if __name__ == "__main__":
    main()