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

        // Если WebSocket еще не создан, создаем его
        if (!webSocket || webSocket.readyState !== WebSocket.OPEN) {
            if (!auth || !auth.token) {
                console.error('Токен отсутствует, невозможно подключиться к WebSocket');
                return;
            }

            const initMessage = inputValue; // Начальное сообщение
            const wsUrl = `ws://localhost:3001/api/helper/ws?Authorization=Bearer:${encodeURIComponent(auth.token)}&initMessage=${encodeURIComponent(initMessage)}`;

            const ws = new WebSocket(wsUrl);

            ws.onopen = () => {
                console.log('WebSocket соединение установлено');

                // Сохраняем WebSocket
                setWebSocket(ws);

                // Отправляем первое сообщение (уже передано в URL)
                const firstMessage = { text: initMessage, isUser: true };
                setMessages((prevMessages) => [...prevMessages, firstMessage]);
                setInputValue('');
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
            };
        } else {
            // Если WebSocket уже открыт, отправляем сообщение через него
            const newMessage = { text: inputValue, isUser: true };
            setMessages((prevMessages) => [...prevMessages, newMessage]);
            setInputValue('');

            webSocket.send(inputValue);
        }
    };

    const handleLogin = () => {
        navigate('/user/auth');
    };

    return (
        <>
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
        </>
    );
};

export default ChatWidget;