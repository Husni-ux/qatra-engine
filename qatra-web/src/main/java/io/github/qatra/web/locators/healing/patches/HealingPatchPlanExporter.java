package io.github.qatra.web.locators.healing.patches;

import io.github.qatra.core.logger.QatraLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Exports reviewable patch suggestions without modifying source code. */
public final class HealingPatchPlanExporter {
    private static final QatraLogger LOG = QatraLogger.getInstance();
    private static final Path PATCH_DIR = Path.of("target", "qatra-reports", "healing", "patch-workflow");

    private HealingPatchPlanExporter() {}

    public static HealingPatchPlanArtifacts export(HealingPatchPlan plan) {
        try {
            Files.createDirectories(PATCH_DIR);
            Path suggestionsJson = PATCH_DIR.resolve("qatra-healing-suggestions.json");
            Path suggestionsMarkdown = PATCH_DIR.resolve("qatra-healing-suggestions.md");
            Path unifiedDiff = PATCH_DIR.resolve("qatra-healing-locator-patches.diff");
            Path approvalTemplate = PATCH_DIR.resolve("qatra-healing-approval-template.json");
            Path reviewHtml = PATCH_DIR.resolve("qatra-healing-review.html");

            Files.writeString(suggestionsJson, plan.toJson(), StandardCharsets.UTF_8);
            Files.writeString(suggestionsMarkdown, plan.toMarkdown(), StandardCharsets.UTF_8);
            Files.writeString(unifiedDiff, plan.toUnifiedDiff(), StandardCharsets.UTF_8);
            Files.writeString(approvalTemplate, plan.toApprovalTemplateJson(), StandardCharsets.UTF_8);
            Files.writeString(reviewHtml, toReviewHtml(plan), StandardCharsets.UTF_8);

            LOG.info("QATRA healing patch workflow exported: {}", reviewHtml.toAbsolutePath());
            return new HealingPatchPlanArtifacts(suggestionsJson, suggestionsMarkdown, unifiedDiff, approvalTemplate, reviewHtml);
        } catch (IOException exception) {
            LOG.warn("Could not export QATRA healing patch workflow: {}", exception.getMessage());
            return new HealingPatchPlanArtifacts(null, null, null, null, null);
        }
    }

    private static String toReviewHtml(HealingPatchPlan plan) {
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html><head><meta charset='utf-8'><title>QATRA Healing Patch Review</title>");
        html.append("<style>body{font-family:Inter,Arial,sans-serif;background:#f8fafc;color:#0f172a;margin:0}.page{padding:32px}.hero{background:linear-gradient(135deg,#111827,#2563eb);color:white;border-radius:20px;padding:26px}.card{background:white;border:1px solid #e2e8f0;border-radius:14px;padding:16px;margin:14px 0;box-shadow:0 8px 24px rgba(15,23,42,.06)}code,pre{font-family:Consolas,monospace}.risk{font-weight:700}.pending{color:#d97706}.ok{color:#16a34a}</style>");
        html.append("</head><body><div class='page'><section class='hero'><h1>QATRA Healing Patch Review</h1><p>Reviewable locator patch suggestions. QATRA never applies source-code changes automatically.</p></section>");
        html.append("<div class='card'><strong>Total suggestions:</strong> ").append(plan.size()).append("</div>");
        for (HealingCodePatchSuggestion s : plan.suggestions()) {
            html.append("<div class='card'>");
            html.append("<h2>").append(escape(s.locatorName())).append("</h2>");
            html.append("<p>Status: <span class='pending'>").append(s.status()).append("</span> | Confidence: ").append(escape(s.confidence())).append(" | Risk: <span class='risk'>").append(escape(s.risk())).append("</span></p>");
            html.append("<p><strong>Target:</strong> ").append(escape(s.target().displayName())).append("</p>");
            html.append("<p><strong>Old:</strong> <code>").append(escape(s.oldLocator())).append("</code></p>");
            html.append("<p><strong>New:</strong> <code>").append(escape(s.newLocator())).append("</code></p>");
            html.append("<pre>").append(escape(s.suggestedJavaCode())).append("</pre>");
            html.append("<ul><li>Confirm this is the same business element.</li><li>Confirm no product bug was hidden.</li><li>Apply manually only after review.</li></ul>");
            html.append("</div>");
        }
        html.append("</div></body></html>");
        return html.toString();
    }

    private static String escape(String value) {
        if (value == null) return "N/A";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
