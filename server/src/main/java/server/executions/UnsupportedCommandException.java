package server.executions;

public class UnsupportedCommandException extends RuntimeException {

    public UnsupportedCommandException(String commandName) {
        super("Команда " + commandName + " не підтримується жодною стратегією");
    }
}