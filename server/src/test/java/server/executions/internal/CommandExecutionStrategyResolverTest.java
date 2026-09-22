package server.executions.internal;

import org.junit.jupiter.api.Test;
import server.executions.CommandExecutionStrategy;
import server.executions.DeviceClient;
import server.executions.UnsupportedCommandException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class CommandExecutionStrategyResolverTest {

    private final DeviceClient deviceClient = mock(DeviceClient.class);
    private final CommandExecutionStrategy turnOn = new TurnOnStrategy(deviceClient);
    private final CommandExecutionStrategy setMode = new SetModeStrategy(deviceClient);
    private final CommandExecutionStrategyResolver resolver =
            new CommandExecutionStrategyResolver(List.of(turnOn, setMode));

    @Test
    void resolvesStrategyByCommandName() {
        assertThat(resolver.resolve("set_mode")).isSameAs(setMode);
    }

    @Test
    void throwsWhenNoStrategySupportsCommand() {
        assertThatThrownBy(() -> resolver.resolve("self_destruct"))
                .isInstanceOf(UnsupportedCommandException.class);
    }
}
