package org.envelope.helperservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.service.ChatService;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j(topic = "Чат")
public class ChatController {
    private final ChatService chatService;
    @MessageMapping("/chat")
    public void sendMessage(Message<String> message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();
        String role = (String) accessor.getSessionAttributes().get("role");
        if (role == null) {
            log.error("Не найдено поле role у пользователя");
            return;
        }
        String payload = message.getPayload().trim();
        chatService.sendMessage(payload, sessionId, role);
    }
}
