package org.envelope.helperservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.Role;
import org.envelope.helperservice.event.WaitingCountEvent;
import org.envelope.helperservice.service.ChatService;
import org.envelope.helperservice.service.SessionService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler {
    private final ChatService chatService;
    private final SessionService sessionService;

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        var headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("Клиент подсоединен по веб-сокету. Session ID: {}", headerAccessor.getSessionId());
    }
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        var headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("Клиент отсоединен от веб-сокета. Session ID: {}, Команда отключения: {}",
                headerAccessor.getSessionId(), headerAccessor.getCommand());
    }
    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String destination = accessor.getDestination();

        Role role = sessionService.getSessionAttribute("role", accessor, Role.class);
        if (destination != null) {
            if (role == Role.HELPER && destination.equals("/user/queue/private")) {
                chatService.resendUnreadMessagesToHelper(sessionId);
                log.info("Клиент {} подписался на приватную очередь: /user/queue/private", sessionId);
            }
            else {
                log.info("Клиент {} подписался на: {}", sessionId, destination);
            }
        }
    }
    @EventListener
    public void handleSessionUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        log.info("Клиент {} отписался от очереди или топика", sessionId);
    }
    @EventListener
    public void handleWaitingCount(WaitingCountEvent event)  {
        String message = "Текущее количество ожидающих пользователей: %d".formatted(event.getWaitingCount());
        chatService.sendMessageToTopic("/topic/dialogs", message);
    }
}
