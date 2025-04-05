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

ADMIN_ID = 588116881 
active_dialogs = {} 
waiting_queue = deque()  
work_started = False
notification_task = None

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
    if update.effective_user.id == ADMIN_ID:
        await update.message.reply_text(
            "Админ-панель готова к работе:",
            reply_markup=await get_admin_keyboard()
        )
    else:
        await update.message.reply_text(
            "Привет! Отправьте ваше сообщение, и оператор с вами свяжется.",
            reply_markup=ReplyKeyboardRemove()
        )

async def handle_message(update: Update, context: ContextTypes.DEFAULT_TYPE):
    global work_started, notification_task
    
    if update.effective_user.id == ADMIN_ID:
        text = update.message.text
        
        if text == "Начать работу":
            work_started = True
            count = len(waiting_queue)
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
                        text=f"Пользователь ID {user_id}:\n\n{update.message.text}"
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
    
    application.add_handlers([
        CommandHandler("start", start),
        MessageHandler(filters.TEXT & ~filters.COMMAND, handle_message)
    ])
    
    application.run_polling()

if __name__ == "__main__":
    main()