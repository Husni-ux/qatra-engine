package io.github.qatra.web.waits.adaptive;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Result of evaluating one QATRA wait condition.
 */
public final class QatraConditionResult {

    private final boolean passed;
    private final String message;
    private final Map<String, String> evidence;
    private final Throwable exception;

    private QatraConditionResult(boolean passed, String message, Map<String, String> evidence, Throwable exception) {
        this.passed = passed;
        this.message = message == null ? "" : message;
        this.evidence = Map.copyOf(evidence == null ? Map.of() : evidence);
        this.exception = exception;
    }

    public static QatraConditionResult passed(String message) {
        return new QatraConditionResult(true, message, Map.of(), null);
    }

    public static QatraConditionResult passed(String message, Map<String, String> evidence) {
        return new QatraConditionResult(true, message, evidence, null);
    }

    public static QatraConditionResult failed(String message) {
        return new QatraConditionResult(false, message, Map.of(), null);
    }

    public static QatraConditionResult failed(String message, Map<String, String> evidence) {
        return new QatraConditionResult(false, message, evidence, null);
    }

    public static QatraConditionResult failed(String message, Throwable exception) {
        return new QatraConditionResult(false, message, Map.of(), exception);
    }

    public static QatraConditionResult failed(String message, Map<String, String> evidence, Throwable exception) {
        return new QatraConditionResult(false, message, evidence, exception);
    }

    public boolean passed() {
        return passed;
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

    public QatraConditionResult withEvidence(String key, String value) {
        Map<String, String> copy = new LinkedHashMap<>(evidence);
        copy.put(key, value);
        return new QatraConditionResult(passed, message, copy, exception);
    }
}
