package io.github.qatra.tests.locators;

import io.github.qatra.web.locators.LocatorResolution;
import io.github.qatra.web.locators.QatraLocator;
import io.github.qatra.web.locators.QatraLocatorEngine;
import io.github.qatra.web.locators.healing.HealingMode;
import io.github.qatra.web.locators.healing.QatraHealingOptions;
import io.github.qatra.web.locators.healing.patches.HealingCodePatchSuggestion;
import io.github.qatra.web.locators.healing.patches.HealingPatchPlan;
import io.github.qatra.web.locators.healing.patches.HealingPatchPlanArtifacts;
import io.github.qatra.web.locators.healing.patches.HealingPatchPlanExporter;
import io.github.qatra.web.locators.healing.patches.HealingPatchReviewStatus;
import io.github.qatra.web.testng.QatraBaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Comparator;

/** Tests the IDE-free locator patch strategy. */
public class QatraHealingPatchWorkflowTest extends QatraBaseTest {

    private static final Path PATCH_DIR = Path.of("target", "qatra-reports", "healing", "patch-workflow");
    private static final Path HEALING_DIR = Path.of("target", "qatra-reports", "healing");

    @BeforeMethod
    public void cleanReports() throws IOException {
        if (Files.exists(HEALING_DIR)) {
            try (var stream = Files.walk(HEALING_DIR)) {
                stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ignored) {
                        // best effort cleanup
                    }
                });
            }
        }
    }

    @Test
    public void healingCreatesIdeFreePatchWorkflowArtifactsTest() throws IOException {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <body dir='rtl'>
                    <button data-testid='save-request' role='button'>حفظ الطلب</button>
                  </body>
                </html>
                """));

        QatraLocator locator = QatraLocator.primary(By.id("old-save-button"))
                .named("Save request button")
                .expectedRole("button")
                .expectedArabicText("حفظ الطلب")
                .expectedAction("save")
                .fallbackDataTestId("save-request")
                .fallbackText("حفظ الطلب")
                .build();

        LocatorResolution resolution = QatraLocatorEngine.resolve(
                driver().getSeleniumDriver(),
                locator,
                Duration.ofSeconds(5),
                QatraHealingOptions.builder()
                        .mode(HealingMode.SAFE_AUTO_HEAL)
                        .minimumConfidence(75)
                        .build()
        );

        Assert.assertTrue(resolution.healed(), "Healing should happen so the patch workflow is exported.");

        Path suggestionsJson = PATCH_DIR.resolve("qatra-healing-suggestions.json");
        Path suggestionsMarkdown = PATCH_DIR.resolve("qatra-healing-suggestions.md");
        Path diff = PATCH_DIR.resolve("qatra-healing-locator-patches.diff");
        Path approvalTemplate = PATCH_DIR.resolve("qatra-healing-approval-template.json");
        Path reviewHtml = PATCH_DIR.resolve("qatra-healing-review.html");

        Assert.assertTrue(Files.exists(suggestionsJson), "JSON patch suggestions should be exported.");
        Assert.assertTrue(Files.exists(suggestionsMarkdown), "Markdown patch suggestions should be exported.");
        Assert.assertTrue(Files.exists(diff), "Unified diff patch suggestion should be exported.");
        Assert.assertTrue(Files.exists(approvalTemplate), "Approval template should be exported.");
        Assert.assertTrue(Files.exists(reviewHtml), "Review HTML should be exported.");

        String json = Files.readString(suggestionsJson, StandardCharsets.UTF_8);
        Assert.assertTrue(json.contains("qatra-healing-patch-plan/v1"));
        Assert.assertTrue(json.contains("autoApply"));
        Assert.assertTrue(json.contains("false"));
        Assert.assertTrue(json.contains("save-request"));

        String approval = Files.readString(approvalTemplate, StandardCharsets.UTF_8);
        Assert.assertTrue(approval.contains("approved"));
        Assert.assertTrue(approval.contains("false"));

        String markdown = Files.readString(suggestionsMarkdown, StandardCharsets.UTF_8);
        Assert.assertTrue(markdown.contains("QATRA does **not** modify source code automatically"));
    }

    @Test
    public void patchPlanExporterCreatesHumanReviewableArtifactsTest() {
        HealingCodePatchSuggestion suggestion = new HealingCodePatchSuggestion(
                "QH-DEMO",
                "Save request button",
                "By.id: old-save-button",
                "By.cssSelector: [data-testid='save-request']",
                "By.cssSelector(\"[data-testid='save-request']\")",
                "94%",
                "LOW",
                HealingPatchReviewStatus.PENDING_REVIEW,
                null,
                null,
                "Manual review required before updating Page Object."
        );

        HealingPatchPlanArtifacts artifacts = HealingPatchPlanExporter.export(new HealingPatchPlan().add(suggestion));

        Assert.assertNotNull(artifacts.suggestionsJson());
        Assert.assertTrue(Files.exists(artifacts.suggestionsJson()));
        Assert.assertTrue(Files.exists(artifacts.unifiedDiff()));
        Assert.assertTrue(Files.exists(artifacts.approvalTemplateJson()));
        Assert.assertTrue(Files.exists(artifacts.reviewHtml()));
    }

    private static String dataUrl(String html) {
        return "data:text/html;charset=utf-8," + URLEncoder.encode(html, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
