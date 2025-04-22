package org.envelope.helperservice.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.dto.Role;
import org.envelope.helperservice.event.DialogEndedEvent;
import org.envelope.helperservice.exception.ClientException;
import org.springframework.context.ApplicationEventPublisher;
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
    private final DialogMap dialogs;
    private final Map<String, String> usersPrivateSubscriptions;
    private final Map<String, String> helpersPrivateSubscriptions;
    private final RabbitService rabbitService;
    private final ApplicationEventPublisher eventPublisher;

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
        if (!dialogs.containsKey(sessionId)) {
            dialogs.put(sessionId, null);
        }
    }

    private void startDialogWithUser(String helperId) {
        if (dialogs.containsValue(helperId)) {
            throw new ClientException("Невозможно начать диалог: Помощник уже ожидает сообщения");
        }
        String waitingUserId = dialogs.getSessionIdByValue(null);
        if (waitingUserId == null) {
            throw new ClientException("Невозможно начать диалог: Отсутствуют ожидающие пользователи");
        }
        dialogs.put(waitingUserId, helperId);
        log.info("Начат диалог [пользователь {}] - [помощник {}]", waitingUserId, helperId);
    }

    private void stopDialog(String initiatorId, @NonNull Role role) {
        switch (role) {
            case USER -> {
                if (!dialogs.containsKey(initiatorId)) {
                    return;
                }
                rabbitService.deleteQueue(initiatorId);
                String companionId = dialogs.get(initiatorId);
                if (companionId != null) {
                    var dialogEndedEvent = new DialogEndedEvent(this, initiatorId, companionId);
                    eventPublisher.publishEvent(dialogEndedEvent);
                }
                dialogs.remove(initiatorId);
            }
            case HELPER -> {
                String companionId = dialogs.getSessionIdByValue(initiatorId);
                if (companionId != null) {
                    var dialogEndedEvent = new DialogEndedEvent(this, initiatorId, companionId);
                    eventPublisher.publishEvent(dialogEndedEvent);
                    dialogs.put(companionId, null);
                }
            }
        }
    }

    public void subscribeToPrivate(String sessionId, Role role, String subscriptionId)
            throws RuntimeException {
        switch (role) {
            case USER -> usersPrivateSubscriptions.put(sessionId, subscriptionId);
            case HELPER -> {
                startDialogWithUser(sessionId);
                helpersPrivateSubscriptions.put(sessionId, subscriptionId);
            }
        }
    }

    public void unsubscribeFromPrivate(String sessionId, Role role) {
        stopDialog(sessionId, role);
        switch (role) {
            case USER -> usersPrivateSubscriptions.remove(sessionId);
            case HELPER -> helpersPrivateSubscriptions.remove(sessionId);
        }
    }
    public int getWaitingUsersCount() {
        return dialogs.getWaitingUsersCount();
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
    @SuppressWarnings("unchecked")
    public <T> T getSessionAttribute(String attributeName, String sessionId, Class<T> type)
            throws RuntimeException {
        WebSocketSession session = sessions.get(sessionId);
        return (T) session.getAttributes().get(attributeName);
    }
}
