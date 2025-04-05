package org.envelope.helperservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResourceNotFoundException extends ResponseStatusException {
    public ResourceNotFoundException() {
        super(HttpStatus.NOT_FOUND, "Искомый ресурс не найден");
    }
    public ResourceNotFoundException(String reason) {
        super(HttpStatus.NOT_FOUND, reason);
    }
}
