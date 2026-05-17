package io.github.qatra.web.locators.healing.reports;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.locators.LocatorHealingReport;
import io.github.qatra.web.locators.LocatorHealingStatus;
import io.github.qatra.web.locators.QatraLocator;
import io.github.qatra.web.locators.healing.patches.QatraHealingPatchWorkflow;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Exports QATRA self-healing locator decisions into a complete QA evidence package.
 *
 * <p>The goal is not only to say that a locator was healed. The report explains why
 * QATRA selected a fallback, what was rejected, how risky the decision was, and what
 * permanent code change should be reviewed by the automation engineer.</p>
 */
public final class HealingReportExporter {
    private static final QatraLogger LOG = QatraLogger.getInstance();
    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    private static final DateTimeFormatter HUMAN_TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Path REPORT_DIR = Path.of("target", "qatra-reports", "healing");
    private static final Path HISTORY_DIR = REPORT_DIR.resolve("history");

    private HealingReportExporter() {}

    public static HealingReportArtifacts export(QatraLocator locator, LocatorHealingReport report) {
        try {
            Files.createDirectories(REPORT_DIR);
            Files.createDirectories(HISTORY_DIR);

            String timestamp = LocalDateTime.now().format(FILE_TS);
            String safeName = safeFileName(report.name() == null ? "locator" : report.name());
            Path text = HISTORY_DIR.resolve("healing-" + safeName + "-" + timestamp + ".txt");
            Path json = HISTORY_DIR.resolve("healing-" + safeName + "-" + timestamp + ".json");
            Path html = HISTORY_DIR.resolve("healing-" + safeName + "-" + timestamp + ".html");
            Path patches = REPORT_DIR.resolve("locator-patches.json");

            String textContent = toText(report);
            String jsonContent = toJson(report);
            String htmlContent = toHtml(report);
            String markdownChecklist = toHumanReviewChecklist(locator, report);
            String candidateCsv = toCandidateComparisonCsv(report);
            String decisionMatrix = toDecisionMatrixJson(report);

            Files.writeString(text, textContent, StandardCharsets.UTF_8);
            Files.writeString(json, jsonContent, StandardCharsets.UTF_8);
            Files.writeString(html, htmlContent, StandardCharsets.UTF_8);

            Files.writeString(REPORT_DIR.resolve("healing-report-latest.txt"), textContent, StandardCharsets.UTF_8);
            Files.writeString(REPORT_DIR.resolve("healing-report-latest.json"), jsonContent, StandardCharsets.UTF_8);
            Files.writeString(REPORT_DIR.resolve("healing-report-latest.html"), htmlContent, StandardCharsets.UTF_8);
            Files.writeString(REPORT_DIR.resolve("human-review-checklist-latest.md"), markdownChecklist, StandardCharsets.UTF_8);
            Files.writeString(REPORT_DIR.resolve("candidate-comparison-latest.csv"), candidateCsv, StandardCharsets.UTF_8);
            Files.writeString(REPORT_DIR.resolve("healing-decision-matrix-latest.json"), decisionMatrix, StandardCharsets.UTF_8);
            Files.writeString(REPORT_DIR.resolve("healing-dashboard-latest.html"), toDashboard(report), StandardCharsets.UTF_8);

            if (report.status() == LocatorHealingStatus.HEALED_WITH_FALLBACK && report.resolvedLocator() != null) {
                appendPatchSuggestion(locator, report, patches);
                appendPatchSuggestionMarkdown(locator, report, REPORT_DIR.resolve("locator-patches.md"));
                QatraHealingPatchWorkflow.exportSuggestion(locator, report);
            }

            updateIndex();
            updateDashboardIndex();

            LOG.info("QATRA advanced healing report exported: {}", html.toAbsolutePath());
            return new HealingReportArtifacts(text, json, html, patches);
        } catch (IOException exception) {
            LOG.warn("Could not export QATRA healing report: {}", exception.getMessage());
            return new HealingReportArtifacts(null, null, null, null);
        }
    }

    private static void appendPatchSuggestion(QatraLocator locator, LocatorHealingReport report, Path patchFile) throws IOException {
        HealingPatchSuggestion suggestion = HealingPatchSuggestion.from(locator, report);
        List<String> existing = Files.exists(patchFile)
                ? Files.readAllLines(patchFile, StandardCharsets.UTF_8)
                : new ArrayList<>();
        String suggestionJson = suggestion.toJson();
        String content;
        if (existing.isEmpty()) {
            content = "[\n" + suggestionJson + "\n]\n";
        } else {
            String old = String.join(System.lineSeparator(), existing).trim();
            if (old.equals("[]")) {
                content = "[\n" + suggestionJson + "\n]\n";
            } else if (old.endsWith("]")) {
                content = old.substring(0, old.length() - 1).trim();
                if (content.endsWith("[")) {
                    content = content + "\n" + suggestionJson + "\n]\n";
                } else {
                    content = content + ",\n" + suggestionJson + "\n]\n";
                }
            } else {
                content = "[\n" + suggestionJson + "\n]\n";
            }
        }
        Files.writeString(patchFile, content, StandardCharsets.UTF_8);
    }

    private static void appendPatchSuggestionMarkdown(QatraLocator locator, LocatorHealingReport report, Path patchFile) throws IOException {
        HealingPatchSuggestion suggestion = HealingPatchSuggestion.from(locator, report);
        StringBuilder markdown = new StringBuilder();
        if (!Files.exists(patchFile)) {
            markdown.append("# QATRA Locator Patch Suggestions\n\n");
            markdown.append("These suggestions are generated for human review. QATRA does not modify source code automatically.\n\n");
        } else {
            markdown.append(Files.readString(patchFile, StandardCharsets.UTF_8));
            if (!markdown.toString().endsWith("\n")) markdown.append("\n");
        }
        markdown.append("---\n\n");
        markdown.append("## ").append(safe(locator == null ? report.name() : locator.name())).append("\n\n");
        markdown.append("- Old locator: `").append(safe(String.valueOf(suggestion.oldLocator()))).append("`\n");
        markdown.append("- Suggested locator: `").append(safe(String.valueOf(suggestion.newLocator()))).append("`\n");
        markdown.append("- Confidence: ").append(suggestion.confidence()).append("\n");
        markdown.append("- Risk: ").append(suggestion.risk()).append("\n");
        markdown.append("- Review status: Pending human approval\n\n");
        markdown.append("```java\n").append(suggestion.suggestedJavaCode()).append("\n```\n\n");
        Files.writeString(patchFile, markdown.toString(), StandardCharsets.UTF_8);
    }

    private static String toText(LocatorHealingReport report) {
        StringBuilder text = new StringBuilder(report.toString());
        text.append(System.lineSeparator()).append("QATRA Advanced Evidence Summary").append(System.lineSeparator());
        text.append("Generated at: ").append(LocalDateTime.now().format(HUMAN_TS)).append(System.lineSeparator());
        text.append("Business risk note: Review healed locators before updating Page Objects permanently.").append(System.lineSeparator());
        text.append("Human approval required when confidence is not high, risk is not LOW, or multiple candidates were found.").append(System.lineSeparator());
        return text.toString();
    }

    private static String toJson(LocatorHealingReport report) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\n  \"schema\": \"qatra-healing-report/v2\",");
        json.append("\n  \"generatedAt\": \"").append(json(LocalDateTime.now().format(HUMAN_TS))).append("\",");
        json.append("\n  \"name\": \"").append(json(report.name())).append("\",");
        json.append("\n  \"primaryLocator\": \"").append(json(String.valueOf(report.primaryLocator()))).append("\",");
        json.append("\n  \"status\": \"").append(json(String.valueOf(report.status()))).append("\",");
        json.append("\n  \"resolvedLocator\": \"").append(json(String.valueOf(report.resolvedLocator()))).append("\",");
        json.append("\n  \"resolvedSource\": \"").append(json(report.resolvedSource())).append("\",");
        json.append("\n  \"confidence\": ").append(report.confidence() == null ? "null" : report.confidence()).append(",");
        json.append("\n  \"riskLevel\": \"").append(json(report.riskLevel())).append("\",");
        json.append("\n  \"decisionSummary\": \"").append(json(report.decisionSummary())).append("\",");
        json.append("\n  \"recommendation\": \"").append(json(report.recommendation())).append("\",");
        json.append("\n  \"reviewRequired\": ").append(reviewRequired(report)).append(",");
        json.append("\n  \"attempts\": [");
        for (int i = 0; i < report.attempts().size(); i++) {
            if (i > 0) json.append(",");
            json.append("\n    {\"index\": ").append(i + 1).append(", \"details\": \"").append(json(report.attempts().get(i))).append("\"}");
        }
        json.append("\n  ],");
        json.append("\n  \"rejectedCandidates\": [");
        for (int i = 0; i < report.rejectedCandidates().size(); i++) {
            if (i > 0) json.append(",");
            json.append("\n    {\"index\": ").append(i + 1).append(", \"details\": \"").append(json(report.rejectedCandidates().get(i))).append("\"}");
        }
        json.append("\n  ]");
        json.append("\n}\n");
        return json.toString();
    }

    private static String toDecisionMatrixJson(LocatorHealingReport report) {
        String outcome = report.status() == LocatorHealingStatus.HEALED_WITH_FALLBACK ? "HEALED" : String.valueOf(report.status());
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"schema\": \"qatra-healing-decision-matrix/v1\",\n");
        json.append("  \"outcome\": \"").append(json(outcome)).append("\",\n");
        json.append("  \"reviewRequired\": ").append(reviewRequired(report)).append(",\n");
        json.append("  \"checks\": [\n");
        json.append(checkJson("primary-locator-failed", report.status() == LocatorHealingStatus.HEALED_WITH_FALLBACK, "Healing was only needed because the primary locator did not resolve."));
        json.append(",\n").append(checkJson("fallback-selected", report.resolvedLocator() != null, "A fallback locator was selected."));
        json.append(",\n").append(checkJson("confidence-available", report.confidence() != null, "Confidence score is present."));
        json.append(",\n").append(checkJson("risk-available", report.riskLevel() != null && !report.riskLevel().isBlank(), "Risk level is present."));
        json.append(",\n").append(checkJson("rejected-candidates-recorded", !report.rejectedCandidates().isEmpty(), "Rejected candidate reasons are recorded."));
        json.append("\n  ]\n}");
        return json.toString();
    }

    private static String checkJson(String id, boolean passed, String description) {
        return "    {\"id\": \"" + json(id) + "\", \"passed\": " + passed + ", \"description\": \"" + json(description) + "\"}";
    }

    private static String toCandidateComparisonCsv(LocatorHealingReport report) {
        StringBuilder csv = new StringBuilder("type,index,details\n");
        for (int i = 0; i < report.attempts().size(); i++) {
            csv.append("attempt,").append(i + 1).append(",\"").append(csv(report.attempts().get(i))).append("\"\n");
        }
        for (int i = 0; i < report.rejectedCandidates().size(); i++) {
            csv.append("rejected,").append(i + 1).append(",\"").append(csv(report.rejectedCandidates().get(i))).append("\"\n");
        }
        return csv.toString();
    }

    private static String toHumanReviewChecklist(QatraLocator locator, LocatorHealingReport report) {
        StringBuilder markdown = new StringBuilder();
        markdown.append("# QATRA Healing Human Review Checklist\n\n");
        markdown.append("Generated at: ").append(LocalDateTime.now().format(HUMAN_TS)).append("\n\n");
        markdown.append("## Locator\n\n");
        markdown.append("- Name: ").append(safe(locator == null ? report.name() : locator.name())).append("\n");
        markdown.append("- Primary: `").append(safe(String.valueOf(report.primaryLocator()))).append("`\n");
        markdown.append("- Resolved: `").append(safe(String.valueOf(report.resolvedLocator()))).append("`\n");
        markdown.append("- Status: ").append(report.status()).append("\n");
        markdown.append("- Confidence: ").append(report.confidence() == null ? "N/A" : report.confidence() + "%").append("\n");
        markdown.append("- Risk: ").append(safe(report.riskLevel())).append("\n\n");
        markdown.append("## Review Checklist\n\n");
        markdown.append("- [ ] Confirm the healed element is the same business element as the old locator.\n");
        markdown.append("- [ ] Confirm the healed element is not hidden, disabled, or covered by overlay.\n");
        markdown.append("- [ ] Confirm Arabic text, RTL direction, and semantic action still match the intended behavior.\n");
        markdown.append("- [ ] Confirm no real product bug was hidden by the healing decision.\n");
        markdown.append("- [ ] If accepted, update the Page Object or locator definition intentionally.\n");
        markdown.append("- [ ] If rejected, add a stronger data-testid/aria-label or improve locator fallback chain.\n\n");
        markdown.append("## Decision Summary\n\n");
        markdown.append(safe(report.decisionSummary())).append("\n\n");
        markdown.append("## Recommendation\n\n");
        markdown.append(safe(report.recommendation())).append("\n");
        return markdown.toString();
    }

    private static String toHtml(LocatorHealingReport report) {
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html><head><meta charset='utf-8'><title>QATRA Advanced Healing Report</title>");
        html.append("<style>");
        html.append("body{font-family:Inter,Arial,sans-serif;margin:0;background:#0f172a;color:#0f172a}.page{padding:32px}.hero{background:linear-gradient(135deg,#111827,#1d4ed8);color:white;border-radius:24px;padding:28px;box-shadow:0 20px 45px rgba(15,23,42,.28)}");
        html.append(".hero p{color:#dbeafe}.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:14px;margin:22px 0}.card{background:white;border:1px solid #e2e8f0;border-radius:16px;padding:18px;box-shadow:0 10px 24px rgba(15,23,42,.08)}");
        html.append(".label{font-size:12px;text-transform:uppercase;letter-spacing:.08em;color:#64748b}.value{font-size:18px;font-weight:700;margin-top:8px}.mono{font-family:Consolas,monospace;white-space:pre-wrap;font-size:13px}.ok{color:#16a34a}.warn{color:#d97706}.danger{color:#dc2626}.pill{display:inline-block;padding:4px 10px;border-radius:999px;background:#e0f2fe;color:#075985;font-size:12px;font-weight:700}table{width:100%;border-collapse:collapse}td,th{border-bottom:1px solid #e2e8f0;padding:10px;text-align:left}th{background:#f8fafc}.section-title{color:white;margin:28px 0 8px}");
        html.append("</style></head><body><div class='page'>");
        html.append("<section class='hero'><span class='pill'>QATRA Healing Intelligence</span><h1>Advanced Self-Healing Locator Report</h1><p>Evidence-based locator healing with confidence, risk, candidate comparison, and human-review patch suggestions.</p></section>");
        html.append("<div class='grid'>");
        card(html, "Name", report.name());
        card(html, "Status", String.valueOf(report.status()));
        card(html, "Primary", String.valueOf(report.primaryLocator()));
        card(html, "Resolved", String.valueOf(report.resolvedLocator()));
        card(html, "Source", report.resolvedSource());
        card(html, "Confidence", report.confidence() == null ? "N/A" : report.confidence() + "%");
        card(html, "Risk", report.riskLevel());
        card(html, "Review", reviewRequired(report) ? "Human review recommended" : "Low-risk auto-heal");
        html.append("</div>");
        html.append("<div class='card'><h2>Decision Explanation</h2><p>").append(escape(report.decisionSummary())).append("</p></div>");
        html.append("<div class='card'><h2>Candidate Comparison</h2><table><thead><tr><th>Type</th><th>Details</th></tr></thead><tbody>");
        for (String attempt : report.attempts()) html.append("<tr><td>Attempt</td><td class='mono'>").append(escape(attempt)).append("</td></tr>");
        for (String rejected : report.rejectedCandidates()) html.append("<tr><td>Rejected</td><td class='mono danger'>").append(escape(rejected)).append("</td></tr>");
        html.append("</tbody></table></div>");
        html.append("<div class='card'><h2>Human Review Checklist</h2><ul>");
        html.append("<li>Confirm the healed element is the same business element.</li>");
        html.append("<li>Confirm Arabic/RTL behavior still matches the expected action.</li>");
        html.append("<li>Confirm no product bug was hidden by healing.</li>");
        html.append("<li>Apply the suggested locator patch only after review.</li>");
        html.append("</ul></div>");
        html.append("<div class='card'><h2>Recommendation</h2><p>").append(escape(report.recommendation())).append("</p></div>");
        html.append("</div></body></html>");
        return html.toString();
    }

    private static String toDashboard(LocatorHealingReport report) {
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html><head><meta charset='utf-8'><title>QATRA Healing Dashboard</title>");
        html.append("<style>body{font-family:Arial,sans-serif;margin:32px;background:#f8fafc;color:#0f172a}.card{background:white;border:1px solid #e2e8f0;border-radius:14px;padding:18px;margin:12px 0}.hero{background:#111827;color:white;border-radius:18px;padding:22px}.links a{display:block;margin:6px 0;color:#2563eb}</style>");
        html.append("</head><body><div class='hero'><h1>QATRA Healing Evidence Dashboard</h1><p>Latest self-healing locator decision and review artifacts.</p></div>");
        html.append("<div class='card'><h2>Latest Decision</h2><p><strong>").append(escape(report.name())).append("</strong> — ").append(escape(String.valueOf(report.status()))).append("</p>");
        html.append("<p>Confidence: ").append(report.confidence() == null ? "N/A" : report.confidence() + "%").append(" | Risk: ").append(escape(report.riskLevel())).append("</p></div>");
        html.append("<div class='card links'><h2>Artifacts</h2>");
        html.append("<a href='healing-report-latest.html'>Latest HTML report</a>");
        html.append("<a href='healing-report-latest.json'>Latest JSON report</a>");
        html.append("<a href='human-review-checklist-latest.md'>Human review checklist</a>");
        html.append("<a href='candidate-comparison-latest.csv'>Candidate comparison CSV</a>");
        html.append("<a href='locator-patches.json'>Locator patch suggestions JSON</a>");
        html.append("<a href='locator-patches.md'>Locator patch suggestions Markdown</a>");
        html.append("<a href='history/index.html'>Healing history</a>");
        html.append("</div></body></html>");
        return html.toString();
    }

    private static void updateIndex() throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html><head><meta charset='utf-8'><title>QATRA Healing History</title>");
        html.append("<style>body{font-family:Arial,sans-serif;margin:32px;background:#f8fafc;color:#0f172a;}a{color:#2563eb}.card{background:white;border:1px solid #e2e8f0;border-radius:12px;padding:16px;margin:8px 0}</style>");
        html.append("</head><body><h1>QATRA Healing History</h1>");
        if (Files.exists(HISTORY_DIR)) {
            try (var stream = Files.list(HISTORY_DIR)) {
                stream.filter(p -> p.getFileName().toString().endsWith(".html"))
                        .sorted()
                        .forEach(path -> html.append("<div class='card'><a href='")
                                .append(escape(path.getFileName().toString()))
                                .append("'>")
                                .append(escape(path.getFileName().toString()))
                                .append("</a></div>"));
            }
        }
        html.append("</body></html>");
        Files.writeString(HISTORY_DIR.resolve("index.html"), html.toString(), StandardCharsets.UTF_8);
    }

    private static void updateDashboardIndex() throws IOException {
        // Keep a stable dashboard entry point for CI artifacts and manual reviewers.
        if (!Files.exists(REPORT_DIR.resolve("healing-dashboard-latest.html"))) {
            Files.writeString(REPORT_DIR.resolve("healing-dashboard-latest.html"), "<html><body>No healing reports yet.</body></html>", StandardCharsets.UTF_8);
        }
    }

    private static void card(StringBuilder html, String title, String value) {
        html.append("<div class='card'><div class='label'>").append(escape(title)).append("</div><div class='value'>").append(escape(value)).append("</div></div>");
    }

    private static boolean reviewRequired(LocatorHealingReport report) {
        if (report.status() != LocatorHealingStatus.HEALED_WITH_FALLBACK) return false;
        if (report.confidence() == null || report.confidence() < 90) return true;
        String risk = report.riskLevel();
        return risk == null || !("LOW".equalsIgnoreCase(risk));
    }

    private static String safeFileName(String value) {
        if (value == null || value.isBlank()) return "locator";
        return value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static String safe(String value) {
        return value == null || value.isBlank() ? "N/A" : value;
    }

    private static String json(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    private static String csv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"").replace("\r", " ").replace("\n", " ");
    }

    private static String escape(String value) {
        if (value == null) return "N/A";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
