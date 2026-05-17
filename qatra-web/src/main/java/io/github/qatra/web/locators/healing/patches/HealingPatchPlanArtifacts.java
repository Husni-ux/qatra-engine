package io.github.qatra.web.locators.healing.patches;

import java.nio.file.Path;

/** Exported artifacts for QATRA healing patch review workflow. */
public final class HealingPatchPlanArtifacts {
    private final Path suggestionsJson;
    private final Path suggestionsMarkdown;
    private final Path unifiedDiff;
    private final Path approvalTemplateJson;
    private final Path reviewHtml;

    public HealingPatchPlanArtifacts(Path suggestionsJson, Path suggestionsMarkdown, Path unifiedDiff,
                                     Path approvalTemplateJson, Path reviewHtml) {
        this.suggestionsJson = suggestionsJson;
        this.suggestionsMarkdown = suggestionsMarkdown;
        this.unifiedDiff = unifiedDiff;
        this.approvalTemplateJson = approvalTemplateJson;
        this.reviewHtml = reviewHtml;
    }

    public Path suggestionsJson() { return suggestionsJson; }
    public Path suggestionsMarkdown() { return suggestionsMarkdown; }
    public Path unifiedDiff() { return unifiedDiff; }
    public Path approvalTemplateJson() { return approvalTemplateJson; }
    public Path reviewHtml() { return reviewHtml; }
}
