package io.github.qatra.web.rtl;

import java.util.Objects;

/**
 * Represents a single Arabic/RTL quality issue detected on a page or element.
 */
public final class RtlIssue {

    private final RtlIssueSeverity severity;
    private final RtlIssueType type;
    private final String locator;
    private final String message;
    private final String actualValue;
    private final String recommendation;

    public RtlIssue(
            RtlIssueSeverity severity,
            String locator,
            String message,
            String actualValue,
            String recommendation
    ) {
        this(severity, RtlIssueType.SCAN, locator, message, actualValue, recommendation);
    }

    public RtlIssue(
            RtlIssueSeverity severity,
            RtlIssueType type,
            String locator,
            String message,
            String actualValue,
            String recommendation
    ) {
        this.severity = severity == null ? RtlIssueSeverity.WARNING : severity;
        this.type = type == null ? RtlIssueType.SCAN : type;
        this.locator = safe(locator);
        this.message = safe(message);
        this.actualValue = safe(actualValue);
        this.recommendation = safe(recommendation);
    }

    public RtlIssueSeverity severity() {
        return severity;
    }

    public RtlIssueType type() {
        return type;
    }

    public String locator() {
        return locator;
    }

    public String message() {
        return message;
    }

    public String actualValue() {
        return actualValue;
    }

    public String recommendation() {
        return recommendation;
    }

    public boolean isError() {
        return severity == RtlIssueSeverity.ERROR;
    }

    public String toReportLine() {
        return "[" + severity + "][" + type + "] " + locator + " - " + message
                + (actualValue.isBlank() ? "" : " | actual: " + actualValue)
                + (recommendation.isBlank() ? "" : " | recommendation: " + recommendation);
    }

    @Override
    public String toString() {
        return toReportLine();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof RtlIssue issue)) return false;
        return severity == issue.severity
                && type == issue.type
                && Objects.equals(locator, issue.locator)
                && Objects.equals(message, issue.message)
                && Objects.equals(actualValue, issue.actualValue)
                && Objects.equals(recommendation, issue.recommendation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(severity, type, locator, message, actualValue, recommendation);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
