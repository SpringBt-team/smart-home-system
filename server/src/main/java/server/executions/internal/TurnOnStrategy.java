package server.executions.internal;

import org.springframework.stereotype.Component;
import server.executions.DeviceClient;

@Component
class TurnOnStrategy extends AbstractDeviceCommandStrategy {

    TurnOnStrategy(DeviceClient deviceClient) {
        super("turn_on", deviceClient);
    }
}
