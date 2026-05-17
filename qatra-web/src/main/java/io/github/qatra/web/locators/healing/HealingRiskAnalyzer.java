package io.github.qatra.web.locators.healing;

import io.github.qatra.web.locators.LocatorCandidate;

/** Converts candidate evidence and confidence into an operational healing risk. */
public final class HealingRiskAnalyzer {
    private HealingRiskAnalyzer() {}

    public static HealingRiskLevel analyze(LocatorCandidate candidate, HealingEvidence evidence, HealingConfidenceScore confidence) {
        if (!evidence.displayed()) return HealingRiskLevel.CRITICAL;
        if (evidence.matchCount() > 3) return HealingRiskLevel.HIGH;
        if (!evidence.enabled()) return HealingRiskLevel.HIGH;
        if (confidence.value() >= 85 && evidence.matchCount() == 1) return HealingRiskLevel.LOW;
        if (confidence.value() >= 65) return HealingRiskLevel.MEDIUM;
        return HealingRiskLevel.HIGH;
    }
}
