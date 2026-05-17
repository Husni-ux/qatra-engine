package io.github.qatra.tests.locators;

import io.github.qatra.web.locators.LocatorHealingStatus;
import io.github.qatra.web.locators.LocatorResolution;
import io.github.qatra.web.locators.healing.components.ComponentLocatorStrategy;
import io.github.qatra.web.testng.QatraBaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/** Tests for Arabic component-intent based self-healing. */
public class QatraArabicComponentSelfHealingTest extends QatraBaseTest {

    @Test
    public void arabicDropdownIsHealedByLabelAndSelectsOptionTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <body dir='rtl'>
                    <label id='cityLabel'>المدينة</label>
                    <select id='generated-city-select' aria-labelledby='cityLabel'>
                      <option>جدة</option>
                      <option>الرياض</option>
                      <option>الدمام</option>
                    </select>
                  </body>
                </html>
                """));

        driver().componentHealing()
                .withTimeout(Duration.ofSeconds(5))
                .dropdown("المدينة")
                .selectArabicText("الرياض")
                .assertSelectedArabicText("الرياض");
    }

    @Test
    public void arabicTableIsHealedByContainedBusinessTextTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <body dir='rtl'>
                    <section class='dynamic-grid-wrapper'>
                      <table id='generated-table-7788' role='table'>
                        <tbody>
                          <tr><td>VIS-001</td><td>منشأة تجريبية</td></tr>
                          <tr><td>VIS-002</td><td>زيارة ميدانية</td></tr>
                        </tbody>
                      </table>
                    </section>
                  </body>
                </html>
                """));

        driver().componentHealing()
                .withTimeout(Duration.ofSeconds(5))
                .tableContaining("منشأة تجريبية")
                .withRows(By.cssSelector("tbody tr"))
                .waitUntilRowsLoaded(2)
                .assertRowContainsArabicText("منشأة تجريبية");
    }

    @Test
    public void arabicModalButtonIsHealedByModalContextAndActionTextTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <body dir='rtl'>
                    <div id='confirmModalGenerated' role='dialog' aria-modal='true'>
                      <h2>تأكيد الحفظ</h2>
                      <p>هل أنت متأكد من حفظ الطلب؟</p>
                      <button id='generatedConfirmButton' onclick="document.getElementById('result').textContent='تم التأكيد';">تأكيد</button>
                    </div>
                    <p id='result'></p>
                  </body>
                </html>
                """));

        WebElement confirm = driver().componentHealing()
                .withTimeout(Duration.ofSeconds(5))
                .modalButton("تأكيد الحفظ", "تأكيد");
        confirm.click();

        Assert.assertEquals(driver().getSeleniumDriver().findElement(By.id("result")).getText(), "تم التأكيد");
    }

    @Test
    public void arabicInputIsHealedByNearbyLabelTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <body dir='rtl'>
                    <form>
                      <label for='facilityGenerated991'>اسم المنشأة</label>
                      <input id='facilityGenerated991' />
                    </form>
                  </body>
                </html>
                """));

        WebElement input = driver().componentHealing()
                .withTimeout(Duration.ofSeconds(5))
                .inputByArabicLabel("اسم المنشأة");
        input.sendKeys("منشأة تجريبية");

        Assert.assertEquals(input.getAttribute("value"), "منشأة تجريبية");
    }

    @Test
    public void componentLocatorStrategyStillProducesHealingReportTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <body dir='rtl'>
                    <div role='alert' class='toast-success'>تم الحفظ بنجاح</div>
                  </body>
                </html>
                """));

        LocatorResolution resolution = driver().componentHealing()
                .withTimeout(Duration.ofSeconds(5))
                .resolve(ComponentLocatorStrategy.toastContainingArabicText("تم الحفظ بنجاح"));

        Assert.assertNotNull(resolution.element());
        Assert.assertTrue(resolution.status() == LocatorHealingStatus.FOUND_PRIMARY
                || resolution.status() == LocatorHealingStatus.HEALED_WITH_FALLBACK);
        Assert.assertTrue(resolution.element().getText().contains("تم الحفظ"));
    }

    private static String dataUrl(String html) {
        return "data:text/html;charset=utf-8," + URLEncoder.encode(html, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
