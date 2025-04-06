package org.envelope.helperservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record HelperDto(
        @Schema(description = "Telegram-ID помощника", example = "404527685")
        @NotBlank
        String tgId,
        @Schema(description = "Имя помощника", example = "Игорь")
        @NotBlank
        String firstname,
        @Schema(description = "Фамилия помощника", example = "Евгеньев")
        @NotBlank
        String lastname
) {
}
