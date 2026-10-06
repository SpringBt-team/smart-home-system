package server.devices;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Доступ користувача до пристрою")
public record DeviceAccessResponse(
        @Schema(description = "Ідентифікатор доступу", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @Schema(description = "Ідентифікатор пристрою", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID deviceId,

        @Schema(description = "Ідентифікатор користувача, який має доступ", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID userId,

        @Schema(description = "Роль на пристрої: OWNER (повний доступ) або GUEST (лише дозволені команди)", example = "GUEST")
        AccessRole role,

        @Schema(description = "Ідентифікатор користувача, який надав доступ", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID grantedBy,

        @Schema(description = "Момент надання доступу (UTC)", example = "2026-10-06T12:00:00Z")
        Instant grantedAt) {
    public static DeviceAccessResponse from(DeviceAccess access) {
        return new DeviceAccessResponse(
                access.getId(),
                access.getDevice().getId(),
                access.getUserId(),
                access.getRole(),
                access.getGrantedById(),
                access.getGrantedAt()
        );
    }
}