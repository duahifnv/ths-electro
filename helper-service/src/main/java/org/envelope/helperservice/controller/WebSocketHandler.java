package org.envelope.helperservice.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.envelope.helperservice.dto.SocketDialog;
import org.envelope.helperservice.entity.WaitingUserRequest;
import org.envelope.helperservice.exception.ResourceNotFoundException;
import org.envelope.helperservice.service.MessageService;
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
        String initMessage;
        try {
            var queryParams = webSocketService.getQueryParams(session);
            userId = webSocketService.getUserIdFromQueryParams(queryParams);
            initMessage = webSocketService.getInitMessageFromQueryParams(queryParams);
        } catch (Exception e) {
            session.sendMessage(new TextMessage("Authentication failed"));
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }
        var socketDialog = SocketDialog.builder()
                .session(session)
                .userId(userId)
                .build();

        WaitingUserRequest request = new WaitingUserRequest();
        request.setUserId(userId);
        request.setMessage("Вопрос от пользователя #" + userId + ": " + initMessage);
        userRequestService.save(request);

        webSocketService.addDialog(userId, socketDialog);
        System.out.println("New connection with userId: " + userId);

        session.sendMessage(new TextMessage("Вопрос принят, ожидайте"));
    }

    // Конфиг для контроллера
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        if (status == CloseStatus.NOT_ACCEPTABLE) {
            System.out.println("Connection closed for unauthorized user");
            return;
        }
        if (status == CloseStatus.GOING_AWAY) {
            System.out.println("Connection close from helper");
            session.sendMessage(new TextMessage("Диалог окончен"));
        }
        try {
            Long userId = webSocketService.getUserIdFromSession(session);
            SocketDialog dialog = webSocketService.deleteDialog(userId);
            dialog.setHelperId(null);

            userRequestService.deleteByUserId(userId);
            if (dialog != null) {
                System.out.println("Connection closed for userId: " + dialog.getUserId());
            } else {
                System.out.println("Connection closed for unknown userId");
            }
        } catch (Exception e) {
            System.out.println("Connection close due to server exception");
        }
    }

    // Контроллер от фронта к беку
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String parsedMessage = webSocketService.parseJsonMessage(message);
            Long userId = webSocketService.getUserIdFromSession(session);
            String helperId = webSocketService.getSessions().get(userId).getHelperId();
            if (helperId == null) {
                throw new IllegalStateException();
            }
            webSocketService.sendMessageFromUser(userId, helperId, parsedMessage);
            System.out.println("Server received: " + parsedMessage);
            session.sendMessage(new TextMessage("Сообщение доставлено и ждет ответа помощника"));
        } catch (IllegalStateException e) {
            System.err.println("Illegal state exception: " + "Отправка сообщений только после ответа помощника");
            session.sendMessage(new TextMessage("Отправка сообщений только после ответа помощника"));
        }
        catch (ResourceNotFoundException e) {
            System.err.println("Resource not found: " + e.getMessage());
            session.sendMessage(new TextMessage("Requested resource not found"));
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
            session.sendMessage(new TextMessage("Invalid JSON format"));
        }
    }
}