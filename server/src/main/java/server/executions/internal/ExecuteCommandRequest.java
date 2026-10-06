package server.executions.internal;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

@Schema(description = "Запит на виконання команди")
record ExecuteCommandRequest(
        @Schema(description = "Аргументи команди згідно з її argsSchema; для команд без аргументів, наприклад turn_on, передається порожній об'єкт", example = "{}")
        @NotNull(message = "Поле args є обов'язковим (можна передати порожній об'єкт {})")
        Map<String, Object> args) {
}
