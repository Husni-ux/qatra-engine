package io.github.qatra.web.locators.healing.patches;

import io.github.qatra.web.locators.LocatorHealingReport;
import io.github.qatra.web.locators.QatraLocator;
import io.github.qatra.web.locators.healing.reports.HealingPatchSuggestion;

import java.nio.file.Path;

/** Entry point for IDE-free code patch suggestion workflow. */
public final class QatraHealingPatchWorkflow {
    private QatraHealingPatchWorkflow() {}

    public static HealingPatchPlanArtifacts exportSuggestion(QatraLocator locator, LocatorHealingReport report) {
        return exportSuggestion(locator, report, null);
    }

    public static HealingPatchPlanArtifacts exportSuggestion(QatraLocator locator, LocatorHealingReport report, Path sourceRoot) {
        HealingPatchSuggestion raw = HealingPatchSuggestion.from(locator, report);
        HealingCodePatchSuggestion suggestion = HealingCodePatchSuggestion.from(raw);
        if (sourceRoot != null) {
            suggestion = PageObjectLocatorScanner.findTarget(sourceRoot, raw)
                    .map(suggestion::withTarget)
                    .orElse(suggestion);
        }
        HealingPatchPlan plan = new HealingPatchPlan().add(suggestion);
        return HealingPatchPlanExporter.export(plan);
    }
}
