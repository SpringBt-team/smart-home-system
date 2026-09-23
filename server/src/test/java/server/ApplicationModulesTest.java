package server;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class ApplicationModulesTest {

    @Test
    void verifyModules() {
        ApplicationModules modules = ApplicationModules.of(ServerApplication.class);
        modules.verify();
    }
}