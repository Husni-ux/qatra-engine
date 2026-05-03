package io.github.qatra.web.rtl;

import io.github.qatra.core.logger.QatraLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Exports QATRA RTL scan results as standalone report files.
 *
 * <p>The exporter is intentionally dependency-free so QATRA can generate reports
 * without requiring a JSON library in the web module.</p>
 */
public final class RtlReportExporter {

    private static final QatraLogger LOG = QatraLogger.getInstance();
    private static final DateTimeFormatter HISTORY_TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private RtlReportExporter() {
    }

    /**
     * Export the given scan result according to {@link RtlScanConfig}.
     *
     * @return exported file paths; empty when export is disabled or the result is null
     */
    public static List<Path> export(RtlScanResult result, RtlScanConfig config) {
        if (result == null) {
            return List.of();
        }

        RtlScanConfig effectiveConfig = config == null ? RtlScanConfig.fromConfig() : config;
        if (!effectiveConfig.isReportExportEnabled()) {
            LOG.debug("RTL report export is disabled by configuration.");
            return List.of();
        }

        try {
            Path reportDir = Path.of(effectiveConfig.reportDir());
            Files.createDirectories(reportDir);

            List<Path> exported = new ArrayList<>();
            String formats = effectiveConfig.reportFormats().toLowerCase(Locale.ROOT);
            String baseName = effectiveConfig.reportFileName();

            exported.addAll(exportCurrentReports(result, reportDir, baseName, formats));

            if (effectiveConfig.isHistoryEnabled()) {
                exported.addAll(exportHistoryReports(result, effectiveConfig, baseName, formats));
            }

            return List.copyOf(exported);
        } catch (IOException e) {
            LOG.warn("Failed to export RTL scan report: {}", e.getMessage());
            return List.of();
        }
    }

    private static List<Path> exportCurrentReports(RtlScanResult result, Path reportDir, String baseName, String formats) throws IOException {
        List<Path> exported = new ArrayList<>();

        if (formatEnabled(formats, "txt")) {
            Path txtPath = reportDir.resolve(baseName + ".txt");
            Files.writeString(txtPath, result.toReport(), StandardCharsets.UTF_8);
            exported.add(txtPath);
        }

        if (formatEnabled(formats, "json")) {
            Path jsonPath = reportDir.resolve(baseName + ".json");
            Files.writeString(jsonPath, toJson(result), StandardCharsets.UTF_8);
            exported.add(jsonPath);
        }

        if (formatEnabled(formats, "html")) {
            Path htmlPath = reportDir.resolve(baseName + ".html");
            Files.writeString(htmlPath, toHtml(result, "QATRA RTL Scan Report"), StandardCharsets.UTF_8);
            exported.add(htmlPath);
        }

        return exported;
    }

    private static List<Path> exportHistoryReports(RtlScanResult result, RtlScanConfig config, String baseName, String formats) throws IOException {
        List<Path> exported = new ArrayList<>();
        Path historyDir = Path.of(config.historyDir());
        Files.createDirectories(historyDir);

        String historyName = baseName + "_" + LocalDateTime.now().format(HISTORY_TS);

        if (formatEnabled(formats, "txt")) {
            Path txtPath = historyDir.resolve(historyName + ".txt");
            Files.writeString(txtPath, result.toReport(), StandardCharsets.UTF_8);
            exported.add(txtPath);
        }

        if (formatEnabled(formats, "json")) {
            Path jsonPath = historyDir.resolve(historyName + ".json");
            Files.writeString(jsonPath, toJson(result), StandardCharsets.UTF_8);
            exported.add(jsonPath);
        }

        if (formatEnabled(formats, "html")) {
            Path htmlPath = historyDir.resolve(historyName + ".html");
            Files.writeString(htmlPath, toHtml(result, "QATRA RTL Historical Scan"), StandardCharsets.UTF_8);
            exported.add(htmlPath);
        }

        if (config.isHistoryIndexEnabled()) {
            Path indexPath = historyDir.resolve("index.html");
            Files.writeString(indexPath, toHistoryIndex(result, historyDir), StandardCharsets.UTF_8);
            exported.add(indexPath);
        }

        return exported;
    }

    public static String toJson(RtlScanResult result) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"generatedAt\": \"").append(escape(result.generatedAt())).append("\",\n");
        json.append("  \"summary\": {\n");
        json.append("    \"elementsScanned\": ").append(result.elementsScanned()).append(",\n");
        json.append("    \"arabicElementsFound\": ").append(result.arabicElementsFound()).append(",\n");
        json.append("    \"issueCount\": ").append(result.issueCount()).append(",\n");
        json.append("    \"errorCount\": ").append(result.errorCount()).append(",\n");
        json.append("    \"warningCount\": ").append(result.warningCount()).append(",\n");
        json.append("    \"infoCount\": ").append(result.infoCount()).append("\n");
        json.append("  },\n");

        json.append("  \"countsByType\": {\n");
        RtlIssueType[] types = RtlIssueType.values();
        for (int i = 0; i < types.length; i++) {
            RtlIssueType type = types[i];
            json.append("    \"").append(type.name()).append("\": ").append(result.countByType(type));
            json.append(i == types.length - 1 ? "\n" : ",\n");
        }
        json.append("  },\n");

        json.append("  \"issues\": [\n");
        List<RtlIssue> issues = result.issues();
        for (int i = 0; i < issues.size(); i++) {
            RtlIssue issue = issues.get(i);
            json.append("    {\n");
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

    public static String toHtml(RtlScanResult result, String title) {
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html>\n");
        html.append("<html lang=\"en\">\n<head>\n");
        html.append("  <meta charset=\"UTF-8\" />\n");
        html.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n");
        html.append("  <title>").append(htmlEscape(title)).append("</title>\n");
        html.append("  <style>\n");
        html.append("    body{font-family:Arial,Helvetica,sans-serif;margin:32px;background:#f7f7f8;color:#202124;}\n");
        html.append("    .card{background:white;border:1px solid #e5e7eb;border-radius:14px;padding:20px;margin-bottom:18px;box-shadow:0 8px 20px rgba(0,0,0,.04);}\n");
        html.append("    .grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(160px,1fr));gap:12px;}\n");
        html.append("    .metric{background:#fafafa;border:1px solid #eee;border-radius:12px;padding:14px;}\n");
        html.append("    .metric strong{font-size:28px;display:block;}\n");
        html.append("    table{width:100%;border-collapse:collapse;background:white;} th,td{padding:10px;border-bottom:1px solid #eee;text-align:left;vertical-align:top;}\n");
        html.append("    th{background:#fafafa;} .ERROR{color:#b42318;font-weight:bold;} .WARNING{color:#b54708;font-weight:bold;} .INFO{color:#175cd3;font-weight:bold;}\n");
        html.append("    code{background:#f2f4f7;padding:2px 6px;border-radius:6px;} .rtl{direction:rtl;text-align:right;}\n");
        html.append("  </style>\n</head>\n<body>\n");
        html.append("  <h1>").append(htmlEscape(title)).append("</h1>\n");
        html.append("  <p>Generated at: <code>").append(htmlEscape(result.generatedAt())).append("</code></p>\n");

        html.append("  <section class=\"card\">\n");
        html.append("    <h2>Summary</h2>\n");
        html.append("    <div class=\"grid\">\n");
        metric(html, "Elements scanned", String.valueOf(result.elementsScanned()));
        metric(html, "Arabic elements", String.valueOf(result.arabicElementsFound()));
        metric(html, "Issues", String.valueOf(result.issueCount()));
        metric(html, "Errors", String.valueOf(result.errorCount()));
        metric(html, "Warnings", String.valueOf(result.warningCount()));
        metric(html, "Info", String.valueOf(result.infoCount()));
        html.append("    </div>\n");
        html.append("  </section>\n");

        html.append("  <section class=\"card\">\n");
        html.append("    <h2>Issue counts by type</h2>\n");
        html.append("    <table><thead><tr><th>Type</th><th>Count</th></tr></thead><tbody>\n");
        for (RtlIssueType type : RtlIssueType.values()) {
            html.append("      <tr><td>").append(type).append("</td><td>").append(result.countByType(type)).append("</td></tr>\n");
        }
        html.append("    </tbody></table>\n");
        html.append("  </section>\n");

        html.append("  <section class=\"card\">\n");
        html.append("    <h2>Issues</h2>\n");
        if (result.issues().isEmpty()) {
            html.append("    <p>No Arabic/RTL issues detected.</p>\n");
        } else {
            html.append("    <table><thead><tr><th>Severity</th><th>Type</th><th>Locator</th><th>Message</th><th>Actual</th><th>Recommendation</th></tr></thead><tbody>\n");
            for (RtlIssue issue : result.issues()) {
                html.append("      <tr>")
                        .append("<td class=\"").append(issue.severity()).append("\">").append(issue.severity()).append("</td>")
                        .append("<td>").append(issue.type()).append("</td>")
                        .append("<td><code>").append(htmlEscape(issue.locator())).append("</code></td>")
                        .append("<td>").append(htmlEscape(issue.message())).append("</td>")
                        .append("<td>").append(htmlEscape(issue.actualValue())).append("</td>")
                        .append("<td>").append(htmlEscape(issue.recommendation())).append("</td>")
                        .append("</tr>\n");
            }
            html.append("    </tbody></table>\n");
        }
        html.append("  </section>\n");
        html.append("</body>\n</html>\n");
        return html.toString();
    }

    private static String toHistoryIndex(RtlScanResult latestResult, Path historyDir) throws IOException {
        List<Path> htmlReports;
        try (Stream<Path> files = Files.list(historyDir)) {
            htmlReports = files
                    .filter(path -> path.getFileName().toString().endsWith(".html"))
                    .filter(path -> !"index.html".equals(path.getFileName().toString()))
                    .sorted(Comparator.comparing(Path::getFileName).reversed())
                    .limit(50)
                    .toList();
        }

        StringBuilder html = new StringBuilder();
        html.append("<!doctype html>\n<html lang=\"en\">\n<head>\n");
        html.append("  <meta charset=\"UTF-8\" />\n");
        html.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n");
        html.append("  <title>QATRA RTL Report History</title>\n");
        html.append("  <style>body{font-family:Arial,Helvetica,sans-serif;margin:32px;background:#f7f7f8;color:#202124}.card{background:white;border:1px solid #e5e7eb;border-radius:14px;padding:20px;margin-bottom:18px}.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(160px,1fr));gap:12px}.metric{background:#fafafa;border:1px solid #eee;border-radius:12px;padding:14px}.metric strong{font-size:28px;display:block}li{margin:8px 0}</style>\n");
        html.append("</head>\n<body>\n");
        html.append("  <h1>QATRA RTL Report History</h1>\n");
        html.append("  <section class=\"card\">\n");
        html.append("    <h2>Latest scan summary</h2>\n");
        html.append("    <div class=\"grid\">\n");
        metric(html, "Elements scanned", String.valueOf(latestResult.elementsScanned()));
        metric(html, "Arabic elements", String.valueOf(latestResult.arabicElementsFound()));
        metric(html, "Issues", String.valueOf(latestResult.issueCount()));
        metric(html, "Errors", String.valueOf(latestResult.errorCount()));
        metric(html, "Warnings", String.valueOf(latestResult.warningCount()));
        metric(html, "Info", String.valueOf(latestResult.infoCount()));
        html.append("    </div>\n");
        html.append("  </section>\n");
        html.append("  <section class=\"card\">\n");
        html.append("    <h2>Recent HTML reports</h2>\n");
        if (htmlReports.isEmpty()) {
            html.append("    <p>No historical HTML reports were found yet.</p>\n");
        } else {
            html.append("    <ol>\n");
            for (Path report : htmlReports) {
                String name = report.getFileName().toString();
                html.append("      <li><a href=\"").append(htmlEscape(name)).append("\">").append(htmlEscape(name)).append("</a></li>\n");
            }
            html.append("    </ol>\n");
        }
        html.append("  </section>\n");
        html.append("</body>\n</html>\n");
        return html.toString();
    }

    private static void metric(StringBuilder html, String label, String value) {
        html.append("      <div class=\"metric\"><strong>")
                .append(htmlEscape(value))
                .append("</strong><span>")
                .append(htmlEscape(label))
                .append("</span></div>\n");
    }

    private static boolean formatEnabled(String formats, String format) {
        if (formats == null || formats.isBlank()) {
            return true;
        }
        for (String token : formats.split(",")) {
            String normalized = token.trim();
            if (format.equalsIgnoreCase(normalized) || "all".equalsIgnoreCase(normalized)) {
                return true;
            }
        }
        return false;
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
}
