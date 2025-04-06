package org.envelope.helperservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.dto.ChatHistoryResponse;
import org.envelope.helperservice.dto.DurationDto;
import org.envelope.helperservice.dto.MessageDto;
import org.envelope.helperservice.entity.Message;
import org.envelope.helperservice.exception.IllegalClientException;
import org.envelope.helperservice.exception.ServerException;
import org.envelope.helperservice.service.MessageService;
import org.envelope.helperservice.service.TelegramService;
import org.envelope.helperservice.service.WebSocketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class TelegramController {
    private final TelegramService telegramService;
    private final WebSocketService webSocketService;
    private final MessageService messageService;

    @Operation(summary = "Получить список недавних сообщений пользователя")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ChatHistoryResponse getRecentChatHistory(@RequestParam @Schema(description = "ID помощника в телеграмме",
                                                            example = "588116881")
                                                    String tgId,
                                                    @RequestParam @Schema(description = "Секретный ключ доступа к сервису",
                                                            example = "g6s75rjhWc6cWxsYf7KSPdl0rO6Rc0RQ")
                                                    String secretKey,
                                                    DurationDto duration) {
        validateHelper(tgId, secretKey);
        Long userId = webSocketService.findUserIdByHelperId(tgId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Помощник не подсоединен к клиенту")
                );
        return new ChatHistoryResponse(messageService.findAllByDuration(tgId, userId, duration).stream()
                .map(Message::getMessage).toList());
    }

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
    public String connectToUser(@RequestParam @Schema(description = "ID помощника в телеграмме",
                                                       example = "588116881")
                                               String tgId,
                                    @RequestParam @Schema(description = "Секретный ключ доступа к сервису",
                                                   example = "g6s75rjhWc6cWxsYf7KSPdl0rO6Rc0RQ")
                                               String secretKey) {
        validateHelper(tgId, secretKey);
        if (webSocketService.existsByHelperId(tgId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Чат с пользователем уже начат");
        }
        return telegramService.pickWaitingUser(tgId);
    }

    @Operation(summary = "Отправить сообщение подключенному пользователю")
    @PostMapping("/message")
    @ResponseStatus(HttpStatus.OK)
    public void sendMessageToUser(@RequestParam @Schema(description = "ID помощника в телеграмме",
                                              example = "588116881")
                                      String tgId,
                                  @RequestParam @Schema(description = "Секретный ключ доступа к сервису",
                                          example = "g6s75rjhWc6cWxsYf7KSPdl0rO6Rc0RQ")
                                      String secretKey,
                                  @RequestBody MessageDto message) {
        validateHelper(tgId, secretKey);
        Long userId = webSocketService.findUserIdByHelperId(tgId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Помощник не подсоединен к клиенту")
                );
        try {
            webSocketService.sendMessageToUser(userId, tgId, message.message());
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    @Operation(summary = "Отключиться от пользователя")
    @PostMapping("/close")
    public void disconnectFromUser(@RequestParam @Schema(description = "ID помощника в телеграмме",
                                     example = "588116881")
                             String tgId,
                         @RequestParam @Schema(description = "Секретный ключ доступа к сервису",
                                 example = "g6s75rjhWc6cWxsYf7KSPdl0rO6Rc0RQ")
                             String secretKey) {
        validateHelper(tgId, secretKey);
        Long userId = webSocketService.findUserIdByHelperId(tgId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Помощник не подсоединен к клиенту")
                );
        try {
            telegramService.disconnectFromUser(userId);
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    private void validateHelper(String tgId, String secretKey) {
        if (!telegramService.isHelperValid(tgId, secretKey)) {
            throw new IllegalClientException();
        }
    }
}
