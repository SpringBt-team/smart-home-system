package com.springbt.smarthome.starter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(NotificationProperties.class)
public class NotificationAutoConfiguration {
    @Bean
    @ConditionalOnProperty(
            prefix = "smarthome.notifications",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    @ConditionalOnMissingBean(NotificationService.class)
    public NotificationService notificationService(NotificationProperties properties) {
        return new LoggingNotificationService(properties.sender());
    }
}