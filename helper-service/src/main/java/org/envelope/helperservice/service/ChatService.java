package org.envelope.helperservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.dto.Role;
import org.envelope.helperservice.exception.ClientException;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Сервис сообщений")
public class ChatService {
    private final SimpMessagingTemplate messagingTemplate;
    private final RabbitService rabbitService;
    private final SessionService sessionService;
    private final DialogMap dialogMap;

    public void sendMessageToTopic(String message, String topicName) {
        messagingTemplate.convertAndSend(topicName, message);
    }
    public void sendJsonToUserQueue(String json, String queueName, String sessionId) {
        if (sessionId == null) {
            log.error("Session id не найден, невозможно отправить сообщение в пользовательскую очередь");
            return;
        }
        String queuePath = String.format("%s-user%s", queueName, sessionId);
        messagingTemplate.convertAndSend(queuePath, json);
        log.info("Сообщение '{}' отправлено в очередь {}", json, queuePath);
    }
    public void sendMessageToPrivateChat(Message<String> message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String payload = message.getPayload().trim();
        String senderId = accessor.getSessionId();
        Role role = sessionService.getSessionAttribute("role", accessor, Role.class);

        String receiverId;
        switch (role) {
            case USER -> {
                var dialogSessions = dialogMap.getDialogSessions();
                receiverId = dialogSessions.get(senderId);
                if (receiverId == null) {
                    if (!dialogSessions.containsKey(senderId)) {
                        log.info("Пользователь {} не был подписан на приватную очередь, подписываем", senderId);
                        sessionService.addUserToDialogs(senderId);
                    }
                    rabbitService.sendToBuffer(payload, senderId);
                    return;
                }
            }
            case HELPER -> {
                receiverId = dialogMap.getSessionIdByValue(senderId);
                if (receiverId == null) {
                    log.warn("Не найден помощник с id: {}", senderId);
                    throw new ClientException("Для отправки сообщений необходимо начать диалог с пользователем");
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + role);
        }
        String senderUsername = sessionService.getSessionAttribute("username", senderId, String.class);
        String receiverUsername = sessionService.getSessionAttribute("username", receiverId, String.class);
        String json = getJson(
                Map.of("message", payload, "sender", senderUsername, "receiver", receiverUsername)
        );
        sendJsonToUserQueue(json, "/queue/private", receiverId);
    }
    public List<String> receiveAllMessages(String senderId) {
        List<String> messages = new ArrayList<>();
        while (true) {
            String message = rabbitService.receiveFromBuffer(senderId);
            if (message == null) break;
            messages.add(message);
        }
        if (!messages.isEmpty()) {
            log.info("От отправителя {} обработаны новые сообщения: {}", senderId, Arrays.stream(messages.toArray()).toArray());
        }
        return messages;
    }
    public void resendUnreadMessagesToHelper(String helperId) {
        var requestedDialog = dialogMap.getDialogWithHelper(helperId);
        String userId = requestedDialog.getKey();
        List<String> userMessages = receiveAllMessages(userId);

        for (String userMessage : userMessages) {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(StompCommand.MESSAGE);
            headerAccessor.setSessionId(userId);
            headerAccessor.setSessionAttributes(Map.of("role", Role.USER));
            Message<String> message = MessageBuilder.createMessage(userMessage,
                    headerAccessor.getMessageHeaders());
            sendMessageToPrivateChat(message);
        }
    }
    public String getJson(Map<String, String> properties) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(properties);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
