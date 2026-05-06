package io.github.qatra.web.waits.adaptive;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Immutable configuration object for QATRA adaptive waits.
 *
 * <p>This object intentionally includes more than Selenium's timeout and polling interval.
 * QATRA waits can also require a quiet window for visual/DOM stability and can collect
 * diagnostics when a timeout happens.</p>
 */
public final class QatraWaitOptions {

    private final Duration timeout;
    private final Duration pollingInterval;
    private final Duration quietWindow;
    private final boolean screenshotOnFailure;
    private final boolean diagnosticsEnabled;
    private final List<Class<? extends Throwable>> ignoredExceptions;

    private QatraWaitOptions(Builder builder) {
        this.timeout = builder.timeout;
        this.pollingInterval = builder.pollingInterval;
        this.quietWindow = builder.quietWindow;
        this.screenshotOnFailure = builder.screenshotOnFailure;
        this.diagnosticsEnabled = builder.diagnosticsEnabled;
        this.ignoredExceptions = List.copyOf(builder.ignoredExceptions);
    }

    public static QatraWaitOptions fromConfig() {
        QatraConfig config = QatraConfig.getInstance();
        int timeoutSeconds = config.getIntProperty(QatraProperties.ELEMENT_TIMEOUT, 10);
        int pollingMs = config.getIntProperty(QatraProperties.WAIT_POLLING_MS, 250);
        return builder()
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(Math.max(50, pollingMs)))
                .quietWindow(Duration.ofMillis(Math.max(200, pollingMs * 2L)))
                .ignore(NoSuchElementException.class)
                .ignore(StaleElementReferenceException.class)
                .ignore(WebDriverException.class)
                .screenshotOnFailure(true)
                .diagnosticsEnabled(true)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        Builder builder = new Builder();
        builder.timeout = timeout;
        builder.pollingInterval = pollingInterval;
        builder.quietWindow = quietWindow;
        builder.screenshotOnFailure = screenshotOnFailure;
        builder.diagnosticsEnabled = diagnosticsEnabled;
        builder.ignoredExceptions.clear();
        builder.ignoredExceptions.addAll(ignoredExceptions);
        return builder;
    }

    public Duration timeout() {
        return timeout;
    }

    public Duration pollingInterval() {
        return pollingInterval;
    }

    public Duration quietWindow() {
        return quietWindow;
    }

    public boolean screenshotOnFailure() {
        return screenshotOnFailure;
    }

    public boolean diagnosticsEnabled() {
        return diagnosticsEnabled;
    }

    public List<Class<? extends Throwable>> ignoredExceptions() {
        return ignoredExceptions;
    }

    public static final class Builder {
        private Duration timeout = Duration.ofSeconds(10);
        private Duration pollingInterval = Duration.ofMillis(250);
        private Duration quietWindow = Duration.ofMillis(500);
        private boolean screenshotOnFailure = true;
        private boolean diagnosticsEnabled = true;
        private final List<Class<? extends Throwable>> ignoredExceptions = new ArrayList<>();

        private Builder() {
            ignoredExceptions.add(NoSuchElementException.class);
            ignoredExceptions.add(StaleElementReferenceException.class);
        }

        public Builder timeout(Duration timeout) {
            if (timeout == null || timeout.isZero() || timeout.isNegative()) {
                throw new IllegalArgumentException("timeout must be a positive duration");
            }
            this.timeout = timeout;
            return this;
        }

        public Builder timeoutSeconds(long seconds) {
            return timeout(Duration.ofSeconds(seconds));
        }

        public Builder pollingEvery(Duration pollingInterval) {
            if (pollingInterval == null || pollingInterval.isZero() || pollingInterval.isNegative()) {
                throw new IllegalArgumentException("pollingInterval must be a positive duration");
            }
            this.pollingInterval = pollingInterval;
            return this;
        }

        public Builder pollingMillis(long millis) {
            return pollingEvery(Duration.ofMillis(Math.max(50, millis)));
        }

        public Builder quietWindow(Duration quietWindow) {
            if (quietWindow == null || quietWindow.isNegative()) {
                throw new IllegalArgumentException("quietWindow must not be negative");
            }
            this.quietWindow = quietWindow;
            return this;
        }

        public Builder screenshotOnFailure(boolean screenshotOnFailure) {
            this.screenshotOnFailure = screenshotOnFailure;
            return this;
        }

        public Builder diagnosticsEnabled(boolean diagnosticsEnabled) {
            this.diagnosticsEnabled = diagnosticsEnabled;
            return this;
        }

        public Builder ignore(Class<? extends Throwable> exceptionType) {
            if (exceptionType != null && !ignoredExceptions.contains(exceptionType)) {
                ignoredExceptions.add(exceptionType);
            }
            return this;
        }

        @SafeVarargs
        public final Builder ignoreAll(Class<? extends Throwable>... exceptionTypes) {
            if (exceptionTypes != null) {
                for (Class<? extends Throwable> exceptionType : exceptionTypes) {
                    ignore(exceptionType);
                }
            }
            return this;
        }

        public QatraWaitOptions build() {
            return new QatraWaitOptions(this);
        }
    }
}
