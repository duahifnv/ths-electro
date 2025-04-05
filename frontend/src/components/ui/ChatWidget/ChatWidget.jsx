import { useState } from 'react';
import styles from './ChatWidget.module.css';
import { useAuth } from '../../../react-envelope/hooks/useAuth'
import {Pizza} from '../../../react-envelope/components/dummies/Icons.jsx'

const ChatWidget = ({ clientId }) => {
    const { auth } = useAuth(); // Используем хук для получения состояния авторизации
    const [isChatOpen, setIsChatOpen] = useState(false); // Состояние видимости окна чата

    // Функция для переключения видимости чата
    const toggleChat = () => {
        setIsChatOpen((prev) => !prev);
    };

    return (
        <>
            {/* Кнопка чата */}
            <button onClick={toggleChat} className={styles.chatButton}>
                <img src={Pizza} alt="Chat Icon" className={styles.chatIcon} />
            </button>

            {/* Окно чата */}
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
                            <h3>Чат</h3>
                            <div className={styles.chatMessages}>
                                {/* Здесь можно добавить логику для отображения сообщений */}
                                <p>Напишите сообщение...</p>
                            </div>
                            <input type="text" placeholder="Введите сообщение..." className={styles.chatInput} />
                        </>
                    )}
                </div>
            )}
        </>
    );
};

export default ChatWidget;