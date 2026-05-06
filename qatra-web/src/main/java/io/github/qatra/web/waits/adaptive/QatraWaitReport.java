package io.github.qatra.web.waits.adaptive;

import io.github.qatra.web.waits.adaptive.diagnostics.WaitAttempt;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Diagnostic summary of all wait attempts.
 */
public final class QatraWaitReport {

    private final String context;
    private final Duration elapsed;
    private final List<WaitAttempt> attempts;
    private final String screenshotPath;

    public QatraWaitReport(String context, Duration elapsed, List<WaitAttempt> attempts, String screenshotPath) {
        this.context = context;
        this.elapsed = elapsed;
        this.attempts = List.copyOf(attempts == null ? List.of() : attempts);
        this.screenshotPath = screenshotPath;
    }

    public String context() {
        return context;
    }

    public Duration elapsed() {
        return elapsed;
    }

    public List<WaitAttempt> attempts() {
        return attempts;
    }

    public Optional<WaitAttempt> lastAttempt() {
        if (attempts.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(attempts.get(attempts.size() - 1));
    }

    public String screenshotPath() {
        return screenshotPath;
    }

    public String toDebugText() {
        StringBuilder builder = new StringBuilder();
        builder.append("QATRA Adaptive Wait Report\n");
        builder.append("Context: ").append(context).append('\n');
        builder.append("Elapsed: ").append(elapsed.toMillis()).append(" ms\n");
        builder.append("Attempts: ").append(attempts.size()).append('\n');
        lastAttempt().ifPresent(attempt -> builder.append("Last attempt:\n").append(attempt.toDebugText()).append('\n'));
        if (screenshotPath != null && !screenshotPath.isBlank()) {
            builder.append("Screenshot: ").append(screenshotPath).append('\n');
        }
        return builder.toString();
    }

    public static Builder builder(String context) {
        return new Builder(context);
    }

    public static final class Builder {
        private final String context;
        private Duration elapsed = Duration.ZERO;
        private final List<WaitAttempt> attempts = new ArrayList<>();
        private String screenshotPath;

        private Builder(String context) {
            this.context = context;
        }

        public Builder elapsed(Duration elapsed) {
            this.elapsed = elapsed == null ? Duration.ZERO : elapsed;
            return this;
        }

        public Builder addAttempt(WaitAttempt attempt) {
            if (attempt != null) {
                attempts.add(attempt);
            }
            return this;
        }

        public Builder screenshotPath(String screenshotPath) {
            this.screenshotPath = screenshotPath;
            return this;
        }

        public QatraWaitReport build() {
            return new QatraWaitReport(context, elapsed, attempts, screenshotPath);
        }
    }
}
