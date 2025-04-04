package org.envelope.identityservice.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record RegistrationRequest(
                          @NotBlank
                          @Schema(description = "Логин", minLength = 4,
                                  maxLength = 255, example = "lena")
                          @Length(min = 4, max = 255)
                          String username,
                          @NotBlank @Email
                          @Schema(description = "Почта", format = "email@domen.xx",
                                  example = "golovach@mail.org")
                          String email,
                          @NotBlank
                          @Schema(description = "Пароль от 4 до 255 символов", minLength = 4,
                                  maxLength = 255, example = "password")
                          @Length(min = 4, max = 255)
                          String password,
                          @NotBlank @Schema(description = "Имя пользователя", example = "Лена")
                          String firstname,
                          @NotBlank @Schema(description = "Фамилия пользователя", example = "Головач")
                          String lastname,
                          @Schema(description = "Отчество пользователя", example = "Безпаповна")
                          String middlename) {}