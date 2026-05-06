package io.github.qatra.web.waits.adaptive.diagnostics;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures a screenshot on adaptive wait timeout when supported by the driver.
 */
public final class WaitScreenshotCapture {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private WaitScreenshotCapture() {
    }

    public static String capture(WebDriver driver, String name) {
        if (!(driver instanceof TakesScreenshot screenshotDriver)) {
            return "";
        }
        try {
            Path dir = Path.of("target", "qatra-reports", "waits", "screenshots");
            Files.createDirectories(dir);
            String safeName = sanitize(name) + "_" + LocalDateTime.now().format(FORMATTER) + ".png";
            Path target = dir.resolve(safeName);
            byte[] data = screenshotDriver.getScreenshotAs(OutputType.BYTES);
            Files.write(target, data);
            return target.toAbsolutePath().toString();
        } catch (IOException | RuntimeException e) {
            return "screenshot-capture-failed: " + e.getMessage();
        }
    }

    private static String sanitize(String name) {
        if (name == null || name.isBlank()) {
            return "qatra_wait_timeout";
        }
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
