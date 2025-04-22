package org.envelope.helperservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.exception.ClientException;
import org.envelope.helperservice.service.ChatService;
import org.envelope.helperservice.service.SessionService;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j(topic = "Чат")
public class ChatController {
    private final ChatService chatService;
    private final SessionService sessionService;

    @MessageMapping("/chat")
    public void sendMessageToChat(Message<String> message) {
        chatService.sendMessageToPrivateChat(message);
    }
    @MessageMapping("/waiting.size")
    public void getWaitingUsersCount(StompHeaderAccessor accessor) {
        String receiver = sessionService.getSessionAttribute("username", accessor, String.class);
        int waitingCount = sessionService.getWaitingUsersCount();
        String json = chatService.getJson(
                Map.of("size", String.valueOf(waitingCount), "receiver", receiver)
        );
        String sessionId = accessor.getSessionId();
        chatService.sendJsonToUserQueue(json, "/queue/dialogs", sessionId);
    }
    @MessageExceptionHandler
    public void handleException(ClientException exception, StompHeaderAccessor accessor) {
        String receiver = sessionService.getSessionAttribute("username", accessor, String.class);
        String json = chatService.getJson(
                Map.of("error", exception.getMessage(), "receiver", receiver)
        );
        String sessionId = accessor.getSessionId();
        chatService.sendJsonToUserQueue(json, "/queue/errors", sessionId);
    }
}
