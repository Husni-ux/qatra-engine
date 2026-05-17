package io.github.qatra.tests.locators;

import io.github.qatra.web.locators.LocatorHealingStatus;
import io.github.qatra.web.locators.LocatorResolution;
import io.github.qatra.web.locators.QatraLocator;
import io.github.qatra.web.locators.QatraLocatorEngine;
import io.github.qatra.web.locators.healing.HealingMode;
import io.github.qatra.web.locators.healing.QatraHealingOptions;
import io.github.qatra.web.locators.healing.accessibility.AccessibleNameResolver;
import io.github.qatra.web.locators.healing.accessibility.AccessibilitySignal;
import io.github.qatra.web.testng.QatraBaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/** Tests for accessibility-tree based locator healing. */
public class QatraAccessibilityLocatorHealingTest extends QatraBaseTest {

    @Test
    public void roleAndAccessibleNameFallbackHealsArabicSaveButtonTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <body dir='rtl'>
                    <button role='button' aria-label='حفظ الطلب' onclick="document.getElementById('result').textContent='تم الحفظ';">
                      <span aria-hidden='true'>💾</span>
                    </button>
                    <p id='result'></p>
                  </body>
                </html>
                """));

        QatraLocator saveButton = QatraLocator.primary(By.id("old-save-button"))
        .named("Save request button")
        .expectedRole("button")
        .expectedAccessibleName("حفظ الطلب")
        .fallbackRoleAndAccessibleName("button", "حفظ الطلب")
        .build();

        LocatorResolution resolution = QatraLocatorEngine.resolve(
                driver().getSeleniumDriver(),
                saveButton,
                Duration.ofSeconds(5),
                QatraHealingOptions.builder()
                        .mode(HealingMode.SAFE_AUTO_HEAL)
                        .minimumConfidence(80)
                        .build()
        );

        Assert.assertEquals(resolution.status(), LocatorHealingStatus.HEALED_WITH_FALLBACK);
        Assert.assertTrue(resolution.report().toString().contains("accessibility")
                || resolution.report().toString().contains("accessible-name"));

        resolution.element().click();
        Assert.assertEquals(driver().getSeleniumDriver().findElement(By.id("result")).getText(), "تم الحفظ");
    }

    @Test
    public void ariaLabelledByAccessibleNameIsResolvedForHealingEvidenceTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <body dir='rtl'>
                    <span id='approveText'>اعتماد الطلب</span>
                    <button id='generated-9876' role='button' aria-labelledby='approveText'></button>
                  </body>
                </html>
                """));

        WebElement button = driver().getSeleniumDriver().findElement(By.id("generated-9876"));
        AccessibilitySignal signal = AccessibleNameResolver.resolve(driver().getSeleniumDriver(), button);

        Assert.assertEquals(signal.role(), "button");
        Assert.assertTrue(signal.accessibleName().contains("اعتماد الطلب"));
    }

    @Test
    public void labelTextFallbackFindsArabicInputFieldTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <body dir='rtl'>
                    <form>
                      <label for='generatedFacilityName'>اسم المنشأة</label>
                      <input id='generatedFacilityName' />
                    </form>
                  </body>
                </html>
                """));

        QatraLocator facilityName = QatraLocator.primary(By.id("facility-name"))
                .named("Facility name textbox")
                .expectedRole("textbox")
                .expectedAccessibleName("اسم المنشأة")
                .fallbackLabelText("اسم المنشأة")
                .build();

        driver().element().smartType(facilityName, "منشأة تجريبية");

        Assert.assertEquals(
                driver().getSeleniumDriver().findElement(By.id("generatedFacilityName")).getAttribute("value"),
                "منشأة تجريبية"
        );
    }

    @Test
    public void placeholderFallbackHealsAccessibleArabicSearchFieldTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <body dir='rtl'>
                    <input id='searchGenerated123' placeholder='بحث عن منشأة' />
                  </body>
                </html>
                """));

        QatraLocator searchBox = QatraLocator.primary(By.id("search-box"))
                .named("Arabic facility search box")
                .expectedRole("textbox")
                .expectedAccessibleName("بحث عن منشأة")
                .fallbackPlaceholder("بحث عن منشأة")
                .build();

        driver().element().smartType(searchBox, "منشأة");

        Assert.assertEquals(
                driver().getSeleniumDriver().findElement(By.id("searchGenerated123")).getAttribute("value"),
                "منشأة"
        );
    }

    private static String dataUrl(String html) {
        return "data:text/html;charset=utf-8," + URLEncoder.encode(html, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
