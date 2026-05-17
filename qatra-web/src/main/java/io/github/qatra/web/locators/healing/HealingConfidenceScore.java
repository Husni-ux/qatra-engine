package io.github.qatra.web.locators.healing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Confidence score and explainable scoring factors for a healing candidate. */
public final class HealingConfidenceScore {
    private int value;
    private final List<String> positiveFactors = new ArrayList<>();
    private final List<String> negativeFactors = new ArrayList<>();

    public HealingConfidenceScore(int initial) {
        this.value = initial;
    }

    public HealingConfidenceScore add(int points, String reason) {
        value += points;
        if (reason != null && !reason.isBlank()) positiveFactors.add("+" + points + " " + reason);
        return this;
    }

    public HealingConfidenceScore subtract(int points, String reason) {
        value -= points;
        if (reason != null && !reason.isBlank()) negativeFactors.add("-" + points + " " + reason);
        return this;
    }

    public int value() {
        return Math.max(0, Math.min(100, value));
    }

    public List<String> positiveFactors() { return Collections.unmodifiableList(positiveFactors); }
    public List<String> negativeFactors() { return Collections.unmodifiableList(negativeFactors); }

    public String summary() {
        return "confidence=" + value() + "% positives=" + positiveFactors + " negatives=" + negativeFactors;
    }
}
