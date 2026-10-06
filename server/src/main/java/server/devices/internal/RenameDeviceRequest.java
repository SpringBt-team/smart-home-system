package server.devices.internal;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

@Schema(description = "Нова назва пристрою")
record RenameDeviceRequest(
        @Schema(description = "Нова назва пристрою", example = "Лампа в спальні")
        @NotBlank(message = "Назва пристрою є обов'язковою")
        String name) {
}
