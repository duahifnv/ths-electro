package org.envelope.helperservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.envelope.helperservice.dto.MessageDto;
import org.envelope.helperservice.dto.SocketDialog;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketService {
    @Getter
    private final Map<Long, SocketDialog> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BEARER_PREFIX = "Bearer:";

    public void sendMessageToUser(Long userId, String message) throws IOException {
        SocketDialog wrapper = sessions.get(userId);
        if (wrapper != null && wrapper.getSession().isOpen()) {
            WebSocketSession session = wrapper.getSession();
            session.sendMessage(new TextMessage(message));
            System.out.println("Message sent to userId: " + wrapper.getUserId() + " message: " + message);
        } else {
            System.out.println("Session not found or closed for userId: " + userId);
        }
    }

    public Long getUserIdFromSession(WebSocketSession session) throws Exception {
        Map<String, String> queryParams = extractQueryParams(session);
        String jwt = extractJwtToken(queryParams);
        System.out.println("Токен пользователя: " + jwt);
        return 1L;
    }

    public void addDialog(Long userId, SocketDialog dialog) {
        sessions.put(userId, dialog);
    }

    public SocketDialog deleteDialog(Long userId) {
        return sessions.remove(userId);
    }

    private String extractJwtToken(Map<String, String> queryParams) throws Exception {
        String authHeader = queryParams.get("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new Exception("Невалидный токен");
        }
        return authHeader.substring(BEARER_PREFIX.length());
    }

    private Map<String, String> extractQueryParams(WebSocketSession session) {
        // Получаем полный URI соединения
        URI uri = session.getUri();
        if (uri == null || uri.getQuery() == null) {
            return new HashMap<>(); // Возвращаем пустую карту, если нет параметров
        }
        // Разбираем строку запроса
        String query = uri.getQuery(); // token=12345&userId=67890
        String[] pairs = query.split("&");
        Map<String, String> queryParams = new HashMap<>();

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                queryParams.put(keyValue[0], keyValue[1]);
            }
        }
        return queryParams;
    }

    public String parseJsonMessage(TextMessage message) throws Exception {
        // Получаем JSON-сообщение от клиента
        String payload = message.getPayload();
        System.out.println("Received raw JSON: " + payload);
        // Десериализуем JSON в объект WebSocketMessage
        MessageDto userMessage = objectMapper.readValue(payload, MessageDto.class);
        System.out.println("Parsed message: " + userMessage);
        return userMessage.message();
    }
}
