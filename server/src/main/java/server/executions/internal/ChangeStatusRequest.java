package server.executions.internal;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;
import server.executions.ExecutionStatus;

@Schema(description = "Запит на зміну статусу виконання")
record ChangeStatusRequest(
        @Schema(description = "Новий статус: PENDING, RUNNING, SUCCESS, FAILED або TIMEOUT; допустимі лише дозволені переходи", example = "SUCCESS")
        @NotNull(message = "Поле status є обов'язковим")
        ExecutionStatus status) {
}
