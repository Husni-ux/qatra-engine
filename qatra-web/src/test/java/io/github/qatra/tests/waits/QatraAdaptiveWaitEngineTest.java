package io.github.qatra.tests.waits;

import io.github.qatra.web.testng.QatraBaseTest;
import io.github.qatra.web.waits.adaptive.QatraAdaptiveWait;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Smoke tests for the QATRA Adaptive Wait Engine.
 */
public class QatraAdaptiveWaitEngineTest extends QatraBaseTest {

    @Test
    public void adaptiveWaitDetectsArabicTextReadinessTest() {
        String html = """
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <head><title>QATRA Adaptive Arabic Wait</title></head>
                  <body dir='rtl' style='direction: rtl;'>
                    <h1 id='login-title' dir='rtl'>تسجيل الدخول</h1>
                  </body>
                </html>
                """;

        driver().browser().navigateTo(dataUrl(html));

        QatraAdaptiveWait.forElement(driver().getSeleniumDriver(), By.id("login-title"))
                .withTimeout(Duration.ofSeconds(5))
                .pollingEvery(Duration.ofMillis(100))
                .untilArabicTextReady("تسجيل الدخول");
    }

    @Test
    public void adaptiveWaitDetectsClickReadinessTest() {
        String html = """
                <!doctype html>
                <html>
                  <head><title>QATRA Adaptive Click Wait</title></head>
                  <body>
                    <button id='submit'>Submit</button>
                  </body>
                </html>
                """;

        driver().browser().navigateTo(dataUrl(html));

        QatraAdaptiveWait.forElement(driver().getSeleniumDriver(), By.id("submit"))
                .withTimeout(Duration.ofSeconds(5))
                .untilReadyForClick();
    }

    @Test
    public void adaptiveWaitDetectsPageReadinessTest() {
        String html = """
                <!doctype html>
                <html>
                  <head><title>QATRA Adaptive Page Wait</title></head>
                  <body><main id='content'>Ready</main></body>
                </html>
                """;

        driver().browser().navigateTo(dataUrl(html));

        QatraAdaptiveWait.forPage(driver().getSeleniumDriver())
                .withTimeout(Duration.ofSeconds(5))
                .withQuietWindow(Duration.ofMillis(200))
                .untilPageFullyReady();
    }

    private static String dataUrl(String html) {
        return "data:text/html;charset=utf-8," + URLEncoder.encode(html, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
