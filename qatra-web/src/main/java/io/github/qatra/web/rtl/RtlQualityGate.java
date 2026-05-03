package io.github.qatra.web.rtl;

import io.github.qatra.core.logger.QatraLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates RTL scan results against configurable quality gate rules.
 */
public final class RtlQualityGate {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private RtlQualityGate() {
    }

    public static RtlQualityGateResult evaluate(RtlScanResult result, RtlScanConfig config) {
        RtlScanResult effectiveResult = result == null ? RtlScanResult.empty() : result;
        RtlScanConfig effectiveConfig = config == null ? RtlScanConfig.fromConfig() : config;

        if (!effectiveConfig.isQualityGateEnabled()) {
            return RtlQualityGateResult.disabled();
        }

        long errors = effectiveResult.errorCount();
        long warnings = effectiveResult.warningCount();
        long info = effectiveResult.infoCount();
        int score = calculateScore(errors, warnings, info);

        List<String> violations = new ArrayList<>();
        if (score < effectiveConfig.qualityGateMinScore()) {
            violations.add("Score " + score + " is below the minimum allowed score " + effectiveConfig.qualityGateMinScore() + ".");
        }
        if (errors > effectiveConfig.qualityGateMaxErrors()) {
            violations.add("Error count " + errors + " is above the maximum allowed errors " + effectiveConfig.qualityGateMaxErrors() + ".");
        }
        if (warnings > effectiveConfig.qualityGateMaxWarnings()) {
            violations.add("Warning count " + warnings + " is above the maximum allowed warnings " + effectiveConfig.qualityGateMaxWarnings() + ".");
        }

        return new RtlQualityGateResult(
                true,
                score,
                effectiveConfig.qualityGateMinScore(),
                errors,
                warnings,
                info,
                effectiveResult.issueCount(),
                effectiveConfig.qualityGateMaxErrors(),
                effectiveConfig.qualityGateMaxWarnings(),
                violations.isEmpty(),
                violations
        );
    }

    public static List<Path> export(RtlQualityGateResult result, RtlScanConfig config) {
        if (result == null) {
            return List.of();
        }
        RtlScanConfig effectiveConfig = config == null ? RtlScanConfig.fromConfig() : config;
        if (!effectiveConfig.isQualityGateReportExportEnabled()) {
            return List.of();
        }

        try {
            Path reportDir = Path.of(effectiveConfig.reportDir());
            Files.createDirectories(reportDir);
            String baseName = effectiveConfig.qualityGateReportFileName();

            Path txtPath = reportDir.resolve(baseName + ".txt");
            Path jsonPath = reportDir.resolve(baseName + ".json");
            Path htmlPath = reportDir.resolve(baseName + ".html");

            Files.writeString(txtPath, result.toReport(), StandardCharsets.UTF_8);
            Files.writeString(jsonPath, toJson(result), StandardCharsets.UTF_8);
            Files.writeString(htmlPath, toHtml(result), StandardCharsets.UTF_8);

            LOG.rtl("RTL quality gate exported: {}", txtPath.toAbsolutePath());
            LOG.rtl("RTL quality gate exported: {}", jsonPath.toAbsolutePath());
            LOG.rtl("RTL quality gate exported: {}", htmlPath.toAbsolutePath());

            return List.of(txtPath, jsonPath, htmlPath);
        } catch (IOException e) {
            LOG.warn("Failed to export RTL quality gate report: {}", e.getMessage());
            return List.of();
        }
    }

    static int calculateScore(long errors, long warnings, long info) {
        long penalty = (errors * 25) + (warnings * 10) + (info * 2);
        return Math.max(0, Math.min(100, 100 - (int) penalty));
    }

    public static String toJson(RtlQualityGateResult result) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"generatedAt\": \"").append(escape(result.generatedAt())).append("\",\n");
        json.append("  \"enabled\": ").append(result.isEnabled()).append(",\n");
        json.append("  \"status\": \"").append(escape(result.status())).append("\",\n");
        json.append("  \"passed\": ").append(result.passed()).append(",\n");
        json.append("  \"score\": ").append(result.score()).append(",\n");
        json.append("  \"minScore\": ").append(result.minScore()).append(",\n");
        json.append("  \"issueCount\": ").append(result.issueCount()).append(",\n");
        json.append("  \"errorCount\": ").append(result.errorCount()).append(",\n");
        json.append("  \"warningCount\": ").append(result.warningCount()).append(",\n");
        json.append("  \"infoCount\": ").append(result.infoCount()).append(",\n");
        json.append("  \"maxErrors\": ").append(result.maxErrors()).append(",\n");
        json.append("  \"maxWarnings\": ").append(result.maxWarnings()).append(",\n");
        json.append("  \"violations\": [\n");
        for (int i = 0; i < result.violations().size(); i++) {
            json.append("    \"").append(escape(result.violations().get(i))).append("\"");
            json.append(i == result.violations().size() - 1 ? "\n" : ",\n");
        }
        json.append("  ]\n");
        json.append("}\n");
        return json.toString();
    }

    public static String toHtml(RtlQualityGateResult result) {
        String statusClass = result.passed() ? "passed" : "failed";
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html>\n<html lang=\"en\">\n<head>\n");
        html.append("  <meta charset=\"UTF-8\" />\n");
        html.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n");
        html.append("  <title>QATRA RTL Quality Gate</title>\n");
        html.append("  <style>body{font-family:Arial,Helvetica,sans-serif;margin:32px;background:#f7f7f8;color:#202124}.card{background:white;border:1px solid #e5e7eb;border-radius:14px;padding:20px;margin-bottom:18px;box-shadow:0 8px 20px rgba(0,0,0,.04)}.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(160px,1fr));gap:12px}.metric{background:#fafafa;border:1px solid #eee;border-radius:12px;padding:14px}.metric strong{font-size:28px;display:block}.passed{color:#067647;font-weight:bold}.failed{color:#b42318;font-weight:bold}li{margin:8px 0}code{background:#f2f4f7;padding:2px 6px;border-radius:6px}</style>\n");
        html.append("</head>\n<body>\n");
        html.append("  <h1>QATRA RTL Quality Gate</h1>\n");
        html.append("  <p>Generated at: <code>").append(htmlEscape(result.generatedAt())).append("</code></p>\n");
        html.append("  <section class=\"card\"><h2>Status: <span class=\"").append(statusClass).append("\">").append(htmlEscape(result.status())).append("</span></h2><div class=\"grid\">\n");
        metric(html, "Score", String.valueOf(result.score()), statusClass);
        metric(html, "Minimum score", String.valueOf(result.minScore()), "");
        metric(html, "Issues", String.valueOf(result.issueCount()), "");
        metric(html, "Errors", result.errorCount() + "/" + result.maxErrors(), result.errorCount() > result.maxErrors() ? "failed" : "passed");
        metric(html, "Warnings", result.warningCount() + "/" + result.maxWarnings(), result.warningCount() > result.maxWarnings() ? "failed" : "passed");
        metric(html, "Info", String.valueOf(result.infoCount()), "");
        html.append("  </div></section>\n");
        html.append("  <section class=\"card\"><h2>Violations</h2>\n");
        if (result.violations().isEmpty()) {
            html.append("    <p>No quality gate violations detected.</p>\n");
        } else {
            html.append("    <ol>\n");
            for (String violation : result.violations()) {
                html.append("      <li>").append(htmlEscape(violation)).append("</li>\n");
            }
            html.append("    </ol>\n");
        }
        html.append("  </section>\n</body>\n</html>\n");
        return html.toString();
    }

    private static void metric(StringBuilder html, String label, String value, String cssClass) {
        html.append("    <div class=\"metric\"><strong");
        if (cssClass != null && !cssClass.isBlank()) {
            html.append(" class=\"").append(cssClass).append("\"");
        }
        html.append(">").append(htmlEscape(value)).append("</strong><span>").append(htmlEscape(label)).append("</span></div>\n");
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
