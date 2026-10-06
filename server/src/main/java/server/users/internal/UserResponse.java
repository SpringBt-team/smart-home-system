package server.users.internal;

import io.swagger.v3.oas.annotations.media.Schema;

import server.users.UserAccount;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Профіль користувача")
record UserResponse(
        @Schema(description = "Ідентифікатор користувача", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Email користувача", example = "owner@example.com")
        String email,

        @Schema(description = "Ім'я користувача", example = "Власник")
        String name,

        @Schema(description = "Момент реєстрації (UTC)", example = "2026-10-06T12:00:00Z")
        Instant createdAt) {

    static UserResponse from(UserAccount account) {
        return new UserResponse(account.id(), account.email(), account.name(), account.createdAt());
    }
}
