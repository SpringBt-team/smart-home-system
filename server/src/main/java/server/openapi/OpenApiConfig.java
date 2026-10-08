package server.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
class OpenApiConfig {

    @Bean
    OpenAPI smartHomeOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Home API")
                        .version("0.1.0")
                        .description("REST API системи керування розумним домом: користувачі, пристрої, "
                                + "команди пристроїв, права доступу та журнал виконання команд."))
                .servers(List.of(new Server()
                        .url("http://localhost:8080")
                        .description("Локальне середовище")));
    }
}
