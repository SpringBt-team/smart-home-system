package server.executions.internal;

import org.junit.jupiter.api.Test;
import server.executions.ExecutionOutcome;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SimulatedDeviceClientTest {

    private final SimulatedDeviceClient deviceClient = new SimulatedDeviceClient();

    @Test
    void recordsLastCommandAndArgsPerDevice() {
        UUID deviceId = UUID.randomUUID();

        ExecutionOutcome result = deviceClient.send(deviceId, "set_brightness", Map.of("brightness", 80));

        assertThat(result.success()).isTrue();
        assertThat(deviceClient.stateOf(deviceId))
                .containsEntry("lastCommand", "set_brightness")
                .containsEntry("brightness", 80);
    }

    @Test
    void keepsDevicesIsolated() {
        UUID lamp = UUID.randomUUID();
        UUID blinds = UUID.randomUUID();

        deviceClient.send(lamp, "turn_on", Map.of());
        deviceClient.send(blinds, "open", Map.of());

        assertThat(deviceClient.stateOf(lamp)).containsEntry("lastCommand", "turn_on");
        assertThat(deviceClient.stateOf(blinds)).containsEntry("lastCommand", "open");
    }
}
