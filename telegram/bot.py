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
    filters
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

ADMIN_ID = 996188029
active_dialogs = {}
waiting_queue = deque()
work_started = False
notification_task = None
secret_key = None  # Глобальная переменная для хранения секретного ключа
COMMANDS = ["Начать работу", "Завершить работу", "Следующий диалог", "Завершить диалог"]
BACKEND_URL = "http://helper-service:8082/api/helper/chat"

async def verify_key(key: str) -> bool:
    """Проверяет ключ на бекенде"""
    try:
        logger.info("Отправка запроса на проверку ключа...")
        logger.info(f"Параметры запроса: key={key}")

        async with aiohttp.ClientSession() as session:
            async with session.post(
                f"{BACKEND_URL}/key",
                params={
                    'key': key,
                }
            ) as response:
                if response.status == 200:
                    logger.info(f"Ключ валиден")
                    return True
                if response.status == 401:
                    logger.info(f"Ключ невалиден")
                    return False
                else:
                    error_message = await response.text()
                    logger.error(f"Ошибка при проверке ключа. Статус: {response.status}, Сообщение: {error_message}")
                    return False
    except Exception as e:
        logger.error(f"Ошибка подключения к серверу при проверке ключа: {e}")
        return False


async def get_queue_size() -> int:
    """Получает размер очереди с бекенда"""
    global secret_key
    if not secret_key:
        logger.warning("Secret key не настроен. Запрос размера очереди отменён.")
        return 0

    try:
        logger.info("Отправка запроса на получение размера очереди...")
        logger.debug(f"Параметры запроса: Authorization=Bearer {secret_key}")

        async with aiohttp.ClientSession() as session:
            async with session.get(
                f"{BACKEND_URL}/queue",
                headers={"Authorization": f"Bearer {secret_key}"},
                timeout=5
            ) as response:
                if response.status == 200:
                    data = await response.json()
                    queue_size = data.get("size", 0)
                    logger.info(f"Размер очереди получен: {queue_size}")
                    return queue_size
                else:
                    error_message = await response.text()
                    logger.error(f"Ошибка при получении размера очереди. Статус: {response.status}, Сообщение: {error_message}")
                    return 0
    except Exception as e:
        logger.error(f"Ошибка подключения к серверу при получении размера очереди: {e}")
        return 0


async def notify_admin(context: ContextTypes.DEFAULT_TYPE):
    global notification_task
    while work_started:
        if waiting_queue and ADMIN_ID not in active_dialogs:
            count = len(waiting_queue)
            logger.info(f"Уведомление администратора о новых диалогах. Всего в очереди: {count}")

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
    global secret_key
    if update.effective_user.id == ADMIN_ID:
        if secret_key is None:
            logger.info("Администратор запрашивает ввод секретного ключа.")
            await update.message.reply_text(
                "🔒 Пожалуйста, введите секретный ключ для доступа к админ-панели:",
                reply_markup=ReplyKeyboardRemove()
            )
        else:
            logger.info("Администратор уже авторизован. Отображение админ-панели.")
            await update.message.reply_text(
                "Админ-панель готова к работе:",
                reply_markup=await get_admin_keyboard()
            )
    else:
        logger.info(f"Обычный пользователь (ID: {update.effective_user.id}) начал общение.")
        await update.message.reply_text(
            "Привет! Отправьте ваше сообщение, и оператор с вами свяжется.",
            reply_markup=ReplyKeyboardRemove()
        )


async def handle_message(update: Update, context: ContextTypes.DEFAULT_TYPE):
    global work_started, notification_task, secret_key
    user_id = update.effective_user.id
    chat_id = update.effective_chat.id
    text = update.message.text

    if user_id == ADMIN_ID:
        # Обработка ввода секретного ключа
        if secret_key is None:
            logger.info("Администратор ввёл секретный ключ.")
            key = text.strip()
            is_valid = await verify_key(key)
            if is_valid:
                secret_key = key
                logger.info("Секретный ключ подтверждён.")
                await update.message.reply_text(
                    "✅ Ключ подтверждён. Доступ к админ-панели разрешён.",
                    reply_markup=await get_admin_keyboard()
                )
            else:
                logger.error("Введён неверный секретный ключ.")
                await update.message.reply_text(
                    "❌ Неверный ключ. Попробуйте ещё раз или обратитесь к администратору."
                )
            return

        # Обработка команд администратора
        if text == "Начать работу":
            logger.info("Администратор начал работу.")
            work_started = True
            count = await get_queue_size()  # Получаем размер очереди с бекенда
            notification_task = asyncio.create_task(notify_admin(context))
            await update.message.reply_text(
                f"ℹ️ Ожидают ответа: {count} пользователей\n"
                "Работа начата. Используйте 'Следующий диалог' для начала общения.",
                reply_markup=await get_admin_keyboard()
            )
        elif text == "Завершить работу":
            logger.info("Администратор завершил работу.")
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
            logger.info("Администратор запросил следующий диалог.")
            if not work_started:
                logger.warning("Работа не начата. Запрос 'Следующий диалог' отклонён.")
                await update.message.reply_text("⚠️ Сначала начните работу!", reply_markup=await get_admin_keyboard())
                return
            if ADMIN_ID in active_dialogs:
                logger.warning("У администратора уже есть активный диалог.")
                await update.message.reply_text(
                    "⚠️ У вас уже есть активный диалог. Завершите его сначала.",
                    reply_markup=await get_admin_keyboard()
                )
            else:
                queue_size = await get_queue_size()
                if queue_size > 0:
                    if waiting_queue:
                        logger.info("Начат новый диалог с пользователем.")
                        user_id, chat_id, first_message, first_message_id = waiting_queue.popleft()
                        active_dialogs[ADMIN_ID] = (user_id, chat_id)
                        await context.bot.send_message(
                            chat_id=chat_id,
                            text="✅ Оператор подключился к диалогу. Можете общаться."
                        )
                        await update.message.reply_text(
                            f"🔄 Новый диалог с пользователем ID: {user_id}\n"
                            f"Первое сообщение:\n{first_message}\n"
                            "Отправляйте сообщения - они будут пересылаться пользователю. "
                            "Используйте 'Завершить диалог' для окончания.",
                            reply_markup=await get_admin_keyboard()
                        )
                else:
                    logger.info("Нет ожидающих диалогов.")
                    await update.message.reply_text(
                        "ℹ️ Нет ожидающих диалогов.",
                        reply_markup=await get_admin_keyboard()
                    )
        elif text == "Завершить диалог":
            logger.info("Администратор завершил текущий диалог.")
            if ADMIN_ID not in active_dialogs:
                logger.warning("Нет активного диалога для завершения.")
                await update.message.reply_text(
                    "⚠️ Нет активного диалога для завершения.",
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
            logger.info("Администратор отправил сообщение пользователю.")
            user_id, chat_id = active_dialogs[ADMIN_ID]
            try:
                await context.bot.send_message(
                    chat_id=chat_id,
                    text=f"Оператор: {update.message.text}"
                )
            except Exception as e:
                logger.error(f"Ошибка отправки сообщения пользователю: {e}")
                await update.message.reply_text(
                    f"❌ Ошибка отправки: {e}",
                    reply_markup=await get_admin_keyboard()
                )
    else:
        logger.info(f"Пользователь (ID: {user_id}) отправил сообщение.")
        if work_started:
            if ADMIN_ID in active_dialogs:
                active_user_id, _ = active_dialogs[ADMIN_ID]
                if user_id == active_user_id:
                    logger.info(f"Пересылка сообщения от пользователя (ID: {user_id}) администратору.")
                    await context.bot.send_message(
                        chat_id=ADMIN_ID,
                        text=f"Пользователь ID {user_id}:\n{update.message.text}"
                    )
                else:
                    logger.info(f"Пользователь (ID: {user_id}) добавлен в очередь.")
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
                logger.info(f"Пользователь (ID: {user_id}) добавлен в очередь.")
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
                        except Exception as e:
                            logger.error(f"Ошибка уведомления администратора: {e}")
                await update.message.reply_text(
                    "✔️ Ваше сообщение получено. Ожидайте подключения оператора.",
                    reply_markup=ReplyKeyboardRemove()
                )
        else:
            logger.info(f"Пользователь (ID: {user_id}) попытался отправить сообщение, но работа не начата.")
            await update.message.reply_text(
                "❌ В настоящее время оператор не принимает сообщения. Попробуйте позже.",
                reply_markup=ReplyKeyboardRemove()
            )


def main():
    application = Application.builder() \
        .token("8170772601:AAGitKaxTm_LVKE4BtaqmsU4iR9RFtzZCYM") \
        .build()

    application.add_handlers([
        CommandHandler("start", start),
        MessageHandler(filters.TEXT & ~filters.COMMAND, handle_message)
    ])

    logger.info("Бот запущен.")
    application.run_polling()


if __name__ == "__main__":
    main()