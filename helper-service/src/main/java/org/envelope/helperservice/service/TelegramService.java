package org.envelope.helperservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.repository.UserRequestRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Телеграм бот")
public class TelegramService {
    private final UserRequestRepository userRequestRepository;
    public Long getQueueSize() {
        long count = userRequestRepository.count();
        log.info("Запрошен размер очереди, текущий размер очереди: {}", count);
        return count;
    }
}
