package org.envelope.helperservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.dto.SocketDialog;
import org.envelope.helperservice.dto.MessageDto;
import org.envelope.helperservice.entity.WaitingUserRequest;
import org.envelope.helperservice.exception.IllegalClientException;
import org.envelope.helperservice.exception.ServerException;
import org.envelope.helperservice.repository.UserRequestRepository;
import org.envelope.helperservice.service.TelegramService;
import org.envelope.helperservice.service.WebSocketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class TelegramController {
    private final TelegramService telegramService;
    private final WebSocketService webSocketService;

    @Operation(summary = "Получить текущий размер очереди пользователей")
    @PostMapping("/queue")
    @ResponseStatus(HttpStatus.OK)
    public Long getQueueSize(@RequestParam @Schema(description = "ID помощника в телеграмме",
                                         example = "588116881")
                                 String tgId,
                             @RequestParam @Schema(description = "Секретный ключ доступа к сервису",
                                     example = "g6s75rjhWc6cWxsYf7KSPdl0rO6Rc0RQ")
                             String secretKey) {
        validateHelper(tgId, secretKey);
        return telegramService.getQueueSize();
    }
    @Operation(summary = "Проверить валидность ключа")
    @PostMapping("/key")
    public ResponseEntity<?> validateSecretKey(@RequestParam String key) {
        return telegramService.isKeyValid(key) ?
                ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    @Operation(summary = "Подсоединиться к одному из ждущих пользователей")
    @PostMapping("/link")
    @ResponseStatus(HttpStatus.OK)
    public MessageDto connectToUser(@RequestParam @Schema(description = "ID помощника в телеграмме",
                                                       example = "588116881")
                                               String tgId,
                                    @RequestParam @Schema(description = "Секретный ключ доступа к сервису",
                                                   example = "g6s75rjhWc6cWxsYf7KSPdl0rO6Rc0RQ")
                                               String secretKey) {
        validateHelper(tgId, secretKey);
        return telegramService.pickWaitingUser(tgId);
    }

    @Operation
    @PostMapping("/message")
    public void sendMessageToUser(@RequestParam @Schema(description = "ID помощника в телеграмме",
                                              example = "588116881")
                                      String tgId,
                                  @RequestParam @Schema(description = "Секретный ключ доступа к сервису",
                                          example = "g6s75rjhWc6cWxsYf7KSPdl0rO6Rc0RQ")
                                      String secretKey,
                                  @RequestBody MessageDto message) {
        validateHelper(tgId, secretKey);
        Long userId = findUserIdByHelperId(webSocketService.getSessions(), tgId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Помощник не подсоединен к клиенту")
                );
        try {
            webSocketService.sendMessageToUser(userId, message.message());
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    private Optional<Long> findUserIdByHelperId(@NotNull Map<Long, SocketDialog> sessions, String helperId) {
        if (sessions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Отсутствуют ожидающие пользователи");
        }
        return sessions.values().stream()
                .filter(dialog -> helperId.equals(dialog.getHelperId()))
                .map(SocketDialog::getUserId)
                .findFirst();
    }

    private void validateHelper(String tgId, String secretKey) {
        if (!telegramService.isHelperValid(tgId, secretKey)) {
            throw new IllegalClientException();
        }
    }
}
