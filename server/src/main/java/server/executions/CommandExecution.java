package server.executions;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record CommandExecution(
        UUID id,
        UUID deviceId,
        UUID commandId,
        Map<String, Object> args,
        ExecutionStatus status,
        Instant requestedAt) {

    public CommandExecution transitionTo(ExecutionStatus newStatus) {
        if (!status.canTransitionTo(newStatus)) {
            throw new InvalidStateTransitionException(status, newStatus);
        }
        return new CommandExecution(id, deviceId, commandId, args, newStatus, requestedAt);
    }
}
