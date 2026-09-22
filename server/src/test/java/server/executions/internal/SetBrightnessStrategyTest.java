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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SetBrightnessStrategyTest {

    private final DeviceClient deviceClient = mock(DeviceClient.class);
    private final SetBrightnessStrategy strategy = new SetBrightnessStrategy(deviceClient);

    @Test
    void supportsOnlyItsOwnCommand() {
        assertThat(strategy.supports("set_brightness")).isTrue();
        assertThat(strategy.supports("set_temperature")).isFalse();
    }

    @Test
    void sendsBrightnessToDevice() {
        UUID deviceId = UUID.randomUUID();
        Map<String, Object> args = Map.of("brightness", 80);
        when(deviceClient.send(any(UUID.class), eq("set_brightness"), anyMap()))
                .thenReturn(DeviceExecutionResult.success("ok"));

        DeviceExecutionResult result = strategy.execute(deviceId, "set_brightness", args);

        verify(deviceClient).send(deviceId, "set_brightness", args);
        assertThat(result.outcome()).isEqualTo(ExecutionOutcome.SUCCESS);
    }

    @Test
    void rejectsBrightnessOutOfRangeWithoutCallingDevice() {
        DeviceExecutionResult result =
                strategy.execute(UUID.randomUUID(), "set_brightness", Map.of("brightness", 150));

        assertThat(result.outcome()).isEqualTo(ExecutionOutcome.REJECTED);
        verifyNoInteractions(deviceClient);
    }

    @Test
    void rejectsMissingBrightnessWithoutCallingDevice() {
        DeviceExecutionResult result = strategy.execute(UUID.randomUUID(), "set_brightness", Map.of());

        assertThat(result.outcome()).isEqualTo(ExecutionOutcome.REJECTED);
        verifyNoInteractions(deviceClient);
    }
}
