package io.github.qatra.tests.locators;

import io.github.qatra.web.locators.LocatorQualityAdvisor;
import io.github.qatra.web.locators.LocatorRiskLevel;
import io.github.qatra.web.locators.QatraLocator;
import io.github.qatra.web.testng.QatraBaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Smoke tests for QATRA Self-Healing Locator Engine.
 */
public class QatraSelfHealingLocatorTest extends QatraBaseTest {

    @Test
    public void smartClickUsesArabicTextFallbackWhenPrimaryLocatorChangesTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <head><title>QATRA Self Healing Arabic Click</title></head>
                  <body dir='rtl'>
                    <button data-testid='save-button' onclick="document.getElementById('result').textContent='تم الحفظ بنجاح';">حفظ</button>
                    <p id='result'></p>
                  </body>
                </html>
                """));

        QatraLocator saveButton = QatraLocator.primary(By.id("oldSaveButton"))
                .named("Save button")
                .fallbackDataTestId("save-button")
                .fallbackText("حفظ")
                .fallbackText("Save")
                .build();

        driver().element()
                .smartClick(saveButton)
                .assertThat()
                .element(By.id("result"))
                .containsText("تم الحفظ بنجاح");
    }

    @Test
    public void smartTypeUsesDataTestIdFallbackWhenInputIdChangesTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <head><title>QATRA Self Healing Arabic Input</title></head>
                  <body dir='rtl'>
                    <label for='facilityNameGenerated'>اسم المنشأة</label>
                    <input id='facilityNameGenerated' data-testid='facility-name' />
                    <button id='copy' onclick="document.getElementById('summary').textContent=document.querySelector('[data-testid=facility-name]').value;">نسخ</button>
                    <p id='summary'></p>
                  </body>
                </html>
                """));

        QatraLocator facilityName = QatraLocator.primary(By.id("facilityName"))
                .named("Facility Arabic name input")
                .fallbackDataTestId("facility-name")
                .fallbackAriaLabel("اسم المنشأة")
                .build();

        driver().element()
                .smartType(facilityName, "منشأة تجريبية")
                .adaptiveClick(By.id("copy"))
                .assertThat()
                .element(By.id("summary"))
                .containsText("منشأة تجريبية");
    }

    @Test
    public void locatorQualityAdvisorScoresAbsoluteXpathAsHighRiskTest() {
        var report = LocatorQualityAdvisor.analyze(By.xpath("/html/body/div[3]/div[2]/button[1]"));

        Assert.assertEquals(report.riskLevel(), LocatorRiskLevel.HIGH,
                "Absolute XPath should be considered high risk.");
        Assert.assertTrue(report.score() < 45,
                "Absolute XPath should have a low quality score.");
        Assert.assertFalse(report.recommendations().isEmpty(),
                "Locator advisor should provide recommendations.");
    }

    private static String dataUrl(String html) {
        return "data:text/html;charset=utf-8," + URLEncoder.encode(html, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
