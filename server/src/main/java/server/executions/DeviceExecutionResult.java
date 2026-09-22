package server.executions;

public record DeviceExecutionResult(ExecutionOutcome outcome, String message) {

    public static DeviceExecutionResult success(String message) {
        return new DeviceExecutionResult(ExecutionOutcome.SUCCESS, message);
    }

    public static DeviceExecutionResult rejected(String message) {
        return new DeviceExecutionResult(ExecutionOutcome.REJECTED, message);
    }

    public static DeviceExecutionResult deviceUnavailable(String message) {
        return new DeviceExecutionResult(ExecutionOutcome.DEVICE_UNAVAILABLE, message);
    }
}
