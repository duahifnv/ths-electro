package org.envelope.helperservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.envelope.helperservice.entity.Helper;
import org.envelope.helperservice.exception.IllegalClientException;
import org.envelope.helperservice.exception.ResourceNotFoundException;
import org.envelope.helperservice.service.HelperService;
import org.envelope.helperservice.service.TelegramService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class TelegramController {
    private final TelegramService telegramService;
    private final HelperService helperService;
    @Value("${telegram.secret-key}")
    private String secretKey;
    @Operation(summary = "Получить текущий размер очереди пользователей")
    @PostMapping("/queue")
    @ResponseStatus(HttpStatus.OK)
    public Long getQueueSize(@RequestParam @Schema(description = "ID помощника в телеграмме",
                                         example = "993439102")
                                 String tgId,
                             @RequestParam @Schema(description = "Секретный ключ доступа к сервису",
                                     example = "g6s75rjhWc6cWxsYf7KSPdl0rO6Rc0RQ")
                             String secretKey) {
        try {
            Helper helper = helperService.findByTgId(tgId);
            if (!secretKey.equals(this.secretKey)) {
                throw new IllegalClientException();
            }
            return telegramService.getQueueSize();
        } catch (ResourceNotFoundException e) {
            throw new IllegalClientException();
        }
    }
}
