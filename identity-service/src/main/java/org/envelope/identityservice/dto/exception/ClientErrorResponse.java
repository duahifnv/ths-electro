package org.envelope.identityservice.dto.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

public record ClientErrorResponse(
        @Schema(description = "Время ошибки", format = "yyyy-mm-dd hh:mm:ss")
        String timestamp,
        @Schema(description = "Код ошибки", example = "404")
        int status,
        @Schema(description = "Наименование ошибки")
        String error,
        @Schema(description = "Описание ошибки")
        String message,
        @JsonIgnore String path) {
}
