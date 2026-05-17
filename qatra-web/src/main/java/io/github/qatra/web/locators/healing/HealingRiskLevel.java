package io.github.qatra.web.locators.healing;

/** Risk level of applying a healing candidate. */
public enum HealingRiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    public boolean higherThan(HealingRiskLevel other) {
        return this.ordinal() > other.ordinal();
    }
}
