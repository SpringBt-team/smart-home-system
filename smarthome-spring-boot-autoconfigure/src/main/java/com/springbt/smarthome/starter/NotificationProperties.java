package com.springbt.smarthome.starter;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "smarthome.notifications")
@Validated
public record NotificationProperties(
        @DefaultValue("true") boolean enabled,
        @DefaultValue("smart-home") @NotBlank String sender
) {
}