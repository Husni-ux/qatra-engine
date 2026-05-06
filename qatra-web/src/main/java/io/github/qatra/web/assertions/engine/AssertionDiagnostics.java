package io.github.qatra.web.assertions.engine;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import io.github.qatra.web.reports.DiagnosticsManager;
import io.github.qatra.web.reports.ScreenshotManager;
import io.github.qatra.web.rtl.RtlEngine;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Captures rich web diagnostics when a QATRA assertion fails.
 *
 * <p>The goal is to make assertion failures useful for real QA work, not only
 * technical stack traces. A failed assertion should explain what was expected,
 * what was actually rendered, where it happened, and what evidence was saved.</p>
 */
public final class AssertionDiagnostics {

    private static final QatraLogger LOG = QatraLogger.getInstance();
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    private static final int MAX_NAME_LENGTH = 90;

    private AssertionDiagnostics() {
    }

    public static String capture(WebDriver driver,
                                 String assertionName,
                                 String locator,
                                 String expected,
                                 String actual,
                                 WebElement element) {
        String safeBaseName = shortName("assertion_" + assertionName + "_" + locator);

        Optional<Path> screenshot = ScreenshotManager.capture(driver, safeBaseName);
        Optional<Path> pageSource = DiagnosticsManager.capturePageSource(driver, safeBaseName);
        Optional<Path> browserLogs = DiagnosticsManager.captureBrowserConsoleLogs(driver, safeBaseName);

        String report = buildReport(driver, assertionName, locator, expected, actual, element,
                screenshot, pageSource, browserLogs);

        Optional<Path> savedReport = saveReport(safeBaseName, report);
        savedReport.ifPresent(path -> AllureReport.attachFile(
                "QATRA assertion diagnostic report - " + path.getFileName(),
                path,
                "text/plain",
                ".txt"
        ));
        AllureReport.attachText("QATRA assertion diagnostics", report);

        return report;
    }

    private static String buildReport(WebDriver driver,
                                      String assertionName,
                                      String locator,
                                      String expected,
                                      String actual,
                                      WebElement element,
                                      Optional<Path> screenshot,
                                      Optional<Path> pageSource,
                                      Optional<Path> browserLogs) {
        StringBuilder report = new StringBuilder();
        report.append(System.lineSeparator())
                .append("QATRA Assertion Diagnostics").append(System.lineSeparator())
                .append("----------------------------------------").append(System.lineSeparator())
                .append("Assertion: ").append(safe(assertionName)).append(System.lineSeparator())
                .append("Locator  : ").append(safe(locator)).append(System.lineSeparator())
                .append("Expected : ").append(safe(expected)).append(System.lineSeparator())
                .append("Actual   : ").append(safe(actual)).append(System.lineSeparator())
                .append("Detected issue: ").append(detectIssue(assertionName, expected, actual, driver, element)).append(System.lineSeparator())
                .append(System.lineSeparator())
                .append("Browser context").append(System.lineSeparator())
                .append("- Current URL: ").append(safeGet(() -> driver == null ? "" : driver.getCurrentUrl())).append(System.lineSeparator())
                .append("- Page title : ").append(safeGet(() -> driver == null ? "" : driver.getTitle())).append(System.lineSeparator())
                .append(System.lineSeparator())
                .append("Element context").append(System.lineSeparator())
                .append(elementContext(driver, element))
                .append(System.lineSeparator())
                .append("Evidence files").append(System.lineSeparator())
                .append("- Screenshot   : ").append(pathOrUnavailable(screenshot)).append(System.lineSeparator())
                .append("- Page source  : ").append(pathOrUnavailable(pageSource)).append(System.lineSeparator())
                .append("- Browser logs : ").append(pathOrUnavailable(browserLogs)).append(System.lineSeparator());
        return report.toString();
    }

    private static String elementContext(WebDriver driver, WebElement element) {
        if (element == null) {
            return "- Element snapshot: unavailable; element could not be resolved." + System.lineSeparator();
        }

        StringBuilder context = new StringBuilder();
        context.append("- Tag        : ").append(safeGet(element::getTagName)).append(System.lineSeparator());
        context.append("- ID         : ").append(safeGet(() -> element.getAttribute("id"))).append(System.lineSeparator());
        context.append("- Class      : ").append(safeGet(() -> element.getAttribute("class"))).append(System.lineSeparator());
        context.append("- Text       : ").append(RtlEngine.summarize(AssertionEvidence.readableContent(element))).append(System.lineSeparator());
        context.append("- Direction  : ").append(safeGet(() -> AssertionEvidence.direction(driver, element))).append(System.lineSeparator());
        context.append("- CSS dir    : ").append(safeGet(() -> element.getCssValue("direction"))).append(System.lineSeparator());
        context.append("- Text align : ").append(safeGet(() -> element.getCssValue("text-align"))).append(System.lineSeparator());
        context.append("- Displayed  : ").append(safeGetBoolean(element::isDisplayed)).append(System.lineSeparator());
        context.append("- Enabled    : ").append(safeGetBoolean(element::isEnabled)).append(System.lineSeparator());
        context.append("- Viewport   : ").append(AssertionEvidence.isInsideViewport(driver, element)).append(System.lineSeparator());
        context.append("- Not covered: ").append(AssertionEvidence.isNotCovered(driver, element)).append(System.lineSeparator());

        try {
            Rectangle rect = element.getRect();
            context.append("- Rectangle  : x=").append(rect.getX())
                    .append(", y=").append(rect.getY())
                    .append(", width=").append(rect.getWidth())
                    .append(", height=").append(rect.getHeight())
                    .append(System.lineSeparator());
        } catch (Throwable ignored) {
            context.append("- Rectangle  : unavailable").append(System.lineSeparator());
        }

        return context.toString();
    }

    private static String detectIssue(String assertionName,
                                      String expected,
                                      String actual,
                                      WebDriver driver,
                                      WebElement element) {
        String combined = (safe(assertionName) + " " + safe(expected) + " " + safe(actual)).toLowerCase();
        String actualText = safe(actual);

        if (actualText.contains("�") || actualText.contains("\uFFFD")) {
            return "Unicode replacement character detected; content may be corrupted.";
        }
        if (actualText.matches("(?s).*(?:[ÃÂØÙÛÐ][\\s\\S]){2,}.*")) {
            return "Mojibake / encoding corruption detected in rendered text.";
        }
        if (actualText.matches(".*\\?{2,}.*")) {
            return "Repeated question marks detected; possible unsupported font or encoding loss.";
        }
        if (combined.contains("rtl") || combined.contains("direction")) {
            String direction = safeGet(() -> element == null ? "" : AssertionEvidence.direction(driver, element));
            return "RTL/direction mismatch. Effective direction: " + direction;
        }
        if (combined.contains("viewport") || combined.contains("covered") || combined.contains("displayed")) {
            return "Visual state mismatch; check visibility, viewport position, overlay coverage, and CSS display state.";
        }
        if (combined.contains("text")) {
            return "Text mismatch; rendered content did not satisfy the expected text assertion.";
        }
        if (combined.contains("attribute") || combined.contains("value")) {
            return "Attribute/value mismatch; DOM attribute value differs from the expected value.";
        }
        if (element == null) {
            return "Element could not be resolved; locator may be wrong or the DOM did not render the element in time.";
        }
        return "Assertion condition was not satisfied. Review expected, actual, element snapshot, and saved evidence.";
    }

    private static Optional<Path> saveReport(String safeBaseName, String report) {
        Path directory = Path.of("target", "qatra-reports", "assertions");
        Path destination = directory.resolve(shortName(safeBaseName) + "_" + LocalDateTime.now().format(TIMESTAMP) + ".txt");
        try {
            Files.createDirectories(directory);
            Files.writeString(destination, report == null ? "" : report, StandardCharsets.UTF_8);
            LOG.info("QATRA assertion diagnostic report saved: {}", destination.toAbsolutePath());
            return Optional.of(destination);
        } catch (IOException e) {
            LOG.warn("Could not save QATRA assertion diagnostic report: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private static String pathOrUnavailable(Optional<Path> path) {
        return path.map(value -> value.toAbsolutePath().toString()).orElse("unavailable");
    }

    private static String shortName(String value) {
        String sanitized = safe(value).replaceAll("[^a-zA-Z0-9._-]", "_").replaceAll("_+", "_");
        if (sanitized.isBlank()) {
            sanitized = "qatra_assertion";
        }
        if (sanitized.length() > MAX_NAME_LENGTH) {
            sanitized = sanitized.substring(0, MAX_NAME_LENGTH);
        }
        return sanitized;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static String safeGet(ValueSupplier supplier) {
        try {
            String value = supplier.get();
            return value == null ? "" : value;
        } catch (Throwable throwable) {
            return "unavailable: " + throwable.getClass().getSimpleName();
        }
    }

    private static String safeGetBoolean(BooleanSupplier supplier) {
        try {
            return String.valueOf(supplier.get());
        } catch (Throwable throwable) {
            return "unavailable: " + throwable.getClass().getSimpleName();
        }
    }

    @FunctionalInterface
    private interface ValueSupplier {
        String get();
    }

    @FunctionalInterface
    private interface BooleanSupplier {
        boolean get();
    }
}
