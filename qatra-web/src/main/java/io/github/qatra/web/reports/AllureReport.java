package io.github.qatra.web.reports;

import io.github.qatra.core.logger.QatraLogger;
import io.qameta.allure.Allure;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Small safe wrapper around Allure.
 *
 * <p>QATRA should never fail a test because reporting failed, so every method
 * in this class is defensive and logs reporting errors only as debug messages.</p>
 */
public final class AllureReport {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private AllureReport() {
    }

    private static boolean hasRunningTestCase() {
        try {
            return Allure.getLifecycle().getCurrentTestCase().isPresent();
        } catch (Throwable ignored) {
            return false;
        }
    }

    /**
     * Add a lightweight Allure step.
     */
    public static void step(String name) {
        try {
            Allure.step(name);
        } catch (Throwable t) {
            LOG.debug("Allure step skipped: {}", t.getMessage());
        }
    }

    /**
     * Attach plain text to the current Allure test result.
     */
    public static void attachText(String name, String content) {
        if (!hasRunningTestCase()) {
            LOG.debug("Allure text attachment skipped because no test case is running: {}", name);
            return;
        }
        try {
            Allure.addAttachment(name, "text/plain", content == null ? "" : content, ".txt");
        } catch (Throwable t) {
            LOG.debug("Allure text attachment skipped: {}", t.getMessage());
        }
    }

    /**
     * Attach a screenshot PNG to the current Allure test result.
     */
    public static void attachScreenshot(Path screenshotPath) {
        if (screenshotPath == null || !Files.exists(screenshotPath)) {
            return;
        }
        if (!hasRunningTestCase()) {
            LOG.debug("Allure screenshot attachment skipped because no test case is running: {}", screenshotPath);
            return;
        }

        try (InputStream inputStream = Files.newInputStream(screenshotPath)) {
            Allure.addAttachment(
                    "Screenshot - " + screenshotPath.getFileName(),
                    "image/png",
                    inputStream,
                    ".png"
            );
        } catch (Throwable t) {
            LOG.debug("Allure screenshot attachment skipped: {}", t.getMessage());
        }
    }

    /**
     * Attach any file to the current Allure test result.
     */
    public static void attachFile(String name, Path filePath, String mimeType, String extension) {
        if (filePath == null || !Files.exists(filePath)) {
            return;
        }
        if (!hasRunningTestCase()) {
            LOG.debug("Allure file attachment skipped because no test case is running: {}", filePath);
            return;
        }

        try (InputStream inputStream = Files.newInputStream(filePath)) {
            Allure.addAttachment(
                    name,
                    mimeType == null ? "application/octet-stream" : mimeType,
                    inputStream,
                    extension == null ? "" : extension
            );
        } catch (Throwable t) {
            LOG.debug("Allure file attachment skipped: {}", t.getMessage());
        }
    }

    /**
     * Attach throwable details in readable text form.
     */
    public static void attachThrowable(Throwable throwable) {
        if (throwable == null) {
            return;
        }

        StringWriter writer = new StringWriter();
        throwable.printStackTrace(new PrintWriter(writer));
        attachText("Failure stack trace", writer.toString());
    }
}
