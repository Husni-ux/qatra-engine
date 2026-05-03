package io.github.qatra.web.rtl;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.nio.file.Path;
import java.util.List;

/**
 * Fluent Arabic/RTL actions and checks.
 *
 * <pre>
 * driver.rtl()
 *       .assertPageDirectionIsRTL()
 *       .scanPage()
 *       .report()
 *       .failOnIssues();
 * </pre>
 */
public class RtlActions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private RtlScanConfig scanConfig = RtlScanConfig.fromConfig();
    private RtlScanResult lastResult = RtlScanResult.empty();
    private RtlBaselineComparisonResult lastBaselineComparison = RtlBaselineComparisonResult.empty(scanConfig.baselinePath());
    private RtlQualityGateResult lastQualityGateResult = RtlQualityGateResult.disabled();

    public RtlActions(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Assert that the body element is rendered in RTL direction.
     */
    public RtlActions assertPageDirectionIsRTL() {
        LOG.rtl("Assert page direction is RTL");
        AllureReport.step("Assert page direction is RTL");

        WebElement body = driver.findElement(By.tagName("body"));
        String direction = RtlEngine.effectiveDirection(driver, body);
        if (!"rtl".equalsIgnoreCase(direction)) {
            fail("Page direction is not RTL. Expected: rtl, Actual: " + direction);
        }

        LOG.rtl("✓ Page direction is RTL");
        return this;
    }

    /**
     * Assert that the page does not contain common Arabic encoding issues.
     */
    public RtlActions assertNoBrokenArabicCharacters() {
        LOG.rtl("Assert page has no broken Arabic characters");
        AllureReport.step("Assert page has no broken Arabic characters");

        String pageText = RtlEngine.collectPageText(driver);
        if (RtlEngine.hasBrokenArabicEncoding(pageText)) {
            fail("Possible broken Arabic encoding detected on page text: " + RtlEngine.summarize(pageText));
        }

        LOG.rtl("✓ No broken Arabic characters detected on page");
        return this;
    }

    /**
     * Assert that the page contains at least one Arabic letter.
     */
    public RtlActions assertArabicTextExists() {
        LOG.rtl("Assert Arabic text exists on page");
        AllureReport.step("Assert Arabic text exists on page");

        String pageText = RtlEngine.collectPageText(driver);
        if (!RtlEngine.containsArabicText(pageText)) {
            fail("Arabic text was not found on the current page. Collected text sample: " + RtlEngine.summarize(pageText));
        }

        LOG.rtl("✓ Arabic text exists on page");
        return this;
    }

    /**
     * Override the scanner configuration for the next scan.
     */
    public RtlActions withConfig(RtlScanConfig config) {
        this.scanConfig = config == null ? RtlScanConfig.fromConfig() : config;
        LOG.rtl("Use RTL scan config: {}", this.scanConfig.describe());
        return this;
    }

    /**
     * Reload the scanner configuration from qatra.properties/system properties.
     */
    public RtlActions reloadConfig() {
        this.scanConfig = RtlScanConfig.fromConfig();
        LOG.rtl("Reload RTL scan config: {}", this.scanConfig.describe());
        return this;
    }

    /**
     * Run a full Arabic/RTL page scan and keep the result for report/fail methods.
     */
    public RtlActions scanPage() {
        LOG.rtl("Run QATRA RTL page scan with config: {}", scanConfig.describe());
        AllureReport.step("Run QATRA RTL page scan");
        lastResult = RtlEngine.scanPage(driver, scanConfig);
        LOG.rtl(lastResult.summary());
        return this;
    }

    /**
     * Attach the latest RTL scan report to Allure, logs, and standalone report files.
     */
    public RtlActions report() {
        String report = lastResult.toReport();
        LOG.rtl(report);

        if (scanConfig.isReportAttachEnabled()) {
            AllureReport.attachText("QATRA RTL Scan Report", report);
        }

        exportReport();
        return this;
    }

    /**
     * Export the latest RTL scan result as standalone TXT/JSON files according to configuration.
     */
    public RtlActions exportReport() {
        List<Path> exportedFiles = RtlReportExporter.export(lastResult, scanConfig);
        for (Path exportedFile : exportedFiles) {
            LOG.rtl("RTL report exported: {}", exportedFile.toAbsolutePath());
            if (scanConfig.isReportAttachEnabled()) {
                String fileName = exportedFile.getFileName().toString();
                String extension;
                String mimeType;
                if (fileName.endsWith(".json")) {
                    extension = ".json";
                    mimeType = "application/json";
                } else if (fileName.endsWith(".html")) {
                    extension = ".html";
                    mimeType = "text/html";
                } else {
                    extension = ".txt";
                    mimeType = "text/plain";
                }
                AllureReport.attachFile("QATRA RTL Report - " + fileName, exportedFile, mimeType, extension);
            }
        }
        return this;
    }


    /**
     * Evaluate the latest RTL scan result against the configured quality gate.
     */
    public RtlActions qualityGate() {
        lastQualityGateResult = RtlQualityGate.evaluate(lastResult, scanConfig);
        String report = lastQualityGateResult.toReport();
        LOG.rtl(report);

        if (scanConfig.isReportAttachEnabled()) {
            AllureReport.attachText("QATRA RTL Quality Gate", report);
        }

        List<Path> exportedFiles = RtlQualityGate.export(lastQualityGateResult, scanConfig);
        for (Path exportedFile : exportedFiles) {
            if (scanConfig.isReportAttachEnabled()) {
                String fileName = exportedFile.getFileName().toString();
                String extension;
                String mimeType;
                if (fileName.endsWith(".json")) {
                    extension = ".json";
                    mimeType = "application/json";
                } else if (fileName.endsWith(".html")) {
                    extension = ".html";
                    mimeType = "text/html";
                } else {
                    extension = ".txt";
                    mimeType = "text/plain";
                }
                AllureReport.attachFile("QATRA RTL Quality Gate - " + fileName, exportedFile, mimeType, extension);
            }
        }

        return this;
    }

    /**
     * Fail the test when the latest RTL quality gate result is failed.
     */
    public RtlActions failOnQualityGateFailure() {
        if (lastQualityGateResult.failed()) {
            fail("RTL quality gate failed:\n" + lastQualityGateResult.toReport());
        }
        return this;
    }

    /**
     * Fail according to qatra.rtl.quality-gate.fail-on-failure.
     */
    public RtlActions failOnConfiguredQualityGate() {
        if (scanConfig.shouldFailOnQualityGateFailure() && lastQualityGateResult.failed()) {
            fail("RTL quality gate failed based on qatra.rtl.quality-gate.fail-on-failure=true:\n" + lastQualityGateResult.toReport());
        }
        return this;
    }

    /**
     * Return the latest RTL quality gate evaluation result.
     */
    public RtlQualityGateResult qualityGateResult() {
        return lastQualityGateResult;
    }

    /**
     * Save the latest RTL scan result as the current baseline.
     */
    public RtlActions saveBaseline() {
        Path baselinePath = RtlBaselineManager.saveBaseline(lastResult, scanConfig);
        String message = "QATRA RTL baseline saved: " + baselinePath.toAbsolutePath();
        LOG.rtl(message);
        if (scanConfig.isReportAttachEnabled()) {
            AllureReport.attachText("QATRA RTL Baseline Saved", message);
        }
        return this;
    }

    /**
     * Compare the latest RTL scan result with the configured baseline file.
     */
    public RtlActions compareWithBaseline() {
        if (!scanConfig.isBaselineEnabled()) {
            LOG.rtl("RTL baseline comparison is disabled by configuration.");
            lastBaselineComparison = RtlBaselineComparisonResult.empty(scanConfig.baselinePath());
            return this;
        }

        lastBaselineComparison = RtlBaselineManager.compare(lastResult, scanConfig);
        String report = lastBaselineComparison.toReport();
        LOG.rtl(report);

        if (scanConfig.isReportAttachEnabled()) {
            AllureReport.attachText("QATRA RTL Baseline Comparison", report);
        }

        List<Path> exportedFiles = RtlBaselineManager.exportComparison(lastBaselineComparison, scanConfig);
        for (Path exportedFile : exportedFiles) {
            if (scanConfig.isReportAttachEnabled()) {
                String fileName = exportedFile.getFileName().toString();
                String extension;
                String mimeType;
                if (fileName.endsWith(".json")) {
                    extension = ".json";
                    mimeType = "application/json";
                } else if (fileName.endsWith(".html")) {
                    extension = ".html";
                    mimeType = "text/html";
                } else {
                    extension = ".txt";
                    mimeType = "text/plain";
                }
                AllureReport.attachFile("QATRA RTL Baseline - " + fileName, exportedFile, mimeType, extension);
            }
        }

        if (scanConfig.isBaselineUpdateEnabled()) {
            saveBaseline();
        }

        return this;
    }

    /**
     * Fail the test if the current scan introduces new RTL issues compared with the baseline.
     */
    public RtlActions failOnNewBaselineIssues() {
        if (lastBaselineComparison.hasNewIssues()) {
            fail("New RTL issues detected compared with baseline:\n" + lastBaselineComparison.toReport());
        }
        return this;
    }

    /**
     * Fail according to qatra.rtl.baseline.fail-on-new-issues.
     */
    public RtlActions failOnConfiguredBaselineRegression() {
        if (scanConfig.shouldFailOnNewBaselineIssues() && lastBaselineComparison.hasNewIssues()) {
            fail("RTL baseline regression detected based on qatra.rtl.baseline.fail-on-new-issues=true:\n" + lastBaselineComparison.toReport());
        }
        return this;
    }

    /**
     * Return the latest RTL baseline comparison result.
     */
    public RtlBaselineComparisonResult baselineComparison() {
        return lastBaselineComparison;
    }

    /**
     * Fail the test if the latest scan has any issue, warning or error.
     */
    public RtlActions failOnIssues() {
        if (lastResult.hasIssues()) {
            fail("RTL scan detected issues:\n" + lastResult.toReport());
        }
        return this;
    }


    /**
     * Fail the test if the latest scan has WARNING or ERROR severity issues.
     * INFO findings are treated as advisory and will not fail the test.
     */
    public RtlActions failOnWarningsAndErrors() {
        if (lastResult.errorCount() > 0 || lastResult.warningCount() > 0) {
            fail("RTL scan detected warnings or errors:\n" + lastResult.toReport());
        }
        return this;
    }

    /**
     * Fail the test only if the latest scan has ERROR severity issues.
     */
    public RtlActions failOnErrors() {
        if (lastResult.hasErrors()) {
            fail("RTL scan detected errors:\n" + lastResult.toReport());
        }
        return this;
    }

    /**
     * Fail according to qatra.rtl.fail-on.
     * Supported values: none, errors, warnings, issues.
     */
    public RtlActions failOnConfiguredLevel() {
        if (scanConfig.shouldFail(lastResult)) {
            fail("RTL scan failed based on qatra.rtl.fail-on='" + scanConfig.failOn() + "':\n" + lastResult.toReport());
        }
        return this;
    }

    /**
     * Return the latest scan result for advanced custom assertions.
     */
    public RtlScanResult result() {
        return lastResult;
    }

    /**
     * Return the active scan configuration.
     */
    public RtlScanConfig config() {
        return scanConfig;
    }

    private void fail(String message) {
        LOG.assertionFailed(message);
        AllureReport.attachText("RTL assertion failure", message);
        Assert.fail(message);
    }
}
