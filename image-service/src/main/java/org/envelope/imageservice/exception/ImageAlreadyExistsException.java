package org.envelope.imageservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ImageAlreadyExistsException extends ResponseStatusException {
    public ImageAlreadyExistsException() {
        super(HttpStatus.BAD_REQUEST, "Изображение с таким именем уже существует");
    }
}
