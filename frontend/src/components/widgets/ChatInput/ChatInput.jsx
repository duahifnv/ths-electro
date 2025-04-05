import styles from './ChatInput.module.css';
import { Send } from "../../dummies/Icons.jsx";

const ChatInput = ({ value, onChange, onSend }) => {
  return (
    <div className={styles.inputWrapper}>
      <input
        type="text"
        placeholder="Введите сообщение..."
        value={value}
        onChange={(e) => onChange(e.target.value)}
        onKeyPress={(e) => e.key === "Enter" && onSend()}
        className={styles.chatInput}
      />
      <button onClick={onSend} className={styles.sendButton}>
        <Send className={styles.sendIcon} />
      </button>
    </div>
  );
};

export default ChatInput;