package server.users.internal;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Дані для реєстрації нового користувача")
record CreateUserRequest(
        @Schema(description = "Email користувача; унікальний, використовується для входу", example = "owner@example.com")
        @NotBlank(message = "Email є обов'язковим")
        @Email(message = "Некоректний формат email")
        String email,

        @Schema(description = "Пароль у відкритому вигляді (8–72 символи); на сервері зберігається лише хеш", example = "Passw0rd123", format = "password")
        @NotBlank(message = "Пароль є обов'язковим")
        @Size(min = 8, max = 72, message = "Пароль має містити від 8 до 72 символів")
        String password,

        @Schema(description = "Ім'я користувача", example = "Власник")
        @NotBlank(message = "Ім'я є обов'язковим")
        String name) {
}
