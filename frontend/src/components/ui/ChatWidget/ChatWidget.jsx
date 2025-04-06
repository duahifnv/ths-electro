import { useState, useEffect } from 'react';
import styles from './ChatWidget.module.css';
import { useAuth } from '../../../react-envelope/hooks/useAuth';
import { ChatDots } from '../../dummies/Icons.jsx';
import ChatInput from '../../widgets/ChatInput/ChatInput';
import ExButton from '../../../react-envelope/components/ui/buttons/ExButton/ExButton';
import { useNavigate } from 'react-router-dom';

const ChatWidget = () => {
    const { auth } = useAuth();
    const [isChatOpen, setIsChatOpen] = useState(false);
    const [messages, setMessages] = useState([]);
    const [inputValue, setInputValue] = useState('');
    const [webSocket, setWebSocket] = useState(null);
    const navigate = useNavigate();

    const toggleChat = () => {
        setIsChatOpen((prev) => !prev);
    };

    const handleSendMessage = async () => {
        if (!inputValue.trim()) return;

        // Создаем новый объект сообщения
        const newMessage = { text: inputValue, isUser: true };

        // Добавляем сообщение в состояние
        setMessages((prevMessages) => [...prevMessages, newMessage]);
        setInputValue('');

        // Создаем JSON-строку из inputValue
        const jsonMessage = JSON.stringify({ message: inputValue });
        // Отправляем сообщение через WebSocket
        if (webSocket && webSocket.readyState === WebSocket.OPEN) {
            webSocket.send(jsonMessage); // Отправляем JSON-строку
        } else {
            console.warn('WebSocket соединение еще не установлено');
        }
    };

    useEffect(() => {
        let ws;
        const connect = () => {
            if (!auth || !auth.token) {
                console.error('Токен отсутствует, невозможно подключиться к WebSocket');
                return;
            }
            // Здесь после initMessage должно идти сообщение, которое пользователь написал первым и ПОСЛЕ КОТОРОГО подключился к сокету
            ws = new WebSocket(`ws://localhost:8082/api/helper/ws?Authorization=Bearer:${encodeURIComponent(auth.token)}&initMessage=вопросик`);

            ws.onopen = () => {
                console.log('WebSocket соединение установлено');
                setWebSocket(ws);
            };

            ws.onmessage = (event) => {
                const serverMessage = { text: event.data, isUser: false };
                setMessages((prevMessages) => [...prevMessages, serverMessage]);
            };

            ws.onerror = (error) => {
                console.error('WebSocket ошибка:', error);
            };

            ws.onclose = () => {
                console.log('WebSocket соединение закрыто');
                setWebSocket(null);

                setTimeout(connect, 3000);
            };
        };

        if (isChatOpen && auth) {
            connect();
        }

        return () => {
            ws?.close();
        };
    }, [isChatOpen, auth]);

    const handleLogin = () => {
        setIsChatOpen(false);
        navigate('/user/auth');
    };

    return (
        <div>
            <div className={styles.kryglyashok}></div>

            <button onClick={toggleChat} className={styles.chatButton}>
                <ChatDots className={styles.chatIcon} />
            </button>

            {isChatOpen && (
                <div className={`${styles.chatWindow} ${!auth ? styles.blurredBackground : ''}`}>
                    {!auth && (
                        <div className={styles.authOverlay}>
                            <p className={styles.authMessage}>Войдите, чтобы воспользоваться чатом</p>
                            <ExButton
                                type="success"
                                onClick={handleLogin}
                                className={styles.authButton}
                            >
                                Войти
                            </ExButton>
                        </div>
                    )}
                    {auth && (
                        <>
                            <div className={styles.chatMessages}>
                                {messages.map((message, index) => (
                                    <div
                                        key={index}
                                        className={`${styles.message} ${
                                            message.isUser ? styles.userMessage : styles.serverMessage
                                        }`}
                                    >
                                        {message.text}
                                    </div>
                                ))}
                            </div>
                            <div className={styles.chatInputContainer}>
                                <ChatInput
                                    value={inputValue}
                                    onChange={setInputValue}
                                    onSend={handleSendMessage}
                                />
                            </div>
                        </>
                    )}
                </div>
            )}
        </div>
    );
};

export default ChatWidget;