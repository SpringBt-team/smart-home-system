package server.devices.internal;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import server.devices.AccessRole;

import java.util.UUID;

@Schema(description = "Дані для надання доступу до пристрою")
record GrantAccessRequest(
        @Schema(description = "Email користувача, якому надається доступ", example = "guest@example.com")
        @NotBlank(message = "Email є обов'язковим")
        String email,

        @Schema(description = "Роль на пристрої: OWNER або GUEST", example = "GUEST")
        @NotNull(message = "Роль є обов'язковою")
        AccessRole role,

        @Schema(description = "Ідентифікатор користувача, який надає доступ", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull(message = "grantedById є обов'язковим")
        UUID grantedById) {
}
