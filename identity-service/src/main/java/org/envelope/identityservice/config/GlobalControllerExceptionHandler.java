package org.envelope.identityservice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.envelope.identityservice.dto.exception.ClientErrorResponse;
import org.envelope.identityservice.exception.ServerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {
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
}