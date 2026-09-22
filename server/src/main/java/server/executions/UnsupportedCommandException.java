package server.executions;

public class UnsupportedCommandException extends RuntimeException {

    public UnsupportedCommandException(String commandName) {
        super("Немає стратегії виконання для команди '" + commandName + "'");
    }
}
