package org.envelope.identityservice.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record PasswordUpdateRequest(@NotBlank
                                    @Schema(description = "Новый пароль", example = "2lf42!Nf@g_",
                                            maxLength = 255)
                                    @Length(min = 4, max = 255)
                                    String password) {
}
