package org.envelope.helperservice.service;

import lombok.RequiredArgsConstructor;
import org.envelope.helperservice.repository.UserRequestRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramService {
    private final UserRequestRepository userRequestRepository;
    public Long getQueueSize() {
        return userRequestRepository.count();
    }
}
