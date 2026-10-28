package server.e2e;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.servlet.client.RestTestClient;
import server.support.E2ETest;
import server.support.SeedData;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@E2ETest
class ApiE2EIT {

    @Autowired
    private RestTestClient client;

    @Autowired
    private JdbcClient jdbc;

    private record LoginToken(String accessToken, String tokenType, long expiresIn) {
    }

    @Test
    void ownerSharesDeviceAndGuestRunsCommand() {
        UUID ownerId = userId(SeedData.OWNER_EMAIL);
        UUID guestId = userId(SeedData.GUEST_EMAIL);
        String ownerToken = login(SeedData.OWNER_EMAIL);

        String deviceBody = client.post().uri("/api/devices")
                .header(HttpHeaders.AUTHORIZATION, ownerToken)
                .header("X-User-Id", ownerId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("name", "Kitchen Lamp", "type", "LAMP"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class).returnResult().getResponseBody();
        UUID deviceId = UUID.fromString(JsonPath.read(deviceBody, "$.id"));
        assertThat(jdbc.sql("select role from device_accesses where device_id = ? and user_id = ?")
                .params(deviceId, ownerId).query(String.class).single()).isEqualTo("OWNER");

        String commandBody = client.post().uri("/api/devices/{id}/commands", deviceId)
                .header(HttpHeaders.AUTHORIZATION, ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("name", "turn_on", "argsSchema", "{}", "requiredRole", "GUEST"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class).returnResult().getResponseBody();
        UUID commandId = UUID.fromString(JsonPath.read(commandBody, "$.id"));
        assertThat(jdbc.sql("select required_role from command where id = ? and device_id = ?")
                .params(commandId, deviceId).query(String.class).single()).isEqualTo("GUEST");

        client.post().uri("/api/devices/{id}/accesses", deviceId)
                .header(HttpHeaders.AUTHORIZATION, ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("email", SeedData.GUEST_EMAIL, "role", "GUEST", "grantedById", ownerId))
                .exchange()
                .expectStatus().isCreated();
        assertThat(jdbc.sql("select role from device_accesses where device_id = ? and user_id = ?")
                .params(deviceId, guestId).query(String.class).single()).isEqualTo("GUEST");

        String guestToken = login(SeedData.GUEST_EMAIL);
        client.post().uri("/api/devices/{id}/commands/{commandId}/executions", deviceId, commandId)
                .header(HttpHeaders.AUTHORIZATION, guestToken)
                .header("X-User-Id", guestId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("args", Map.of()))
                .exchange()
                .expectStatus().isAccepted()
                .expectHeader().exists(HttpHeaders.LOCATION);
        assertThat(jdbc.sql("select status from command_executions where command_id = ? and executed_by_id = ?")
                .params(commandId, guestId).query(String.class).single()).isEqualTo("SUCCESS");

        client.get().uri("/api/devices/{id}/executions?userId={userId}", deviceId, guestId)
                .header(HttpHeaders.AUTHORIZATION, guestToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].commandName").isEqualTo("turn_on")
                .jsonPath("$[0].executedByEmail").isEqualTo(SeedData.GUEST_EMAIL)
                .jsonPath("$[0].status").isEqualTo("SUCCESS");
    }

    @Test
    void guestCannotCreateCommand() {
        UUID deviceId = seedDeviceId();

        client.post().uri("/api/devices/{id}/commands", deviceId)
                .header(HttpHeaders.AUTHORIZATION, login(SeedData.GUEST_EMAIL))
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("name", "turn_on", "argsSchema", "{}", "requiredRole", "GUEST"))
                .exchange()
                .expectStatus().isForbidden();

        assertThat(jdbc.sql("select count(*) from command where device_id = ?")
                .param(deviceId).query(Integer.class).single()).isZero();
    }

    @Test
    void duplicateCommandReturnsConflictProblem() {
        UUID deviceId = seedDeviceId();
        String ownerToken = login(SeedData.OWNER_EMAIL);
        Map<String, String> command = Map.of("name", "turn_on", "argsSchema", "{}", "requiredRole", "GUEST");

        client.post().uri("/api/devices/{id}/commands", deviceId)
                .header(HttpHeaders.AUTHORIZATION, ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(command)
                .exchange()
                .expectStatus().isCreated();

        client.post().uri("/api/devices/{id}/commands", deviceId)
                .header(HttpHeaders.AUTHORIZATION, ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(command)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409);

        assertThat(jdbc.sql("select count(*) from command where device_id = ? and name = ?")
                .params(deviceId, "turn_on").query(Integer.class).single()).isEqualTo(1);
    }

    private String login(String email) {
        LoginToken token = client.post().uri("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("email", email, "password", SeedData.PASSWORD))
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginToken.class).returnResult().getResponseBody();
        return token.tokenType() + " " + token.accessToken();
    }

    private UUID userId(String email) {
        return jdbc.sql("select id from users where email = ?").param(email).query(UUID.class).single();
    }

    private UUID seedDeviceId() {
        return jdbc.sql("select id from devices where name = ?")
                .param(SeedData.SEED_DEVICE_NAME).query(UUID.class).single();
    }
}
