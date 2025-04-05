package org.envelope.helperservice.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.envelope.helperservice.dto.SocketDialog;
import org.envelope.helperservice.entity.WaitingUserRequest;
import org.envelope.helperservice.repository.UserRequestRepository;
import org.envelope.helperservice.service.UserRequestService;
import org.envelope.helperservice.service.WebSocketService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
    @Getter
    private final WebSocketService webSocketService;
    private final UserRequestService userRequestService;
    // Конфиг для контроллера
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId;
        try {
            userId = webSocketService.getUserIdFromSession(session);
        } catch (Exception e) {
            session.sendMessage(new TextMessage("Invalid JWT"));
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }
        var socketDialog = SocketDialog.builder()
                .session(session)
                .userId(userId)
                .helperId("588116881")
                .build();

        WaitingUserRequest request = new WaitingUserRequest();
        request.setUserId(userId);
        request.setMessage("init message");
        userRequestService.save(request);

        webSocketService.addDialog(userId, socketDialog);
        System.out.println("New connection with userId: " + userId);
    }

    // Конфиг для контроллера
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (status == CloseStatus.NOT_ACCEPTABLE) {
            System.out.println("Connection closed for unauthorized user");
            return;
        }
        Long userId = webSocketService.getUserIdFromSession(session);
        SocketDialog dialog = webSocketService.deleteDialog(userId);
        userRequestService.deleteByUserId(userId);
        if (dialog != null) {
            System.out.println("Connection closed for userId: " + dialog.getUserId());
        } else {
            System.out.println("Connection closed for unknown userId: " + userId);
        }
    }

    // Контроллер от фронта к беку
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String parsedMessage = webSocketService.parseJsonMessage(message);
            String response = "Server received: " + parsedMessage;
            session.sendMessage(new TextMessage(response));
        } catch (Exception e) {
            // Обработка ошибок парсинга JSON
            System.err.println("Error parsing JSON: " + e.getMessage());
            session.sendMessage(new TextMessage("Invalid JSON format"));
        }
    }
}