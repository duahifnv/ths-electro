package org.envelope.identityservice.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
                          @NotBlank @Email
                          @Schema(description = "Почта", format = "email@domen.xx",
                                  example = "azazin@mail.org")
                          String email,
                          @NotBlank
                          @Schema(description = "Тег пользователя (@<тег>)", format = "tagname",
                                  example = "azazlo")
                          String tag,
                          @NotBlank @Schema(description = "Имя пользователя", example = "Азазин")
                          String firstname,
                          @NotBlank @Schema(description = "Фамилия пользователя", example = "Азазинов")
                          String lastname,
                          @Schema(description = "Отчество пользователя", example = "Кридович")
                          String middlename) {}