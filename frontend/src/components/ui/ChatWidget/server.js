const WebSocket = require('ws');
const http = require('http');

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

wss.on('connection', (ws) => {
    console.log('Новый клиент подключен');

    // Отправляем приветственное сообщение клиенту
    ws.send('Добро пожаловать в чат!');

    // Обработка входящих сообщений
    ws.on('message', (message) => {
        console.log(`Получено сообщение: ${message}`);

        // Отправляем автоматический ответ клиенту
        ws.send(`Сервер получил: ${message}`);
    });

    // Обработка закрытия соединения
    ws.on('close', () => {
        console.log('Клиент отключился');
    });
});

// Запускаем сервер на порту 3001
server.listen(3001, () => {
    console.log('HTTP и WebSocket сервер запущены на localhost:3001');
});