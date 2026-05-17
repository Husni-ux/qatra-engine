package io.github.qatra.web.locators.advisor;

import io.github.qatra.web.locators.LocatorRiskLevel;

/**
 * One finding produced by the proactive locator advisor.
 */
public final class LocatorQualityIssue {

    private final LocatorRiskLevel severity;
    private final String code;
    private final String message;
    private final int impact;
    private final String recommendation;

    public LocatorQualityIssue(LocatorRiskLevel severity, String code, String message, int impact, String recommendation) {
        this.severity = severity == null ? LocatorRiskLevel.MEDIUM : severity;
        this.code = code == null ? "GENERAL" : code;
        this.message = message == null ? "Locator quality issue detected." : message;
        this.impact = impact;
        this.recommendation = recommendation == null ? "Review the locator and prefer stable semantic attributes." : recommendation;
    }

    public LocatorRiskLevel severity() {
        return severity;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    /**
     * Negative score impact for this issue.
     */
    public int impact() {
        return impact;
    }

    public String recommendation() {
        return recommendation;
    }

    @Override
    public String toString() {
        return "[" + severity + "][" + code + "] " + message + " | impact=" + impact +
                " | recommendation=" + recommendation;
    }
}
