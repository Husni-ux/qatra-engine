package io.github.qatra.web.locators.advisor;

import io.github.qatra.core.logger.QatraLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * Exports proactive locator quality reports to target/qatra-reports/locators.
 */
public final class LocatorAdvisorReportExporter {

    private static final QatraLogger LOG = QatraLogger.getInstance();
    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    private static final Path REPORT_DIR = Path.of("target", "qatra-reports", "locators");
    private static final Path HISTORY_DIR = REPORT_DIR.resolve("history");

    private LocatorAdvisorReportExporter() {
    }

    public static Path export(LocatorAdvisorReport report) {
        if (report == null) {
            throw new IllegalArgumentException("LocatorAdvisorReport cannot be null");
        }
        try {
            Files.createDirectories(REPORT_DIR);
            Files.createDirectories(HISTORY_DIR);
            String safeName = safe(report.name());
            String timestamp = LocalDateTime.now().format(FILE_TS);
            Path historyHtml = HISTORY_DIR.resolve("locator-quality-" + safeName + "-" + timestamp + ".html");
            Path latestHtml = REPORT_DIR.resolve("locator-quality-latest.html");
            Path latestJson = REPORT_DIR.resolve("locator-quality-latest.json");
            Path latestTxt = REPORT_DIR.resolve("locator-quality-latest.txt");

            Files.writeString(historyHtml, toHtml(report), StandardCharsets.UTF_8);
            Files.writeString(latestHtml, toHtml(report), StandardCharsets.UTF_8);
            Files.writeString(latestJson, toJson(report), StandardCharsets.UTF_8);
            Files.writeString(latestTxt, report.toString(), StandardCharsets.UTF_8);
            updateIndex();
            LOG.info("QATRA locator quality report exported: {}", latestHtml.toAbsolutePath());
            return latestHtml;
        } catch (IOException exception) {
            LOG.warn("Could not export QATRA locator quality report: {}", exception.getMessage());
            return null;
        }
    }

    private static void updateIndex() throws IOException {
        Files.createDirectories(HISTORY_DIR);
        String links = Files.list(HISTORY_DIR)
                .filter(path -> path.getFileName().toString().endsWith(".html"))
                .sorted()
                .map(path -> "<li><a href='" + html(path.getFileName().toString()) + "'>" + html(path.getFileName().toString()) + "</a></li>")
                .collect(Collectors.joining(System.lineSeparator()));
        String html = "<!doctype html><html><head><meta charset='utf-8'><title>QATRA Locator Quality History</title>" +
                "<style>body{font-family:Arial,sans-serif;margin:32px;background:#f8fafc;color:#0f172a}li{margin:8px 0}</style>" +
                "</head><body><h1>QATRA Locator Quality History</h1><ul>" + links + "</ul></body></html>";
        Files.writeString(HISTORY_DIR.resolve("index.html"), html, StandardCharsets.UTF_8);
    }

    private static String toHtml(LocatorAdvisorReport report) {
        return "<!doctype html><html><head><meta charset='utf-8'><title>QATRA Locator Quality Advisor</title>" +
                "<style>body{font-family:Inter,Arial,sans-serif;margin:32px;background:#f8fafc;color:#0f172a}" +
                ".card{background:white;border:1px solid #e2e8f0;border-radius:16px;padding:20px;margin:16px 0;box-shadow:0 8px 22px rgba(15,23,42,.06)}" +
                ".score{font-size:44px;font-weight:800}.risk{display:inline-block;padding:6px 12px;border-radius:999px;background:#e0f2fe}" +
                "code{background:#f1f5f9;padding:2px 6px;border-radius:6px}li{margin:7px 0}</style></head><body>" +
                "<h1>QATRA Locator Quality Advisor</h1>" +
                "<div class='card'><div class='score'>" + report.score() + "/100</div><p><b>Risk:</b> <span class='risk'>" + html(report.riskLevel().name()) + "</span></p>" +
                "<p><b>Name:</b> " + html(report.name()) + "</p><p><b>Locator:</b> <code>" + html(String.valueOf(report.primaryLocator())) + "</code></p>" +
                "<p><b>Suggested stable locator:</b> <code>" + html(report.suggestedStableLocator()) + "</code></p></div>" +
                "<div class='card'><h2>Issues</h2><ul>" + report.issues().stream().map(issue -> "<li>" + html(issue.toString()) + "</li>").collect(Collectors.joining()) + "</ul></div>" +
                "<div class='card'><h2>Strengths</h2><ul>" + report.strengths().stream().map(v -> "<li>" + html(v) + "</li>").collect(Collectors.joining()) + "</ul></div>" +
                "<div class='card'><h2>Recommendations</h2><ul>" + report.recommendations().stream().map(v -> "<li>" + html(v) + "</li>").collect(Collectors.joining()) + "</ul></div>" +
                "<div class='card'><h2>Candidate Notes</h2><ul>" + report.candidateNotes().stream().map(v -> "<li>" + html(v) + "</li>").collect(Collectors.joining()) + "</ul></div>" +
                "</body></html>";
    }

    private static String toJson(LocatorAdvisorReport report) {
        return "{" +
                "\n  \"name\": \"" + json(report.name()) + "\"," +
                "\n  \"locator\": \"" + json(String.valueOf(report.primaryLocator())) + "\"," +
                "\n  \"score\": " + report.score() + "," +
                "\n  \"risk\": \"" + report.riskLevel() + "\"," +
                "\n  \"suggestedStableLocator\": \"" + json(report.suggestedStableLocator()) + "\"," +
                "\n  \"issues\": [" + report.issues().stream().map(issue -> "\"" + json(issue.toString()) + "\"").collect(Collectors.joining(", ")) + "]," +
                "\n  \"recommendations\": [" + report.recommendations().stream().map(v -> "\"" + json(v) + "\"").collect(Collectors.joining(", ")) + "]" +
                "\n}";
    }

    private static String safe(String value) {
        String safe = value == null ? "locator" : value.replaceAll("[^a-zA-Z0-9._-]", "_");
        return safe.isBlank() ? "locator" : safe;
    }

    private static String html(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static String json(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}
