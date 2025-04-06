package org.envelope.helperservice.service;

import lombok.RequiredArgsConstructor;
import org.envelope.helperservice.dto.DurationDto;
import org.envelope.helperservice.entity.Helper;
import org.envelope.helperservice.entity.Message;
import org.envelope.helperservice.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final HelperService helperService;
    public List<Message> findAllByDuration(String tgId, Long userId, DurationDto durationDto) {
        long currentTimeMillis = System.currentTimeMillis();
        // Вычитаем количество миллисекунд (1 секунда = 1000 миллисекунд)
        long adjustedTimeMillis = currentTimeMillis - (durationDto.seconds() * 1000L);
        Helper helper = helperService.findByTgId(tgId);
        return messageRepository
                .findAllByTimestampAfterAndUserIdAndHelperAndMessageTo(new Timestamp(adjustedTimeMillis),
                        userId, helper, "helper");
    }
    @Transactional
    public void addMessage(String messageText, String messageTo, Long userId, String tgId) {
        Message message = new Message();
        message.setMessage(messageText);
        message.setMessageTo(messageTo);
        message.setUserId(userId);
        message.setHelper(helperService.findByTgId(tgId));
        messageRepository.save(message);
    }
}
