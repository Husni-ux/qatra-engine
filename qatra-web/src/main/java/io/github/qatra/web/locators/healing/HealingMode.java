package io.github.qatra.web.locators.healing;

/**
 * Controls how QATRA handles fallback locator candidates.
 *
 * <p>The goal is to prevent self-healing from hiding real bugs. Teams can start with
 * REPORT_ONLY or SUGGEST_ONLY, then move to SAFE_AUTO_HEAL once confidence thresholds
 * and guardrails are trusted.</p>
 */
public enum HealingMode {
    /** Healing is completely disabled. Only the primary locator is allowed. */
    OFF,

    /** QATRA evaluates candidates and writes reports, but never uses fallback locators. */
    REPORT_ONLY,

    /** QATRA suggests a fallback locator, but the test still fails until a human reviews it. */
    SUGGEST_ONLY,

    /** QATRA auto-heals only when confidence is high, risk is low, and guardrails pass. */
    SAFE_AUTO_HEAL,

    /** QATRA auto-heals using configured thresholds. Recommended only after pilot validation. */
    AUTO_HEAL,

    /** QATRA auto-heals only candidates explicitly approved in an approval file. */
    STRICT_APPROVAL
}
