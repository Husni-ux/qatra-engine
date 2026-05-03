package io.github.qatra.web.rtl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result of comparing the current RTL scan against a saved baseline.
 *
 * <p>The comparison is fingerprint-based. A fingerprint is generated from the
 * stable parts of an issue: severity, issue type, locator, and message. This
 * keeps the comparison useful even when dynamic actual values slightly change.</p>
 */
public final class RtlBaselineComparisonResult {

    private final List<RtlIssue> newIssues;
    private final List<RtlIssue> existingIssues;
    private final List<String> resolvedIssueFingerprints;
    private final int baselineIssueCount;
    private final int currentIssueCount;
    private final String baselinePath;
    private final String generatedAt;

    public RtlBaselineComparisonResult(
            List<RtlIssue> newIssues,
            List<RtlIssue> existingIssues,
            List<String> resolvedIssueFingerprints,
            int baselineIssueCount,
            int currentIssueCount,
            String baselinePath
    ) {
        this.newIssues = Collections.unmodifiableList(new ArrayList<>(newIssues == null ? List.of() : newIssues));
        this.existingIssues = Collections.unmodifiableList(new ArrayList<>(existingIssues == null ? List.of() : existingIssues));
        this.resolvedIssueFingerprints = Collections.unmodifiableList(new ArrayList<>(resolvedIssueFingerprints == null ? List.of() : resolvedIssueFingerprints));
        this.baselineIssueCount = baselineIssueCount;
        this.currentIssueCount = currentIssueCount;
        this.baselinePath = baselinePath == null ? "" : baselinePath;
        this.generatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public List<RtlIssue> newIssues() {
        return newIssues;
    }

    public List<RtlIssue> existingIssues() {
        return existingIssues;
    }

    public List<String> resolvedIssueFingerprints() {
        return resolvedIssueFingerprints;
    }

    public int baselineIssueCount() {
        return baselineIssueCount;
    }

    public int currentIssueCount() {
        return currentIssueCount;
    }

    public String baselinePath() {
        return baselinePath;
    }

    public String generatedAt() {
        return generatedAt;
    }

    public int newIssueCount() {
        return newIssues.size();
    }

    public int existingIssueCount() {
        return existingIssues.size();
    }

    public int resolvedIssueCount() {
        return resolvedIssueFingerprints.size();
    }

    public boolean hasNewIssues() {
        return !newIssues.isEmpty();
    }

    public boolean hasResolvedIssues() {
        return !resolvedIssueFingerprints.isEmpty();
    }

    public boolean hasExistingIssues() {
        return !existingIssues.isEmpty();
    }

    public String summary() {
        return "QATRA RTL Baseline Comparison: baselineIssues=" + baselineIssueCount
                + ", currentIssues=" + currentIssueCount
                + ", newIssues=" + newIssueCount()
                + ", existingIssues=" + existingIssueCount()
                + ", resolvedIssues=" + resolvedIssueCount();
    }

    public String toReport() {
        StringBuilder report = new StringBuilder();
        report.append("Generated at: ").append(generatedAt).append(System.lineSeparator());
        report.append("Baseline path: ").append(baselinePath).append(System.lineSeparator());
        report.append(summary()).append(System.lineSeparator());

        report.append(System.lineSeparator()).append("New RTL issues:").append(System.lineSeparator());
        if (newIssues.isEmpty()) {
            report.append("- None").append(System.lineSeparator());
        } else {
            for (int i = 0; i < newIssues.size(); i++) {
                report.append(i + 1).append(". ").append(newIssues.get(i).toReportLine()).append(System.lineSeparator());
            }
        }

        report.append(System.lineSeparator()).append("Existing RTL issues:").append(System.lineSeparator());
        if (existingIssues.isEmpty()) {
            report.append("- None").append(System.lineSeparator());
        } else {
            for (int i = 0; i < existingIssues.size(); i++) {
                report.append(i + 1).append(". ").append(existingIssues.get(i).toReportLine()).append(System.lineSeparator());
            }
        }

        report.append(System.lineSeparator()).append("Resolved RTL issue fingerprints:").append(System.lineSeparator());
        if (resolvedIssueFingerprints.isEmpty()) {
            report.append("- None").append(System.lineSeparator());
        } else {
            for (int i = 0; i < resolvedIssueFingerprints.size(); i++) {
                report.append(i + 1).append(". ").append(resolvedIssueFingerprints.get(i)).append(System.lineSeparator());
            }
        }

        return report.toString();
    }

    public static RtlBaselineComparisonResult empty(String baselinePath) {
        return new RtlBaselineComparisonResult(List.of(), List.of(), List.of(), 0, 0, baselinePath);
    }
}
