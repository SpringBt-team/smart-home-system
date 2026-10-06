package server.openapi;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocsTest {

    private static final int DOCUMENTED_OPERATIONS = 24;

    @Autowired
    private MockMvc mockMvc;

    private String apiDocs() throws Exception {
        return mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void apiDocsContainAllPaths() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Smart Home API"))
                .andExpect(jsonPath("$.paths['/users']").exists())
                .andExpect(jsonPath("$.paths['/users/{id}']").exists())
                .andExpect(jsonPath("$.paths['/tokens']").exists())
                .andExpect(jsonPath("$.paths['/devices']").exists())
                .andExpect(jsonPath("$.paths['/devices/{id}']").exists())
                .andExpect(jsonPath("$.paths['/devices/{id}/accesses']").exists())
                .andExpect(jsonPath("$.paths['/devices/{id}/accesses/{userId}']").exists())
                .andExpect(jsonPath("$.paths['/users/{userId}/devices']").exists())
                .andExpect(jsonPath("$.paths['/devices/{deviceId}/commands']").exists())
                .andExpect(jsonPath("$.paths['/devices/{deviceId}/commands/{commandId}']").exists())
                .andExpect(jsonPath("$.paths['/devices/{deviceId}/commands/{commandId}/executions']").exists())
                .andExpect(jsonPath("$.paths['/devices/{deviceId}/executions']").exists())
                .andExpect(jsonPath("$.paths['/devices/{deviceId}/executions/{executionId}']").exists());
    }

    @Test
    void everyOperationHasSummaryAndDescription() throws Exception {
        String docs = apiDocs();

        List<String> summaries = JsonPath.read(docs, "$.paths.*.*.summary");
        List<String> descriptions = JsonPath.read(docs, "$.paths.*.*.description");
        List<String> tags = JsonPath.read(docs, "$.paths.*.*.tags[0]");

        assertThat(summaries).hasSize(DOCUMENTED_OPERATIONS).allSatisfy(s -> assertThat(s).isNotBlank());
        assertThat(descriptions).hasSize(DOCUMENTED_OPERATIONS).allSatisfy(s -> assertThat(s).isNotBlank());
        assertThat(tags).hasSize(DOCUMENTED_OPERATIONS)
                .containsOnly("Користувачі", "Автентифікація", "Пристрої", "Доступи до пристроїв",
                        "Команди пристрою", "Виконання команд");
    }

    @Test
    void schemasContainExamples() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.schemas.CreateDeviceRequest.properties.name.example")
                        .value("Лампа у вітальні"))
                .andExpect(jsonPath("$.components.schemas.CreateUserRequest.properties.email.example")
                        .value("owner@example.com"))
                .andExpect(jsonPath("$.components.schemas.CreateCommandRequest.properties.requiredRole.example")
                        .value("GUEST"));
    }

    @Test
    void errorResponsesUseProblemDetailAndSuccessCodesAreExplicit() throws Exception {
        String docs = apiDocs();

        assertThat(JsonPath.<Object>read(docs,
                "$.paths['/devices/{id}'].get.responses['404'].content['application/problem+json'].schema"))
                .isNotNull();
        assertThat(JsonPath.<Object>read(docs,
                "$.paths['/devices/{id}/accesses'].post.responses['409']")).isNotNull();
        assertThat(JsonPath.<Object>read(docs, "$.paths['/devices'].post.responses['201']")).isNotNull();
        assertThat(JsonPath.<Object>read(docs,
                "$.paths['/devices/{deviceId}/commands/{commandId}/executions'].post.responses['202']"))
                .isNotNull();
        assertThat(JsonPath.<Object>read(docs, "$.paths['/devices/{id}'].delete.responses['204']")).isNotNull();
    }

    @Test
    void swaggerUiIsAvailable() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
