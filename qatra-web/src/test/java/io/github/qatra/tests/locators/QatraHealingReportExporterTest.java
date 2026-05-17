package io.github.qatra.tests.locators;

import io.github.qatra.web.locators.LocatorResolution;
import io.github.qatra.web.locators.QatraLocator;
import io.github.qatra.web.locators.QatraLocatorEngine;
import io.github.qatra.web.locators.healing.HealingMode;
import io.github.qatra.web.locators.healing.QatraHealingOptions;
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

/** Tests for QATRA healing report and patch suggestion export. */
public class QatraHealingReportExporterTest extends QatraBaseTest {

    private static final Path HEALING_DIR = Path.of("target", "qatra-reports", "healing");

    @BeforeMethod
    public void cleanHealingReports() throws IOException {
        if (Files.exists(HEALING_DIR)) {
            try (var stream = Files.walk(HEALING_DIR)) {
                stream.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException ignored) {
                                // Best-effort cleanup for deterministic report assertions.
                            }
                        });
            }
        }
    }

    @Test
    public void healingExporterCreatesHtmlJsonTextAndPatchSuggestionTest() throws IOException {
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

        Assert.assertTrue(resolution.healed(), "The test should use a fallback locator to trigger report export.");

        Path latestHtml = HEALING_DIR.resolve("healing-report-latest.html");
        Path latestJson = HEALING_DIR.resolve("healing-report-latest.json");
        Path latestText = HEALING_DIR.resolve("healing-report-latest.txt");
        Path patchFile = HEALING_DIR.resolve("locator-patches.json");
        Path historyIndex = HEALING_DIR.resolve("history").resolve("index.html");
        Path dashboard = HEALING_DIR.resolve("healing-dashboard-latest.html");
        Path reviewChecklist = HEALING_DIR.resolve("human-review-checklist-latest.md");
        Path candidateCsv = HEALING_DIR.resolve("candidate-comparison-latest.csv");
        Path decisionMatrix = HEALING_DIR.resolve("healing-decision-matrix-latest.json");
        Path patchMarkdown = HEALING_DIR.resolve("locator-patches.md");

        Assert.assertTrue(Files.exists(latestHtml), "HTML healing report should be exported.");
        Assert.assertTrue(Files.exists(latestJson), "JSON healing report should be exported.");
        Assert.assertTrue(Files.exists(latestText), "Text healing report should be exported.");
        Assert.assertTrue(Files.exists(patchFile), "Patch suggestion file should be exported for healed locators.");
        Assert.assertTrue(Files.exists(historyIndex), "Healing history index should be exported.");
        Assert.assertTrue(Files.exists(dashboard), "Advanced dashboard should be exported.");
        Assert.assertTrue(Files.exists(reviewChecklist), "Human review checklist should be exported.");
        Assert.assertTrue(Files.exists(candidateCsv), "Candidate comparison CSV should be exported.");
        Assert.assertTrue(Files.exists(decisionMatrix), "Decision matrix JSON should be exported.");
        Assert.assertTrue(Files.exists(patchMarkdown), "Markdown patch suggestion should be exported.");

        String json = Files.readString(latestJson, StandardCharsets.UTF_8);
        Assert.assertTrue(json.contains("qatra-healing-report/v2"));
        Assert.assertTrue(json.contains("HEALED_WITH_FALLBACK"));
        Assert.assertTrue(json.contains("save-request"));

        String html = Files.readString(latestHtml, StandardCharsets.UTF_8);
        Assert.assertTrue(html.contains("Candidate Comparison"));
        Assert.assertTrue(html.contains("Human Review Checklist"));

        String patch = Files.readString(patchFile, StandardCharsets.UTF_8);
        Assert.assertTrue(patch.contains("suggestedJavaCode"));
        Assert.assertTrue(patch.contains("By.cssSelector"));

        String checklist = Files.readString(reviewChecklist, StandardCharsets.UTF_8);
        Assert.assertTrue(checklist.contains("Confirm no real product bug was hidden"));
    }

    private static String dataUrl(String html) {
        return "data:text/html;charset=utf-8," + URLEncoder.encode(html, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
