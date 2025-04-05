const WebSocket = require('ws');
const http = require('http');
const url = require('url');

// Функция для проверки токена (замените на свою логику)
function verifyToken(token) {
    // Например, проверяем, что токен соответствует определенному формату
    if (!token || token !== 'valid-token') {
        throw new Error('Неверный токен');
    }
    return { userId: 1 }; // Возвращаем данные пользователя
}

// Создаем HTTP сервер
const server = http.createServer((req, res) => {
    if (req.method === 'POST' && req.url === '/chat/start') {
        let body = '';
        req.on('data', (chunk) => {
            body += chunk.toString();
        });
        req.on('end', () => {
            try {
                const data = JSON.parse(body);
                console.log('Получен POST-запрос:', data);

                // Ответ на POST-запрос
                res.writeHead(200, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({ message: 'Первое сообщение успешно отправлено!' }));
            } catch (error) {
                console.error('Ошибка при парсинге JSON:', error);
                res.writeHead(400, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({ error: 'Неверный формат JSON' }));
            }
        });
    } else {
        res.writeHead(200, { 'Content-Type': 'text/plain' });
        res.end('WebSocket Server is running');
    }
});

// Создаем WebSocket сервер
const wss = new WebSocket.Server({ server });

wss.on('connection', (ws, req) => {
    // Извлекаем токен из URL
    const parsedUrl = url.parse(req.url, true);
    const token = parsedUrl.query.token;

    if (!token) {
        console.error('Токен отсутствует');
        ws.close(); // Закрываем соединение
        return;
    }

    try {
        // Проверяем токен
        const user = verifyToken(token);
        console.log('Пользователь подключен:', user);

        // Отправляем приветственное сообщение клиенту
        ws.send('Добро пожаловать в чат!');

        // Обработка входящих сообщений
        ws.on('message', (message) => {
            console.log(`Получено сообщение: ${message}`);
            ws.send(`Сервер получил: ${message}, ${token}`);
        });

        // Обработка закрытия соединения
        ws.on('close', () => {
            console.log('Клиент отключился');
        });
    } catch (error) {
        console.error('Ошибка при проверке токена:', error.message);
        ws.close(); // Закрываем соединение
    }
});

// Запускаем сервер на порту 3001
server.listen(3001, () => {
    console.log('HTTP и WebSocket сервер запущены на localhost:3001');
});