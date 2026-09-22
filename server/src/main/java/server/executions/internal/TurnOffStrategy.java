package server.executions.internal;

import org.springframework.stereotype.Component;
import server.executions.DeviceClient;

@Component
class TurnOffStrategy extends AbstractDeviceCommandStrategy {

    TurnOffStrategy(DeviceClient deviceClient) {
        super("turn_off", deviceClient);
    }
}
