package io.github.qatra.tests.components;

import io.github.qatra.web.testng.QatraBaseTest;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/** Smoke tests for the web-focused component layer. */
public class QatraWebComponentsTest extends QatraBaseTest {

    @Test
    public void nativeDropdownSelectsArabicTextTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <head><title>QATRA Dropdown Component</title></head>
                  <body dir='rtl'>
                    <select id='city'>
                      <option>جدة</option>
                      <option>الرياض</option>
                      <option>الدمام</option>
                    </select>
                  </body>
                </html>
                """));

        driver().dropdown(By.id("city"))
                .withTimeout(Duration.ofSeconds(5))
                .selectArabicText("الرياض")
                .assertSelectedArabicText("الرياض");
    }

    @Test
    public void dynamicTableFindsArabicRowTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <head><title>QATRA Table Component</title></head>
                  <body dir='rtl'>
                    <table id='visits-table'>
                      <tbody>
                        <tr><td>VIS-001</td><td>منشأة تجريبية</td></tr>
                        <tr><td>VIS-002</td><td>زيارة ميدانية</td></tr>
                      </tbody>
                    </table>
                  </body>
                </html>
                """));

        driver().webTable(By.id("visits-table"))
                .withRows(By.cssSelector("tbody tr"))
                .withTimeout(Duration.ofSeconds(5))
                .waitUntilRowsLoaded(2)
                .assertRowContainsArabicText("منشأة تجريبية");
    }

    @Test
    public void toastWaitsForArabicSuccessMessageTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <head>
                    <title>QATRA Toast Component</title>
                    <script>
                      window.addEventListener('load', function () {
                        setTimeout(function () {
                          const toast = document.getElementById('toast');
                          toast.textContent = 'تم الحفظ بنجاح';
                          toast.style.display = 'block';
                        }, 200);
                      });
                    </script>
                  </head>
                  <body dir='rtl'>
                    <div id='toast' role='alert' style='display:none'></div>
                  </body>
                </html>
                """));

        driver().toast(By.id("toast"))
                .withTimeout(Duration.ofSeconds(5))
                .successMessageContains("تم الحفظ");
    }

    @Test
    public void adaptiveActionsSupportDynamicWebFormsTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html>
                  <head><title>QATRA Adaptive Actions</title></head>
                  <body>
                    <input id='username' />
                    <button id='save' onclick="document.getElementById('result').textContent='Saved ' + document.getElementById('username').value;">Save</button>
                    <p id='result'></p>
                  </body>
                </html>
                """));

        driver().element()
                .adaptiveType(By.id("username"), "Husni")
                .adaptiveClick(By.id("save"))
                .assertThat()
                .element(By.id("result"))
                .containsText("Saved Husni");
    }

    private static String dataUrl(String html) {
        return "data:text/html;charset=utf-8," + URLEncoder.encode(html, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
