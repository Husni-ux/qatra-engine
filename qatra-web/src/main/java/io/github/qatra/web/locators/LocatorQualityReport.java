package io.github.qatra.web.locators;

import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Readable quality report for a Selenium locator.
 */
public final class LocatorQualityReport {

    private final By locator;
    private final int score;
    private final LocatorRiskLevel riskLevel;
    private final List<String> reasons;
    private final List<String> recommendations;

    public LocatorQualityReport(By locator, int score, LocatorRiskLevel riskLevel,
                                List<String> reasons, List<String> recommendations) {
        this.locator = locator;
        this.score = Math.max(0, Math.min(100, score));
        this.riskLevel = riskLevel;
        this.reasons = new ArrayList<>(reasons);
        this.recommendations = new ArrayList<>(recommendations);
    }

    public By locator() {
        return locator;
    }

    public int score() {
        return score;
    }

    public LocatorRiskLevel riskLevel() {
        return riskLevel;
    }

    public List<String> reasons() {
        return Collections.unmodifiableList(reasons);
    }

    public List<String> recommendations() {
        return Collections.unmodifiableList(recommendations);
    }

    @Override
    public String toString() {
        return "Locator quality score: " + score + "/100" + System.lineSeparator() +
                "Risk: " + riskLevel + System.lineSeparator() +
                "Locator: " + locator + System.lineSeparator() +
                "Reasons: " + reasons + System.lineSeparator() +
                "Recommendations: " + recommendations;
    }
}
