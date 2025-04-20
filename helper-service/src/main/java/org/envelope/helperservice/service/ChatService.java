package org.envelope.helperservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.dto.Role;
import org.envelope.helperservice.exception.ClientException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    public void sendMessageToUserQueue(String message, String queueName, String sessionId) {
        if (sessionId == null) {
            log.error("Session id не найден, невозможно отправить сообщение в пользовательскую очередь");
            return;
        }
        String queuePath = String.format("%s-user%s", queueName, sessionId);
        messagingTemplate.convertAndSend(queuePath, message);
        log.info("Сообщение '{}' отправлено в очередь {}", message, queuePath);
    }
    public void sendMessageToPrivateChat(String message, String senderId, @NonNull Role role) {
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
                    rabbitService.sendToBuffer(message, senderId);
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
        sendMessageToUserQueue(message, "/queue/private", receiverId);
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
            sendMessageToPrivateChat(userMessage, userId, Role.USER);
        }
    }
    public String getWaitingCountJson(Integer waitingCount) {
        return getJson(Map.of("size", String.valueOf(waitingCount)));
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
