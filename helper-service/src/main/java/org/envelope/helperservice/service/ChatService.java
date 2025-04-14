package org.envelope.helperservice.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.Role;
import org.envelope.helperservice.exception.ClientException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Сервис сообщений")
public class ChatService {
    private final SimpMessagingTemplate messagingTemplate;
    private final RabbitService rabbitService;
    private final SessionService sessionService;
    private final DialogMap dialogMap;

    public void sendMessageToTopic(String topicName, String message) {
        messagingTemplate.convertAndSend(topicName, message);
    }
    public void sendMessageToPrivate(String message, String senderId, @NonNull Role role) {
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
        sendMessageToPrivate(message, receiverId);
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
            sendMessageToPrivate(userMessage, userId, Role.USER);
        }
    }
    private void sendMessageToPrivate(String message, String receiverId) {
        String queuePath = "/queue/private-user" + receiverId;
        messagingTemplate.convertAndSend(queuePath, message);
        log.info("Сообщение {} отправлено в очередь {}", message, queuePath);
    }
}
