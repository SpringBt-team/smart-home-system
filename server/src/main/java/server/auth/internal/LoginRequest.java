package server.auth.internal;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Облікові дані для входу")
record LoginRequest(
        @Schema(description = "Email зареєстрованого користувача", example = "owner@example.com")
        @NotBlank(message = "Email є обов'язковим")
        @Email(message = "Некоректний формат email")
        String email,

        @Schema(description = "Пароль користувача", example = "Passw0rd123", format = "password")
        @NotBlank(message = "Пароль є обов'язковим")
        String password) {
}
