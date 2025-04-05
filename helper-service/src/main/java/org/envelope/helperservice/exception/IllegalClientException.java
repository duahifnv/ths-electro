package org.envelope.helperservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IllegalClientException extends ResponseStatusException {
    public IllegalClientException() {
        super(HttpStatus.FORBIDDEN, "Сервис недоступен для данного пользователя");
    }
    public IllegalClientException(String reason) {
        super(HttpStatus.FORBIDDEN, reason);
    }
}
