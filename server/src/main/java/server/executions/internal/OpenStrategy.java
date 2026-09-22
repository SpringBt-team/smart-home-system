package server.executions.internal;

import org.springframework.stereotype.Component;
import server.executions.DeviceClient;

@Component
class OpenStrategy extends AbstractDeviceCommandStrategy {

    OpenStrategy(DeviceClient deviceClient) {
        super("open", deviceClient);
    }
}
