package org.envelope.helperservice.service;

import org.springframework.stereotype.Service;

@Service
public class TelegramService {
    public Integer getQueueSize() {
        return 10;
    }
}
