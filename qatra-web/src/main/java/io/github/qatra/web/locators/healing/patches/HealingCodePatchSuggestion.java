package io.github.qatra.web.locators.healing.patches;

import io.github.qatra.web.locators.healing.reports.HealingPatchSuggestion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Reviewable code patch suggestion generated from a healed locator. */
public final class HealingCodePatchSuggestion {
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String id;
    private final String locatorName;
    private final String oldLocator;
    private final String newLocator;
    private final String suggestedJavaCode;
    private final String confidence;
    private final String risk;
    private final HealingPatchReviewStatus status;
    private final HealingPatchTarget target;
    private final String generatedAt;
    private final String reviewNote;

    public HealingCodePatchSuggestion(String id,
                                      String locatorName,
                                      String oldLocator,
                                      String newLocator,
                                      String suggestedJavaCode,
                                      String confidence,
                                      String risk,
                                      HealingPatchReviewStatus status,
                                      HealingPatchTarget target,
                                      String generatedAt,
                                      String reviewNote) {
        this.id = id;
        this.locatorName = locatorName;
        this.oldLocator = oldLocator;
        this.newLocator = newLocator;
        this.suggestedJavaCode = suggestedJavaCode;
        this.confidence = confidence;
        this.risk = risk;
        this.status = status;
        this.target = target == null ? HealingPatchTarget.unknown() : target;
        this.generatedAt = generatedAt == null ? LocalDateTime.now().format(TS) : generatedAt;
        this.reviewNote = reviewNote;
    }

    public static HealingCodePatchSuggestion from(HealingPatchSuggestion suggestion) {
        String id = stableId(suggestion.locatorName(), String.valueOf(suggestion.oldLocator()), String.valueOf(suggestion.newLocator()));
        return new HealingCodePatchSuggestion(
                id,
                suggestion.locatorName(),
                String.valueOf(suggestion.oldLocator()),
                String.valueOf(suggestion.newLocator()),
                suggestion.suggestedJavaCode(),
                suggestion.confidence(),
                suggestion.risk(),
                HealingPatchReviewStatus.PENDING_REVIEW,
                HealingPatchTarget.unknown(),
                LocalDateTime.now().format(TS),
                "Review before updating Page Object code. QATRA never applies locator patches automatically."
        );
    }

    public HealingCodePatchSuggestion withTarget(HealingPatchTarget target) {
        return new HealingCodePatchSuggestion(id, locatorName, oldLocator, newLocator, suggestedJavaCode,
                confidence, risk, status, target, generatedAt, reviewNote);
    }

    public String id() { return id; }
    public String locatorName() { return locatorName; }
    public String oldLocator() { return oldLocator; }
    public String newLocator() { return newLocator; }
    public String suggestedJavaCode() { return suggestedJavaCode; }
    public String confidence() { return confidence; }
    public String risk() { return risk; }
    public HealingPatchReviewStatus status() { return status; }
    public HealingPatchTarget target() { return target; }
    public String generatedAt() { return generatedAt; }
    public String reviewNote() { return reviewNote; }

    public String toJson() {
        return "{" +
                "\n  \"id\": \"" + json(id) + "\"," +
                "\n  \"locatorName\": \"" + json(locatorName) + "\"," +
                "\n  \"oldLocator\": \"" + json(oldLocator) + "\"," +
                "\n  \"newLocator\": \"" + json(newLocator) + "\"," +
                "\n  \"suggestedJavaCode\": \"" + json(suggestedJavaCode) + "\"," +
                "\n  \"confidence\": \"" + json(confidence) + "\"," +
                "\n  \"risk\": \"" + json(risk) + "\"," +
                "\n  \"status\": \"" + status + "\"," +
                "\n  \"targetFile\": \"" + json(target.file() == null ? null : target.file().toString()) + "\"," +
                "\n  \"targetLine\": " + (target.lineNumber() == null ? "null" : target.lineNumber()) + "," +
                "\n  \"targetField\": \"" + json(target.fieldName()) + "\"," +
                "\n  \"oldExpression\": \"" + json(target.oldExpression()) + "\"," +
                "\n  \"generatedAt\": \"" + json(generatedAt) + "\"," +
                "\n  \"reviewNote\": \"" + json(reviewNote) + "\"" +
                "\n}";
    }

    public String toMarkdown() {
        StringBuilder markdown = new StringBuilder();
        markdown.append("### ").append(safe(locatorName)).append("\n\n");
        markdown.append("- ID: `").append(safe(id)).append("`\n");
        markdown.append("- Status: **").append(status).append("**\n");
        markdown.append("- Confidence: ").append(safe(confidence)).append("\n");
        markdown.append("- Risk: ").append(safe(risk)).append("\n");
        markdown.append("- Target: ").append(target.displayName()).append("\n");
        markdown.append("- Old locator: `").append(safe(oldLocator)).append("`\n");
        markdown.append("- New locator: `").append(safe(newLocator)).append("`\n\n");
        markdown.append("Suggested Java code:\n\n```java\n").append(safe(suggestedJavaCode)).append("\n```\n\n");
        markdown.append("> ").append(safe(reviewNote)).append("\n");
        return markdown.toString();
    }

    public String toUnifiedDiff() {
        String oldExpression = target.oldExpression() == null || target.oldExpression().isBlank()
                ? oldLocator
                : target.oldExpression();
        String file = target.file() == null ? "UNKNOWN_SOURCE.java" : target.file().toString().replace('\\', '/');
        StringBuilder diff = new StringBuilder();
        diff.append("diff --qatra a/").append(file).append(" b/").append(file).append("\n");
        diff.append("--- a/").append(file).append("\n");
        diff.append("+++ b/").append(file).append("\n");
        diff.append("@@ QATRA locator suggestion: ").append(safe(locatorName)).append(" @@\n");
        diff.append("-").append(oldExpression).append("\n");
        diff.append("+").append(suggestedJavaCode).append("\n");
        return diff.toString();
    }

    private static String stableId(String name, String oldLocator, String newLocator) {
        String raw = safe(name) + "|" + safe(oldLocator) + "|" + safe(newLocator);
        return "QH-" + Integer.toHexString(raw.hashCode()).replace("-", "N").toUpperCase();
    }

    private static String json(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }

    private static String safe(String value) {
        return value == null || value.isBlank() ? "N/A" : value;
    }
}
