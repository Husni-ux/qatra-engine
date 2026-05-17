package io.github.qatra.web.locators.healing;

import io.github.qatra.web.locators.LocatorCandidate;

/** Explainable decision for a healing candidate. */
public final class HealingDecision {
    private final LocatorCandidate candidate;
    private final HealingDecisionStatus status;
    private final HealingConfidenceScore confidence;
    private final HealingRiskLevel riskLevel;
    private final HealingEvidence evidence;
    private final String reason;

    public HealingDecision(LocatorCandidate candidate, HealingDecisionStatus status,
                           HealingConfidenceScore confidence, HealingRiskLevel riskLevel,
                           HealingEvidence evidence, String reason) {
        this.candidate = candidate;
        this.status = status;
        this.confidence = confidence;
        this.riskLevel = riskLevel;
        this.evidence = evidence;
        this.reason = reason;
    }

    public LocatorCandidate candidate() { return candidate; }
    public HealingDecisionStatus status() { return status; }
    public HealingConfidenceScore confidence() { return confidence; }
    public HealingRiskLevel riskLevel() { return riskLevel; }
    public HealingEvidence evidence() { return evidence; }
    public String reason() { return reason; }

    public boolean approved() {
        return status == HealingDecisionStatus.PRIMARY_FOUND
                || status == HealingDecisionStatus.AUTO_APPROVED
                || status == HealingDecisionStatus.APPROVED_BY_APPROVAL_FILE;
    }

    public String summary() {
        return status + " | confidence=" + confidence.value() + "% | risk=" + riskLevel + " | " + reason +
                " | text='" + safe(evidence.visibleText()) + "' role='" + safe(evidence.role()) + "' direction='" + safe(evidence.cssDirection()) + "'";
    }

    private static String safe(String value) {
        if (value == null) return "";
        return value.length() > 80 ? value.substring(0, 80) + "..." : value;
    }
}
