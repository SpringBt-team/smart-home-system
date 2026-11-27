package server.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(nullable = false)
    private String methodName;

    @Column(length = 2000)
    private String arguments;

    @Column(length = 2000)
    private String result;

    @Column(nullable = false)
    private boolean success;

    private String errorType;

    @Column(length = 1000)
    private String errorMessage;

    @Column(nullable = false)
    private Instant performedAt;

    @Column(nullable = false)
    private long durationMs;

    protected AuditLogEntity() {
    }

    public AuditLogEntity(String username, String action, String methodName, String arguments,
                          String result, boolean success, String errorType, String errorMessage,
                          Instant performedAt, long durationMs) {
        this.username = username;
        this.action = action;
        this.methodName = methodName;
        this.arguments = arguments;
        this.result = result;
        this.success = success;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
        this.performedAt = performedAt;
        this.durationMs = durationMs;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getAction() {
        return action;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getArguments() {
        return arguments;
    }

    public String getResult() {
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getPerformedAt() {
        return performedAt;
    }

    public long getDurationMs() {
        return durationMs;
    }
}
