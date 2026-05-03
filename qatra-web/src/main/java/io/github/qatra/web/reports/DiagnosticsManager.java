package io.github.qatra.web.reports;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.logger.QatraLogger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Captures failure evidence that helps testers understand why a test failed.
 *
 * <p>Evidence is saved under {@code target/qatra-reports} by default and is also
 * attached to Allure when Allure is available.</p>
 */
public final class DiagnosticsManager {

    private static final QatraLogger LOG = QatraLogger.getInstance();
    private static final QatraConfig CONFIG = QatraConfig.getInstance();
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private DiagnosticsManager() {
    }

    /**
     * Capture the full failure evidence package.
     */
    public static void captureFailureEvidence(WebDriver driver, String testName, Throwable throwable) {
        boolean enabled = CONFIG.getBooleanProperty(QatraProperties.EVIDENCE_ON_FAILURE, true);
        if (!enabled) {
            LOG.debug("Failure evidence capture is disabled");
            return;
        }

        String safeName = sanitize(testName);
        attachBrowserState(driver, "Browser state on failure");
        AllureReport.attachThrowable(throwable);
        ScreenshotManager.captureOnFailure(driver, safeName);

        if (CONFIG.getBooleanProperty(QatraProperties.PAGE_SOURCE_ON_FAILURE, true)) {
            capturePageSource(driver, safeName + "_FAILED");
        }

        if (CONFIG.getBooleanProperty(QatraProperties.BROWSER_LOGS_ON_FAILURE, true)) {
            captureBrowserConsoleLogs(driver, safeName + "_FAILED");
        }
    }

    /**
     * Attach current browser title and URL as text evidence.
     */
    public static void attachBrowserState(WebDriver driver, String attachmentName) {
        if (driver == null) {
            AllureReport.attachText(attachmentName, "Selenium driver is null.");
            return;
        }

        StringBuilder state = new StringBuilder();
        try {
            state.append("Current URL: ").append(driver.getCurrentUrl()).append(System.lineSeparator());
        } catch (Throwable t) {
            state.append("Current URL: <unavailable: ").append(t.getMessage()).append(">").append(System.lineSeparator());
        }

        try {
            state.append("Page title : ").append(driver.getTitle()).append(System.lineSeparator());
        } catch (Throwable t) {
            state.append("Page title : <unavailable: ").append(t.getMessage()).append(">").append(System.lineSeparator());
        }

        try {
            state.append("Window     : ").append(driver.getWindowHandle()).append(System.lineSeparator());
        } catch (Throwable t) {
            state.append("Window     : <unavailable: ").append(t.getMessage()).append(">").append(System.lineSeparator());
        }

        AllureReport.attachText(attachmentName, state.toString());
    }

    /**
     * Save and attach current page source.
     */
    public static Optional<Path> capturePageSource(WebDriver driver, String name) {
        if (driver == null) {
            LOG.warn("Page source capture skipped because Selenium driver is null");
            return Optional.empty();
        }

        String directory = CONFIG.getProperty(QatraProperties.PAGE_SOURCE_DIR, "target/qatra-reports/page-source");
        Path outputDirectory = Path.of(directory);
        Path destination = outputDirectory.resolve(sanitize(name) + "_" + timestamp() + ".html");

        try {
            Files.createDirectories(outputDirectory);
            String pageSource = driver.getPageSource();
            Files.writeString(destination, pageSource == null ? "" : pageSource, StandardCharsets.UTF_8);
            LOG.info("Page source saved: {}", destination.toAbsolutePath());
            AllureReport.attachFile("Page source - " + destination.getFileName(), destination, "text/html", ".html");
            return Optional.of(destination);
        } catch (WebDriverException | IOException e) {
            LOG.warn("Could not capture page source: {}", e.getMessage());
            AllureReport.attachText("Page source capture failed", e.toString());
            return Optional.empty();
        }
    }

    /**
     * Save and attach browser console logs when supported by the browser.
     */
    public static Optional<Path> captureBrowserConsoleLogs(WebDriver driver, String name) {
        if (driver == null) {
            LOG.warn("Browser console log capture skipped because Selenium driver is null");
            return Optional.empty();
        }

        String directory = CONFIG.getProperty(QatraProperties.BROWSER_LOGS_DIR, "target/qatra-reports/browser-logs");
        Path outputDirectory = Path.of(directory);
        Path destination = outputDirectory.resolve(sanitize(name) + "_" + timestamp() + ".log");

        try {
            Files.createDirectories(outputDirectory);
            LogEntries entries = driver.manage().logs().get(LogType.BROWSER);
            StringBuilder builder = new StringBuilder();

            if (entries == null || entries.getAll().isEmpty()) {
                builder.append("No browser console logs were captured.").append(System.lineSeparator());
            } else {
                for (LogEntry entry : entries) {
                    builder.append(entry.getLevel())
                            .append(" | ")
                            .append(entry.getTimestamp())
                            .append(" | ")
                            .append(entry.getMessage())
                            .append(System.lineSeparator());
                }
            }

            Files.writeString(destination, builder.toString(), StandardCharsets.UTF_8);
            LOG.info("Browser console logs saved: {}", destination.toAbsolutePath());
            AllureReport.attachFile("Browser console logs - " + destination.getFileName(), destination, "text/plain", ".log");
            return Optional.of(destination);
        } catch (Throwable e) {
            String message = "Browser console logs are not available for this driver/browser: " + e;
            LOG.warn(message);
            AllureReport.attachText("Browser console logs capture skipped", message);
            return Optional.empty();
        }
    }

    private static String timestamp() {
        return LocalDateTime.now().format(TIMESTAMP);
    }

    private static String sanitize(String value) {
        if (value == null || value.isBlank()) {
            return "qatra_evidence";
        }
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
