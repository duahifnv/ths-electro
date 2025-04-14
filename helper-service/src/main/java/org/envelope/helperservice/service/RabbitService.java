package org.envelope.helperservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "RabbitMQ очереди")
public class RabbitService {
    private final RabbitAdmin rabbitAdmin;
    private final RabbitTemplate rabbitTemplate;
    public boolean existsQueue(String queueName) {
        return rabbitAdmin.getQueueInfo(queueName) != null;
    }
    public void createQueue(String userId) {
        String queueName = getPrivateQueuePath(userId);

        if (existsQueue(queueName)) return;
        Queue queue = new Queue(queueName, true);
        rabbitAdmin.declareQueue(queue);
        log.info("Создан новый буфер: {}", queueName);
    }
    public void deleteQueue(String userId) {
        String queueName = getPrivateQueuePath(userId);

        if (!existsQueue(queueName)) return;
        rabbitAdmin.deleteQueue(queueName);
        log.info("Удален буфер: {}", queueName);
    }
    public void sendToBuffer(String message, String receiverId) {
        String queueName = getPrivateQueuePath(receiverId);

        if (!existsQueue(queueName)) {
            createQueue(receiverId);
        }
        rabbitTemplate.convertAndSend(queueName, message);
        log.info("Отправлено сообщение в буфер: {} => {}", queueName, message);
    }
    public String receiveFromBuffer(String senderId) {
        String queueName = getPrivateQueuePath(senderId);

        if (!existsQueue(queueName)) {
            throw new RuntimeException("Не найдено буфера для id: " + senderId);
        }
        Object received = rabbitTemplate.receiveAndConvert(queueName);
        if (received != null) {
            log.info("Получено сообщение из буфера: {} => ['{}']", queueName, received);
            return received.toString();
        }
        return null;
    }
    private String getPrivateQueuePath(String userId) {
        return "user.queue." + userId;
    }
}
