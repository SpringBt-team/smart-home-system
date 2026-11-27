package server.integration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import server.support.IntegrationTest;
import server.support.SeedData;

import java.sql.ResultSetMetaData;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
class AuditSuccessTest {

    private static final String EMPTY_SCHEMA = "{\"type\":\"object\",\"additionalProperties\":false}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcClient jdbc;

    @BeforeEach
    void clearAuditLog() {
        jdbc.sql("delete from audit_log").update();
    }

    @Test
    void createDevice_ShouldWriteAuditRowWithUserArgsResultAndTime() throws Exception {
        String ownerToken = bearerToken(SeedData.OWNER_EMAIL);
        UUID ownerId = userId(SeedData.OWNER_EMAIL);

        UUID deviceId = createDevice(ownerToken, ownerId, "Audit Lamp");

        assertThat(auditCount("CREATE_DEVICE")).isEqualTo(1);
        assertThat(auditValue("CREATE_DEVICE", "username")).isEqualTo(SeedData.OWNER_EMAIL);
        assertThat(auditValue("CREATE_DEVICE", "method_name")).isEqualTo("DeviceServiceImpl.create");
        assertThat(auditValue("CREATE_DEVICE", "success")).isEqualTo("true");
        assertThat(auditValue("CREATE_DEVICE", "arguments")).contains("Audit Lamp");
        assertThat(auditValue("CREATE_DEVICE", "result")).contains(deviceId.toString());
        assertThat(jdbc.sql("select performed_at is not null from audit_log where action = 'CREATE_DEVICE'")
                .query(Boolean.class).single()).isTrue();
        assertThat(jdbc.sql("select duration_ms from audit_log where action = 'CREATE_DEVICE'")
                .query(Long.class).single()).isGreaterThanOrEqualTo(0L);
    }

    @Test
    void registerUser_ShouldHidePasswordAndRecordAnonymous() throws Exception {
        String password = "Secr3tPassw0rd";
        String email = "audit-" + UUID.randomUUID() + "@example.com";

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password
                                + "\",\"name\":\"Audit User\"}"))
                .andExpect(status().isCreated());

        assertThat(auditCount("REGISTER_USER")).isEqualTo(1);
        assertThat(auditValue("REGISTER_USER", "username")).isEqualTo("anonymous");
        assertThat(auditValue("REGISTER_USER", "arguments")).isEqualTo("[hidden]");
        assertThat(auditRowAsString("REGISTER_USER")).doesNotContain(password);
    }

    @Test
    void executeCommand_ShouldWriteAuditRow() throws Exception {
        String ownerToken = bearerToken(SeedData.OWNER_EMAIL);
        UUID ownerId = userId(SeedData.OWNER_EMAIL);
        UUID deviceId = createDevice(ownerToken, ownerId, "Audit Execution Lamp");
        UUID commandId = createCommand(ownerToken, deviceId, "turn_on", EMPTY_SCHEMA, "OWNER");

        mockMvc.perform(post("/api/devices/{deviceId}/commands/{commandId}/executions", deviceId, commandId)
                        .header(HttpHeaders.AUTHORIZATION, ownerToken)
                        .header("X-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"args\":{}}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertThat(auditCount("EXECUTE_COMMAND")).isEqualTo(1);
        assertThat(auditValue("EXECUTE_COMMAND", "username")).isEqualTo(SeedData.OWNER_EMAIL);
        assertThat(auditValue("EXECUTE_COMMAND", "success")).isEqualTo("true");
    }

    @Test
    void renameDevice_ShouldWriteAuditRowWithStringAction() throws Exception {
        String ownerToken = bearerToken(SeedData.OWNER_EMAIL);
        UUID ownerId = userId(SeedData.OWNER_EMAIL);
        UUID deviceId = createDevice(ownerToken, ownerId, "Audit Rename Lamp");

        mockMvc.perform(put("/api/devices/{id}", deviceId)
                        .header(HttpHeaders.AUTHORIZATION, ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Renamed Audit Lamp\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Renamed Audit Lamp"));

        assertThat(auditCount("RENAME_DEVICE")).isEqualTo(1);
        assertThat(auditValue("RENAME_DEVICE", "action")).isEqualTo("RENAME_DEVICE");
        assertThat(auditValue("RENAME_DEVICE", "result")).contains("Renamed Audit Lamp");
    }

    @Test
    void readRequest_ShouldNotCreateAuditRow() throws Exception {
        String ownerToken = bearerToken(SeedData.OWNER_EMAIL);

        mockMvc.perform(get("/api/devices")
                        .header(HttpHeaders.AUTHORIZATION, ownerToken))
                .andExpect(status().isOk());

        assertThat(jdbc.sql("select count(*) from audit_log").query(Integer.class).single()).isZero();
    }

    private UUID createDevice(String token, UUID ownerId, String name) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/devices")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .header("X-User-Id", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"" + name + "\",\"type\":\"LAMP\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        return UUID.fromString(JsonPath.read(result.getResponse().getContentAsString(), "$.id"));
    }

    private UUID createCommand(String token, UUID deviceId, String name, String argsSchema, String role)
            throws Exception {
        MvcResult result = mockMvc.perform(post("/api/devices/{deviceId}/commands", deviceId)
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commandJson(name, argsSchema, role)))
                .andExpect(status().isCreated())
                .andReturn();
        return UUID.fromString(JsonPath.read(result.getResponse().getContentAsString(), "$.id"));
    }

    private String commandJson(String name, String argsSchema, String role) {
        return "{\"name\":\"" + name + "\",\"argsSchema\":\""
                + argsSchema.replace("\"", "\\\"") + "\",\"requiredRole\":\"" + role + "\"}";
    }

    private String bearerToken(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + SeedData.PASSWORD + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return "Bearer " + JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
    }

    private UUID userId(String email) {
        return jdbc.sql("select id from users where email = ?")
                .param(email).query(UUID.class).single();
    }

    private int auditCount(String action) {
        return jdbc.sql("select count(*) from audit_log where action = ?")
                .param(action).query(Integer.class).single();
    }

    private String auditValue(String action, String column) {
        return jdbc.sql("select " + column + " from audit_log where action = ?")
                .param(action).query(String.class).single();
    }

    private String auditRowAsString(String action) {
        return jdbc.sql("select * from audit_log where action = ?")
                .param(action)
                .query((resultSet, rowNumber) -> {
                    ResultSetMetaData metadata = resultSet.getMetaData();
                    StringBuilder row = new StringBuilder();
                    for (int column = 1; column <= metadata.getColumnCount(); column++) {
                        row.append(resultSet.getString(column));
                    }
                    return row.toString();
                })
                .single();
    }
}
