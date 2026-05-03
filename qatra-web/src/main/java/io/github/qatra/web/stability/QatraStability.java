package io.github.qatra.web.stability;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import org.testng.Assert;

import java.time.Duration;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * Utility methods for short, explicit stability retries inside one test step.
 *
 * <p>Use this for conditions that can legitimately need a short retry, such as async UI state,
 * eventual DOM update, or delayed local test server response.</p>
 */
public final class QatraStability {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private QatraStability() {
    }

    public static void eventually(String name, BooleanSupplier condition) {
        int attempts = QatraConfig.getInstance().getIntProperty(QatraProperties.STABILITY_ATTEMPTS, 3);
        long delayMs = QatraConfig.getInstance().getIntProperty(QatraProperties.STABILITY_DELAY_MS, 250);
        eventually(name, attempts, Duration.ofMillis(delayMs), condition);
    }

    public static void eventually(String name, int attempts, Duration delay, BooleanSupplier condition) {
        Objects.requireNonNull(condition, "condition must not be null");
        eventuallyValue(name, attempts, delay, () -> {
            boolean ok = condition.getAsBoolean();
            if (!ok) {
                throw new AssertionError("Condition returned false");
            }
            return true;
        });
    }

    public static <T> T eventuallyValue(String name, Supplier<T> supplier) {
        int attempts = QatraConfig.getInstance().getIntProperty(QatraProperties.STABILITY_ATTEMPTS, 3);
        long delayMs = QatraConfig.getInstance().getIntProperty(QatraProperties.STABILITY_DELAY_MS, 250);
        return eventuallyValue(name, attempts, Duration.ofMillis(delayMs), supplier);
    }

    public static <T> T eventuallyValue(String name, int attempts, Duration delay, Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "supplier must not be null");
        int normalizedAttempts = Math.max(1, attempts);
        Duration normalizedDelay = delay == null ? Duration.ZERO : delay;
        Throwable lastFailure = null;

        for (int attempt = 1; attempt <= normalizedAttempts; attempt++) {
            try {
                T value = supplier.get();
                if (attempt > 1) {
                    String message = "Stability step recovered: " + name + " on attempt " + attempt;
                    LOG.warn(message);
                    AllureReport.attachText("QATRA Stability Recovery", message);
                }
                return value;
            } catch (Throwable failure) {
                lastFailure = failure;
                if (attempt < normalizedAttempts) {
                    LOG.warn("Stability retry {}/{} for '{}': {}", attempt, normalizedAttempts, name, failure.getMessage());
                    sleep(normalizedDelay);
                }
            }
        }

        String message = "Stability step failed after " + normalizedAttempts + " attempts: " + name;
        AllureReport.attachText("QATRA Stability Failure", message + "\n" + (lastFailure != null ? lastFailure : ""));
        if (lastFailure instanceof AssertionError assertionError) {
            throw assertionError;
        }
        Assert.fail(message, lastFailure);
        return null;
    }

    private static void sleep(Duration delay) {
        if (delay.isZero() || delay.isNegative()) {
            return;
        }
        try {
            Thread.sleep(delay.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting between QATRA stability retries", e);
        }
    }
}
