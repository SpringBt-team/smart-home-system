package server.executions;

import java.util.Map;
import java.util.UUID;

public interface CommandExecutionService {

    CommandExecution execute(UUID deviceId, UUID commandId, Map<String, Object> args);
}
