import React, { useState, useEffect } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useAuth } from '../../../react-envelope/hooks/useAuth';
import { ChatDots } from '../../dummies/Icons.jsx';
import ChatInput from '../../widgets/ChatInput/ChatInput';
import ExButton from '../../../react-envelope/components/ui/buttons/ExButton/ExButton';
import { useNavigate } from 'react-router-dom';

import styles from './ChatWidget.module.css';

const ChatWidget = () => {
    const { auth } = useAuth();
    const [isChatOpen, setIsChatOpen] = useState(false);
    const [messages, setMessages] = useState([]);
    const [inputValue, setInputValue] = useState('');
    const [stompClient, setStompClient] = useState(null);
    const navigate = useNavigate();

    const toggleChat = () => {
        setIsChatOpen((prev) => !prev);
    };

    const connectWebSocket = () => {
        if (!auth || !auth.token) {
            console.error('Токен отсутствует, невозможно подключиться к WebSocket');
            return;
        }

        const client = new Client({
            webSocketFactory: () => new SockJS(`http://localhost:8082/api/helper/ws?token=${encodeURIComponent(auth.token)}`),
            onConnect: () => {
                console.log('STOMP соединение установлено');

                // Подписываемся на приватную очередь
                client.subscribe('/user/queue/private', (message) => {
                    const serverMessage = { text: message.body, isUser: false };
                    setMessages((prevMessages) => [...prevMessages, serverMessage]);
                });

                // Сохраняем STOMP клиент
                setStompClient(client);
            },
            onStompError: (frame) => {
                console.error('Ошибка STOMP:', frame);
            },
            onDisconnect: () => {
                console.log('STOMP соединение закрыто');
                setStompClient(null);
            },
        });

        client.activate();
    };

    const disconnectWebSocket = () => {
        if (stompClient && stompClient.connected) {
            stompClient.deactivate();
        }
    };

    const handleSendMessage = () => {
        if (!inputValue.trim()) return;

        // Если STOMP клиент не подключен, создаем соединение
        if (!stompClient || !stompClient.connected) {
            connectWebSocket();
        }

        // Отправляем сообщение через STOMP
        if (stompClient && stompClient.connected) {
            const newMessage = { text: inputValue, isUser: true };
            setMessages((prevMessages) => [...prevMessages, newMessage]);
            setInputValue('');

            stompClient.publish({
                destination: '/app/chat',
                body: inputValue, // Тело сообщения
            });
        } else {
            console.warn('STOMP соединение еще не установлено');
        }
    };

    const handleLogin = () => {
        navigate('/user/auth');
    };

    useEffect(() => {
        // Подключаемся к WebSocket при открытии чата
        if (isChatOpen && auth?.token) {
            connectWebSocket();
        }

        // Отключаемся при закрытии чата или размонтировании компонента
        return () => {
            disconnectWebSocket();
        };
    }, [isChatOpen, auth]);

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