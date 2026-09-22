package server.executions.internal;

import org.springframework.stereotype.Component;
import server.executions.DeviceClient;
import server.executions.DeviceExecutionResult;

import java.util.Map;
import java.util.Optional;

@Component
class SetPositionStrategy extends AbstractDeviceCommandStrategy {

    SetPositionStrategy(DeviceClient deviceClient) {
        super("set_position", deviceClient);
    }

    @Override
    Optional<DeviceExecutionResult> validate(Map<String, Object> args) {
        return requireIntInRange(args, "position", 0, 100);
    }
}
