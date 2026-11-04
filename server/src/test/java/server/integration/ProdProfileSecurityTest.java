package server.integration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import server.support.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "security.jwt.secret=12345678901234567890123456789012")
@AutoConfigureMockMvc
@ActiveProfiles("prod")
@Import(TestcontainersConfiguration.class)
class ProdProfileSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void swaggerUiIsClosedAnonymous() throws Exception {
        mockMvc.perform(get("/swagger-ui.html")).andExpect(status().is(not(200)));
    }

    @Test
    void apiDocsIsClosedAnonymous() throws Exception {
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().is(not(200)));
    }

    @Test
    void h2ConsoleIsClosedAnonymous() throws Exception {
        mockMvc.perform(get("/h2-console")).andExpect(status().is(not(200)));
    }
}
