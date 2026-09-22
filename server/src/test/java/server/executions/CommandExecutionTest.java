package server.executions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandExecutionTest {

    private static final Set<Arguments> ALLOWED_TRANSITIONS = Set.of(
            Arguments.of(ExecutionStatus.PENDING, ExecutionStatus.RUNNING),
            Arguments.of(ExecutionStatus.RUNNING, ExecutionStatus.SUCCESS),
            Arguments.of(ExecutionStatus.RUNNING, ExecutionStatus.FAILED),
            Arguments.of(ExecutionStatus.RUNNING, ExecutionStatus.TIMEOUT));

    static Stream<Arguments> allowedTransitions() {
        return ALLOWED_TRANSITIONS.stream();
    }

    static Stream<Arguments> forbiddenTransitions() {
        return EnumSet.allOf(ExecutionStatus.class).stream()
                .flatMap(from -> EnumSet.allOf(ExecutionStatus.class).stream()
                        .map(to -> Arguments.of(from, to)))
                .filter(pair -> !isAllowed(pair));
    }

    private static boolean isAllowed(Arguments pair) {
        Object[] values = pair.get();
        return ALLOWED_TRANSITIONS.stream()
                .anyMatch(allowed -> allowed.get()[0] == values[0] && allowed.get()[1] == values[1]);
    }

    private CommandExecution executionWithStatus(ExecutionStatus status) {
        return new CommandExecution(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                Map.of(), status, Instant.now());
    }

    @ParameterizedTest(name = "{0} -> {1} is allowed")
    @MethodSource("allowedTransitions")
    void allowedTransitionChangesStatus(ExecutionStatus from, ExecutionStatus to) {
        CommandExecution execution = executionWithStatus(from);

        CommandExecution updated = execution.transitionTo(to);

        assertEquals(to, updated.status());
    }

    @ParameterizedTest(name = "{0} -> {1} is forbidden")
    @MethodSource("forbiddenTransitions")
    void forbiddenTransitionThrows(ExecutionStatus from, ExecutionStatus to) {
        CommandExecution execution = executionWithStatus(from);

        assertThrows(InvalidStateTransitionException.class,
                () -> execution.transitionTo(to));
    }
}   
