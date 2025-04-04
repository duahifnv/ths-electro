package org.envelope.apigateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.envelope.apigateway.dto.ClientErrorResponse;
import org.envelope.apigateway.dto.ValidationErrorResponse;
import org.envelope.apigateway.dto.Violation;
import org.envelope.apigateway.exception.ServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {
    // Обработка исключений, которые так и не были обработаны
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handle(Exception e) {
        log.error("Unhandled exception {}: {}", e.getClass().getSimpleName(), e.getMessage());
        return "Сбой на сервере";
    }
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handle(NoResourceFoundException e) {
        log.info("Не найден ресурс: {}", e.getResourcePath());
        return ResponseEntity.status(e.getStatusCode()).body(e.getBody());
    }
    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ClientErrorResponse handle(HttpClientErrorException e) {
        try {
            String responseBody = e.getResponseBodyAsString();
            log.error("Выброшено исключение от клиента: {}", responseBody);

            ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(responseBody, ClientErrorResponse.class);
        }
        catch (JsonProcessingException jpe) {
            log.error("Ошибка JSON-парсинга: {}", jpe.getMessage());
            throw new ServerException(jpe);
        }
    }
    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handle(JwtException e) {
        log.info("Jwt exception: {}", e.getMessage());
        return e.getMessage();
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handle(ResponseStatusException e) {
        log.info("Response status exception: {}", e.getMessage());
        return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
    }
    // Обработка ошибок валидации параметров запроса и переменных пути запроса
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handle(ConstraintViolationException e) {
        List<Violation> violations = e.getConstraintViolations().stream()
                .map(v -> new Violation(
                        v.getPropertyPath().toString(), v.getMessage()
                )).toList();
        return new ValidationErrorResponse(violations);
    }
    // Обработка ошибок полей тела запроса
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handle(MethodArgumentNotValidException e) {
        List<Violation> fieldViolations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(
                        error.getField(), error.getDefaultMessage()
                ))
                .toList();
        return new ValidationErrorResponse(fieldViolations);
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(MethodArgumentTypeMismatchException e) {
        log.warn("MethodArgumentTypeMismatchException: {}", e.getMessage());
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        log.warn("HttpMessageNotReadableException: {}", e.getHttpInputMessage());
    }
}
