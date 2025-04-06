package org.envelope.helperservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.dto.MessageDto;
import org.envelope.helperservice.dto.SocketDialog;
import org.envelope.helperservice.entity.WaitingUserRequest;
import org.envelope.helperservice.exception.ServerException;
import org.envelope.helperservice.repository.UserRequestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Телеграм бот")
public class TelegramService {
    private final UserRequestRepository requestRepository;
    private final HelperService helperService;
    private final WebSocketService webSocketService;
    @Value("${telegram.secret-key}")
    private String secretKey;
    public Long getQueueSize() {
        long count = requestRepository.count();
        log.info("Запрошен размер очереди, текущий размер очереди: {}", count);
        return count;
    }
    public boolean isKeyValid(String key) {
        return secretKey.equals(key);
    }
    public boolean isHelperValid(String tgId,
                                 String secretKey) {
        return helperService.existsByTgId(tgId) && isKeyValid(secretKey);
    }
    public String pickWaitingUser(String tgId) {
        WaitingUserRequest request = requestRepository.findFirstByOrderByTimestampDesc()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Отсуствуют ждущие пользователи"));
        try {
            SocketDialog socketDialog = webSocketService.getSessions().get(request.getUserId());
            socketDialog.setHelperId(tgId);

            MessageDto messageDto = MessageDto.builder()
                    .message(request.getMessage())
                    .build();

            requestRepository.delete(request);
            return messageDto.message();
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }
    public void disconnectFromUser(Long userId) throws IOException {
        SocketDialog socketDialog = webSocketService.getSessions().get(userId);
        socketDialog.setHelperId(null);
        WebSocketSession session = socketDialog.getSession();
        if (session.isOpen()) {
            session.close(CloseStatus.GOING_AWAY);
        }
    }
}
