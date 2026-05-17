package io.github.qatra.web.locators.advisor;

import io.github.qatra.web.locators.QatraLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Fluent entry point for proactive locator quality analysis.
 */
public final class QatraLocatorAdvisor {

    private final WebDriver driver;
    private LocatorAdvisorOptions options = LocatorAdvisorOptions.defaults();

    private QatraLocatorAdvisor(WebDriver driver) {
        this.driver = driver;
    }

    public static QatraLocatorAdvisor of(WebDriver driver) {
        return new QatraLocatorAdvisor(driver);
    }

    public QatraLocatorAdvisor withOptions(LocatorAdvisorOptions options) {
        this.options = options == null ? LocatorAdvisorOptions.defaults() : options;
        return this;
    }

    public LocatorAdvisorReport analyze(By locator) {
        LocatorAdvisorReport report = ProactiveLocatorQualityAdvisor.analyze(locator);
        exportIfEnabled(report);
        return report;
    }

    public LocatorAdvisorReport analyze(String name, By locator) {
        LocatorAdvisorReport report = ProactiveLocatorQualityAdvisor.analyze(name, locator);
        exportIfEnabled(report);
        return report;
    }

    public LocatorAdvisorReport analyze(QatraLocator locator) {
        LocatorAdvisorReport report = ProactiveLocatorQualityAdvisor.analyze(locator);
        exportIfEnabled(report);
        return report;
    }

    public LocatorAdvisorReport requireQuality(By locator) {
        LocatorAdvisorReport report = analyze(locator);
        LocatorQualityGate.withOptions(options).require(report);
        return report;
    }

    public LocatorAdvisorReport requireQuality(QatraLocator locator) {
        LocatorAdvisorReport report = analyze(locator);
        LocatorQualityGate.withOptions(options).require(report);
        return report;
    }

    public WebDriver seleniumDriver() {
        return driver;
    }

    private void exportIfEnabled(LocatorAdvisorReport report) {
        if (options.exportReports()) {
            LocatorAdvisorReportExporter.export(report);
        }
    }
}
