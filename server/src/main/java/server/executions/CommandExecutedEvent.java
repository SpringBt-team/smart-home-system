package server.executions;

import java.util.UUID;

public record CommandExecutedEvent(UUID executionId, ExecutionStatus status) {
}
