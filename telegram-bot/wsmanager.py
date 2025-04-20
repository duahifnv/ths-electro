import threading
import time
import logging
import websocket

# Настройка логирования
logging.basicConfig(
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    level=logging.INFO
)
logger = logging.getLogger(__name__)

class WebSocketManager:
    def __init__(self, subscribe_topics, ws_url, message_handler=None, send_apis_on_connect=None):
        self.ws = None
        self.ws_url = ws_url
        self.subscriptions = {}
        self.token = None
        self.connected = False
        self.subscribe_topics = subscribe_topics
        self.send_apis_on_connect = send_apis_on_connect
        self.message_handler = message_handler

    def connect(self):
        """Подключение к WebSocket серверу"""
        try:
            self.ws = websocket.WebSocketApp(
                self.ws_url,
                header=["user-agent: python_ws"],
                on_open=self.on_open,
                on_message=self.on_message,
                on_error=self.on_error,
                on_close=self.on_close
            )

            # Запускаем WebSocket в отдельном потоке
            self.thread = threading.Thread(target=self.ws.run_forever)
            self.thread.daemon = True
            self.thread.start()

            # Ждем подключения
            timeout = 5  # секунд
            start_time = time.time()
            while not self.connected and time.time() - start_time < timeout:
                time.sleep(0.1)

            if not self.connected:
                raise ConnectionError("Не удалось подключиться к WebSocket")

            return True

        except Exception as e:
            logger.error(f"WebSocket connection error: {e}")
            return False

    def on_open(self, ws):
        """Обработчик открытия соединения"""
        logger.info("WebSocket соединение установлено")
        self.send_connect()
        self.connected = True

    def on_message(self, ws, message):
        """Обработчик входящих сообщений"""
        logger.debug(f"Получено сообщение: {message}")

        # Разбираем STOMP фрейм
        if "\n\n" in message:
            headers_part, body = message.split("\n\n", 1)
            command = headers_part.split("\n")[0]
            headers = {}

            for line in headers_part.split("\n")[1:]:
                if ":" in line:
                    key, value = line.split(":", 1)
                    headers[key.strip()] = value.strip()

            if command == "CONNECTED":
                logger.info("STOMP соединение установлено")
                for sub_id, topic in self.subscribe_topics.items():
                    self.subscribe(sub_id, topic)
                for api in self.send_apis_on_connect:
                    self.send(api, "")

            elif command == "MESSAGE":
                destination = headers.get("destination", "")
                try:
                    self.message_handler(destination, body.rstrip('\x00'))
                except Exception as e:
                    logger.error(f"Ошибка в обработчике сообщений: {e}")

    def on_error(self, ws, error):
        """Обработчик ошибок"""
        logger.error(f"WebSocket error: {error}")
        self.connected = False

    def on_close(self, ws, close_status_code, close_msg):
        """Обработчик закрытия соединения"""
        logger.info("WebSocket соединение закрыто")
        self.connected = False

    def send_connect(self):
        """Отправка CONNECT фрейма"""
        connect_frame = (
            "CONNECT\n"
            "accept-version:1.2\n"
            "host:/api/helper\n"
            "heart-beat:0,0\n"
            "\n\0"
        )
        self.ws.send(connect_frame)

    def send(self, destination, body, headers=None):
        """Отправка SEND фрейма"""
        if not self.connected:
            raise ConnectionError("WebSocket не подключен")

        if headers is None:
            headers = {}

        headers["destination"] = destination

        frame = "SEND\n"
        for key, value in headers.items():
            frame += f"{key}:{value}\n"

        frame += f"\n{body}\0"
        self.ws.send(frame)

    def subscribe(self, sub_id, destination):
        """Подписка на топик/очередь"""
        if not self.connected:
            raise ConnectionError("WebSocket не подключен")

        subscribe_frame = (
            f"SUBSCRIBE\n"
            f"id:{sub_id}\n"
            f"destination:{destination}\n"
            f"\n\0"
        )
        self.subscriptions[sub_id] = destination
        self.ws.send(subscribe_frame)

    def unsubscribe(self, sub_id):
        """Отписка от топика/очереди"""
        if not self.connected:
            raise ConnectionError("WebSocket не подключен")

        if sub_id in self.subscriptions:
            unsubscribe_frame = (
                f"UNSUBSCRIBE\n"
                f"id:{sub_id}\n"
                f"\n\0"
            )
            self.ws.send(unsubscribe_frame)
            del self.subscriptions[sub_id]

    def disconnect(self):
        """Закрытие соединения"""
        if self.ws:
            disconnect_frame = "DISCONNECT\n\n\0"
            self.ws.send(disconnect_frame)
            self.ws.close()
            self.connected = False