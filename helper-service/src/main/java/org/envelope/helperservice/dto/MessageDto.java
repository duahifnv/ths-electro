package org.envelope.helperservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MessageDto(@NotNull String message) {
}
