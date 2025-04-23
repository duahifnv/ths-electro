import React, { useState, useEffect } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { useAuth } from '../../../react-envelope/hooks/useAuth';
import { ChatDots } from '../../dummies/Icons.jsx';
import ChatInput from '../../widgets/ChatInput/ChatInput';
import ExButton from '../../../react-envelope/components/ui/buttons/ExButton/ExButton';
import { useNavigate } from 'react-router-dom';

import styles from './ChatWidget.module.css';

const PRIVATE_CHAT_SUB_ID = 'sub-4'
const DIALOG_END_QUEUE_SUB_ID = 'sub-5'

const ChatWidget = () => {
    const { auth } = useAuth();
    const [isChatOpen, setIsChatOpen] = useState(false);
    const [messages, setMessages] = useState([]);
    const [inputValue, setInputValue] = useState('');
    const [stompClient, setStompClient] = useState(null);
    const [showDialogEndModal, setShowDialogEndModal] = useState(false);
    const [isDialogEnded, setIsDialogEnded] = useState(false);
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
            webSocketFactory: () =>
                new SockJS(`http://localhost:8082/api/helper/ws?token=${encodeURIComponent(auth.token)}&username=${encodeURIComponent(auth.login)}`),
            onConnect: () => {
                console.log('STOMP соединение установлено');

                // Подписываемся на приватную очередь
                client.subscribe('/user/queue/private', (message) => {
                    let jsonMessage = JSON.parse(message.body);
                    const serverMessage = {
                        text: jsonMessage.message,
                        isUser: false,
                        isEnded: isDialogEnded
                    };
                    setMessages((prevMessages) => [...prevMessages, serverMessage]);
                }, { id: PRIVATE_CHAT_SUB_ID } );

                client.subscribe('/user/queue/dialog.end', () => {
                    setShowDialogEndModal(true);
                }, { id: DIALOG_END_QUEUE_SUB_ID });

                setStompClient(client);
            },
            onStompError: (frame) => {
                console.error('Ошибка STOMP:', frame);
                setStompClient(null);
            },
            onDisconnect: () => {
                console.log('STOMP соединение закрыто');
                setStompClient(null);
            },
            reconnectDelay: 30000 // 30s
        });

        client.activate();
    };

    const handleDialogEndResponse = (isResolved) => {
        setShowDialogEndModal(false);

        if (isResolved) {
            setMessages(prevMessages =>
                prevMessages.map(msg => ({ ...msg, isEnded: true }))
            );
            setIsDialogEnded(true);
            if (stompClient) {
                stompClient.unsubscribe(PRIVATE_CHAT_SUB_ID);
                stompClient.unsubscribe(DIALOG_END_QUEUE_SUB_ID);
            }
        }
    };

    const disconnectWebSocket = () => {
        if (stompClient && stompClient.connected) {
            stompClient.deactivate();
        }
    };

    const handleSendMessage = () => {
        if (!inputValue.trim()) return;

        if (!stompClient || !stompClient.connected) {
            connectWebSocket();
        }

        if (stompClient && stompClient.connected) {
            const newMessage = {
                text: inputValue,
                isUser: true,
                isEnded: false
            };
            setMessages((prevMessages) => [...prevMessages, newMessage]);
            setInputValue('');

            stompClient.publish({
                destination: '/app/chat',
                body: inputValue,
            });
            if (isDialogEnded) setIsDialogEnded(false);
        } else {
            console.warn('STOMP соединение еще не установлено');
        }
    };

    const handleLogin = () => {
        navigate('/user/auth');
    };

    useEffect(() => {
        if (auth?.token) {
            connectWebSocket();
        }

        return () => {
            disconnectWebSocket();
        };
    }, [auth]);

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
                                        } ${message.isEnded ? styles.endedMessage : ''}`}
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
                                    disabled={isDialogEnded}
                                />
                            </div>

                            {/* Модальное окно завершения диалога */}
                            {showDialogEndModal && (
                                <div className={styles.dialogEndModal}>
                                    <div className={styles.dialogEndContent}>
                                        <p>Ваш вопрос был решен администратором?</p>
                                        <div className={styles.dialogEndButtons}>
                                            <ExButton
                                                type="success"
                                                onClick={() => handleDialogEndResponse(true)}
                                                className={styles.dialogEndButton}
                                            >
                                                Да
                                            </ExButton>
                                            <ExButton
                                                type="danger"
                                                onClick={() => handleDialogEndResponse(false)}
                                                className={styles.dialogEndButton}
                                            >
                                                Нет
                                            </ExButton>
                                        </div>
                                    </div>
                                </div>
                            )}
                        </>
                    )}
                </div>
            )}
        </>
    );
};

export default ChatWidget;