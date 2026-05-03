package io.github.qatra.web.rtl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Holds the output of a QATRA Arabic/RTL page scan.
 */
public final class RtlScanResult {

    private final List<RtlIssue> issues;
    private final int elementsScanned;
    private final int arabicElementsFound;
    private final String generatedAt;

    public RtlScanResult(List<RtlIssue> issues, int elementsScanned, int arabicElementsFound) {
        this.issues = Collections.unmodifiableList(new ArrayList<>(issues == null ? List.of() : issues));
        this.elementsScanned = elementsScanned;
        this.arabicElementsFound = arabicElementsFound;
        this.generatedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public List<RtlIssue> issues() {
        return issues;
    }

    public int elementsScanned() {
        return elementsScanned;
    }

    public int arabicElementsFound() {
        return arabicElementsFound;
    }

    public String generatedAt() {
        return generatedAt;
    }

    public int issueCount() {
        return issues.size();
    }

    public boolean hasIssues() {
        return !issues.isEmpty();
    }

    public boolean hasErrors() {
        return issues.stream().anyMatch(RtlIssue::isError);
    }

    public boolean hasIssueType(RtlIssueType type) {
        return issues.stream().anyMatch(issue -> issue.type() == type);
    }

    public long errorCount() {
        return issues.stream().filter(RtlIssue::isError).count();
    }

    public long warningCount() {
        return issues.stream().filter(issue -> issue.severity() == RtlIssueSeverity.WARNING).count();
    }

    public long infoCount() {
        return issues.stream().filter(issue -> issue.severity() == RtlIssueSeverity.INFO).count();
    }

    public long countByType(RtlIssueType type) {
        return issues.stream().filter(issue -> issue.type() == type).count();
    }

    public long countBySeverity(RtlIssueSeverity severity) {
        return issues.stream().filter(issue -> issue.severity() == severity).count();
    }

    public Map<RtlIssueType, Long> countsByType() {
        Map<RtlIssueType, Long> counts = new EnumMap<>(RtlIssueType.class);
        for (RtlIssueType type : RtlIssueType.values()) {
            counts.put(type, countByType(type));
        }
        return Collections.unmodifiableMap(counts);
    }

    public List<RtlIssue> issuesByType(RtlIssueType type) {
        return issues.stream()
                .filter(issue -> issue.type() == type)
                .collect(Collectors.toUnmodifiableList());
    }

    public List<RtlIssue> issuesBySeverity(RtlIssueSeverity severity) {
        return issues.stream()
                .filter(issue -> issue.severity() == severity)
                .collect(Collectors.toUnmodifiableList());
    }

    public String summary() {
        return "QATRA RTL Scan Summary: scanned=" + elementsScanned
                + ", arabicElements=" + arabicElementsFound
                + ", issues=" + issueCount()
                + ", errors=" + errorCount()
                + ", warnings=" + warningCount()
                + ", info=" + infoCount()
                + ", direction=" + countByType(RtlIssueType.DIRECTION)
                + ", encoding=" + countByType(RtlIssueType.ENCODING)
                + ", placeholder=" + countByType(RtlIssueType.PLACEHOLDER)
                + ", digits=" + countByType(RtlIssueType.DIGITS)
                + ", mixedDirection=" + countByType(RtlIssueType.MIXED_DIRECTION)
                + ", alignment=" + countByType(RtlIssueType.ALIGNMENT);
    }

    public String toReport() {
        StringBuilder report = new StringBuilder();
        report.append("Generated at: ").append(generatedAt).append(System.lineSeparator());
        report.append(summary()).append(System.lineSeparator());
        report.append(System.lineSeparator()).append("Issue counts by type:").append(System.lineSeparator());
        for (RtlIssueType type : RtlIssueType.values()) {
            long count = countByType(type);
            if (count > 0) {
                report.append("- ").append(type).append(": ").append(count).append(System.lineSeparator());
            }
        }

        if (issues.isEmpty()) {
            report.append(System.lineSeparator())
                    .append("No Arabic/RTL issues detected.")
                    .append(System.lineSeparator());
            return report.toString();
        }

        for (RtlIssueType type : RtlIssueType.values()) {
            List<RtlIssue> typedIssues = issuesByType(type);
            if (typedIssues.isEmpty()) {
                continue;
            }
            report.append(System.lineSeparator())
                    .append(type)
                    .append(" issues:")
                    .append(System.lineSeparator());
            for (int i = 0; i < typedIssues.size(); i++) {
                report.append(i + 1)
                        .append(". ")
                        .append(typedIssues.get(i).toReportLine())
                        .append(System.lineSeparator());
            }
        }

        return report.toString();
    }

    public static RtlScanResult empty() {
        return new RtlScanResult(List.of(), 0, 0);
    }
}
