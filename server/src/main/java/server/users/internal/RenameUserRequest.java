package server.users.internal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Нове ім'я користувача")
public record RenameUserRequest(
        @Schema(description = "Нове ім'я користувача", example = "Олександра")
        @NotBlank(message = "Ім'я є обов'язковим!")
        String name) {
}
