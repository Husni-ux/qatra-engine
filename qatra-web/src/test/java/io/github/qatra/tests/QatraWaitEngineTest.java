package io.github.qatra.tests;

import io.github.qatra.web.testng.QatraBaseTest;
import io.github.qatra.web.waits.QatraWait;
import io.github.qatra.web.waits.WaitOptions;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class QatraWaitEngineTest extends QatraBaseTest {

    @Test
    public void browserWaitUntilPageReadyKeepsFluentChainTest() {
        driver()
                .browser()
                .navigateTo(dataUrl("""
                        <!doctype html>
                        <html>
                          <head><title>QATRA Wait Chain</title></head>
                          <body><h1 id='title'>Ready</h1></body>
                        </html>
                        """))
                .waitUntilPageReady()
                .assertThat()
                .browser()
                .title()
                .contains("QATRA Wait Chain");
    }

    @Test
    public void qatraWaitArabicRtlAndEncodingConditionsTest() {
        driver()
                .browser()
                .navigateTo(dataUrl("""
                        <!doctype html>
                        <html lang='ar' dir='rtl'>
                          <head>
                            <title>QATRA Arabic Waits</title>
                            <script>
                              window.addEventListener('load', function () {
                                setTimeout(function () {
                                  const status = document.getElementById('status');
                                  status.textContent = 'مرحبا بك في قطرة';
                                  status.setAttribute('dir', 'rtl');
                                  status.style.direction = 'rtl';
                                }, 300);
                              });
                            </script>
                          </head>
                          <body dir='rtl'>
                            <h1 id='status'>Loading...</h1>
                          </body>
                        </html>
                        """));

        WaitOptions options = WaitOptions.builder()
                .timeout(Duration.ofSeconds(5))
                .pollingInterval(Duration.ofMillis(100))
                .build();

        QatraWait.forElement(driver().getSeleniumDriver(), By.id("status"), options)
                .waitUntilArabicTextIsVisible("مرحبا")
                .waitUntilTextIsNotBroken()
                .waitUntilRtlDirectionApplied()
                .waitUntilArabicTextRenderedCorrectly();

        driver().assertThat()
                .element(By.id("status"))
                .containsText("مرحبا")
                .hasDirectionRTL();
    }

    @Test
    public void qatraWaitCustomComponentReadyAndStableTest() {
        driver()
                .browser()
                .navigateTo(dataUrl("""
                        <!doctype html>
                        <html>
                          <head>
                            <title>QATRA Component Wait</title>
                            <script>
                              window.addEventListener('load', function () {
                                setTimeout(function () {
                                  const widget = document.getElementById('city-widget');
                                  widget.className = 'dropdown ready';
                                  widget.setAttribute('aria-busy', 'false');
                                  widget.textContent = 'الرياض';
                                }, 300);
                              });
                            </script>
                          </head>
                          <body>
                            <div id='city-widget' class='dropdown loading' aria-busy='true'>Loading</div>
                          </body>
                        </html>
                        """));

        QatraWait.forElement(driver().getSeleniumDriver(), By.id("city-widget"))
                .waitUntilCustomComponentReady()
                .waitUntilElementIsStable()
                .waitUntilArabicTextIsVisible("الرياض");

        driver().assertThat()
                .element(By.id("city-widget"))
                .containsText("الرياض");
    }

    private String dataUrl(String html) {
        return "data:text/html;charset=utf-8," + URLEncoder.encode(html, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
