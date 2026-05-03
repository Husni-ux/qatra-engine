package io.github.qatra.web.rtl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of evaluating an RTL scan against a quality gate.
 */
public final class RtlQualityGateResult {

    private final boolean enabled;
    private final int score;
    private final int minScore;
    private final long errorCount;
    private final long warningCount;
    private final long infoCount;
    private final int issueCount;
    private final int maxErrors;
    private final int maxWarnings;
    private final boolean passed;
    private final List<String> violations;
    private final String generatedAt;

    public RtlQualityGateResult(
            boolean enabled,
            int score,
            int minScore,
            long errorCount,
            long warningCount,
            long infoCount,
            int issueCount,
            int maxErrors,
            int maxWarnings,
            boolean passed,
            List<String> violations
    ) {
        this.enabled = enabled;
        this.score = clamp(score);
        this.minScore = clamp(minScore);
        this.errorCount = Math.max(0, errorCount);
        this.warningCount = Math.max(0, warningCount);
        this.infoCount = Math.max(0, infoCount);
        this.issueCount = Math.max(0, issueCount);
        this.maxErrors = Math.max(0, maxErrors);
        this.maxWarnings = Math.max(0, maxWarnings);
        this.passed = passed;
        this.violations = Collections.unmodifiableList(new ArrayList<>(violations == null ? List.of() : violations));
        this.generatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public boolean isEnabled() { return enabled; }

    public int score() { return score; }

    public int minScore() { return minScore; }

    public long errorCount() { return errorCount; }

    public long warningCount() { return warningCount; }

    public long infoCount() { return infoCount; }

    public int issueCount() { return issueCount; }

    public int maxErrors() { return maxErrors; }

    public int maxWarnings() { return maxWarnings; }

    public boolean passed() { return passed; }

    public boolean failed() { return !passed; }

    public List<String> violations() { return violations; }

    public String generatedAt() { return generatedAt; }

    public String status() {
        if (!enabled) {
            return "DISABLED";
        }
        return passed ? "PASSED" : "FAILED";
    }

    public String summary() {
        return "QATRA RTL Quality Gate: status=" + status()
                + ", score=" + score
                + ", minScore=" + minScore
                + ", issues=" + issueCount
                + ", errors=" + errorCount + "/" + maxErrors
                + ", warnings=" + warningCount + "/" + maxWarnings
                + ", info=" + infoCount;
    }

    public String toReport() {
        StringBuilder report = new StringBuilder();
        report.append("Generated at: ").append(generatedAt).append(System.lineSeparator());
        report.append(summary()).append(System.lineSeparator());
        report.append(System.lineSeparator());
        report.append("Quality Gate Rules:").append(System.lineSeparator());
        report.append("- Minimum score: ").append(minScore).append(System.lineSeparator());
        report.append("- Maximum errors: ").append(maxErrors).append(System.lineSeparator());
        report.append("- Maximum warnings: ").append(maxWarnings).append(System.lineSeparator());
        report.append(System.lineSeparator());

        if (!enabled) {
            report.append("Quality gate is disabled by configuration.").append(System.lineSeparator());
            return report.toString();
        }

        if (violations.isEmpty()) {
            report.append("No quality gate violations detected.").append(System.lineSeparator());
            return report.toString();
        }

        report.append("Violations:").append(System.lineSeparator());
        for (int i = 0; i < violations.size(); i++) {
            report.append(i + 1).append(". ").append(violations.get(i)).append(System.lineSeparator());
        }
        return report.toString();
    }

    public static RtlQualityGateResult disabled() {
        return new RtlQualityGateResult(false, 100, 0, 0, 0, 0, 0, 0, 0, true, List.of());
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
