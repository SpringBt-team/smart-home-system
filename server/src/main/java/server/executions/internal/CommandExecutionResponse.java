package server.executions.internal;

import io.swagger.v3.oas.annotations.media.Schema;

import server.executions.CommandExecution;
import server.users.User;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Schema(description = "Запис журналу виконання команди")
record CommandExecutionResponse(
        @Schema(description = "Ідентифікатор виконання", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Ідентифікатор пристрою", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID deviceId,

        @Schema(description = "Ідентифікатор команди", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID commandId,

        @Schema(description = "Назва виконаної команди", example = "turn_on")
        String commandName,

        @Schema(description = "Ідентифікатор користувача, який запустив виконання (може бути порожнім)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID executedById,

        @Schema(description = "Email користувача, який запустив виконання (може бути порожнім)", example = "owner@example.com")
        String executedByEmail,

        @Schema(description = "Аргументи, з якими виконано команду", example = "{}")
        Map<String, Object> args,

        @Schema(description = "Статус виконання: PENDING, RUNNING, SUCCESS, FAILED або TIMEOUT", example = "SUCCESS")
        String status,

        @Schema(description = "Момент запиту (UTC)", example = "2026-10-06T12:00:00Z")
        Instant requestedAt,

        @Schema(description = "Момент завершення (UTC); порожній, поки виконання не завершене", example = "2026-10-06T12:00:00Z")
        Instant completedAt) {

    static CommandExecutionResponse from(CommandExecution execution) {
        User executedBy = execution.getExecutedBy();
        return new CommandExecutionResponse(
                execution.getId(),
                execution.getDevice().getId(),
                execution.getCommand().getId(),
                execution.getCommand().getName(),
                executedBy == null ? null : executedBy.getId(),
                executedBy == null ? null : executedBy.getEmail(),
                execution.getArgs(),
                execution.getStatus().name(),
                execution.getRequestedAt(),
                execution.getCompletedAt());
    }
}
