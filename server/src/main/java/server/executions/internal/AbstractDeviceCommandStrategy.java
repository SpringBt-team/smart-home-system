package server.executions.internal;

import server.executions.CommandExecutionStrategy;
import server.executions.DeviceClient;
import server.executions.DeviceExecutionResult;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

abstract class AbstractDeviceCommandStrategy implements CommandExecutionStrategy {

    private final String commandName;
    private final DeviceClient deviceClient;

    AbstractDeviceCommandStrategy(String commandName, DeviceClient deviceClient) {
        this.commandName = commandName;
        this.deviceClient = deviceClient;
    }

    @Override
    public boolean supports(String commandName) {
        return this.commandName.equals(commandName);
    }

    @Override
    public DeviceExecutionResult execute(UUID deviceId, String command, Map<String, Object> args) {
        Map<String, Object> safeArgs = args == null ? Map.of() : args;
        return validate(safeArgs).orElseGet(() -> deviceClient.send(deviceId, commandName, safeArgs));
    }

    Optional<DeviceExecutionResult> validate(Map<String, Object> args) {
        return Optional.empty();
    }

    static Optional<DeviceExecutionResult> requireNumber(Map<String, Object> args, String key) {
        if (!(args.get(key) instanceof Number)) {
            return Optional.of(DeviceExecutionResult.rejected(
                    "Аргумент " + key + " є обов'язковим і має бути числом"));
        }
        return Optional.empty();
    }

    static Optional<DeviceExecutionResult> requireIntInRange(
            Map<String, Object> args, String key, int min, int max) {
        if (!(args.get(key) instanceof Number number)) {
            return Optional.of(DeviceExecutionResult.rejected(
                    "Аргумент " + key + " є обов'язковим і має бути числом"));
        }
        int value = number.intValue();
        if (value < min || value > max) {
            return Optional.of(DeviceExecutionResult.rejected(
                    "Аргумент " + key + " має бути в межах " + min + ".." + max));
        }
        return Optional.empty();
    }

    static Optional<DeviceExecutionResult> requireOneOf(
            Map<String, Object> args, String key, Set<String> allowed) {
        if (!(args.get(key) instanceof String value) || value.isBlank()) {
            return Optional.of(DeviceExecutionResult.rejected(
                    "Аргумент " + key + " є обов'язковим і має бути рядком"));
        }
        if (!allowed.contains(value)) {
            return Optional.of(DeviceExecutionResult.rejected(
                    "Аргумент " + key + " має бути одним із " + allowed));
        }
        return Optional.empty();
    }
}
