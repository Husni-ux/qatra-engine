package io.github.qatra.tests.locators;

import io.github.qatra.web.locators.LocatorHealingStatus;
import io.github.qatra.web.locators.LocatorResolution;
import io.github.qatra.web.locators.QatraLocator;
import io.github.qatra.web.locators.QatraLocatorEngine;
import io.github.qatra.web.locators.healing.HealingMode;
import io.github.qatra.web.locators.healing.HealingRiskLevel;
import io.github.qatra.web.locators.healing.QatraHealingOptions;
import io.github.qatra.web.testng.QatraBaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Tests for QATRA Healing Modes and bug-safety guardrails.
 */
public class QatraHealingModesGuardrailsTest extends QatraBaseTest {

    @Test
    public void reportOnlyModeDoesNotUseFallbackButWritesDecisionTest() {
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
                .expectedAction("save")
                .fallbackDataTestId("save-request")
                .build();

        try {
            QatraLocatorEngine.resolve(driver().getSeleniumDriver(), locator, Duration.ofMillis(900),
                    QatraHealingOptions.builder()
                            .mode(HealingMode.REPORT_ONLY)
                            .minimumConfidence(50)
                            .exportModeAudit(true)
                            .build());
            Assert.fail("REPORT_ONLY mode must not auto-heal fallback locators.");
        } catch (NoSuchElementException expected) {
            Assert.assertTrue(expected.getMessage().contains("REPORTED_ONLY") || expected.getMessage().contains("reporting only"));
        }

        Assert.assertTrue(Files.exists(Path.of("target", "qatra-reports", "healing", "modes", "healing-mode-audit-latest.json")),
                "Healing mode audit should be generated for review.");
    }

    @Test
    public void safeAutoHealBlocksAmbiguousCandidatesTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <body dir='rtl'>
                    <button data-testid='save-request' role='button'>حفظ الطلب</button>
                    <button data-testid='save-request' role='button'>حفظ نسخة</button>
                  </body>
                </html>
                """));

        QatraLocator locator = QatraLocator.primary(By.id("old-save-button"))
                .named("Ambiguous save button")
                .expectedRole("button")
                .expectedAction("save")
                .fallbackDataTestId("save-request")
                .build();

        try {
            QatraLocatorEngine.resolve(driver().getSeleniumDriver(), locator, Duration.ofMillis(900),
                    QatraHealingOptions.builder()
                            .mode(HealingMode.SAFE_AUTO_HEAL)
                            .minimumConfidence(50)
                            .maximumAutoHealRisk(HealingRiskLevel.HIGH)
                            .failOnAmbiguousCandidates(true)
                            .maximumMatchesForAutoHeal(1)
                            .build());
            Assert.fail("SAFE_AUTO_HEAL should block ambiguous locator candidates.");
        } catch (NoSuchElementException expected) {
            Assert.assertTrue(expected.getMessage().contains("BLOCKED_BY_GUARDRAIL") || expected.getMessage().contains("Ambiguous"));
        }
    }

    @Test
    public void strictApprovalRequiresApprovalFileTest() throws Exception {
        Path approvalFile = Files.createTempFile("qatra-healing-approved", ".json");
        Files.writeString(approvalFile, """
                {
                  "approvals": [
                    {
                      "locatorName": "Approved save button",
                      "oldLocator": "By.id: old-save-button",
                      "newLocator": "By.cssSelector: [data-testid='save-request']",
                      "minConfidence": 80
                    }
                  ]
                }
                """);

        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <body dir='rtl'>
                    <button data-testid='save-request' role='button'>حفظ الطلب</button>
                  </body>
                </html>
                """));

        QatraLocator locator = QatraLocator.primary(By.id("old-save-button"))
                .named("Approved save button")
                .expectedRole("button")
                .expectedAction("save")
                .fallbackDataTestId("save-request")
                .build();

        LocatorResolution resolution = QatraLocatorEngine.resolve(driver().getSeleniumDriver(), locator, Duration.ofSeconds(5),
                QatraHealingOptions.builder()
                        .mode(HealingMode.STRICT_APPROVAL)
                        .minimumConfidence(50)
                        .maximumAutoHealRisk(HealingRiskLevel.LOW)
                        .strictApprovalFile(approvalFile)
                        .build());

        Assert.assertEquals(resolution.status(), LocatorHealingStatus.HEALED_WITH_FALLBACK);
        Assert.assertTrue(resolution.report().toString().contains("APPROVED_BY_APPROVAL_FILE")
                || resolution.report().toString().contains("strict approval"));
    }

    private static String dataUrl(String html) {
        return "data:text/html;charset=utf-8," + URLEncoder.encode(html, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
