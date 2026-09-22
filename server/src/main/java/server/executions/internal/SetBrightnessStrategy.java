package server.executions.internal;

import org.springframework.stereotype.Component;
import server.executions.DeviceClient;
import server.executions.ExecutionOutcome;

import java.util.Map;
import java.util.Optional;

@Component
class SetBrightnessStrategy extends AbstractDeviceCommandStrategy {

    SetBrightnessStrategy(DeviceClient deviceClient) {
        super("set_brightness", deviceClient);
    }

    @Override
    Optional<ExecutionOutcome> validate(Map<String, Object> args) {
        return requireIntInRange(args, "brightness", 0, 100);
    }
}
