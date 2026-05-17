package io.github.qatra.tests.examples;

import io.github.qatra.web.testng.QatraBaseTest;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Real-world style examples that run locally using data URLs.
 *
 * These tests demonstrate how QATRA can be used in practical Arabic/RTL web scenarios
 * without depending on external websites or production systems.
 */
public class QatraWebRealWorldExamplesTest extends QatraBaseTest {

    @Test
    public void arabicLoginSmokeWithAdaptiveActionsAndDiagnosticsTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <head>
                    <title>QATRA Arabic Login Example</title>
                    <style>
                      body { direction: rtl; font-family: Arial, sans-serif; }
                      #overlay { position: fixed; inset: 0; background: rgba(0,0,0,.08); z-index: 99; }
                      main { margin: 40px; }
                    </style>
                    <script>
                      window.addEventListener('load', function () {
                        setTimeout(function () {
                          document.getElementById('overlay').style.display = 'none';
                          const title = document.getElementById('login-title');
                          title.textContent = 'تسجيل الدخول';
                          title.setAttribute('dir', 'rtl');
                          title.style.direction = 'rtl';
                        }, 250);
                      });
                      function login() {
                        document.getElementById('message').textContent = 'مرحباً ' + document.getElementById('username').value;
                      }
                    </script>
                  </head>
                  <body dir='rtl'>
                    <div id='overlay' class='loading' aria-busy='true'></div>
                    <main>
                      <h1 id='login-title'>Loading...</h1>
                      <input id='username' aria-label='اسم المستخدم' />
                      <input id='password' type='password' aria-label='كلمة المرور' />
                      <button id='login' onclick='login()'>دخول</button>
                      <p id='message'></p>
                    </main>
                  </body>
                </html>
                """));

        driver().element()
                .waitUntilArabicTextReady(By.id("login-title"), "تسجيل الدخول")
                .adaptiveType(By.id("username"), "Husni")
                .adaptiveType(By.id("password"), "secret")
                .adaptiveClick(By.id("login"))
                .assertThat()
                .element(By.id("message"))
                .containsText("مرحباً Husni");

        driver().expect(By.id("login-title"))
                .exists()
                .isVisible()
                .text()
                    .contains("تسجيل الدخول")
                    .containsArabic()
                    .and()
                .rtl()
                    .hasArabicText()
                    .hasRtlDirection();

        driver().screenshot("real_world_arabic_login_smoke");
        driver().diagnostics("real_world_arabic_login_smoke");
    }

    @Test
    public void rtlPageQualitySmokeExampleTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <head><title>QATRA RTL Quality Example</title></head>
                  <body dir='rtl' style='direction: rtl; text-align: right;'>
                    <header><h1 id='headline' dir='rtl'>لوحة التحكم</h1></header>
                    <main>
                      <section id='summary' dir='rtl'>
                        <p>عدد الطلبات اليوم ١٢</p>
                        <p>آخر تحديث: QATRA 2026</p>
                      </section>
                    </main>
                  </body>
                </html>
                """));

        driver().browser().waitUntilPageReady();

        driver().expect(By.id("headline"))
                .exists()
                .isVisible()
                .rtl()
                    .hasValidArabicRendering();

        driver().rtl()
                .assertArabicTextExists()
                .scanPage()
                .report();
    }

    @Test
    public void componentBasedArabicWorkflowExampleTest() {
        driver().browser().navigateTo(dataUrl("""
                <!doctype html>
                <html lang='ar' dir='rtl'>
                  <head>
                    <title>QATRA Component Workflow Example</title>
                    <style>
                      body { direction: rtl; font-family: Arial, sans-serif; }
                      #toast { display: none; padding: 8px; border: 1px solid #0a0; }
                    </style>
                    <script>
                      function save() {
                        const city = document.getElementById('city').selectedOptions[0].textContent;
                        const tbody = document.querySelector('#visits-table tbody');
                        tbody.innerHTML = '<tr><td>منشأة تجريبية</td><td>' + city + '</td></tr>';
                        const toast = document.getElementById('toast');
                        toast.textContent = 'تم الحفظ بنجاح';
                        toast.style.display = 'block';
                      }
                    </script>
                  </head>
                  <body dir='rtl'>
                    <label for='city'>المدينة</label>
                    <select id='city'>
                      <option>جدة</option>
                      <option>الرياض</option>
                      <option>الدمام</option>
                    </select>
                    <button id='save' onclick='save()'>حفظ</button>
                    <div id='toast' role='alert'></div>
                    <table id='visits-table'>
                      <thead><tr><th>المنشأة</th><th>المدينة</th></tr></thead>
                      <tbody></tbody>
                    </table>
                  </body>
                </html>
                """));

        driver().dropdown(By.id("city"))
                .selectArabicText("الرياض")
                .assertSelectedArabicText("الرياض");

        driver().element().adaptiveClick(By.id("save"));

        driver().toast(By.id("toast"))
                .successMessageContains("تم الحفظ بنجاح");

        driver().webTable(By.id("visits-table"))
                .waitUntilRowsLoaded(1)
                .assertRowContainsArabicText("منشأة تجريبية")
                .assertRowContainsArabicText("الرياض");
    }

    private static String dataUrl(String html) {
        return "data:text/html;charset=utf-8," + URLEncoder.encode(html, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
