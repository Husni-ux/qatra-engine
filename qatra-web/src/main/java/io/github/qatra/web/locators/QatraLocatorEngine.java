package io.github.qatra.web.locators;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.locators.healing.HealingDecision;
import io.github.qatra.web.locators.healing.QatraHealingEngine;
import io.github.qatra.web.locators.healing.QatraHealingOptions;
import io.github.qatra.web.locators.healing.reports.HealingReportExporter;
import io.github.qatra.web.locators.advisor.LocatorAdvisorReport;
import io.github.qatra.web.locators.advisor.LocatorAdvisorReportExporter;
import io.github.qatra.web.locators.advisor.ProactiveLocatorQualityAdvisor;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.List;

/**
 * Self-healing locator resolver.
 *
 * <p>It tries the primary locator first, then falls back to configured alternatives.
 * The engine does not guess silently; it records each attempt and exposes a report so
 * teams can improve weak locators instead of hiding locator problems forever.</p>
 */
public final class QatraLocatorEngine {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private QatraLocatorEngine() {
    }

    public static LocatorResolution resolve(WebDriver driver, QatraLocator locator, Duration timeout) {
        return resolve(driver, locator, timeout, QatraHealingOptions.defaults());
    }

    public static LocatorResolution resolve(WebDriver driver, QatraLocator locator, Duration timeout, QatraHealingOptions options) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver cannot be null");
        }
        if (locator == null) {
            throw new IllegalArgumentException("QatraLocator cannot be null");
        }
        QatraHealingOptions effectiveOptions = options == null ? QatraHealingOptions.defaults() : options;
        Duration effectiveTimeout = timeout == null ? effectiveOptions.timeout() : timeout;
        LocatorHealingReport report = new LocatorHealingReport(locator.name(), locator.primaryLocator(), effectiveTimeout);
        exportProactiveQualityReport(locator);

        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(effectiveTimeout)
                .pollingEvery(Duration.ofMillis(200))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);

        try {
            LocatorResolution resolution = wait.until(currentDriver -> tryResolveOnce(currentDriver, locator, report, effectiveOptions));
            if (resolution != null && resolution.healed() && effectiveOptions.exportReport()) {
                HealingReportExporter.export(locator, report);
            }
            return resolution;
        } catch (TimeoutException timeoutException) {
            LocatorQualityReport quality = locator.qualityReport();
            report.recommendation("Locator was not resolved. Primary locator quality: " + quality.score() +
                    "/100 (" + quality.riskLevel() + "). Suggestions: " + quality.recommendations());
            if (effectiveOptions.exportReport()) {
                HealingReportExporter.export(locator, report);
            }
            throw new NoSuchElementException("QATRA could not resolve locator after self-healing attempts.\n" + report, timeoutException);
        }
    }


    private static void exportProactiveQualityReport(QatraLocator locator) {
        try {
            LocatorAdvisorReport advisorReport = ProactiveLocatorQualityAdvisor.analyze(locator);
            if (advisorReport.riskLevel() == LocatorRiskLevel.HIGH || advisorReport.riskLevel() == LocatorRiskLevel.CRITICAL) {
                LocatorAdvisorReportExporter.export(advisorReport);
            }
        } catch (RuntimeException exception) {
            LOG.warn("QATRA locator quality advisor could not analyze locator '{}': {}", locator.name(), exception.getMessage());
        }
    }

    private static LocatorResolution tryResolveOnce(WebDriver driver, QatraLocator locator, LocatorHealingReport report, QatraHealingOptions options) {
        List<LocatorCandidate> candidates = locator.candidates();
        for (LocatorCandidate candidate : candidates) {
            List<WebElement> matches;
            try {
                matches = driver.findElements(candidate.locator());
            } catch (InvalidSelectorException invalidSelectorException) {
                report.attempt(candidate, 0, "invalid selector: " + invalidSelectorException.getMessage());
                continue;
            }

            WebElement selected = firstUsable(matches);
            if (selected == null) {
                report.attempt(candidate, matches.size(), "no displayed/enabled preferred element yet");
                continue;
            }

            HealingDecision decision = QatraHealingEngine.decide(driver, locator, candidate, selected, matches.size(), options);
            report.attempt(candidate, matches.size(), decision.summary());
            report.decisionDetails(decision.confidence().value(), decision.riskLevel().name(), decision.reason());

            if (!decision.approved()) {
                report.rejected(candidate, decision.summary());
                continue;
            }

            LocatorHealingStatus status = candidate.primary()
                    ? LocatorHealingStatus.FOUND_PRIMARY
                    : LocatorHealingStatus.HEALED_WITH_FALLBACK;
            report.resolved(candidate, status);
            if (status == LocatorHealingStatus.HEALED_WITH_FALLBACK) {
                LOG.warn("QATRA locator healed using {} -> {} | confidence={} risk={}",
                        candidate.source(), candidate.locator(), decision.confidence().value(), decision.riskLevel());
            } else {
                LOG.info("QATRA locator resolved using primary locator: {}", candidate.locator());
            }
            return new LocatorResolution(selected, candidate.locator(), candidate, status, report);
        }
        return null;
    }

    private static WebElement firstUsable(List<WebElement> elements) {
        if (elements == null || elements.isEmpty()) {
            return null;
        }
        WebElement firstExisting = null;
        for (WebElement element : elements) {
            if (firstExisting == null) {
                firstExisting = element;
            }
            try {
                if (element.isDisplayed()) {
                    return element;
                }
            } catch (StaleElementReferenceException ignored) {
                // Try the next candidate/match.
            }
        }
        return firstExisting;
    }
}
