package server.audit.internal;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

final class AuditValues {

    private static final int MAX_VALUE_LENGTH = 2000;

    private AuditValues() {
    }

    static String formatAll(Object[] values) {
        String joined = Arrays.stream(values)
                .map(AuditValues::format)
                .collect(Collectors.joining(", ", "[", "]"));
        return truncate(joined, MAX_VALUE_LENGTH);
    }

    static String format(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof CharSequence || value instanceof Number || value instanceof Boolean
                || value instanceof UUID || value instanceof Enum<?> || value instanceof Record
                || value instanceof Map<?, ?>) {
            return truncate(value.toString(), MAX_VALUE_LENGTH);
        }
        try {
            Object id = value.getClass().getMethod("getId").invoke(value);
            return value.getClass().getSimpleName() + "(id=" + id + ")";
        } catch (ReflectiveOperationException e) {
            return value.getClass().getSimpleName();
        }
    }

    static String truncate(String value, int maxLength) {
        return value == null || value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
