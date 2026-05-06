package io.github.qatra.web.waits.adaptive.diagnostics;

import io.github.qatra.web.waits.adaptive.QatraWaitReport;

/**
 * Text formatter for adaptive wait reports.
 */
public final class WaitDebugReport {

    private WaitDebugReport() {
    }

    public static String format(QatraWaitReport report) {
        return report == null ? "No QATRA wait report available" : report.toDebugText();
    }
}
