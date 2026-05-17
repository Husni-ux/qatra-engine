package io.github.qatra.tests.locators;

import io.github.qatra.web.locators.LocatorHealingStatus;
import io.github.qatra.web.locators.LocatorResolution;
import io.github.qatra.web.locators.QatraLocator;
import io.github.qatra.web.locators.QatraLocatorEngine;
import io.github.qatra.web.locators.healing.HealingMode;
import io.github.qatra.web.locators.healing.QatraHealingOptions;
import io.github.qatra.web.testng.QatraBaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Tests for the QATRA Healing Confidence, Risk, and Approval Engine.
 */
public class QatraHealingDecisionEngineTest extends QatraBaseTest {

    @Test
    public void safeAutoHealingApprovesHighConfidenceArabicCandidateTest() {
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

        Assert.assertEquals(resolution.status(), LocatorHealingStatus.HEALED_WITH_FALLBACK);
        Assert.assertTrue(resolution.report().toString().contains("Confidence"));
        Assert.assertTrue(resolution.report().toString().contains("Risk"));
    }

    @Test
    public void suggestOnlyModeDoesNotAutoHealFallbackTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <body dir='rtl'>
                    <button data-testid='delete-request' role='button'>حذف الطلب</button>
                  </body>
                </html>
                """));

        QatraLocator locator = QatraLocator.primary(By.id("old-delete-button"))
                .named("Delete request button")
                .expectedRole("button")
                .expectedArabicText("حذف الطلب")
                .fallbackDataTestId("delete-request")
                .build();

        try {
            QatraLocatorEngine.resolve(
                    driver().getSeleniumDriver(),
                    locator,
                    Duration.ofMillis(800),
                    QatraHealingOptions.builder()
                            .mode(HealingMode.SUGGEST_ONLY)
                            .minimumConfidence(50)
                            .build()
            );
            Assert.fail("SUGGEST_ONLY mode should not auto-heal fallback locators.");
        } catch (org.openqa.selenium.NoSuchElementException expected) {
            Assert.assertTrue(expected.getMessage().contains("SUGGESTED_ONLY") || expected.getMessage().contains("Rejected candidates"));
        }
    }

    private static String dataUrl(String html) {
        return "data:text/html;charset=utf-8," + URLEncoder.encode(html, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
