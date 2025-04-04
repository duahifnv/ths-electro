package org.envelope.identityservice.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CredentialsRequest(@NotBlank
                                 @Schema(description = "Логин", minLength = 4,
                                         maxLength = 255, example = "azazin")
                                 @Length(min = 4, max = 255)
                                 String username,
                                 @NotBlank
                                 @Schema(description = "Пароль", minLength = 4, maxLength = 30,
                                         example = "azazin")
                                 String password) {}
