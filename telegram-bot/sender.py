import asyncio
import threading
import logging

from telegram import error
from telegram.ext import Application

class AsyncBotSender:
    def __init__(self, application_token):
        self.application = Application.builder().token(application_token).build()
        self.loop = None
        self.thread = None
        self.start_loop()

    def start_loop(self):
        self.loop = asyncio.new_event_loop()

        def run_loop():
            asyncio.set_event_loop(self.loop)
            self.loop.run_forever()

        self.thread = threading.Thread(target=run_loop, daemon=True)
        self.thread.start()

    def send(self, chat_id, text, reply_markup=None):
        future = asyncio.run_coroutine_threadsafe(
            self._send_message(chat_id, text, reply_markup),
            self.loop
        )
        future.add_done_callback(self._handle_result)

    async def _send_message(self, chat_id, text, reply_markup=None):
        try:
            await self.application.bot.send_message(
                chat_id=chat_id, text=text, parse_mode="HTML", reply_markup=reply_markup
            )
        except error.Forbidden:
            logging.warning(f"Пользователь {chat_id} заблокировал бота. Невозможно отправить сообщение")
        except Exception as e:
            logging.error(f"Ошибка отправки пользователю {chat_id}: {e}")

    def _handle_result(self, future):
        try:
            future.result()
        except Exception as e:
            logging.error(f"Ошибка асинхронного обработчика: {e}")