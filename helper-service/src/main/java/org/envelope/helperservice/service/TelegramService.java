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
    public MessageDto pickWaitingUser(String tgId) {
        WaitingUserRequest request = requestRepository.findFirstByOrderByTimestampDesc()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Отсуствуют ждущие пользователи"));
        try {
            SocketDialog socketDialog = webSocketService.getSessions().get(request.getUserId());
            socketDialog.setHelperId(tgId);

            requestRepository.delete(request);

            return MessageDto.builder()
                    .message(request.getMessage())
                    .build();
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }
}
