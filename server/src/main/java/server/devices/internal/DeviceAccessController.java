package server.devices.internal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import server.openapi.BadRequestResponse;
import server.openapi.ConflictResponse;
import server.openapi.NotFoundResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import server.devices.DeviceAccess;
import server.devices.DeviceAccessResponse;
import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Доступи до пристроїв", description = "Надання, відкликання та перегляд доступів користувачів до пристроїв")
class DeviceAccessController {

    private final DeviceAccessService accessService;
    DeviceAccessController(DeviceAccessService accessService) {
        this.accessService = accessService;
    }

    @Operation(summary = "Надати доступ до пристрою", description = "Надає користувачеві (за email) роль OWNER або GUEST на пристрої.")
    @ApiResponse(responseCode = "201", description = "Доступ надано")
    @BadRequestResponse
    @NotFoundResponse
    @ConflictResponse
    @PostMapping("/devices/{id}/accesses")
    ResponseEntity<DeviceAccessResponse> grant(
            @Parameter(description = "Ідентифікатор пристрою", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Email користувача, роль і хто надає доступ") @Valid @RequestBody GrantAccessRequest request) {
        DeviceAccess access = accessService.grant(
                id,
                request.email(),
                request.role(),
                request.grantedById()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DeviceAccessResponse.from(access));
    }

    @Operation(summary = "Відкликати доступ", description = "Прибирає доступ користувача до пристрою.")
    @ApiResponse(responseCode = "204", description = "Доступ відкликано")
    @NotFoundResponse
    @DeleteMapping("/devices/{id}/accesses/{userId}")
    ResponseEntity<Void> revoke(
            @Parameter(description = "Ідентифікатор пристрою", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id,
            @Parameter(description = "Ідентифікатор користувача, якому відкликається доступ", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID userId) {
        accessService.revoke(id, userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Переглянути доступи пристрою", description = "Повертає всіх користувачів, які мають доступ до пристрою, з їхніми ролями.")
    @ApiResponse(responseCode = "200", description = "Список доступів")
    @NotFoundResponse
    @GetMapping("/devices/{id}/accesses")
    ResponseEntity<List<DeviceAccessResponse>> findByDevice(
            @Parameter(description = "Ідентифікатор пристрою", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID id) {
        List<DeviceAccessResponse> responses =
                accessService.findByDevice(id).stream()
                        .map(DeviceAccessResponse::from)
                        .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Переглянути пристрої користувача", description = "Повертає доступи користувача разом з пристроями, до яких він має доступ.")
    @ApiResponse(responseCode = "200", description = "Список доступів користувача")
    @GetMapping("/users/{userId}/devices")
    ResponseEntity<List<DeviceAccessResponse>> findByUser(
            @Parameter(description = "Ідентифікатор користувача", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6") @PathVariable UUID userId) {
        List<DeviceAccessResponse> responses =
                accessService.findByUser(userId).stream()
                        .map(DeviceAccessResponse::from)
                        .toList();
        return ResponseEntity.ok(responses);
    }
}