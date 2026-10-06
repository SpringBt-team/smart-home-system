package server.commands.internal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import server.commands.RequiredRole;

@Schema(description = "Нові параметри команди пристрою")
record UpdateCommandRequest(
        @Schema(description = "JSON Schema аргументів команди (рядок із JSON)", example = "{\"type\":\"object\",\"additionalProperties\":false}")
        @NotNull(message = "Схема аргументів є обов'язковою")
        String argsSchema,

        @Schema(description = "Роль, якій доступна команда: OWNER або GUEST", example = "GUEST")
        @NotNull(message = "Роль доступу є обов'язковою")
        RequiredRole requiredRole) {
}
