package io.github.qatra.tests.actions;

import io.github.qatra.web.testng.QatraBaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Smoke tests for adaptive element actions powered by the QATRA Adaptive Wait Engine.
 */
public class QatraAdaptiveElementActionsTest extends QatraBaseTest {

    @Test
    public void adaptiveClickWaitsForOverlayToDisappearTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html>
                  <head>
                    <title>QATRA Adaptive Click</title>
                    <style>
                      #overlay { position: fixed; inset: 0; background: rgba(0,0,0,.15); z-index: 9999; }
                    </style>
                    <script>
                      window.addEventListener('load', function () {
                        setTimeout(function () {
                          const overlay = document.getElementById('overlay');
                          overlay.style.display = 'none';
                          overlay.setAttribute('aria-busy', 'false');
                        }, 250);
                      });
                    </script>
                  </head>
                  <body>
                    <div id='overlay' class='loading' aria-busy='true'></div>
                    <button id='save' onclick="document.getElementById('result').textContent='Saved';">Save</button>
                    <p id='result'></p>
                  </body>
                </html>
                """));

        driver().element()
                .adaptiveClick(By.id("save"))
                .assertThat()
                .element(By.id("result"))
                .containsText("Saved");
    }

    @Test
    public void adaptiveTypeClearAndArabicReadyTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <head>
                    <title>QATRA Adaptive Arabic Input</title>
                    <script>
                      window.addEventListener('load', function () {
                        setTimeout(function () {
                          const title = document.getElementById('title');
                          title.textContent = 'تسجيل الدخول';
                          title.setAttribute('dir', 'rtl');
                          title.style.direction = 'rtl';
                        }, 200);
                      });
                    </script>
                  </head>
                  <body dir='rtl' style='direction: rtl;'>
                    <h1 id='title' dir='rtl'>Loading...</h1>
                    <input id='name' value='old value' />
                  </body>
                </html>
                """));

        driver().element()
                .waitUntilArabicTextReady(By.id("title"), "تسجيل الدخول")
                .adaptiveClear(By.id("name"))
                .adaptiveType(By.id("name"), "منشأة تجريبية")
                .assertThat()
                .element(By.id("name"))
                .hasValue("منشأة تجريبية");
    }

    @Test
    public void adaptiveSelectAndSubmitTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <head><title>QATRA Adaptive Select</title></head>
                  <body dir='rtl'>
                    <select id='city'>
                      <option value='jeddah'>جدة</option>
                      <option value='riyadh'>الرياض</option>
                      <option value='dammam'>الدمام</option>
                    </select>
                    <input id='search' />
                    <p id='result'></p>
                    <script>
                      document.getElementById('search').addEventListener('keydown', function(e) {
                        if (e.key === 'Enter') {
                          document.getElementById('result').textContent = this.value + ' - ' + document.getElementById('city').value;
                        }
                      });
                    </script>
                  </body>
                </html>
                """));

        driver().element()
                .adaptiveSelectByText(By.id("city"), "الرياض")
                .adaptiveTypeAndPress(By.id("search"), "بحث", Keys.ENTER)
                .assertThat()
                .element(By.id("result"))
                .containsText("بحث - riyadh");
    }

    @Test
    public void adaptiveHoverAndDoubleClickTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html>
                  <head><title>QATRA Adaptive Mouse Actions</title></head>
                  <body>
                    <button id='hoverTarget' onmouseover="document.getElementById('status').textContent='Hovered';">Hover</button>
                    <button id='doubleTarget' ondblclick="document.getElementById('status').textContent='Double clicked';">Double</button>
                    <p id='status'></p>
                  </body>
                </html>
                """));

        driver().element()
                .adaptiveHover(By.id("hoverTarget"))
                .assertThat()
                .element(By.id("status"))
                .containsText("Hovered");

        driver().element()
                .adaptiveDoubleClick(By.id("doubleTarget"))
                .assertThat()
                .element(By.id("status"))
                .containsText("Double clicked");
    }

    private static String dataUrl(String html) {
        return "data:text/html;charset=utf-8," + URLEncoder.encode(html, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
