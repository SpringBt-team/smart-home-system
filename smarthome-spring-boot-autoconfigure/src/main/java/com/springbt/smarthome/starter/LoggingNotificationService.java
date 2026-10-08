package com.springbt.smarthome.starter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingNotificationService implements NotificationService {
    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationService.class);
    private final String sender;
    public LoggingNotificationService(String sender) {
        this.sender = sender;
    }
    @Override
    public void send(String recipient, String message) {
        log.info("[{}] -> {}: {}", sender, recipient, message);
    }
}