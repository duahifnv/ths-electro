package org.envelope.helperservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Сервис сессий")
public class SessionService {
    private final Map<String, WebSocketSession> sessions;
    private final Map<String, String> dialogSessions; // key: userId, value: helperId
    private final RabbitService rabbitService;

    public void addClientSession(String sessionId, WebSocketSession session) {
        synchronized (sessions) {
            if (!sessions.containsKey(sessionId) && session.isOpen()) {
                sessions.put(sessionId, session);
            }
        }
    }
    public boolean subscribeClient(String sessionId, String role) {
        if (role.equals("user")) {
            if (!dialogSessions.containsKey(sessionId)) {
                dialogSessions.put(sessionId, null);
            }
            else return false;
        }
        else if (role.equals("helper")) {
            if (dialogSessions.containsValue(sessionId)) {
                log.error("Ошибка подписки: Помощник уже ожидает сообщения");
                return false;
            }
            String waitingUserId = getSessionIdByValue(null);
            if (waitingUserId == null) {
                log.error("Ошибка подписки: Отсутствуют ожидающие пользователи");
                return false;
            }
            dialogSessions.put(waitingUserId, sessionId);
            log.info("Начат диалог [пользователь {}] - [помощник {}]", waitingUserId, sessionId);
        }
        return true;
    }
    public void unsubscribeClient(String sessionId, String role) {
        if (role.equals("user")) {
            if (!dialogSessions.containsKey(sessionId)) {
                return;
            }
            rabbitService.deleteQueue(sessionId);
            String helperId = dialogSessions.get(sessionId);
            if (helperId != null) {
                removeClientSession(helperId);
            }
            dialogSessions.remove(sessionId);
        }
        else if (role.equals("helper")) {
            String userId = getSessionIdByValue(sessionId);
            if (userId != null) {
                dialogSessions.put(userId, null);
            }
        }
    }
    public void removeClientSession(String sessionId) {
        synchronized (sessions) {
            var clientSession = sessions.get(sessionId);
            if (clientSession != null && clientSession.isOpen()) {
                try {
                    clientSession.close();
                    sessions.remove(sessionId);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public <T> String getSessionIdByValue(T value) {
        return dialogSessions.entrySet().stream()
                .filter(e -> e.getValue() == value)
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
