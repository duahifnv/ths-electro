package org.envelope.helperservice.controller;

import lombok.RequiredArgsConstructor;
import org.envelope.helperservice.entity.Helper;
import org.envelope.helperservice.exception.IllegalClientException;
import org.envelope.helperservice.exception.ResourceNotFoundException;
import org.envelope.helperservice.service.HelperService;
import org.envelope.helperservice.service.TelegramService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class TelegramController {
    private final TelegramService telegramService;
    private final HelperService helperService;
    @Value("${telegram.secret-key}")
    private String secretKey;
    @PostMapping("/queue")
    public Integer getQueueSize(@RequestParam String tgId, @RequestParam String secretKey) {
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
