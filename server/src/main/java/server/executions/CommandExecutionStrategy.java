package server.executions;

import java.util.Map;
import java.util.UUID;

public interface CommandExecutionStrategy {

    boolean supports(String commandName);

    DeviceExecutionResult execute(UUID deviceId, String command, Map<String, Object> args);
}
