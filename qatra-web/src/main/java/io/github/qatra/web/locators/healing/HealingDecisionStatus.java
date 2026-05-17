package io.github.qatra.web.locators.healing;

/** Final decision for a locator healing candidate. */
public enum HealingDecisionStatus {
    PRIMARY_FOUND,
    AUTO_APPROVED,
    APPROVED_BY_APPROVAL_FILE,
    SUGGESTED_ONLY,
    REPORTED_ONLY,
    PENDING_APPROVAL,
    BLOCKED_BY_GUARDRAIL,
    REJECTED,
    DISABLED
}
