package server.devices.internal;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import server.devices.DeviceType;

@Schema(description = "Дані для створення пристрою")
record CreateDeviceRequest(
        @Schema(description = "Назва пристрою", example = "Лампа у вітальні")
        @NotBlank(message = "Назва пристрою є обов'язковою")
        String name,

        @Schema(description = "Тип пристрою: LAMP, KETTLE, AC, COFFEE_MACHINE або BLINDS", example = "LAMP")
        @NotNull(message = "Тип пристрою є обов'язковим")
        DeviceType type) {
}
