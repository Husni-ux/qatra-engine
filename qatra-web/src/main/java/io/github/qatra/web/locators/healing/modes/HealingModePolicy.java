package io.github.qatra.web.locators.healing.modes;

import io.github.qatra.web.locators.LocatorCandidate;
import io.github.qatra.web.locators.QatraLocator;
import io.github.qatra.web.locators.healing.*;

/**
 * Central policy that converts a scored candidate into a mode-aware decision.
 *
 * <p>This is the heart of QATRA's "do not hide bugs" strategy. Every fallback candidate must pass guardrails,
 * mode rules, confidence thresholds, risk thresholds, and optional human approval before it can be used.</p>
 */
public final class HealingModePolicy {
    private HealingModePolicy() {}

    public static HealingModePolicyResult decide(QatraLocator locator,
                                                 LocatorCandidate candidate,
                                                 HealingEvidence evidence,
                                                 HealingConfidenceScore score,
                                                 HealingRiskLevel risk,
                                                 QatraHealingOptions options) {
        if (candidate.primary()) {
            return HealingModePolicyResult.of(HealingDecisionStatus.PRIMARY_FOUND,
                    "Primary locator found; healing was not needed.");
        }

        if (options.mode() == HealingMode.OFF) {
            return HealingModePolicyResult.of(HealingDecisionStatus.DISABLED,
                    "Healing is disabled by configuration.");
        }

        HealingModePolicyResult guardrail = HealingBugGuardrail.evaluate(candidate, evidence, options);
        if (guardrail != null) {
            return guardrail;
        }

        if (options.mode() == HealingMode.REPORT_ONLY) {
            return HealingModePolicyResult.of(HealingDecisionStatus.REPORTED_ONLY,
                    "Candidate was evaluated for reporting only. The fallback will not be used.");
        }

        if (options.mode() == HealingMode.SUGGEST_ONLY) {
            return HealingModePolicyResult.of(HealingDecisionStatus.SUGGESTED_ONLY,
                    "Candidate is suggested only. Human review is required before updating test code.");
        }

        if (options.requireSemanticMatchForAutoHeal() && locator.hasSemanticHints() && !evidence.semanticMatch()) {
            return HealingModePolicyResult.of(HealingDecisionStatus.REJECTED,
                    "Candidate rejected: semantic hints were provided but no semantic signal matched.");
        }

        if (options.mode() == HealingMode.STRICT_APPROVAL) {
            if (score.value() < options.minimumConfidence()) {
                return HealingModePolicyResult.of(HealingDecisionStatus.REJECTED,
                        "Strict approval candidate rejected: confidence " + score.value() + "% is below " + options.minimumConfidence() + "%.");
            }
            if (risk.higherThan(options.maximumAutoHealRisk())) {
                return HealingModePolicyResult.of(HealingDecisionStatus.REJECTED,
                        "Strict approval candidate rejected: risk " + risk + " is higher than allowed " + options.maximumAutoHealRisk() + ".");
            }
            boolean approved = HealingApprovalStore.isApproved(locator, candidate, score, risk, evidence, options);
            return approved
                    ? HealingModePolicyResult.of(HealingDecisionStatus.APPROVED_BY_APPROVAL_FILE,
                            "Candidate approved by strict approval file: " + options.strictApprovalFile())
                    : HealingModePolicyResult.of(HealingDecisionStatus.PENDING_APPROVAL,
                            "Candidate requires human approval. Add it to " + options.strictApprovalFile() + " to allow STRICT_APPROVAL healing.");
        }

        int threshold = options.mode() == HealingMode.SAFE_AUTO_HEAL
                ? Math.max(options.minimumConfidence(), options.safeAutoHealMinimumConfidence())
                : options.minimumConfidence();

        if (score.value() < threshold) {
            return HealingModePolicyResult.of(HealingDecisionStatus.REJECTED,
                    "Candidate rejected: confidence " + score.value() + "% is below required threshold " + threshold + "% for " + options.mode() + ".");
        }

        if (risk.higherThan(options.maximumAutoHealRisk())) {
            return HealingModePolicyResult.of(HealingDecisionStatus.REJECTED,
                    "Candidate rejected: risk " + risk + " is higher than allowed " + options.maximumAutoHealRisk() + " for " + options.mode() + ".");
        }

        return HealingModePolicyResult.of(HealingDecisionStatus.AUTO_APPROVED,
                "Candidate accepted by " + options.mode() + " with score " + score.value() + "% and risk " + risk + ".");
    }
}
