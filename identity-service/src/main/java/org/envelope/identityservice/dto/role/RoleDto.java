package org.envelope.identityservice.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RoleDto(@Schema(description = "ID роли", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
                      @NotNull @Min(1)
                        Integer id,
                      @Schema(description = "Название роли", example = "developer")
                      @NotBlank
                        String roleName) {
}
