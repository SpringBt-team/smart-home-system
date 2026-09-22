package server.executions;

import java.util.Map;
import java.util.UUID;

public interface DeviceClient {

    ExecutionOutcome send(UUID deviceId, String commandName, Map<String, Object> args);
}
