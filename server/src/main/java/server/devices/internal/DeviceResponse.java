package server.devices.internal;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import server.devices.Device;
import server.devices.DeviceAccessResponse;
import server.devices.DeviceType;

@Schema(description = "Пристрій разом зі списком доступів")
record DeviceResponse(
        @Schema(description = "Ідентифікатор пристрою", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Назва пристрою", example = "Лампа у вітальні")
        String name,

        @Schema(description = "Тип пристрою", example = "LAMP")
        DeviceType type,

        @Schema(description = "Токен підключення симулятора пристрою", example = "d5e8a929-7d73-458e-b9ba-f04f6b899de9")
        String connectionToken,

        @Schema(description = "Момент створення (UTC)", example = "2026-10-06T12:00:00Z")
        Instant createdAt,

        @Schema(description = "Користувачі, які мають доступ до пристрою")
        List<DeviceAccessResponse> accesses) {
    static DeviceResponse from(Device device) {
        List<DeviceAccessResponse> accesses = device.getAccesses().stream()
                .map(DeviceAccessResponse::from)
                .toList();
        return new DeviceResponse(device.getId(), device.getName(), device.getType(),
                device.getConnectionToken(), device.getCreatedAt(), accesses);
    }
}
