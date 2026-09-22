package server.executions.internal;

import org.springframework.stereotype.Component;
import server.executions.DeviceClient;
import server.executions.DeviceExecutionResult;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
class SimulatedDeviceClient implements DeviceClient {

    private final Map<UUID, Map<String, Object>> statesByDeviceId = new ConcurrentHashMap<>();

    @Override
    public DeviceExecutionResult send(UUID deviceId, String commandName, Map<String, Object> args) {
        Map<String, Object> state = statesByDeviceId.computeIfAbsent(deviceId, id -> new ConcurrentHashMap<>());
        state.put("lastCommand", commandName);
        state.putAll(args);
        return DeviceExecutionResult.success("Команду " + commandName + " виконано на пристрої " + deviceId);
    }

    Map<String, Object> stateOf(UUID deviceId) {
        return Map.copyOf(statesByDeviceId.getOrDefault(deviceId, Map.of()));
    }
}
