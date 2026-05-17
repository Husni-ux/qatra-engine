package io.github.qatra.web.locators;

import org.openqa.selenium.By;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Diagnostic report produced by the QATRA Self-Healing Locator Engine.
 */
public final class LocatorHealingReport {

    private final String name;
    private final By primaryLocator;
    private final Duration timeout;
    private final List<String> attempts = new ArrayList<>();
    private LocatorHealingStatus status = LocatorHealingStatus.NOT_FOUND;
    private By resolvedLocator;
    private String resolvedSource;
    private String recommendation;
    private Integer confidence;
    private String riskLevel;
    private String decisionSummary;
    private final List<String> rejectedCandidates = new ArrayList<>();

    public LocatorHealingReport(String name, By primaryLocator, Duration timeout) {
        this.name = name;
        this.primaryLocator = primaryLocator;
        this.timeout = timeout;
    }

    public void attempt(LocatorCandidate candidate, int matches, String details) {
        attempts.add("[" + candidate.source() + "] " + candidate.locator() + " -> matches=" + matches +
                (details == null || details.isBlank() ? "" : " | " + details));
    }

    public void resolved(LocatorCandidate candidate, LocatorHealingStatus status) {
        this.status = status;
        this.resolvedLocator = candidate.locator();
        this.resolvedSource = candidate.source();
    }

    public void recommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public void decisionDetails(int confidence, String riskLevel, String decisionSummary) {
        this.confidence = confidence;
        this.riskLevel = riskLevel;
        this.decisionSummary = decisionSummary;
    }

    public void rejected(LocatorCandidate candidate, String reason) {
        rejectedCandidates.add("[" + candidate.source() + "] " + candidate.locator() + " -> " + reason);
    }

    public String name() {
        return name;
    }

    public By primaryLocator() {
        return primaryLocator;
    }

    public Duration timeout() {
        return timeout;
    }

    public LocatorHealingStatus status() {
        return status;
    }

    public By resolvedLocator() {
        return resolvedLocator;
    }

    public String resolvedSource() {
        return resolvedSource;
    }

    public String recommendation() {
        return recommendation;
    }

    public Integer confidence() {
        return confidence;
    }

    public String riskLevel() {
        return riskLevel;
    }

    public String decisionSummary() {
        return decisionSummary;
    }

    public List<String> rejectedCandidates() {
        return Collections.unmodifiableList(rejectedCandidates);
    }

    public List<String> attempts() {
        return Collections.unmodifiableList(attempts);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("QATRA Self-Healing Locator Report").append(System.lineSeparator());
        builder.append("Name: ").append(name == null ? "N/A" : name).append(System.lineSeparator());
        builder.append("Primary: ").append(primaryLocator).append(System.lineSeparator());
        builder.append("Status: ").append(status).append(System.lineSeparator());
        builder.append("Resolved locator: ").append(resolvedLocator == null ? "N/A" : resolvedLocator).append(System.lineSeparator());
        builder.append("Resolved source: ").append(resolvedSource == null ? "N/A" : resolvedSource).append(System.lineSeparator());
        if (confidence != null) {
            builder.append("Confidence: ").append(confidence).append("%").append(System.lineSeparator());
        }
        if (riskLevel != null) {
            builder.append("Risk: ").append(riskLevel).append(System.lineSeparator());
        }
        if (decisionSummary != null && !decisionSummary.isBlank()) {
            builder.append("Decision: ").append(decisionSummary).append(System.lineSeparator());
        }
        builder.append("Timeout: ").append(timeout).append(System.lineSeparator());
        builder.append("Attempts:").append(System.lineSeparator());
        for (String attempt : attempts) {
            builder.append("- ").append(attempt).append(System.lineSeparator());
        }
        if (!rejectedCandidates.isEmpty()) {
            builder.append("Rejected candidates:").append(System.lineSeparator());
            for (String rejected : rejectedCandidates) {
                builder.append("- ").append(rejected).append(System.lineSeparator());
            }
        }
        if (recommendation != null && !recommendation.isBlank()) {
            builder.append("Recommendation: ").append(recommendation).append(System.lineSeparator());
        }
        return builder.toString();
    }
}
