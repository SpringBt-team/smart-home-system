package server.executions;

public class InvalidStateTransitionException extends RuntimeException {

    public InvalidStateTransitionException(ExecutionStatus from, ExecutionStatus to) {
        super("Заборонений перехід статусу виконання команди з " + from + " у " + to);
    }
}
