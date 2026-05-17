package io.github.qatra.web.locators.healing.patches;

/** Human-review lifecycle for generated locator patch suggestions. */
public enum HealingPatchReviewStatus {
    PENDING_REVIEW,
    APPROVED,
    REJECTED,
    NEEDS_STRONGER_LOCATOR,
    APPLIED_MANUALLY
}
