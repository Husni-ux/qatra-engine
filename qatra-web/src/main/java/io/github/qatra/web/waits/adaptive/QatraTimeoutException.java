package io.github.qatra.web.waits.adaptive;

import org.openqa.selenium.TimeoutException;

/**
 * Timeout exception with QATRA diagnostic context.
 */
public class QatraTimeoutException extends TimeoutException {

    private final QatraWaitReport report;

    public QatraTimeoutException(String message, QatraWaitReport report, Throwable cause) {
        super(message + System.lineSeparator() + (report == null ? "" : report.toDebugText()), cause);
        this.report = report;
    }

    public QatraWaitReport report() {
        return report;
    }
}
