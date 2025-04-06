package org.envelope.helperservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.envelope.helperservice.dto.MessageDto;
import org.envelope.helperservice.dto.SocketDialog;
import org.envelope.helperservice.dto.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    @Getter
    private final Map<Long, SocketDialog> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BEARER_PREFIX = "Bearer:";
    private final RestTemplate restTemplate;
    private final MessageService messageService;
    @Value("${client.identity-service.url}")
    private String identityServiceUrl;
    @Value("${client.identity-service.context-path}")
    private String identityServiceContextPath;

    public void sendMessageToUser(Long userId, String tgId, String message) throws IOException {
        SocketDialog wrapper = sessions.get(userId);
        if (wrapper != null && wrapper.getSession().isOpen()) {
            WebSocketSession session = wrapper.getSession();
            session.sendMessage(new TextMessage("Ответ от администратора: " + message));
            System.out.println("Message sent to userId: " + wrapper.getUserId() + " message: " + message);
            messageService.addMessage(message, "user", userId, tgId);
        } else {
            System.out.println("Session not found or closed for userId: " + userId);
        }
    }

    public void sendMessageFromUser(Long userId, String tgId, String message) {
        messageService.addMessage(message, "helper", userId, tgId);
    }

    public Map<String, String> getQueryParams(WebSocketSession session) throws Exception {
        return extractQueryParams(session);

    }
    public Long getUserIdFromQueryParams(Map<String, String> queryParams) throws Exception {
        String jwt = extractJwtToken(queryParams);
        System.out.println("Токен пользователя: " + jwt);
        UserResponse userResponse = exchangeUserResponse(jwt);
        return userResponse.id();
    }

    public Long getUserIdFromSession(WebSocketSession session) throws Exception {
        Map<String, String> queryParams = getQueryParams(session);
        return getUserIdFromQueryParams(queryParams);
    }

    public String getInitMessageFromQueryParams(Map<String, String> queryParams) throws Exception {
        return Optional.ofNullable(queryParams.get("initMessage"))
                .orElseThrow(Exception::new);
    }

    public void addDialog(Long userId, SocketDialog dialog) {
        sessions.put(userId, dialog);
    }

    public SocketDialog deleteDialog(Long userId) {
        return sessions.remove(userId);
    }

    public boolean existsByHelperId(String helperId) {
        return sessions.values().stream()
                .anyMatch(socketDialog -> helperId.equals(socketDialog.getHelperId()));
    }

    public Optional<Long> findUserIdByHelperId(String helperId) {
        if (sessions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Отсутствуют ожидающие пользователи");
        }
        return sessions.values().stream()
                .filter(dialog -> helperId.equals(dialog.getHelperId()))
                .map(SocketDialog::getUserId)
                .findFirst();
    }

    private UserResponse exchangeUserResponse(String token) throws Exception {
        try {
            String url = identityServiceUrl + identityServiceContextPath + "/users/me";
            // Настройка заголовков
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            // Создание HTTP-запроса
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            // Выполнение запроса
            ResponseEntity<UserResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    UserResponse.class
            );
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new Exception();
        }
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
