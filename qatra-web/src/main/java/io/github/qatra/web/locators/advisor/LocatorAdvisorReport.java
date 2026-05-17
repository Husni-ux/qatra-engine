package io.github.qatra.web.locators.advisor;

import io.github.qatra.web.locators.LocatorRiskLevel;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Advanced, proactive quality report for one locator or one QatraLocator chain.
 */
public final class LocatorAdvisorReport {

    private final String name;
    private final By primaryLocator;
    private final int score;
    private final LocatorRiskLevel riskLevel;
    private final List<LocatorQualityIssue> issues;
    private final List<String> strengths;
    private final List<String> recommendations;
    private final List<String> candidateNotes;
    private final String suggestedStableLocator;

    public LocatorAdvisorReport(String name,
                                By primaryLocator,
                                int score,
                                LocatorRiskLevel riskLevel,
                                List<LocatorQualityIssue> issues,
                                List<String> strengths,
                                List<String> recommendations,
                                List<String> candidateNotes,
                                String suggestedStableLocator) {
        this.name = name == null || name.isBlank() ? "Unnamed locator" : name;
        this.primaryLocator = primaryLocator;
        this.score = Math.max(0, Math.min(100, score));
        this.riskLevel = riskLevel == null ? LocatorRiskLevel.MEDIUM : riskLevel;
        this.issues = new ArrayList<>(issues == null ? List.of() : issues);
        this.strengths = new ArrayList<>(strengths == null ? List.of() : strengths);
        this.recommendations = deduplicate(recommendations == null ? List.of() : recommendations);
        this.candidateNotes = new ArrayList<>(candidateNotes == null ? List.of() : candidateNotes);
        this.suggestedStableLocator = suggestedStableLocator;
    }

    public String name() {
        return name;
    }

    public By primaryLocator() {
        return primaryLocator;
    }

    public int score() {
        return score;
    }

    public LocatorRiskLevel riskLevel() {
        return riskLevel;
    }

    public List<LocatorQualityIssue> issues() {
        return Collections.unmodifiableList(issues);
    }

    public List<String> strengths() {
        return Collections.unmodifiableList(strengths);
    }

    public List<String> recommendations() {
        return Collections.unmodifiableList(recommendations);
    }

    public List<String> candidateNotes() {
        return Collections.unmodifiableList(candidateNotes);
    }

    public String suggestedStableLocator() {
        return suggestedStableLocator;
    }

    public boolean passed(LocatorAdvisorOptions options) {
        LocatorAdvisorOptions effective = options == null ? LocatorAdvisorOptions.defaults() : options;
        if (effective.failOnCritical() && riskLevel == LocatorRiskLevel.CRITICAL) {
            return false;
        }
        return score >= effective.minimumScore();
    }

    public String summary() {
        return "Locator Advisor Report" + System.lineSeparator() +
                "Name: " + name + System.lineSeparator() +
                "Locator: " + primaryLocator + System.lineSeparator() +
                "Score: " + score + "/100" + System.lineSeparator() +
                "Risk: " + riskLevel + System.lineSeparator() +
                "Issues: " + issues.size() + System.lineSeparator() +
                "Recommendations: " + recommendations;
    }

    @Override
    public String toString() {
        return summary() + System.lineSeparator() +
                "Strengths: " + strengths + System.lineSeparator() +
                "Issues: " + issues + System.lineSeparator() +
                "Candidate notes: " + candidateNotes + System.lineSeparator() +
                "Suggested stable locator: " + suggestedStableLocator;
    }

    private static List<String> deduplicate(List<String> values) {
        Set<String> unique = new LinkedHashSet<>(values);
        return new ArrayList<>(unique);
    }
}
