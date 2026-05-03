package io.github.qatra.web.rtl;

import io.github.qatra.core.logger.QatraLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates RTL baselines and compares current RTL scan results against them.
 */
public final class RtlBaselineManager {

    private static final QatraLogger LOG = QatraLogger.getInstance();
    private static final Pattern FINGERPRINT_PATTERN = Pattern.compile("\\\"fingerprint\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\"])*)\\\"");

    private RtlBaselineManager() {
    }

    public static Path saveBaseline(RtlScanResult result, RtlScanConfig config) {
        RtlScanConfig effectiveConfig = config == null ? RtlScanConfig.fromConfig() : config;
        return saveBaseline(result, Path.of(effectiveConfig.baselinePath()));
    }

    public static Path saveBaseline(RtlScanResult result, Path baselinePath) {
        if (result == null) {
            throw new IllegalArgumentException("Cannot save RTL baseline because scan result is null.");
        }
        if (baselinePath == null) {
            throw new IllegalArgumentException("Cannot save RTL baseline because baseline path is null.");
        }

        try {
            Path parent = baselinePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(baselinePath, toBaselineJson(result), StandardCharsets.UTF_8);
            LOG.rtl("RTL baseline saved: {}", baselinePath.toAbsolutePath());
            return baselinePath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save RTL baseline: " + baselinePath, e);
        }
    }

    public static RtlBaselineComparisonResult compare(RtlScanResult currentResult, RtlScanConfig config) {
        RtlScanConfig effectiveConfig = config == null ? RtlScanConfig.fromConfig() : config;
        return compare(currentResult, Path.of(effectiveConfig.baselinePath()));
    }

    public static RtlBaselineComparisonResult compare(RtlScanResult currentResult, Path baselinePath) {
        if (currentResult == null) {
            return RtlBaselineComparisonResult.empty(baselinePath == null ? "" : baselinePath.toString());
        }

        if (baselinePath == null || !Files.exists(baselinePath)) {
            LOG.warn("RTL baseline was not found. All current RTL issues will be treated as new. Path: {}", baselinePath);
            return new RtlBaselineComparisonResult(
                    currentResult.issues(),
                    List.of(),
                    List.of(),
                    0,
                    currentResult.issueCount(),
                    baselinePath == null ? "" : baselinePath.toString()
            );
        }

        Set<String> baselineFingerprints = readFingerprints(baselinePath);
        Set<String> currentFingerprints = new LinkedHashSet<>();
        List<RtlIssue> newIssues = new ArrayList<>();
        List<RtlIssue> existingIssues = new ArrayList<>();

        for (RtlIssue issue : currentResult.issues()) {
            String fingerprint = fingerprint(issue);
            currentFingerprints.add(fingerprint);
            if (baselineFingerprints.contains(fingerprint)) {
                existingIssues.add(issue);
            } else {
                newIssues.add(issue);
            }
        }

        List<String> resolved = new ArrayList<>();
        for (String baselineFingerprint : baselineFingerprints) {
            if (!currentFingerprints.contains(baselineFingerprint)) {
                resolved.add(baselineFingerprint);
            }
        }

        return new RtlBaselineComparisonResult(
                newIssues,
                existingIssues,
                resolved,
                baselineFingerprints.size(),
                currentResult.issueCount(),
                baselinePath.toString()
        );
    }

    public static List<Path> exportComparison(RtlBaselineComparisonResult comparison, RtlScanConfig config) {
        if (comparison == null) {
            return List.of();
        }
        RtlScanConfig effectiveConfig = config == null ? RtlScanConfig.fromConfig() : config;
        if (!effectiveConfig.isBaselineReportExportEnabled()) {
            return List.of();
        }

        try {
            Path reportDir = Path.of(effectiveConfig.reportDir());
            Files.createDirectories(reportDir);
            String baseName = effectiveConfig.baselineReportFileName();

            Path txtPath = reportDir.resolve(baseName + ".txt");
            Path jsonPath = reportDir.resolve(baseName + ".json");
            Path htmlPath = reportDir.resolve(baseName + ".html");

            Files.writeString(txtPath, comparison.toReport(), StandardCharsets.UTF_8);
            Files.writeString(jsonPath, toComparisonJson(comparison), StandardCharsets.UTF_8);
            Files.writeString(htmlPath, toComparisonHtml(comparison), StandardCharsets.UTF_8);

            LOG.rtl("RTL baseline comparison exported: {}", txtPath.toAbsolutePath());
            LOG.rtl("RTL baseline comparison exported: {}", jsonPath.toAbsolutePath());
            LOG.rtl("RTL baseline comparison exported: {}", htmlPath.toAbsolutePath());

            return List.of(txtPath, jsonPath, htmlPath);
        } catch (IOException e) {
            LOG.warn("Failed to export RTL baseline comparison: {}", e.getMessage());
            return List.of();
        }
    }

    public static String fingerprint(RtlIssue issue) {
        if (issue == null) {
            return "";
        }
        return normalize(issue.severity().name()) + "|"
                + normalize(issue.type().name()) + "|"
                + normalize(issue.locator()) + "|"
                + normalize(issue.message());
    }

    private static Set<String> readFingerprints(Path baselinePath) {
        try {
            String json = Files.readString(baselinePath, StandardCharsets.UTF_8);
            Matcher matcher = FINGERPRINT_PATTERN.matcher(json);
            Set<String> fingerprints = new LinkedHashSet<>();
            while (matcher.find()) {
                fingerprints.add(unescape(matcher.group(1)));
            }
            return fingerprints;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read RTL baseline: " + baselinePath, e);
        }
    }

    private static String toBaselineJson(RtlScanResult result) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"generatedAt\": \"").append(escape(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))).append("\",\n");
        json.append("  \"issueCount\": ").append(result.issueCount()).append(",\n");
        json.append("  \"issues\": [\n");
        List<RtlIssue> issues = result.issues();
        for (int i = 0; i < issues.size(); i++) {
            RtlIssue issue = issues.get(i);
            json.append("    {\n");
            json.append("      \"fingerprint\": \"").append(escape(fingerprint(issue))).append("\",\n");
            json.append("      \"severity\": \"").append(escape(issue.severity().name())).append("\",\n");
            json.append("      \"type\": \"").append(escape(issue.type().name())).append("\",\n");
            json.append("      \"locator\": \"").append(escape(issue.locator())).append("\",\n");
            json.append("      \"message\": \"").append(escape(issue.message())).append("\",\n");
            json.append("      \"actualValue\": \"").append(escape(issue.actualValue())).append("\",\n");
            json.append("      \"recommendation\": \"").append(escape(issue.recommendation())).append("\"\n");
            json.append("    }");
            json.append(i == issues.size() - 1 ? "\n" : ",\n");
        }
        json.append("  ]\n");
        json.append("}\n");
        return json.toString();
    }

    private static String toComparisonJson(RtlBaselineComparisonResult comparison) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"generatedAt\": \"").append(escape(comparison.generatedAt())).append("\",\n");
        json.append("  \"baselinePath\": \"").append(escape(comparison.baselinePath())).append("\",\n");
        json.append("  \"baselineIssueCount\": ").append(comparison.baselineIssueCount()).append(",\n");
        json.append("  \"currentIssueCount\": ").append(comparison.currentIssueCount()).append(",\n");
        json.append("  \"newIssueCount\": ").append(comparison.newIssueCount()).append(",\n");
        json.append("  \"existingIssueCount\": ").append(comparison.existingIssueCount()).append(",\n");
        json.append("  \"resolvedIssueCount\": ").append(comparison.resolvedIssueCount()).append(",\n");
        json.append("  \"newIssues\": [\n");
        List<RtlIssue> newIssues = comparison.newIssues();
        for (int i = 0; i < newIssues.size(); i++) {
            RtlIssue issue = newIssues.get(i);
            json.append("    {\"fingerprint\": \"").append(escape(fingerprint(issue))).append("\", ")
                    .append("\"severity\": \"").append(escape(issue.severity().name())).append("\", ")
                    .append("\"type\": \"").append(escape(issue.type().name())).append("\", ")
                    .append("\"locator\": \"").append(escape(issue.locator())).append("\", ")
                    .append("\"message\": \"").append(escape(issue.message())).append("\"}");
            json.append(i == newIssues.size() - 1 ? "\n" : ",\n");
        }
        json.append("  ],\n");
        json.append("  \"resolvedIssueFingerprints\": [\n");
        List<String> resolved = comparison.resolvedIssueFingerprints();
        for (int i = 0; i < resolved.size(); i++) {
            json.append("    \"").append(escape(resolved.get(i))).append("\"");
            json.append(i == resolved.size() - 1 ? "\n" : ",\n");
        }
        json.append("  ]\n");
        json.append("}\n");
        return json.toString();
    }

    private static String toComparisonHtml(RtlBaselineComparisonResult comparison) {
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html>\n<html lang=\"en\">\n<head>\n");
        html.append("  <meta charset=\"UTF-8\" />\n");
        html.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n");
        html.append("  <title>QATRA RTL Baseline Comparison</title>\n");
        html.append("  <style>body{font-family:Arial,Helvetica,sans-serif;margin:32px;background:#f7f7f8;color:#202124}.card{background:white;border:1px solid #e5e7eb;border-radius:14px;padding:20px;margin-bottom:18px;box-shadow:0 8px 20px rgba(0,0,0,.04)}.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(160px,1fr));gap:12px}.metric{background:#fafafa;border:1px solid #eee;border-radius:12px;padding:14px}.metric strong{font-size:28px;display:block}.new{color:#b42318;font-weight:bold}.resolved{color:#067647;font-weight:bold}.existing{color:#175cd3;font-weight:bold}table{width:100%;border-collapse:collapse}th,td{padding:10px;border-bottom:1px solid #eee;text-align:left;vertical-align:top}code{background:#f2f4f7;padding:2px 6px;border-radius:6px}</style>\n");
        html.append("</head>\n<body>\n");
        html.append("  <h1>QATRA RTL Baseline Comparison</h1>\n");
        html.append("  <p>Baseline: <code>").append(htmlEscape(comparison.baselinePath())).append("</code></p>\n");
        html.append("  <section class=\"card\"><h2>Summary</h2><div class=\"grid\">\n");
        metric(html, "Baseline issues", String.valueOf(comparison.baselineIssueCount()), "existing");
        metric(html, "Current issues", String.valueOf(comparison.currentIssueCount()), "existing");
        metric(html, "New issues", String.valueOf(comparison.newIssueCount()), "new");
        metric(html, "Existing issues", String.valueOf(comparison.existingIssueCount()), "existing");
        metric(html, "Resolved issues", String.valueOf(comparison.resolvedIssueCount()), "resolved");
        html.append("  </div></section>\n");
        html.append("  <section class=\"card\"><h2>New issues</h2>");
        appendIssueTable(html, comparison.newIssues());
        html.append("</section>\n");
        html.append("  <section class=\"card\"><h2>Resolved issue fingerprints</h2>\n");
        if (comparison.resolvedIssueFingerprints().isEmpty()) {
            html.append("    <p>None</p>\n");
        } else {
            html.append("    <ol>\n");
            for (String fingerprint : comparison.resolvedIssueFingerprints()) {
                html.append("      <li><code>").append(htmlEscape(fingerprint)).append("</code></li>\n");
            }
            html.append("    </ol>\n");
        }
        html.append("  </section>\n");
        html.append("</body>\n</html>\n");
        return html.toString();
    }

    private static void appendIssueTable(StringBuilder html, List<RtlIssue> issues) {
        if (issues.isEmpty()) {
            html.append("<p>None</p>\n");
            return;
        }
        html.append("<table><thead><tr><th>Severity</th><th>Type</th><th>Locator</th><th>Message</th></tr></thead><tbody>\n");
        for (RtlIssue issue : issues) {
            html.append("<tr><td>").append(htmlEscape(issue.severity().name())).append("</td><td>")
                    .append(htmlEscape(issue.type().name())).append("</td><td><code>")
                    .append(htmlEscape(issue.locator())).append("</code></td><td>")
                    .append(htmlEscape(issue.message())).append("</td></tr>\n");
        }
        html.append("</tbody></table>\n");
    }

    private static void metric(StringBuilder html, String label, String value, String cssClass) {
        html.append("    <div class=\"metric\"><strong class=\"").append(cssClass).append("\">")
                .append(htmlEscape(value)).append("</strong><span>")
                .append(htmlEscape(label)).append("</span></div>\n");
    }

    private static String normalize(String value) {
        return value == null ? "" : value.replaceAll("\\s+", " ").trim();
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder escaped = new StringBuilder(value.length() + 16);
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '\\' -> escaped.append("\\\\");
                case '"' -> escaped.append("\\\"");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (ch < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) ch));
                    } else {
                        escaped.append(ch);
                    }
                }
            }
        }
        return escaped.toString();
    }

    private static String unescape(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        StringBuilder unescaped = new StringBuilder(value.length());
        boolean escaping = false;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (escaping) {
                switch (ch) {
                    case 'n' -> unescaped.append('\n');
                    case 'r' -> unescaped.append('\r');
                    case 't' -> unescaped.append('\t');
                    case 'b' -> unescaped.append('\b');
                    case 'f' -> unescaped.append('\f');
                    case '"' -> unescaped.append('"');
                    case '\\' -> unescaped.append('\\');
                    default -> unescaped.append(ch);
                }
                escaping = false;
            } else if (ch == '\\') {
                escaping = true;
            } else {
                unescaped.append(ch);
            }
        }
        return unescaped.toString();
    }

    private static String htmlEscape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
