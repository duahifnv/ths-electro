package org.envelope.identityservice.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponse(@Schema(description = "ID пользователя", example = "42")
                           Long id,
                           @Schema(description = "Логин", example = "azazin")
                           String username,
                           @Schema(description = "Почта", example = "mail@mail.ru")
                           String email,
                           @Schema(description = "Тег пользователя (@<тег>)", example = "azazlo")
                           String tag,
                           @Schema(description = "Имя пользователя", example = "Юзер")
                           String firstname,
                           @Schema(description = "Фамилия пользователя", example = "Юзернеймов")
                           String lastname,
                           @Schema(description = "Отчество пользователя", example = "Клиентович")
                           String middlename,
                           @Schema(description = "Идентификатор аватарки пользователя", example = "user_3158.jpg")
                           String avatarId) {
}
