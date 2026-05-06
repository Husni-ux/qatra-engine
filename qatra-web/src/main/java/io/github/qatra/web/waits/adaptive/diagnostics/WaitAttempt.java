package io.github.qatra.web.waits.adaptive.diagnostics;

import io.github.qatra.web.waits.adaptive.QatraConditionResult;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Captures one poll attempt for one adaptive wait.
 */
public final class WaitAttempt {

    private final int index;
    private final Instant timestamp;
    private final String failedCondition;
    private final String message;
    private final Map<String, String> evidence;
    private final Throwable exception;

    public WaitAttempt(int index, String failedCondition, QatraConditionResult result) {
        this.index = index;
        this.timestamp = Instant.now();
        this.failedCondition = failedCondition;
        this.message = result == null ? "Unknown condition result" : result.message();
        this.evidence = result == null ? Map.of() : Map.copyOf(result.evidence());
        this.exception = result == null ? null : result.exception();
    }

    public int index() {
        return index;
    }

    public Instant timestamp() {
        return timestamp;
    }

    public String failedCondition() {
        return failedCondition;
    }

    public String message() {
        return message;
    }

    public Map<String, String> evidence() {
        return evidence;
    }

    public Throwable exception() {
        return exception;
    }

    public String toDebugText() {
        StringBuilder builder = new StringBuilder();
        builder.append("Attempt #").append(index).append(" at ").append(timestamp).append('\n');
        builder.append("Failed condition: ").append(failedCondition).append('\n');
        builder.append("Message: ").append(message).append('\n');
        if (!evidence.isEmpty()) {
            builder.append("Evidence:\n");
            Map<String, String> sorted = new LinkedHashMap<>(evidence);
            sorted.forEach((key, value) -> builder.append("- ").append(key).append(": ").append(value).append('\n'));
        }
        if (exception != null) {
            builder.append("Exception: ").append(exception.getClass().getSimpleName()).append(" - ").append(exception.getMessage()).append('\n');
        }
        return builder.toString();
    }
}
