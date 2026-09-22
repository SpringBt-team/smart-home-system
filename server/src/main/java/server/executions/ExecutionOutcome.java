package server.executions;

public record ExecutionOutcome(Status status, String message) {

    public enum Status {
        SUCCESS,
        REJECTED,
        DEVICE_UNAVAILABLE
    }

    public static ExecutionOutcome success(String message) {
        return new ExecutionOutcome(Status.SUCCESS, message);
    }

    public static ExecutionOutcome rejected(String message) {
        return new ExecutionOutcome(Status.REJECTED, message);
    }

    public static ExecutionOutcome deviceUnavailable(String message) {
        return new ExecutionOutcome(Status.DEVICE_UNAVAILABLE, message);
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
}
