package io.github.qatra.web.locators.healing;

import io.github.qatra.web.locators.LocatorCandidate;
import io.github.qatra.web.locators.QatraLocator;
import io.github.qatra.web.locators.healing.modes.HealingModeAuditExporter;
import io.github.qatra.web.locators.healing.modes.HealingModePolicy;
import io.github.qatra.web.locators.healing.modes.HealingModePolicyResult;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Locale;

/**
 * Explainable decision engine for QATRA self-healing locators.
 *
 * <p>The engine does not blindly heal. It scores candidates, assigns risk, applies the selected
 * healing mode, validates bug-safety guardrails, and writes an audit trail.</p>
 */
public final class QatraHealingEngine {
    private QatraHealingEngine() {}

    public static HealingDecision decide(WebDriver driver, QatraLocator locator, LocatorCandidate candidate,
                                         WebElement element, int matchCount, QatraHealingOptions options) {
        QatraHealingOptions effectiveOptions = options == null ? QatraHealingOptions.defaults() : options;
        HealingEvidence evidence = HealingEvidence.collect(driver, element, candidate, locator, matchCount);
        HealingConfidenceScore score = score(locator, candidate, evidence);
        HealingRiskLevel risk = HealingRiskAnalyzer.analyze(candidate, evidence, score);
        HealingModePolicyResult policy = HealingModePolicy.decide(locator, candidate, evidence, score, risk, effectiveOptions);
        HealingDecision decision = new HealingDecision(candidate, policy.status(), score, risk, evidence, policy.reason());
        HealingModeAuditExporter.export(locator, candidate, decision, effectiveOptions);
        return decision;
    }

    private static HealingConfidenceScore score(QatraLocator locator, LocatorCandidate candidate, HealingEvidence evidence) {
        if (candidate.primary()) {
            return new HealingConfidenceScore(100).add(0, "Primary locator resolved successfully");
        }
        String source = candidate.source().toLowerCase(Locale.ROOT);
        HealingConfidenceScore score = new HealingConfidenceScore(35);
        if (source.contains("data-testid") || source.contains("data-test") || source.contains("data-qa")) {
            score.add(35, "Stable test attribute fallback");
        }
        if (source.contains("aria")) {
            score.add(25, "Accessible aria fallback");
        }
        if (source.contains("accessibility")) {
            score.add(28, "Accessibility-tree fallback");
        }
        if (source.contains("role + accessible-name")) {
            score.add(35, "Role and accessible-name matched as a stable accessibility signal");
        }
        if (source.contains("label text") || source.contains("placeholder")) {
            score.add(20, "Label/placeholder accessibility fallback");
        }
        if (source.contains("text")) {
            score.add(18, "Visible text fallback");
        }
        if (source.contains("arabic semantic action")) {
            score.add(30, "Arabic semantic action fallback");
        }
        if (evidence.expectedRoleMatch()) score.add(15, "Expected role matched");
        if (evidence.expectedAccessibleNameMatch()) score.add(30, "Expected accessible name matched");
        if (evidence.accessibility().hasAccessibleName()) score.add(8, "Candidate exposes accessible name");
        if (evidence.expectedTextMatch()) score.add(18, "Expected visible text matched");
        if (evidence.expectedArabicTextMatch()) score.add(25, "Expected Arabic text matched");
        if (evidence.expectedActionMatch()) score.add(32, "Expected Arabic business action matched");
        if (evidence.arabicSemanticMatch()) score.add(12, "Arabic semantic similarity matched");
        if (evidence.displayed()) score.add(8, "Element is displayed"); else score.subtract(40, "Element is not displayed");
        if (evidence.enabled()) score.add(5, "Element is enabled"); else score.subtract(20, "Element is disabled");
        if (evidence.matchCount() == 1) score.add(10, "Candidate uniquely matched one element");
        if (evidence.matchCount() > 1) score.subtract(Math.min(30, evidence.matchCount() * 5), "Multiple elements matched");
        if (locator.expectedAction() != null && !locator.expectedAction().isBlank() && !evidence.expectedActionMatch()) {
            score.subtract(20, "Expected Arabic business action did not match");
        }
        if (locator.hasSemanticHints() && !evidence.semanticMatch()) {
            score.subtract(25, "No semantic hint matched");
        }
        return score;
    }
}
