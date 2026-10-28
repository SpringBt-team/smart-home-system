package server.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.servlet.MockMvc;
import server.support.IntegrationTest;
import server.support.SeedData;
import server.users.UserService;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class DeviceControlFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcClient jdbc;

    @Autowired
    private UserService userService;

    @Test
    void ownerSharesDeviceGuestRunsCommandOwnerRemovesDevice() throws Exception {
        UUID ownerId = userId(SeedData.OWNER_EMAIL);
        UUID guestId = userId(SeedData.GUEST_EMAIL);
        String ownerToken = login(SeedData.OWNER_EMAIL);

        // (1) owner creates a device and becomes its OWNER
        String deviceBody = mockMvc.perform(post("/api/devices")
                        .header(HttpHeaders.AUTHORIZATION, ownerToken)
                        .header("X-User-Id", ownerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Kitchen Lamp\",\"type\":\"LAMP\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        UUID deviceId = UUID.fromString(JsonPath.read(deviceBody, "$.id"));
        assertThat(role(deviceId, ownerId)).isEqualTo("OWNER");

        // (2) turn_on for GUEST, set_brightness for OWNER
        UUID turnOnId = createCommand(ownerToken, deviceId, "turn_on", "GUEST");
        UUID brightnessId = createCommand(ownerToken, deviceId, "set_brightness", "OWNER");
        assertThat(jdbc.sql("select required_role from command where id = ?")
                .param(turnOnId).query(String.class).single()).isEqualTo("GUEST");
        assertThat(jdbc.sql("select required_role from command where id = ?")
                .param(brightnessId).query(String.class).single()).isEqualTo("OWNER");

        // (3) owner grants guest access
        mockMvc.perform(post("/api/devices/{id}/accesses", deviceId)
                        .header(HttpHeaders.AUTHORIZATION, ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + SeedData.GUEST_EMAIL + "\",\"role\":\"GUEST\",\"grantedById\":\""
                                + ownerId + "\"}"))
                .andExpect(status().isCreated());
        assertThat(role(deviceId, guestId)).isEqualTo("GUEST");

        // (4) guest runs turn_on
        String guestToken = login(SeedData.GUEST_EMAIL);
        runTurnOn(guestToken, guestId, deviceId, turnOnId);
        assertThat(jdbc.sql("select status from command_executions where command_id = ? and executed_by_id = ?")
                .params(turnOnId, guestId).query(String.class).single()).isEqualTo("SUCCESS");

        // (5) owner runs it too; the journal shows both, the guest filter only the guest's
        runTurnOn(ownerToken, ownerId, deviceId, turnOnId);
        mockMvc.perform(get("/api/devices/{id}/executions", deviceId)
                        .header(HttpHeaders.AUTHORIZATION, ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(get("/api/devices/{id}/executions", deviceId)
                        .param("userId", guestId.toString())
                        .header(HttpHeaders.AUTHORIZATION, guestToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].executedByEmail").value(SeedData.GUEST_EMAIL));

        // (7) owner revokes guest access and deletes the device
        mockMvc.perform(delete("/api/devices/{id}/accesses/{userId}", deviceId, guestId)
                        .header(HttpHeaders.AUTHORIZATION, ownerToken))
                .andExpect(status().isNoContent());
        assertThat(count("select count(*) from device_accesses where device_id = ? and user_id = ?",
                deviceId, guestId)).isZero();

        mockMvc.perform(delete("/api/devices/{id}", deviceId)
                        .header(HttpHeaders.AUTHORIZATION, ownerToken))
                .andExpect(status().isNoContent());
        assertThat(count("select count(*) from devices where id = ?", deviceId)).isZero();
        assertThat(count("select count(*) from device_accesses where device_id = ?", deviceId)).isZero();
        assertThat(count("select count(*) from command where device_id = ?", deviceId)).isZero();
        assertThat(count("select count(*) from command_executions where device_id = ?", deviceId)).isZero();
    }

    @Test
    void guestAndStrangerCannotManageCommandsOrAccess() throws Exception {
        UUID deviceId = seedDeviceId();
        UUID ownerId = userId(SeedData.OWNER_EMAIL);
        UUID commandId = createCommand(login(SeedData.OWNER_EMAIL), deviceId, "turn_on", "GUEST");
        String guestToken = login(SeedData.GUEST_EMAIL);

        // (6) guest: create, update, delete a command and grant access are all 403
        mockMvc.perform(post("/api/devices/{id}/commands", deviceId)
                        .header(HttpHeaders.AUTHORIZATION, guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"turn_off\",\"argsSchema\":\"{}\",\"requiredRole\":\"GUEST\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/devices/{id}/commands/{commandId}", deviceId, commandId)
                        .header(HttpHeaders.AUTHORIZATION, guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"argsSchema\":\"{}\",\"requiredRole\":\"OWNER\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/devices/{id}/commands/{commandId}", deviceId, commandId)
                        .header(HttpHeaders.AUTHORIZATION, guestToken))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/devices/{id}/accesses", deviceId)
                        .header(HttpHeaders.AUTHORIZATION, guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + SeedData.STRANGER_EMAIL + "\",\"role\":\"GUEST\",\"grantedById\":\""
                                + ownerId + "\"}"))
                .andExpect(status().isForbidden());

        // stranger cannot create a command
        mockMvc.perform(post("/api/devices/{id}/commands", deviceId)
                        .header(HttpHeaders.AUTHORIZATION, login(SeedData.STRANGER_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"turn_off\",\"argsSchema\":\"{}\",\"requiredRole\":\"GUEST\"}"))
                .andExpect(status().isForbidden());

        // tables unchanged: still the one command, still GUEST, no new access
        assertThat(count("select count(*) from command where device_id = ?", deviceId)).isEqualTo(1);
        assertThat(jdbc.sql("select required_role from command where id = ?")
                .param(commandId).query(String.class).single()).isEqualTo("GUEST");
        assertThat(count("select count(*) from device_accesses where device_id = ? and user_id = ?",
                deviceId, userId(SeedData.STRANGER_EMAIL))).isZero();
    }

    @Test
    void devicePageShowsCommandsByRole() throws Exception {
        UUID deviceId = seedDeviceId();
        String ownerToken = login(SeedData.OWNER_EMAIL);
        createCommand(ownerToken, deviceId, "turn_on", "GUEST");
        createCommand(ownerToken, deviceId, "set_brightness", "OWNER");

        mockMvc.perform(get("/devices/{id}", deviceId)
                        .with(user(userService.getUserByEmail(SeedData.OWNER_EMAIL))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Додати команду")))
                .andExpect(content().string(containsString("set_brightness")));

        mockMvc.perform(get("/devices/{id}", deviceId)
                        .with(user(userService.getUserByEmail(SeedData.GUEST_EMAIL))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("turn_on")))
                .andExpect(content().string(not(containsString("Додати команду"))))
                .andExpect(content().string(not(containsString("set_brightness"))));

        mockMvc.perform(get("/devices/{id}", deviceId)
                        .with(user(userService.getUserByEmail(SeedData.STRANGER_EMAIL))))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/devices/{id}/commands", deviceId)
                        .with(user(userService.getUserByEmail(SeedData.GUEST_EMAIL)))
                        .with(csrf())
                        .param("name", "turn_off")
                        .param("argsSchema", "{}")
                        .param("requiredRole", "GUEST"))
                .andExpect(status().isForbidden());
        assertThat(count("select count(*) from command where device_id = ?", deviceId)).isEqualTo(2);
    }

    private UUID createCommand(String token, UUID deviceId, String name, String requiredRole) throws Exception {
        String body = mockMvc.perform(post("/api/devices/{id}/commands", deviceId)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"argsSchema\":\"{}\",\"requiredRole\":\""
                                + requiredRole + "\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(JsonPath.read(body, "$.id"));
    }

    private void runTurnOn(String token, UUID userId, UUID deviceId, UUID commandId) throws Exception {
        mockMvc.perform(post("/api/devices/{id}/commands/{commandId}/executions", deviceId, commandId)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .header("X-User-Id", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"args\":{}}"))
                .andExpect(status().isAccepted());
    }

    private String login(String email) throws Exception {
        String body = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + SeedData.PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(body, "$.accessToken");
    }

    private String role(UUID deviceId, UUID userId) {
        return jdbc.sql("select role from device_accesses where device_id = ? and user_id = ?")
                .params(deviceId, userId).query(String.class).single();
    }

    private int count(String sql, Object... params) {
        return jdbc.sql(sql).params(params).query(Integer.class).single();
    }

    private UUID userId(String email) {
        return jdbc.sql("select id from users where email = ?").param(email).query(UUID.class).single();
    }

    private UUID seedDeviceId() {
        return jdbc.sql("select id from devices where name = ?")
                .param(SeedData.SEED_DEVICE_NAME).query(UUID.class).single();
    }
}
