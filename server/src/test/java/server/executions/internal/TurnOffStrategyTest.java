package server.executions.internal;

import org.junit.jupiter.api.Test;
import server.executions.DeviceClient;
import server.executions.DeviceExecutionResult;
import server.executions.ExecutionOutcome;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TurnOffStrategyTest {

    private final DeviceClient deviceClient = mock(DeviceClient.class);
    private final TurnOffStrategy strategy = new TurnOffStrategy(deviceClient);

    @Test
    void supportsOnlyItsOwnCommand() {
        assertThat(strategy.supports("turn_off")).isTrue();
        assertThat(strategy.supports("turn_on")).isFalse();
    }

    @Test
    void sendsCommandToDevice() {
        UUID deviceId = UUID.randomUUID();
        when(deviceClient.send(any(UUID.class), eq("turn_off"), anyMap()))
                .thenReturn(DeviceExecutionResult.success("ok"));

        DeviceExecutionResult result = strategy.execute(deviceId, "turn_off", Map.of());

        verify(deviceClient).send(deviceId, "turn_off", Map.of());
        assertThat(result.outcome()).isEqualTo(ExecutionOutcome.SUCCESS);
    }
}
