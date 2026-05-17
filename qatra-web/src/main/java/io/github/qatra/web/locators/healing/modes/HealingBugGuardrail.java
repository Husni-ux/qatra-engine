package io.github.qatra.web.locators.healing.modes;

import io.github.qatra.web.locators.LocatorCandidate;
import io.github.qatra.web.locators.healing.HealingDecisionStatus;
import io.github.qatra.web.locators.healing.HealingEvidence;
import io.github.qatra.web.locators.healing.QatraHealingOptions;

/** Safety checks that prevent self-healing from hiding real product bugs. */
public final class HealingBugGuardrail {
    private HealingBugGuardrail() {}

    public static HealingModePolicyResult evaluate(LocatorCandidate candidate,
                                                   HealingEvidence evidence,
                                                   QatraHealingOptions options) {
        if (candidate.primary()) {
            return null;
        }
        if (options.blockHiddenCandidates() && !evidence.displayed()) {
            return HealingModePolicyResult.of(
                    HealingDecisionStatus.BLOCKED_BY_GUARDRAIL,
                    "Candidate blocked: element is not displayed. This may be a real UI state issue, not a locator issue."
            );
        }
        if (options.blockDisabledCandidates() && !evidence.enabled()) {
            return HealingModePolicyResult.of(
                    HealingDecisionStatus.BLOCKED_BY_GUARDRAIL,
                    "Candidate blocked: element is disabled. Healing must not hide disabled-button business rules."
            );
        }
        if (options.failOnAmbiguousCandidates() && evidence.matchCount() > options.maximumMatchesForAutoHeal()) {
            return HealingModePolicyResult.of(
                    HealingDecisionStatus.BLOCKED_BY_GUARDRAIL,
                    "Candidate blocked: locator matched " + evidence.matchCount() + " elements. Ambiguous healing can click the wrong element."
            );
        }
        return null;
    }
}
