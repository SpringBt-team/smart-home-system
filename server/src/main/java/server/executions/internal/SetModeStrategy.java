package server.executions.internal;

import org.springframework.stereotype.Component;
import server.executions.DeviceClient;
import server.executions.ExecutionOutcome;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
class SetModeStrategy extends AbstractDeviceCommandStrategy {

    private static final Set<String> MODES = Set.of("auto", "cool", "heat", "dry", "fan");

    SetModeStrategy(DeviceClient deviceClient) {
        super("set_mode", deviceClient);
    }

    @Override
    Optional<ExecutionOutcome> validate(Map<String, Object> args) {
        return requireOneOf(args, "mode", MODES);
    }
}
