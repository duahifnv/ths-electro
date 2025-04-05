import { useState } from 'react';
import styles from './ChatWidget.module.css';
import { useAuth } from '../../../react-envelope/hooks/useAuth';
import { ChatDots } from '../../dummies/Icons';
import ChatInput from '../../widgets/ChatInput/ChatInput';

const ChatWidget = () => {
    const { auth } = useAuth();
    const [isChatOpen, setIsChatOpen] = useState(false);
    const [messages, setMessages] = useState([]);
    const [inputValue, setInputValue] = useState('');

    const toggleChat = () => {
        setIsChatOpen((prev) => !prev);
    };

    const handleSendMessage = () => {
        if (!inputValue.trim()) return;

        const newMessage = { text: inputValue, isUser: true };
        setMessages((prevMessages) => [...prevMessages, newMessage]);
        setInputValue('');

        setTimeout(() => {
            const serverResponse = { text: 'Это автоматический ответ!', isUser: false };
            setMessages((prevMessages) => [...prevMessages, serverResponse]);
        }, 1000);
    };

    return (
        <>
            <button onClick={toggleChat} className={styles.chatButton}>
                <ChatDots className={styles.chatIcon} />
            </button>

            {isChatOpen && (
                <div className={`${styles.chatWindow} ${auth ? '' : styles.blurredChatWindow}`}>
                    {!auth && (
                        <div className={styles.unauthorizedContent}>
                            <p>Войдите чтобы воспользоваться чатом</p>
                            <button className={styles.loginButton}>Войти</button>
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