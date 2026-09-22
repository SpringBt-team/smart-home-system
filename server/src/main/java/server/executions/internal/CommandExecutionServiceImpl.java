package server.executions.internal;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import server.commands.Command;
import server.commands.CommandService;
import server.executions.CommandExecutedEvent;
import server.executions.CommandExecution;
import server.executions.CommandExecutionRepository;
import server.executions.CommandExecutionService;
import server.executions.CommandExecutionStrategy;
import server.executions.ExecutionOutcome;
import server.executions.ExecutionStatus;
import server.executions.UnsupportedCommandException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
class CommandExecutionServiceImpl implements CommandExecutionService {

    private final CommandService commandService;
    private final CommandArgsValidator commandArgsValidator;
    private final CommandExecutionRepository commandExecutionRepository;
    private final List<CommandExecutionStrategy> strategies;
    private final ApplicationEventPublisher eventPublisher;

    CommandExecutionServiceImpl(
            CommandService commandService,
            CommandArgsValidator commandArgsValidator,
            CommandExecutionRepository commandExecutionRepository,
            List<CommandExecutionStrategy> strategies,
            ApplicationEventPublisher eventPublisher) {
        this.commandService = commandService;
        this.commandArgsValidator = commandArgsValidator;
        this.commandExecutionRepository = commandExecutionRepository;
        this.strategies = strategies;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CommandExecution execute(UUID deviceId, UUID commandId, Map<String, Object> args) {
        Command command = commandService.findByDeviceIdAndCommandId(deviceId, commandId);
        commandArgsValidator.validate(command.argsSchema(), args);

        CommandExecutionStrategy strategy = strategies.stream()
                .filter(s -> s.supports(command.name()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedCommandException(command.name()));

        CommandExecution execution = new CommandExecution(
                UUID.randomUUID(), deviceId, commandId, args, ExecutionStatus.PENDING, Instant.now());
        execution = commandExecutionRepository.save(execution.transitionTo(ExecutionStatus.RUNNING));

        ExecutionOutcome outcome = strategy.execute(deviceId, command, args);
        execution = commandExecutionRepository.save(
                execution.transitionTo(outcome.success() ? ExecutionStatus.SUCCESS : ExecutionStatus.FAILED));

        eventPublisher.publishEvent(new CommandExecutedEvent(execution.id(), execution.status()));

        return execution;
    }
}
