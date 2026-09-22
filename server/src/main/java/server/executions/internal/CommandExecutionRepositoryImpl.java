package server.executions.internal;

import org.springframework.stereotype.Component;
import server.executions.CommandExecution;
import server.executions.CommandExecutionRepository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
class CommandExecutionRepositoryImpl implements CommandExecutionRepository {

    private final Map<UUID, CommandExecution> executions = new ConcurrentHashMap<>();

    @Override
    public CommandExecution save(CommandExecution execution) {
        executions.put(execution.id(), execution);
        return execution;
    }

    @Override
    public Optional<CommandExecution> findById(UUID id) {
        return Optional.ofNullable(executions.get(id));
    }
}
