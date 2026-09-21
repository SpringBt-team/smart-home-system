package server.executions.internal;

import server.executions.CommandExecution;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

record CommandExecutionResponse(
        UUID id,
        UUID deviceId,
        UUID commandId,
        Map<String, Object> args,
        String status,
        Instant requestedAt) {

    static CommandExecutionResponse from(CommandExecution execution) {
        return new CommandExecutionResponse(
                execution.id(),
                execution.deviceId(),
                execution.commandId(),
                execution.args(),
                execution.status().name(),
                execution.requestedAt());
    }
}
