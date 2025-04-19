package org.envelope.helperservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.Role;
import org.envelope.helperservice.exception.ClientException;
import org.envelope.helperservice.service.ChatService;
import org.envelope.helperservice.service.SessionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j(topic = "Чат")
public class ChatController {
    private final ChatService chatService;
    private final SessionService sessionService;

    @MessageMapping("/chat")
    public void sendMessageToChat(Message<String> message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();
        Role role = sessionService.getSessionAttribute("role", accessor, Role.class);
        String payload = message.getPayload().trim();
        chatService.sendMessageToPrivateChat(payload, sessionId, role);
    }
    @MessageMapping("/waiting.size")
    public void getWaitingUsersCount(SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        int waitingCount = sessionService.getWaitingUsersCount();
        String jsonMessage = chatService.getWaitingCountJson(waitingCount);
        chatService.sendMessageToUserQueue(jsonMessage, "/queue/dialogs", sessionId);
    }
    @MessageExceptionHandler
    public void handleException(ClientException exception, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        chatService.sendMessageToUserQueue(exception.getMessage(), "/queue/errors", sessionId);
    }
}
