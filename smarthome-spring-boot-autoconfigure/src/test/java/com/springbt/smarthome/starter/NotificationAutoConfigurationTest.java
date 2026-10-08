package com.springbt.smarthome.starter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.assertj.core.api.Assertions.assertThat;

class NotificationAutoConfigurationTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(NotificationAutoConfiguration.class));
    @Test
    void Test01() {
        runner.withPropertyValues("smarthome.notifications.enabled=true")
                .run(context -> assertThat(context).hasSingleBean(NotificationService.class));
    }
    @Test
    void Test02() {
        runner.withPropertyValues("smarthome.notifications.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(NotificationService.class));
    }
    @Test
    void Test03() {
        runner.run(context -> assertThat(context).hasSingleBean(NotificationService.class));
    }
    @Test
    void Test04() {
        runner.withUserConfiguration(CustomConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(NotificationService.class);
                    assertThat(context.getBean(NotificationService.class))
                            .isInstanceOf(CustomNotificationService.class);
                });
    }
    @Test
    void Test05() {
        runner.run(context -> assertThat(context.getBean(NotificationProperties.class).sender())
                .isEqualTo("smart-home"));
    }
    @Configuration(proxyBeanMethods = false)
    static class CustomConfig {
        @Bean
        NotificationService customNotificationService() {
            return new CustomNotificationService();
        }
    }
    static class CustomNotificationService implements NotificationService {
        @Override
        public void send(String recipient, String message) {
        }
    }
}