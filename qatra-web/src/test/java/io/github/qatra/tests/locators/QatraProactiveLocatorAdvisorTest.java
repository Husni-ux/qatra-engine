package io.github.qatra.tests.locators;

import io.github.qatra.web.locators.LocatorRiskLevel;
import io.github.qatra.web.locators.QatraLocator;
import io.github.qatra.web.locators.advisor.LocatorAdvisorOptions;
import io.github.qatra.web.locators.advisor.LocatorAdvisorReport;
import io.github.qatra.web.locators.advisor.LocatorAdvisorReportExporter;
import io.github.qatra.web.locators.advisor.LocatorQualityGate;
import io.github.qatra.web.locators.advisor.ProactiveLocatorQualityAdvisor;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tests for QATRA proactive locator quality advisor.
 */
public class QatraProactiveLocatorAdvisorTest {

    @Test
    public void proactiveAdvisorDetectsAbsoluteXpathBeforeFailureTest() {
        LocatorAdvisorReport report = ProactiveLocatorQualityAdvisor.analyze(
                "Dangerous absolute XPath",
                By.xpath("/html/body/div[3]/div[2]/button[1]")
        );

        Assert.assertEquals(report.riskLevel(), LocatorRiskLevel.CRITICAL,
                "Absolute XPath should be classified as critical in the proactive advisor.");
        Assert.assertTrue(report.score() < 35,
                "Absolute XPath should receive a very low score.");
        Assert.assertTrue(report.issues().stream().anyMatch(issue -> issue.code().equals("ABSOLUTE_XPATH")),
                "Report should contain a specific absolute XPath issue code.");
        Assert.assertTrue(report.recommendations().stream().anyMatch(value -> value.toLowerCase().contains("data-testid")),
                "Report should recommend adding a stable data-testid.");
    }

    @Test
    public void proactiveAdvisorRewardsStableDataTestIdLocatorTest() {
        LocatorAdvisorReport report = ProactiveLocatorQualityAdvisor.analyze(
                "Save request button",
                By.cssSelector("[data-testid='save-request']")
        );

        Assert.assertEquals(report.riskLevel(), LocatorRiskLevel.LOW,
                "data-testid locators should be low risk.");
        Assert.assertTrue(report.score() >= 85,
                "data-testid locators should receive a high score.");
        Assert.assertFalse(report.strengths().isEmpty(),
                "Report should explain why the locator is strong.");
    }

    @Test
    public void proactiveAdvisorAnalyzesQatraLocatorChainWithArabicHintsTest() {
        QatraLocator locator = QatraLocator.primary(By.xpath("/html/body/div[2]/button[1]"))
                .named("Save Arabic request button")
                .expectedRole("button")
                .expectedArabicText("حفظ الطلب")
                .semanticArabicAction("save")
                .fallbackDataTestId("save-request")
                .fallbackText("حفظ الطلب")
                .build();

        LocatorAdvisorReport report = ProactiveLocatorQualityAdvisor.analyze(locator);

        Assert.assertTrue(report.candidateNotes().size() >= 2,
                "QatraLocator chain report should include fallback candidate notes.");
        Assert.assertTrue(report.strengths().stream().anyMatch(value -> value.contains("expected role")),
                "Expected role should improve the quality context.");
        Assert.assertTrue(report.suggestedStableLocator().contains("data-testid"),
                "Advisor should suggest a stable data-testid locator.");
    }

    @Test
    public void locatorQualityGateCanFailWeakLocatorsBeforeRuntimeFailureTest() {
        LocatorAdvisorReport report = ProactiveLocatorQualityAdvisor.analyze(
                "Weak generated selector",
                By.cssSelector(".MuiButton-root-123:nth-child(2)")
        );

        boolean failed = false;
        try {
            LocatorQualityGate.withOptions(LocatorAdvisorOptions.builder()
                            .minimumScore(80)
                            .failOnCritical(true)
                            .build())
                    .require(report);
        } catch (AssertionError error) {
            failed = true;
            Assert.assertTrue(error.getMessage().contains("QATRA Locator Quality Gate failed"));
        }

        Assert.assertTrue(failed, "Locator quality gate should fail weak locators before runtime failure.");
    }

    @Test
    public void proactiveAdvisorExportsHtmlJsonAndTxtReportsTest() {
        LocatorAdvisorReport report = ProactiveLocatorQualityAdvisor.analyze(
                "Exported locator report",
                By.xpath("/html/body/main/div[1]/button[1]")
        );

        Path html = LocatorAdvisorReportExporter.export(report);

        Assert.assertNotNull(html, "HTML report path should be returned.");
        Assert.assertTrue(Files.exists(Path.of("target", "qatra-reports", "locators", "locator-quality-latest.html")));
        Assert.assertTrue(Files.exists(Path.of("target", "qatra-reports", "locators", "locator-quality-latest.json")));
        Assert.assertTrue(Files.exists(Path.of("target", "qatra-reports", "locators", "locator-quality-latest.txt")));
    }
}
