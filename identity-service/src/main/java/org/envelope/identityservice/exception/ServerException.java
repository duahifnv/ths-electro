package org.envelope.identityservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j(topic = "Сервер API-шлюза")
public class ServerException extends ResponseStatusException {
    public ServerException(Exception e) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Технические неполадки");
        log.error("Ошибка: {}", e.getMessage());
    }
}
