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

        const newMessage = { text: inputValue, isUser: true };
        setMessages((prevMessages) => [...prevMessages, newMessage]);
        setInputValue('');

        if (!webSocket) {
            try {
                const response = await fetch('http://localhost:3001/chat/start', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ message: inputValue }),
                });

                if (response.ok) {
                    console.log('Первое сообщение отправлено через POST');
                } else {
                    console.error('Ошибка при отправке первого сообщения');
                }
            } catch (error) {
                console.error('Ошибка сети:', error);
            }
        }

        if (webSocket && webSocket.readyState === WebSocket.OPEN) {
            webSocket.send(inputValue);
        } else {
            console.warn('WebSocket соединение еще не установлено');
        }
    };

    useEffect(() => {
        let ws;
        const connect = () => {
            ws = new WebSocket('ws://localhost:3001');

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