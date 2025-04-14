package org.envelope.helperservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.service.ChatService;
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
        String role = (String) accessor.getSessionAttributes().get("role");
        if (role != null && role.equals("helper")) {
            chatService.resendUnreadMessagesToHelper(sessionId);
        }

        String destination = accessor.getDestination();
        if (destination != null && destination.startsWith("/user/")) {
            log.info("Клиент {} подписался на приватную очередь: {}", sessionId, destination);
        }
        else log.info("Клиент {} подписался на: {}", sessionId, destination);
    }
    @EventListener
    public void handleSessionUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        log.info("Клиент {} отписался от очереди или топика", sessionId);
    }
}
