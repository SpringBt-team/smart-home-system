package server.commands;
import java.util.UUID;

public interface CommandService {
    Command create(UUID deviceId, String name, String argsSchema, RequiredRole requiredRole);
    Command findByDeviceIdAndCommandId(UUID deviceId, UUID commandId);
}
