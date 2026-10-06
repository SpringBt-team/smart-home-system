package server.auth.internal;

import io.swagger.v3.oas.annotations.media.Schema;

import server.auth.IssuedToken;

import java.util.UUID;

@Schema(description = "Результат успішного входу")
record TokenResponse(
        @Schema(description = "Токен автентифікації", example = "f3c1b7a0-5d6e-4c2b-9a84-1e0d2b7c9a11")
        String token,

        @Schema(description = "Ідентифікатор користувача, якому видано токен", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID userId) {

    static TokenResponse from(IssuedToken issuedToken) {
        return new TokenResponse(issuedToken.token(), issuedToken.userId());
    }
}
