package server.executions.internal;

import org.springframework.stereotype.Component;
import server.executions.CommandExecutionStrategy;
import server.executions.UnsupportedCommandException;

import java.util.List;

@Component
class CommandExecutionStrategyResolver {

    private final List<CommandExecutionStrategy> strategies;

    CommandExecutionStrategyResolver(List<CommandExecutionStrategy> strategies) {
        this.strategies = List.copyOf(strategies);
    }

    CommandExecutionStrategy resolve(String commandName) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(commandName))
                .findFirst()
                .orElseThrow(() -> new UnsupportedCommandException(commandName));
    }
}
