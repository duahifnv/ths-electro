package org.envelope.helperservice.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.Role;
import org.envelope.helperservice.exception.ClientException;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Сервис сессий")
public class SessionService {
    private final Map<String, WebSocketSession> sessions;
    private final DialogMap dialogMap;
    private final Map<String, String> usersPrivateSubscriptions;
    private final Map<String, String> helpersPrivateSubscriptions;
    private final RabbitService rabbitService;

    public void addClientSession(String sessionId, WebSocketSession session) {
        synchronized (sessions) {
            if (!sessions.containsKey(sessionId) && session.isOpen()) {
                sessions.put(sessionId, session);
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

    public void addUserToDialogs(String sessionId) {
        if (!dialogMap.containsKey(sessionId)) {
            dialogMap.put(sessionId, null);
        }
    }

    private void startPrivateSession(String sessionId) throws RuntimeException {
        if (dialogMap.containsValue(sessionId)) {
            throw new ClientException("Ошибка подписки: Помощник уже ожидает сообщения");
        }
        String waitingUserId = dialogMap.getSessionIdByValue(null);
        if (waitingUserId == null) {
            throw new ClientException("Ошибка подписки: Отсутствуют ожидающие пользователи");
        }
        dialogMap.put(waitingUserId, sessionId);
        log.info("Начат диалог [пользователь {}] - [помощник {}]", waitingUserId, sessionId);
    }

    private void stopPrivateSession(String sessionId, @NonNull Role role) {
        switch (role) {
            case USER -> {
                if (!dialogMap.containsKey(sessionId)) {
                    return;
                }
                rabbitService.deleteQueue(sessionId);
                String helperId = dialogMap.get(sessionId);
                if (helperId != null) {
                    removeClientSession(helperId);
                }
                dialogMap.remove(sessionId);
            }
            case HELPER -> {
                String userId = dialogMap.getSessionIdByValue(sessionId);
                if (userId != null) {
                    dialogMap.put(userId, null);
                }
            }
        }
    }

    public void subscribeToPrivate(String sessionId, Role role, String subscriptionId)
            throws RuntimeException {
        switch (role) {
            case USER -> usersPrivateSubscriptions.put(sessionId, subscriptionId);
            case HELPER -> {
                startPrivateSession(sessionId);
                helpersPrivateSubscriptions.put(sessionId, subscriptionId);
            }
        }
    }

    public void unsubscribeFromPrivate(String sessionId, Role role) {
        stopPrivateSession(sessionId, role);
        switch (role) {
            case USER -> {
                usersPrivateSubscriptions.remove(sessionId);
            }
            case HELPER -> {
                helpersPrivateSubscriptions.remove(sessionId);
            }
        }
    }
    public int getWaitingUsersCount() {
        return dialogMap.getWaitingUsersCount();
    }
    public String getPrivateSubscriptionId(String sessionId, Role role) {
        return switch (role) {
            case USER -> usersPrivateSubscriptions.get(sessionId);
            case HELPER -> helpersPrivateSubscriptions.get(sessionId);
        };
    }
    @SuppressWarnings("unchecked")
    public <T> T getSessionAttribute(String attributeName, StompHeaderAccessor accessor, Class<T> type)
            throws RuntimeException {
        return (T) accessor.getSessionAttributes().get(attributeName);
    }
}
