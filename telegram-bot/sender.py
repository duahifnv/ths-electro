import asyncio
import threading

from telegram import error

class AsyncMessageSender:
    def __init__(self):
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

    def send(self, bot, chat_id, text):
        future = asyncio.run_coroutine_threadsafe(
            self._send_message(bot, chat_id, text),
            self.loop
        )
        future.add_done_callback(self._handle_result)

    async def _send_message(self, bot, chat_id, text):
        try:
            await bot.send_message(chat_id=chat_id, text=text)
        except error.Forbidden:
            print(f"Пользователь {chat_id} заблокировал бота. Невозможно отправить сообщение")
        except Exception as e:
            print(f"Ошибка отправки пользователю {chat_id}: {e}")

    def _handle_result(self, future):
        try:
            future.result()
        except Exception as e:
            print(f"Ошибка асинхронного обработчика: {e}")