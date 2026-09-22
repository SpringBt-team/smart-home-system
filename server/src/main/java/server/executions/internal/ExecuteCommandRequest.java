package server.executions.internal;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

record ExecuteCommandRequest(
        @NotNull(message = "Поле args є обов'язковим (можна передати порожній об'єкт {})")
        Map<String, Object> args) {
}
