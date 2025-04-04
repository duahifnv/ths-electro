package org.envelope.identityservice.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RoleNamesDto(
        @Schema(description = "Передаваемые роли", example = "user, developer")
        @NotNull List<String> roles) {
}
