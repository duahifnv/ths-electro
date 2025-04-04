package org.envelope.identityservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomBadCredentialsException extends ResponseStatusException {
    public CustomBadCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, "Неверный пароль");
    }
}
