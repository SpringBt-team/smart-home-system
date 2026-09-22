package server.executions.internal;

import org.springframework.stereotype.Component;
import server.executions.DeviceClient;
import server.executions.ExecutionOutcome;

import java.util.Map;
import java.util.Optional;

@Component
class SetTemperatureStrategy extends AbstractDeviceCommandStrategy {

    SetTemperatureStrategy(DeviceClient deviceClient) {
        super("set_temperature", deviceClient);
    }

    @Override
    Optional<ExecutionOutcome> validate(Map<String, Object> args) {
        return requireNumber(args, "temperature");
    }
}
