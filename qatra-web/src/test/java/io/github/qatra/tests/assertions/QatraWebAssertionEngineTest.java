package io.github.qatra.tests.assertions;

import io.github.qatra.web.assertions.engine.QatraAssert;
import io.github.qatra.web.testng.QatraBaseTest;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Smoke tests for the focused QATRA Web Assertion Engine. */
public class QatraWebAssertionEngineTest extends QatraBaseTest {

    @Test
    public void webAssertionEngineValidatesArabicRtlAndVisualStateTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <head><title>QATRA Assertion Engine</title></head>
                  <body dir='rtl' style='direction:rtl'>
                    <main>
                      <h1 id='title' dir='rtl' style='direction:rtl'>تسجيل الدخول ١٢٣</h1>
                      <input id='username' value='husni' />
                    </main>
                  </body>
                </html>
                """));

        driver().expect(By.id("title"))
                .exists()
                .isVisible()
                .text()
                    .contains("تسجيل")
                    .containsArabic()
                    .containsArabicDigits()
                    .and()
                .rtl()
                    .hasArabicText()
                    .hasReadableArabicText()
                    .hasRtlDirection()
                    .hasArabicDigits()
                    .hasValidMixedArabicEnglishLayout()
                    .and()
                .encoding()
                    .isUtf8SafeContent()
                    .and()
                .visual()
                    .isDisplayed()
                    .isInsideViewport()
                    .notCovered();

        driver().assertThat()
                .expect(By.id("username"))
                .hasValue("husni");
    }

    @Test
    public void staticEntryPointDetectsCleanArabicTextTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <head><title>QATRA Static Assert</title></head>
                  <body dir='rtl' style='direction:rtl'>
                    <p id='message' dir='rtl'>مرحبا بك في قطرة</p>
                  </body>
                </html>
                """));

        QatraAssert.that(driver().getSeleniumDriver(), By.id("message"))
                .exists()
                .rtl()
                    .hasValidArabicRendering();
    }

    @Test
    public void encodingAssertionFailsForMojibakeTextTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html>
                  <head><title>QATRA Encoding Negative</title></head>
                  <body>
                    <p id='broken'>ØªØ³Ø¬ÙŠÙ„ Ø§Ù„Ø¯Ø®ÙˆÙ„</p>
                  </body>
                </html>
                """));

        boolean failedAsExpected = false;
        try {
            driver().expect(By.id("broken"))
                    .encoding()
                    .hasNoMojibake();
        } catch (AssertionError expected) {
            String message = expected.getMessage();
            failedAsExpected = message.contains("No mojibake")
                    && message.contains("QATRA Assertion Diagnostics")
                    && message.contains("Detected issue")
                    && message.contains("Mojibake / encoding corruption")
                    && message.contains("Screenshot")
                    && message.contains("Page source")
                    && message.contains("Browser logs");
        }

        Assert.assertTrue(failedAsExpected, "Mojibake assertion should fail with rich QATRA diagnostics.");
    }

    private static String dataUrl(String html) {
        return "data:text/html;charset=utf-8," + URLEncoder.encode(html, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
