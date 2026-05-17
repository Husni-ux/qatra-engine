package io.github.qatra.web.locators.healing.modes;

import io.github.qatra.web.locators.healing.HealingDecisionStatus;

/** Result of applying the selected healing mode and safety guardrails. */
public final class HealingModePolicyResult {
    private final HealingDecisionStatus status;
    private final String reason;

    private HealingModePolicyResult(HealingDecisionStatus status, String reason) {
        this.status = status;
        this.reason = reason;
    }

    public static HealingModePolicyResult of(HealingDecisionStatus status, String reason) {
        return new HealingModePolicyResult(status, reason);
    }

    public HealingDecisionStatus status() {
        return status;
    }

    public String reason() {
        return reason;
    }
}
