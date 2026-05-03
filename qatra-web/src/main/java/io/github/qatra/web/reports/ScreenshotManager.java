package io.github.qatra.web.reports;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.logger.QatraLogger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Handles screenshot capture for QATRA web tests.
 *
 * <p>Default output directory:</p>
 * <pre>target/qatra-reports/screenshots</pre>
 */
public final class ScreenshotManager {

    private static final QatraLogger LOG = QatraLogger.getInstance();
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotManager() {
    }

    /**
     * Capture a screenshot using the configured screenshot directory.
     *
     * @param driver active Selenium driver
     * @param name logical screenshot name, usually the test method name
     * @return screenshot path when capture succeeds
     */
    public static Optional<Path> capture(WebDriver driver, String name) {
        if (driver == null) {
            LOG.warn("Screenshot skipped because Selenium driver is null");
            return Optional.empty();
        }

        if (!(driver instanceof TakesScreenshot takesScreenshot)) {
            LOG.warn("Screenshot skipped because driver does not support TakesScreenshot");
            return Optional.empty();
        }

        String directory = QatraConfig.getInstance()
                .getProperty(QatraProperties.SCREENSHOTS_DIR, "target/qatra-reports/screenshots");

        Path outputDirectory = Path.of(directory);
        String fileName = sanitize(name) + "_" + LocalDateTime.now().format(TIMESTAMP) + ".png";
        Path destination = outputDirectory.resolve(fileName);

        try {
            Files.createDirectories(outputDirectory);
            Path source = takesScreenshot.getScreenshotAs(OutputType.FILE).toPath();
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            LOG.screenshot(destination.toAbsolutePath().toString());
            AllureReport.attachScreenshot(destination);
            return Optional.of(destination);
        } catch (WebDriverException | IOException e) {
            LOG.error("Could not capture screenshot", e);
            return Optional.empty();
        }
    }

    /**
     * Capture a failure screenshot when qatra.screenshots.on.failure=true.
     */
    public static Optional<Path> captureOnFailure(WebDriver driver, String testName) {
        boolean enabled = QatraConfig.getInstance()
                .getBooleanProperty(QatraProperties.SCREENSHOT_ON_FAILURE, true);

        if (!enabled) {
            LOG.debug("Screenshot on failure is disabled");
            return Optional.empty();
        }

        return capture(driver, testName + "_FAILED");
    }

    private static String sanitize(String value) {
        if (value == null || value.isBlank()) {
            return "screenshot";
        }
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
