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
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Сервис сообщений")
public class ChatService {
    private final SimpMessagingTemplate messagingTemplate;
    private final RabbitService rabbitService;
    private final Map<String, String> dialogSessions;
    private final SessionService sessionService;

    public void sendMessage(String message, String senderId, @NonNull Role role) {
        String receiverId;
        switch (role) {
            case USER -> {
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
                receiverId = sessionService.getSessionIdByValue(senderId);
                if (receiverId == null) {
                    log.warn("Не найден помощник с id: {}", senderId);
                    throw new ClientException("Для отправки сообщений необходимо начать диалог с пользователем");
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + role);
        }
        sendMessage(message, receiverId);
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
        var requestedDialog = dialogSessions.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue().equals(helperId))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Не найден диалог с помощником: " + helperId));
        String userId = requestedDialog.getKey();
        List<String> userMessages = receiveAllMessages(userId);

        for (String userMessage : userMessages) {
            sendMessage(userMessage, userId, Role.USER);
        }
    }
    private void sendMessage(String message, String receiverId) {
        String queuePath = "/queue/private-user" + receiverId;
        messagingTemplate.convertAndSend(queuePath, message);
        log.info("Сообщение {} отправлено в очередь {}", message, queuePath);
    }
}
