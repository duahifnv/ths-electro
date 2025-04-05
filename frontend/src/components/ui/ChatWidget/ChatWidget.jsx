import { useState } from 'react';
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
    const navigate = useNavigate();

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