package server.executions;

import server.commands.Command;

import java.util.Map;
import java.util.UUID;

public interface CommandExecutionStrategy {

    boolean supports(String commandName);

    ExecutionOutcome execute(UUID deviceId, Command command, Map<String, Object> args);
}