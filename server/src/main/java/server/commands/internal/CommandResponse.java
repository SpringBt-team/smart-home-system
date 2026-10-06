package server.commands.internal;

import io.swagger.v3.oas.annotations.media.Schema;
import server.commands.Command;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Команда пристрою")
record CommandResponse(
        @Schema(description = "Ідентифікатор команди", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Ідентифікатор пристрою", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID deviceId,

        @Schema(description = "Назва команди", example = "turn_on")
        String name,

        @Schema(description = "JSON Schema аргументів команди", example = "{\"type\":\"object\",\"additionalProperties\":false}")
        String argsSchema,

        @Schema(description = "Роль, якій доступна команда: OWNER або GUEST", example = "GUEST")
        String requiredRole,

        @Schema(description = "Момент створення (UTC)", example = "2026-10-06T12:00:00Z")
        Instant createdAt) {
        static CommandResponse from(Command command) {
            return new CommandResponse(
                command.getId(), command.getDevice().getId(), command.getName(),
                command.getArgsSchema(), command.getRequiredRole().name(), command.getCreatedAt());
    }
}
