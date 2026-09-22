package server.executions;

public record ExecutionOutcome(Status status, String message) {

    public enum Status {
        SUCCESS,
        REJECTED,
        DEVICE_UNAVAILABLE
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
}