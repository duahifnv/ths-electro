package org.envelope.identityservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends ResponseStatusException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Пользователь не найден");
    }
    public UserNotFoundException(String username) {
        super(HttpStatus.NOT_FOUND, "Пользователь %s не найден".formatted(username));
    }
}
