package server.executions.internal;

import org.springframework.stereotype.Component;
import server.executions.DeviceClient;

@Component
class CloseStrategy extends AbstractDeviceCommandStrategy {

    CloseStrategy(DeviceClient deviceClient) {
        super("close", deviceClient);
    }
}
