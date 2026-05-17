package io.github.qatra.tests.locators;

import io.github.qatra.web.locators.LocatorHealingStatus;
import io.github.qatra.web.locators.LocatorResolution;
import io.github.qatra.web.locators.QatraLocator;
import io.github.qatra.web.locators.QatraLocatorEngine;
import io.github.qatra.web.locators.healing.HealingMode;
import io.github.qatra.web.locators.healing.QatraHealingOptions;
import io.github.qatra.web.locators.healing.arabic.ArabicActionDictionary;
import io.github.qatra.web.locators.healing.arabic.ArabicTextSimilarity;
import io.github.qatra.web.testng.QatraBaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/** Tests for Arabic-aware semantic locator healing. */
public class QatraArabicSemanticLocatorHealingTest extends QatraBaseTest {

    @Test
    public void semanticArabicActionHealsSaveButtonWhenExactLocatorChangedTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <body dir='rtl'>
                    <section id='request-form'>
                      <button data-testid='submit-request' role='button'>حفظ الطلب</button>
                    </section>
                  </body>
                </html>
                """));

        QatraLocator locator = QatraLocator.primary(By.id("old-save-btn"))
                .named("Save request button")
                .expectedRole("button")
                .semanticArabicAction("save")
                .fallbackDataTestId("submit-request")
                .fallbackArabicAction("save")
                .build();

        LocatorResolution resolution = QatraLocatorEngine.resolve(
                driver().getSeleniumDriver(),
                locator,
                Duration.ofSeconds(5),
                QatraHealingOptions.builder()
                        .mode(HealingMode.SAFE_AUTO_HEAL)
                        .minimumConfidence(80)
                        .build()
        );

        Assert.assertEquals(resolution.status(), LocatorHealingStatus.HEALED_WITH_FALLBACK);
        Assert.assertTrue(resolution.report().toString().contains("Arabic business action")
                || resolution.report().toString().contains("arabic semantic action"));
        Assert.assertEquals(resolution.element().getText(), "حفظ الطلب");
    }

    @Test
    public void semanticArabicActionHealsApproveButtonByArabicSynonymTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <body dir='rtl'>
                    <div class='toolbar'>
                      <button role='button'>اعتماد الطلب</button>
                    </div>
                  </body>
                </html>
                """));

        QatraLocator locator = QatraLocator.primary(By.id("approve-btn"))
                .named("Approve request button")
                .expectedRole("button")
                .semanticArabicAction("approve")
                .build();

        LocatorResolution resolution = QatraLocatorEngine.resolve(
                driver().getSeleniumDriver(),
                locator,
                Duration.ofSeconds(5),
                QatraHealingOptions.builder()
                        .mode(HealingMode.SAFE_AUTO_HEAL)
                        .minimumConfidence(70)
                        .build()
        );

        Assert.assertTrue(resolution.healed(), "Arabic synonym fallback should heal the locator.");
        Assert.assertEquals(resolution.element().getText(), "اعتماد الطلب");
    }

    @Test
    public void arabicNearbyLabelFallbackFindsArabicInputFieldTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <body dir='rtl'>
                    <form>
                      <div class='field-row'>
                        <label>اسم المنشأة</label>
                        <input name='facilityName' />
                      </div>
                    </form>
                  </body>
                </html>
                """));

        QatraLocator locator = QatraLocator.primary(By.id("facility-name"))
                .named("Facility name field")
                .expectedRole("textbox")
                .expectedArabicText("اسم المنشأة")
                .fallbackArabicLabel("اسم المنشأة")
                .build();

        driver().element().smartType(locator, "منشأة تجريبية");

        String value = driver().getSeleniumDriver().findElement(By.name("facilityName")).getAttribute("value");
        Assert.assertEquals(value, "منشأة تجريبية");
    }

    @Test
    public void arabicDictionaryAndNormalizationSupportCommonUiVariantsTest() {
        Assert.assertTrue(ArabicActionDictionary.matchesAction("save", "حفظ الطلب"));
        Assert.assertTrue(ArabicActionDictionary.matchesAction("submit", "إرسال الطلب"));
        Assert.assertTrue(ArabicActionDictionary.matchesAction("approve", "اعتماد الطلب"));
        Assert.assertTrue(ArabicTextSimilarity.semanticContains("حــفـظ الطَّلب", "حفظ الطلب"));
    }

    private static String dataUrl(String html) {
        return "data:text/html;charset=utf-8," + URLEncoder.encode(html, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
