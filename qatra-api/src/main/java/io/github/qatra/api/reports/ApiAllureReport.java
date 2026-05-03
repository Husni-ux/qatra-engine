package io.github.qatra.api.reports;

import io.github.qatra.core.logger.QatraLogger;
import io.qameta.allure.Allure;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Safe Allure wrapper for qatra-api.
 */
public final class ApiAllureReport {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private ApiAllureReport() {
    }

    public static void step(String name) {
        try {
            Allure.step(name);
        } catch (Throwable t) {
            LOG.debug("Allure API step skipped: {}", t.getMessage());
        }
    }

    public static void attachText(String name, String content) {
        try {
            Allure.addAttachment(name, "text/plain", content == null ? "" : content, ".txt");
        } catch (Throwable t) {
            LOG.debug("Allure API text attachment skipped: {}", t.getMessage());
        }
    }

    public static void attachJson(String name, String content) {
        try {
            Allure.addAttachment(name, "application/json", content == null ? "" : content, ".json");
        } catch (Throwable t) {
            LOG.debug("Allure API JSON attachment skipped: {}", t.getMessage());
        }
    }

    public static void attachThrowable(Throwable throwable) {
        if (throwable == null) {
            return;
        }
        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        attachText("API failure stack trace", writer.toString());
    }
}
