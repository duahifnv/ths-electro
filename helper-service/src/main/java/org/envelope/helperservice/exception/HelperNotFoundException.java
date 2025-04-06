package org.envelope.helperservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class HelperNotFoundException extends ResponseStatusException {
    public HelperNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Помощник не найден");
    }
    public HelperNotFoundException(String username) {
        super(HttpStatus.NOT_FOUND, "Помощник %s не найден".formatted(username));
    }
}
