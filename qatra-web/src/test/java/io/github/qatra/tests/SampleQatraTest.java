package io.github.qatra.tests;

import io.github.qatra.web.testng.QatraBaseTest;
import io.github.qatra.web.page.QatraComponent;
import io.github.qatra.web.page.QatraElement;
import io.github.qatra.web.page.QatraElementCollection;
import io.github.qatra.web.page.QatraFindBy;
import io.github.qatra.web.page.QatraPage;
import io.github.qatra.web.page.QatraPageLoaded;
import io.github.qatra.web.page.QatraPageUrl;
import io.github.qatra.web.data.QatraData;
import io.github.qatra.web.data.QatraDataFile;
import io.github.qatra.web.data.QatraDataProvider;
import io.github.qatra.web.data.QatraDataRecord;
import io.github.qatra.web.data.QatraDataSet;
import io.github.qatra.web.rtl.RtlIssueType;
import io.github.qatra.web.rtl.RtlBaselineComparisonResult;
import io.github.qatra.web.rtl.RtlQualityGateResult;
import io.github.qatra.web.rtl.RtlScanResult;
import io.github.qatra.web.rtl.RtlScanConfig;
import io.github.qatra.web.stability.QatraParallel;
import io.github.qatra.web.stability.QatraRetry;
import io.github.qatra.web.stability.QatraRetryAnalyzer;
import io.github.qatra.web.stability.QatraStability;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import com.sun.net.httpserver.HttpServer;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * QATRA sample tests.
 *
 * <p>These tests use small local data URLs, so they are stable and do not depend
 * on external websites being available.</p>
 */
public class SampleQatraTest extends QatraBaseTest {

    private static final AtomicInteger RETRY_SAMPLE_ATTEMPTS = new AtomicInteger(0);

    @Test
    public void browserTitleTest() {
        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html>
                          <head><title>QATRA Demo Page</title></head>
                          <body>
                            <h1 id='title'>QATRA Test Automation</h1>
                          </body>
                        </html>
                        """))
                .assertThat()
                .browser().title().contains("QATRA");
    }

    @Test
    public void elementTypingTest() {
        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html>
                          <head><title>QATRA Form</title></head>
                          <body>
                            <input id='username' name='username' />
                            <button id='loginBtn' onclick="document.getElementById('flash').textContent='Login clicked';">Login</button>
                            <div id='flash'></div>
                          </body>
                        </html>
                        """))
                .element()
                .type(By.id("username"), "tomsmith")
                .click(By.id("loginBtn"))
                .assertThat()
                .element(By.id("username")).hasAttribute("value", "tomsmith")
                .element(By.id("flash")).containsText("Login clicked");
    }

    @Test
    public void arabicPageDirectionIsRTL() {
        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html lang='ar' dir='rtl'>
                          <head><title>أخبار عربية</title></head>
                          <body dir='rtl' style='direction: rtl;'>
                            <h1 id='headline'>أخبار</h1>
                          </body>
                        </html>
                        """))
                .assertThat()
                .element(By.tagName("body")).isRTL()
                .element(By.id("headline")).containsText("أخبار");
    }

    /**
     * Example of manual screenshot capture.
     */
    @Test
    public void manualScreenshotTest() {
        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html>
                          <head><title>Screenshot Demo</title></head>
                          <body><h1 id='status'>Ready for screenshot</h1></body>
                        </html>
                        """))
                .assertThat()
                .element(By.id("status")).containsText("Ready");

        driver().screenshot("manual_screenshot_demo");
    }





    /**
     * Example of manual diagnostics capture: browser state, page source, and console logs.
     */
    @Test
    public void manualDiagnosticsTest() {
        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html>
                          <head>
                            <title>Diagnostics Demo</title>
                            <script>console.error('QATRA diagnostics demo console error');</script>
                          </head>
                          <body><h1 id='status'>Diagnostics ready</h1></body>
                        </html>
                        """))
                .assertThat()
                .browser().title().contains("Diagnostics");

        driver().diagnostics("manual_diagnostics_demo");
    }


    @Test
    public void advancedElementAssertionsTest() {
        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html lang='ar' dir='rtl'>
                          <head><title>Advanced Assertions Demo</title></head>
                          <body dir='rtl' style='direction: rtl;'>
                            <input id='username' value='admin' class='form-control active' />
                            <button id='disabledBtn' disabled>Disabled</button>
                            <input id='remember' type='checkbox' checked />
                            <input id='optional' type='checkbox' />
                            <div id='hiddenBox' style='display:none'>Hidden</div>
                            <span class='item'>One</span>
                            <span class='item'>Two</span>
                            <p id='english-price' style='color: rgb(255, 0, 0);'>Price 123 SAR</p>
                            <p id='arabic-message'>مرحبا ١٢٣</p>
                          </body>
                        </html>
                        """))
                .assertThat()
                .element(By.id("username"))
                    .exists()
                    .isVisible()
                    .isEnabled()
                    .hasValue("admin")
                    .hasAttribute("class", "form-control active")
                    .hasClass("active")
                    .hasTagName("input")
                .element(By.id("disabledBtn"))
                    .exists()
                    .isDisabled()
                .element(By.id("remember"))
                    .isSelected()
                    .isChecked()
                .element(By.id("optional"))
                    .isNotSelected()
                    .isUnchecked()
                .element(By.id("hiddenBox"))
                    .isNotVisible()
                .element(By.id("missingElement"))
                    .doesNotExist()
                .element(By.className("item"))
                    .hasCount(2)
                    .hasMinimumCount(1)
                .element(By.id("english-price"))
                    .containsText("123")
                    .hasEnglishDigits()
                    .cssValueContains("color", "255")
                .element(By.id("arabic-message"))
                    .hasArabicText()
                    .hasArabicDigits()
                    .hasNoBrokenArabicCharacters()
                    .isRTL();
    }


    @Test
    public void smartWaitsAndBetterActionsTest() {
        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html>
                          <head>
                            <title>Smart Wait Demo</title>
                            <script>
                              window.addEventListener('load', function() {
                                setTimeout(function() {
                                  document.getElementById('delayedBtn').removeAttribute('disabled');
                                  document.getElementById('message').textContent = 'Ready';
                                }, 300);
                              });
                            </script>
                          </head>
                          <body>
                            <input id='search' value='' />
                            <button id='delayedBtn' disabled onclick="document.getElementById('message').textContent='Clicked';">Delayed Button</button>
                            <div id='message'>Loading</div>
                          </body>
                        </html>
                        """))
                .element()
                .waitUntilPageReady()
                .waitUntilVisible(By.id("search"))
                .highlight(By.id("search"))
                .clearAndType(By.id("search"), "QATRA")
                .append(By.id("search"), " Engine")
                .typeAndPress(By.id("search"), "QATRA Engine", Keys.TAB)
                .waitUntilTextContains(By.id("message"), "Ready")
                .waitUntilClickable(By.id("delayedBtn"))
                .click(By.id("delayedBtn"))
                .assertThat()
                .element(By.id("search")).hasValue("QATRA Engine")
                .element(By.id("message")).containsText("Clicked");
    }



    @Test
    public void rtlEngineExpansionTest() {
        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html lang='ar' dir='rtl'>
                          <head><title>محرك QATRA RTL</title></head>
                          <body dir='rtl' style='direction: rtl;'>
                            <main id='content' dir='rtl' style='direction: rtl;'>
                              <h1 id='arabic-title'>مرحبا بكم في قطرة</h1>
                              <p id='price'>السعر ١٢٣ ريال</p>
                              <label for='name'>الاسم</label>
                              <input id='name' dir='rtl' style='direction: rtl;' placeholder='اكتب اسمك' aria-label='اسم المستخدم' />
                              <section id='summary'>تم تحميل الصفحة العربية بنجاح</section>
                            </main>
                          </body>
                        </html>
                        """))
                .assertThat()
                .element(By.tagName("body"))
                    .hasDirectionRTL()
                    .hasValidArabicRendering()
                .element(By.id("arabic-title"))
                    .hasArabicText()
                    .usesRtlDirectionWhenArabic()
                    .hasNoEncodingIssues()
                .element(By.id("price"))
                    .hasArabicDigits()
                    .hasValidArabicRendering()
                .element(By.id("name"))
                    .hasArabicPlaceholder()
                    .hasPlaceholderDirectionRTL();

        driver().rtl()
                .assertPageDirectionIsRTL()
                .assertArabicTextExists()
                .assertNoBrokenArabicCharacters()
                .scanPage()
                .report()
                .failOnIssues();
    }



    @Test
    public void rtlScannerEnhancementsTest() {
        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html lang='ar'>
                          <head><title>RTL Scanner Issues Demo</title></head>
                          <body style='direction: ltr;'>
                            <h1 id='wrong-direction'>مرحبا بالمستخدم</h1>
                            <input id='bad-placeholder' placeholder='اكتب اسمك' />
                            <p id='broken-encoding'>Ø§Ù„Ù†Øµ Ø§Ù„Ø¹Ø±Ø¨ÙŠ</p>
                            <p id='digits' data-qatra-digits='arabic'>رقم الطلب 123</p>
                            <p id='mixed' dir='rtl' style='direction: rtl;'>مرحبا QATRA user</p>
                            <p id='alignment' dir='rtl' style='direction: rtl; text-align: left;'>النص العربي</p>
                          </body>
                        </html>
                        """));

        RtlScanConfig scanAllRules = RtlScanConfig.builder()
                .scanDirection(true)
                .scanPlaceholder(true)
                .scanEncoding(true)
                .scanDigits(true)
                .scanMixedDirection(true)
                .scanAlignment(true)
                .failOn(RtlScanConfig.FAIL_ON_ERRORS)
                .build();

        RtlScanResult result = driver().rtl()
                .withConfig(scanAllRules)
                .scanPage()
                .report()
                .result();

        Assert.assertTrue(result.issueCount() > 0, "RTL scanner should detect demo issues.");
        Assert.assertTrue(result.hasIssueType(RtlIssueType.DIRECTION), "Direction issue should be detected.");
        Assert.assertTrue(result.hasIssueType(RtlIssueType.PLACEHOLDER), "Placeholder issue should be detected.");
        Assert.assertTrue(result.hasIssueType(RtlIssueType.ENCODING), "Encoding issue should be detected.");
        Assert.assertTrue(result.hasIssueType(RtlIssueType.DIGITS), "Digits issue should be detected.");
        Assert.assertTrue(result.hasIssueType(RtlIssueType.MIXED_DIRECTION), "Mixed direction advisory should be detected.");
        Assert.assertTrue(result.hasIssueType(RtlIssueType.ALIGNMENT), "Alignment advisory should be detected.");
        Assert.assertTrue(result.hasErrors(), "Broken Arabic encoding should be reported as ERROR.");
    }



    @Test
    public void rtlScannerConfigurationTest() {
        RtlScanConfig config = RtlScanConfig.builder()
                .scanDigits(false)
                .failOn(RtlScanConfig.FAIL_ON_ERRORS)
                .build();

        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html lang='ar'>
                          <head><title>RTL Scanner Config Demo</title></head>
                          <body style='direction: ltr;'>
                            <p id='wrong-direction' data-qatra-digits='arabic'>مرحبا 123</p>
                          </body>
                        </html>
                        """));

        RtlScanResult result = driver().rtl()
                .withConfig(config)
                .scanPage()
                .report()
                .failOnConfiguredLevel()
                .result();

        Assert.assertTrue(result.hasIssueType(RtlIssueType.DIRECTION), "Direction issue should still be detected.");
        Assert.assertFalse(result.hasIssueType(RtlIssueType.DIGITS), "Digits scan should be disabled by configuration.");
        Assert.assertFalse(result.hasErrors(), "The demo page should only produce warnings when digits scan is disabled.");
        Assert.assertTrue(result.warningCount() > 0, "At least one warning should be reported.");
    }



    @Test
    public void rtlReportExportTest() throws Exception {
        RtlScanConfig config = RtlScanConfig.builder()
                .exportReport(true)
                .reportFormats("txt,json")
                .reportDir("target/qatra-reports/rtl")
                .reportFileName("rtl-scan-report")
                .failOn(RtlScanConfig.FAIL_ON_ERRORS)
                .build();

        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html lang='ar'>
                          <head><title>RTL Report Export Demo</title></head>
                          <body style='direction: ltr;'>
                            <h1 id='title'>مرحبا بالمستخدم</h1>
                            <input id='name' placeholder='اكتب اسمك' />
                            <p id='digits' data-qatra-digits='arabic'>رقم الطلب 123</p>
                          </body>
                        </html>
                        """));

        RtlScanResult result = driver().rtl()
                .withConfig(config)
                .scanPage()
                .report()
                .failOnConfiguredLevel()
                .result();

        Path txtReport = Path.of("target", "qatra-reports", "rtl", "rtl-scan-report.txt");
        Path jsonReport = Path.of("target", "qatra-reports", "rtl", "rtl-scan-report.json");

        Assert.assertTrue(Files.exists(txtReport), "RTL TXT report should be exported.");
        Assert.assertTrue(Files.exists(jsonReport), "RTL JSON report should be exported.");
        Assert.assertTrue(Files.readString(txtReport).contains("QATRA RTL Scan Summary"), "TXT report should contain the scan summary.");
        Assert.assertTrue(Files.readString(jsonReport).contains("\"issueCount\""), "JSON report should contain issueCount.");
        Assert.assertTrue(result.hasIssues(), "The export demo page should produce RTL issues.");
    }



    @Test
    public void rtlReportHistoryAndHtmlSummaryTest() throws Exception {
        RtlScanConfig config = RtlScanConfig.builder()
                .exportReport(true)
                .reportFormats("txt,json,html")
                .reportDir("target/qatra-reports/rtl")
                .reportFileName("rtl-history-demo")
                .historyEnabled(true)
                .historyDir("target/qatra-reports/rtl/history")
                .historyIndexEnabled(true)
                .failOn(RtlScanConfig.FAIL_ON_ERRORS)
                .build();

        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html lang='ar'>
                          <head><title>RTL History Demo</title></head>
                          <body style='direction: ltr;'>
                            <h1 id='title'>مرحبا بالمستخدم</h1>
                            <input id='name' placeholder='اكتب اسمك' />
                            <p id='digits' data-qatra-digits='arabic'>رقم الطلب 123</p>
                          </body>
                        </html>
                        """));

        RtlScanResult result = driver().rtl()
                .withConfig(config)
                .scanPage()
                .report()
                .failOnConfiguredLevel()
                .result();

        Path reportDir = Path.of("target", "qatra-reports", "rtl");
        Path htmlReport = reportDir.resolve("rtl-history-demo.html");
        Path historyDir = reportDir.resolve("history");
        Path indexReport = historyDir.resolve("index.html");

        Assert.assertTrue(Files.exists(htmlReport), "RTL HTML summary report should be exported.");
        Assert.assertTrue(Files.exists(indexReport), "RTL history index should be exported.");
        Assert.assertTrue(Files.readString(htmlReport).contains("QATRA RTL Scan Report"), "HTML report should contain the report title.");
        Assert.assertTrue(Files.readString(indexReport).contains("QATRA RTL Report History"), "History index should contain its title.");

        boolean historicalHtmlExists;
        try (var files = Files.list(historyDir)) {
            historicalHtmlExists = files.anyMatch(path -> path.getFileName().toString().startsWith("rtl-history-demo_")
                    && path.getFileName().toString().endsWith(".html"));
        }

        Assert.assertTrue(historicalHtmlExists, "A timestamped historical HTML report should be exported.");
        Assert.assertTrue(result.hasIssues(), "The history demo page should produce RTL issues.");
    }




    @Test
    public void rtlBaselineRegressionComparisonTest() throws Exception {
        Path baselinePath = Path.of("target", "qatra-reports", "rtl", "baseline", "rtl-baseline-demo.json");
        Files.deleteIfExists(baselinePath);

        RtlScanConfig config = RtlScanConfig.builder()
                .baselineEnabled(true)
                .baselinePath(baselinePath.toString())
                .baselineFailOnNewIssues(false)
                .baselineReportExport(true)
                .baselineReportFileName("rtl-baseline-demo-comparison")
                .failOn(RtlScanConfig.FAIL_ON_ERRORS)
                .build();

        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html lang='ar'>
                          <head><title>RTL Baseline Original</title></head>
                          <body style='direction: ltr;'>
                            <h1 id='wrong-direction'>مرحبا بالمستخدم</h1>
                            <input id='bad-placeholder' placeholder='اكتب اسمك' />
                          </body>
                        </html>
                        """));

        driver().rtl()
                .withConfig(config)
                .scanPage()
                .saveBaseline();

        Assert.assertTrue(Files.exists(baselinePath), "RTL baseline file should be created.");

        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html lang='ar'>
                          <head><title>RTL Baseline Current</title></head>
                          <body style='direction: ltr;'>
                            <h1 id='wrong-direction'>مرحبا بالمستخدم</h1>
                            <input id='fixed-placeholder' dir='rtl' style='direction: rtl;' placeholder='اكتب اسمك' />
                            <p id='digits' data-qatra-digits='arabic'>رقم الطلب 123</p>
                          </body>
                        </html>
                        """));

        RtlBaselineComparisonResult comparison = driver().rtl()
                .withConfig(config)
                .scanPage()
                .compareWithBaseline()
                .baselineComparison();

        Path comparisonTxt = Path.of("target", "qatra-reports", "rtl", "rtl-baseline-demo-comparison.txt");
        Path comparisonJson = Path.of("target", "qatra-reports", "rtl", "rtl-baseline-demo-comparison.json");
        Path comparisonHtml = Path.of("target", "qatra-reports", "rtl", "rtl-baseline-demo-comparison.html");

        Assert.assertTrue(comparison.hasNewIssues(), "Current scan should introduce at least one new RTL issue.");
        Assert.assertTrue(comparison.hasExistingIssues(), "Current scan should keep at least one known RTL issue.");
        Assert.assertTrue(comparison.hasResolvedIssues(), "Current scan should resolve at least one baseline issue.");
        Assert.assertTrue(Files.exists(comparisonTxt), "Baseline comparison TXT report should be exported.");
        Assert.assertTrue(Files.exists(comparisonJson), "Baseline comparison JSON report should be exported.");
        Assert.assertTrue(Files.exists(comparisonHtml), "Baseline comparison HTML report should be exported.");
    }



    @Test
    public void rtlQualityGateTest() throws Exception {
        RtlScanConfig config = RtlScanConfig.builder()
                .qualityGateEnabled(true)
                .qualityGateMinScore(95)
                .qualityGateMaxErrors(0)
                .qualityGateMaxWarnings(0)
                .qualityGateFailOnFailure(false)
                .qualityGateReportExport(true)
                .qualityGateReportFileName("rtl-quality-gate-demo")
                .failOn(RtlScanConfig.FAIL_ON_NONE)
                .build();

        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html lang='ar'>
                          <head><title>RTL Quality Gate Demo</title></head>
                          <body style='direction: ltr;'>
                            <h1 id='wrong-direction'>مرحبا بالمستخدم</h1>
                            <input id='bad-placeholder' placeholder='اكتب اسمك' />
                            <p id='digits' data-qatra-digits='arabic'>رقم الطلب 123</p>
                            <p id='mixed'>مرحبا QATRA</p>
                          </body>
                        </html>
                        """));

        RtlQualityGateResult gate = driver().rtl()
                .withConfig(config)
                .scanPage()
                .report()
                .qualityGate()
                .qualityGateResult();

        Path gateTxt = Path.of("target", "qatra-reports", "rtl", "rtl-quality-gate-demo.txt");
        Path gateJson = Path.of("target", "qatra-reports", "rtl", "rtl-quality-gate-demo.json");
        Path gateHtml = Path.of("target", "qatra-reports", "rtl", "rtl-quality-gate-demo.html");

        Assert.assertTrue(gate.failed(), "Quality gate should fail because the demo page has warnings and a strict threshold.");
        Assert.assertTrue(gate.score() < 95, "Quality gate score should be below the configured minimum score.");
        Assert.assertTrue(gate.warningCount() > 0, "The demo page should produce RTL warnings.");
        Assert.assertTrue(Files.exists(gateTxt), "RTL quality gate TXT report should be exported.");
        Assert.assertTrue(Files.exists(gateJson), "RTL quality gate JSON report should be exported.");
        Assert.assertTrue(Files.exists(gateHtml), "RTL quality gate HTML report should be exported.");
        Assert.assertTrue(Files.readString(gateJson).contains("\"status\": \"FAILED\""), "JSON quality gate report should show FAILED status.");
    }



    @Test
    public void seleniumCoreAlertsFramesAndWindowsTest() {
        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html>
                          <head><title>Selenium Core Demo</title></head>
                          <body>
                            <button id='alertBtn' onclick="alert('QATRA alert ready')">Alert</button>
                            <button id='promptBtn' onclick="document.getElementById('promptResult').textContent = prompt('Your name?', '');">Prompt</button>
                            <div id='promptResult'></div>
                            <iframe id='demoFrame' srcdoc="<html><body><h2 id='inside'>Frame Content</h2></body></html>"></iframe>
                          </body>
                        </html>
                        """));

        driver().element()
                .click(By.id("alertBtn"))
                .alert()
                .assertTextContains("QATRA alert")
                .accept()
                .element()
                .click(By.id("promptBtn"))
                .alert()
                .type("Husni")
                .accept()
                .assertThat()
                .element(By.id("promptResult")).containsText("Husni");

        driver().frame()
                .switchTo(By.id("demoFrame"));

        driver().assertThat()
                .element(By.id("inside")).containsText("Frame Content");

        driver().frame()
                .defaultContent()
                .assertThat()
                .browser().title().contains("Selenium Core");

        int before = driver().window().count();
        driver().window()
                .openNewTab()
                .browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html><head><title>Second QATRA Tab</title></head><body>Second tab</body></html>
                        """))
                .window()
                .switchToTitleContains("Second QATRA Tab");

        Assert.assertEquals(driver().window().count(), before + 1, "A new tab should be opened.");

        driver().window()
                .closeCurrent()
                .switchToIndex(0)
                .assertThat()
                .browser().title().contains("Selenium Core");
    }


    @Test
    public void seleniumCoreCookiesAndStorageTest() throws IOException {
        HttpServer server = startLocalHtmlServer("""
                <!doctype html>
                <html>
                  <head><title>Cookie Storage Demo</title></head>
                  <body><h1 id='status'>Cookie and storage ready</h1></body>
                </html>
                """);

        try {
            String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/";

            driver().browser()
                    .navigateTo(url)
                    .cookies()
                    .add("qatra_session", "active")
                    .assertExists("qatra_session")
                    .assertValue("qatra_session", "active")
                    .storage()
                    .setLocal("language", "ar")
                    .setSession("role", "qa")
                    .assertLocalValue("language", "ar")
                    .assertSessionValue("role", "qa");

            driver().cookies()
                    .delete("qatra_session")
                    .assertNotExists("qatra_session");
        } finally {
            server.stop(0);
        }
    }


    @Test
    public void seleniumCoreShadowDomAndTableTest() {
        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html>
                          <head>
                            <title>Shadow Table Demo</title>
                            <script>
                              customElements.define('qatra-card', class extends HTMLElement {
                                connectedCallback() {
                                  const root = this.attachShadow({mode: 'open'});
                                  root.innerHTML = `<button id="shadowBtn">Shadow Click</button><span id="shadowText">Shadow Ready</span>`;
                                  root.getElementById('shadowBtn').addEventListener('click', () => {
                                    root.getElementById('shadowText').textContent = 'Shadow Clicked';
                                  });
                                }
                              });
                            </script>
                          </head>
                          <body>
                            <qatra-card id='host'></qatra-card>
                            <table id='users'>
                              <thead><tr><th>Name</th><th>Role</th></tr></thead>
                              <tbody>
                                <tr><td>Husni</td><td>QA</td></tr>
                                <tr><td>QATRA</td><td>Automation</td></tr>
                              </tbody>
                            </table>
                          </body>
                        </html>
                        """));

        Assert.assertEquals(driver().shadow().getText(By.id("host"), By.cssSelector("#shadowText")), "Shadow Ready");

        driver().shadow()
                .click(By.id("host"), By.cssSelector("#shadowBtn"));

        Assert.assertEquals(driver().shadow().getText(By.id("host"), By.cssSelector("#shadowText")), "Shadow Clicked");

        driver().table(By.id("users"))
                .assertRowCount(2)
                .assertColumnCount(2)
                .assertCellContains(0, 1, "QA")
                .assertColumnContains("Role", "Automation")
                .assertContainsText("Husni");
    }


    @Test
    public void seleniumCoreDragAndDropTest() {
        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html>
                          <head><title>Drag Drop Demo</title></head>
                          <body>
                            <div id='source' draggable='true' style='width:80px;height:40px;background:#ddd;'>Drag me</div>
                            <div id='target' style='width:180px;height:80px;border:1px solid #333;'>Drop here</div>
                            <script>
                              const target = document.getElementById('target');
                              target.addEventListener('dragover', event => event.preventDefault());
                              target.addEventListener('drop', event => {
                                event.preventDefault();
                                target.textContent = 'Dropped by QATRA';
                              });
                            </script>
                          </body>
                        </html>
                        """))
                .element()
                .html5DragAndDrop(By.id("source"), By.id("target"))
                .assertThat()
                .element(By.id("target")).containsText("Dropped by QATRA");
    }



    @Test
    public void seleniumCoreAlertAndWindowAssertionsTest() {
        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html>
                          <head><title>Assertion Helpers Main</title></head>
                          <body>
                            <button id='alertBtn' onclick="alert('Phase 3.2 alert ready')">Alert</button>
                          </body>
                        </html>
                        """));

        int before = driver().window().count();

        driver().element().click(By.id("alertBtn"));
        driver().assertThat()
                .alert()
                .isPresent()
                .textEquals("Phase 3.2 alert ready");

        driver().alert().accept();
        driver().assertThat().alert().isNotPresent();

        driver().window()
                .openNewTab()
                .browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html><head><title>Assertions Tab</title></head><body>Window assertion demo</body></html>
                        """))
                .assertThat()
                .window()
                .hasCount(before + 1)
                .currentTitleContains("Assertions Tab")
                .and()
                .window()
                .hasWindowWithTitleContaining("Assertions Tab");

        driver().window()
                .closeCurrent()
                .switchToIndex(0);
    }


    @Test
    public void seleniumCoreCookieStorageShadowTableAndPageHealthAssertionsTest() throws IOException {
        HttpServer server = startLocalHtmlServer("""
                <!doctype html>
                <html>
                  <head>
                    <title>Core Assertion Helpers</title>
                    <script>
                      customElements.define('qatra-status', class extends HTMLElement {
                        connectedCallback() {
                          const root = this.attachShadow({mode: 'open'});
                          root.innerHTML = `<span id="shadowText" data-state="ready">Shadow Ready</span>`;
                        }
                      });
                    </script>
                  </head>
                  <body>
                    <a id='okLink' href='/ok'>Healthy Link</a>
                    <img id='logo' alt='QATRA Logo' src='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+/p9sAAAAASUVORK5CYII=' />
                    <qatra-status id='host'></qatra-status>
                    <table id='users'>
                      <thead><tr><th>Name</th><th>Role</th></tr></thead>
                      <tbody>
                        <tr><td>Ali</td><td>Automation Tester</td></tr>
                        <tr><td>Husni</td><td>QA Lead</td></tr>
                      </tbody>
                    </table>
                  </body>
                </html>
                """);

        try {
            String url = "http://127.0.0.1:" + server.getAddress().getPort() + "/";

            driver().browser()
                    .navigateTo(url)
                    .cookies()
                    .add("qatra_session", "active")
                    .storage()
                    .setLocal("language", "ar")
                    .setSession("role", "qa");

            driver().assertThat()
                    .cookies()
                    .exists("qatra_session")
                    .hasValue("qatra_session", "active")
                    .and()
                    .storage()
                    .localExists("language")
                    .localValue("language", "ar")
                    .sessionExists("role")
                    .sessionValue("role", "qa")
                    .and()
                    .pageHealth()
                    .allLinksHaveHref()
                    .hasNoBrokenLinks()
                    .hasNoBrokenImages()
                    .and()
                    .shadow(By.id("host"), By.cssSelector("#shadowText"))
                    .exists()
                    .isVisible()
                    .containsText("Shadow Ready")
                    .hasAttribute("data-state", "ready")
                    .and()
                    .table(By.id("users"))
                    .hasHeaders("Name", "Role")
                    .hasRowCount(2)
                    .hasColumnCount(2)
                    .cellContains(0, 1, "Automation")
                    .columnContains("Role", "QA")
                    .columnIsSortedAscending("Name");
        } finally {
            server.stop(0);
        }
    }


    @Test
    public void seleniumCoreDownloadAssertionsTest() throws IOException {
        Path downloadDir = Path.of("target", "qatra-downloads-demo");
        Files.createDirectories(downloadDir);
        Files.writeString(downloadDir.resolve("qatra-report.txt"), "QATRA download evidence is ready", StandardCharsets.UTF_8);

        driver().assertThat()
                .downloads(downloadDir)
                .directoryExists()
                .fileExists("qatra-report.txt")
                .fileContains("qatra-report.txt", "QATRA download evidence")
                .fileWithExtensionExists(".txt")
                .fileCountAtLeast(1);
    }


    @Test
    public void pageObjectModelSupportTest() {
        LoginPage page = driver().page(LoginPage.class)
                .openLoginPage(html("""
                        <!doctype html>
                        <html>
                          <head><title>QATRA POM Demo</title></head>
                          <body>
                            <h1 id='page-title'>Login Page</h1>
                            <input id='username' data-testid='username-input' />
                            <input id='password' type='password' />
                            <button id='loginBtn' data-testid='login-button'
                                    onclick="document.getElementById('flash').textContent='Welcome ' + document.getElementById('username').value;">Login</button>
                            <div id='flash'></div>
                            <ul id='features'>
                              <li class='feature'>Fluent API</li>
                              <li class='feature'>Smart Waits</li>
                              <li class='feature'>Page Objects</li>
                            </ul>
                          </body>
                        </html>
                        """));

        page.assertLoaded()
                .loginAs("admin", "secret")
                .assertWelcomeMessage("Welcome admin")
                .assertFeatureCount(3);
    }

    public static class LoginPage extends QatraPage {

        @QatraFindBy(id = "page-title")
        private QatraElement title;

        @QatraFindBy(testId = "username-input")
        private QatraElement username;

        @QatraFindBy(id = "password")
        private QatraElement password;

        @QatraFindBy(dataTestId = "login-button")
        private QatraElement loginButton;

        @QatraFindBy(id = "flash")
        private QatraElement flash;

        @QatraFindBy(css = "#features .feature")
        private QatraElementCollection features;

        public LoginPage(io.github.qatra.web.WebDriver driver) {
            super(driver);
        }

        public LoginPage openLoginPage(String url) {
            return navigateTo(url);
        }

        public LoginPage assertLoaded() {
            title.assertThat().containsText("Login Page");
            username.assertThat().isVisible();
            loginButton.assertThat().isEnabled();
            return this;
        }

        public LoginPage loginAs(String user, String pass) {
            username.clearAndType(user);
            password.clearAndType(pass);
            loginButton.click();
            return this;
        }

        public LoginPage assertWelcomeMessage(String expectedText) {
            flash.assertThat().containsText(expectedText);
            return this;
        }

        public LoginPage assertFeatureCount(int expectedCount) {
            features.assertCount(expectedCount)
                    .assertContainsText("Page Objects");
            return this;
        }
    }



    @Test
    public void pageObjectAdvancedFeaturesTest() {
        DashboardPage dashboard = driver().page(LoginWithHeaderPage.class)
                .openLoginPage(html("""
                        <!doctype html>
                        <html>
                          <head><title>QATRA Login Portal</title></head>
                          <body>
                            <header id='app-header'>
                              <span data-testid='brand'>QATRA Engine</span>
                              <span data-testid='user-chip'>Guest</span>
                            </header>
                            <main>
                              <h1 id='login-title'>Login Portal</h1>
                              <input id='username' />
                              <input id='password' type='password' />
                              <button id='loginBtn' onclick="document.body.innerHTML = `
                                  <header id='app-header'>
                                    <span data-testid='brand'>QATRA Engine</span>
                                    <span data-testid='user-chip'>Husni</span>
                                  </header>
                                  <main>
                                    <h1 id='dashboard-title'>Dashboard</h1>
                                    <section id='stats-card' data-testid='stats-card'>
                                      <h2 data-testid='component-title'>Automation Coverage</h2>
                                      <p data-testid='component-value'>34 Features</p>
                                    </section>
                                  </main>`; history.pushState({}, '', '#/dashboard'); document.title='QATRA Dashboard';">Login</button>
                            </main>
                          </body>
                        </html>
                        """))
                .assertHeaderBrand("QATRA Engine")
                .loginAs("Husni", "secret");

        dashboard.verifyPageLoaded();
        dashboard.assertHeaderUser("Husni")
                .assertCoverageCard("Automation Coverage", "34 Features");
    }

    @QatraPageUrl(titleContains = "Login")
    @QatraPageLoaded(id = "login-title")
    public static class LoginWithHeaderPage extends QatraPage {

        @QatraFindBy(id = "app-header")
        private HeaderComponent header;

        @QatraFindBy(id = "username")
        private QatraElement username;

        @QatraFindBy(id = "password")
        private QatraElement password;

        @QatraFindBy(id = "loginBtn")
        private QatraElement loginButton;

        public LoginWithHeaderPage(io.github.qatra.web.WebDriver driver) {
            super(driver);
        }

        public LoginWithHeaderPage openLoginPage(String url) {
            return open(url);
        }

        public LoginWithHeaderPage assertHeaderBrand(String expectedBrand) {
            header.assertBrand(expectedBrand);
            return this;
        }

        public DashboardPage loginAs(String user, String pass) {
            username.clearAndType(user);
            password.clearAndType(pass);
            loginButton.click();
            return transitionTo(DashboardPage.class);
        }
    }

    @QatraPageUrl(contains = "#/dashboard", titleContains = "Dashboard")
    @QatraPageLoaded(id = "dashboard-title")
    public static class DashboardPage extends QatraPage {

        @QatraFindBy(id = "app-header")
        private HeaderComponent header;

        @QatraFindBy(testId = "stats-card")
        private StatsCardComponent statsCard;

        public DashboardPage(io.github.qatra.web.WebDriver driver) {
            super(driver);
        }

        public DashboardPage assertHeaderUser(String expectedUser) {
            header.assertLoggedInUser(expectedUser);
            return this;
        }

        public DashboardPage assertCoverageCard(String expectedTitle, String expectedValue) {
            statsCard.assertMetric(expectedTitle, expectedValue);
            return this;
        }
    }

    public static class HeaderComponent extends QatraComponent {

        @QatraFindBy(testId = "brand")
        private QatraElement brand;

        @QatraFindBy(testId = "user-chip")
        private QatraElement userChip;

        public HeaderComponent(io.github.qatra.web.WebDriver driver, By rootLocator) {
            super(driver, rootLocator);
        }

        public HeaderComponent assertBrand(String expectedBrand) {
            Assert.assertEquals(brand.text(), expectedBrand, "Unexpected header brand text.");
            return this;
        }

        public HeaderComponent assertLoggedInUser(String expectedUser) {
            Assert.assertEquals(userChip.text(), expectedUser, "Unexpected logged-in user in header component.");
            return this;
        }
    }

    public static class StatsCardComponent extends QatraComponent {

        @QatraFindBy(testId = "component-title")
        private QatraElement title;

        @QatraFindBy(testId = "component-value")
        private QatraElement value;

        public StatsCardComponent(io.github.qatra.web.WebDriver driver, By rootLocator) {
            super(driver, rootLocator);
        }

        public StatsCardComponent assertMetric(String expectedTitle, String expectedValue) {
            Assert.assertEquals(title.text(), expectedTitle, "Unexpected component title.");
            Assert.assertEquals(value.text(), expectedValue, "Unexpected component value.");
            return this;
        }
    }


    @Test(dataProvider = "qatraData", dataProviderClass = QatraDataProvider.class)
    @QatraDataFile(path = "test-data/login-users.csv")
    public void dataDrivenCsvProviderTest(QatraDataRecord user) {
        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html>
                          <head><title>QATRA Data Driven CSV</title></head>
                          <body>
                            <input id='username' />
                            <input id='password' />
                            <button id='login' onclick="document.getElementById('welcome').textContent='Welcome ' + document.getElementById('username').value;">Login</button>
                            <div id='welcome'></div>
                          </body>
                        </html>
                        """))
                .element()
                .clearAndType(By.id("username"), user.required("username"))
                .clearAndType(By.id("password"), user.required("password"))
                .click(By.id("login"))
                .assertThat()
                .element(By.id("welcome")).containsText("Welcome " + user.required("username"));

        Assert.assertTrue(user.getBoolean("active"), "CSV sample user should be active.");
    }

    @Test
    public void dataDrivenJsonDirectLoadTest() {
        QatraDataSet users = QatraData.fromJson("test-data/api-users.json");

        Assert.assertEquals(users.size(), 2, "JSON data set should contain two records.");
        Assert.assertEquals(users.first().required("username"), "ali");
        Assert.assertEquals(users.first().required("role"), "API Tester");
        Assert.assertTrue(users.first().getBoolean("active"));
    }

    @Test
    public void dataDrivenExcelDirectLoadTest() {
        QatraDataSet users = QatraData.fromExcel("test-data/ui-users.xlsx", "Users");
        QatraDataRecord firstUser = users.first();

        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html>
                          <head><title>QATRA Data Driven Excel</title></head>
                          <body>
                            <input id='displayName' />
                            <select id='role'>
                              <option value='Automation'>Automation</option>
                              <option value='Manual QA'>Manual QA</option>
                            </select>
                            <p id='summary'></p>
                            <button id='save' onclick="document.getElementById('summary').textContent=document.getElementById('displayName').value + ' - ' + document.getElementById('role').value;">Save</button>
                          </body>
                        </html>
                        """))
                .element()
                .clearAndType(By.id("displayName"), firstUser.required("displayName"))
                .selectByText(By.id("role"), firstUser.required("role"))
                .click(By.id("save"))
                .assertThat()
                .element(By.id("summary")).containsText(firstUser.required("displayName"))
                .element(By.id("summary")).containsText(firstUser.required("role"));
    }


    @Test(retryAnalyzer = QatraRetryAnalyzer.class)
    @QatraRetry(count = 1, reason = "Demo: prove QATRA can retry a controlled flaky test once")
    public void retryAnalyzerRecoversControlledFlakyTest() {
        int attempt = RETRY_SAMPLE_ATTEMPTS.incrementAndGet();
        if (attempt == 1) {
            Assert.fail("Intentional first-attempt failure to demonstrate QATRA retry analyzer.");
        }

        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html>
                          <head><title>QATRA Retry Demo</title></head>
                          <body><h1 id='status'>Recovered after retry</h1></body>
                        </html>
                        """))
                .assertThat()
                .element(By.id("status")).containsText("Recovered");
    }

    @Test
    public void stabilityEventuallyHelperTest() {
        AtomicInteger attempts = new AtomicInteger(0);

        QatraStability.eventually(
                "async counter becomes ready",
                3,
                Duration.ofMillis(25),
                () -> attempts.incrementAndGet() >= 2
        );

        Assert.assertTrue(attempts.get() >= 2, "Stability helper should retry until condition becomes true.");
    }

    @Test
    public void parallelDriverContextSmokeTest() {
        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html>
                          <head><title>QATRA Parallel Demo</title></head>
                          <body><h1 id='thread'>Thread safe driver</h1></body>
                        </html>
                        """))
                .assertThat()
                .browser().title().contains("Parallel");

        QatraParallel.assertDriverIsBoundToCurrentThread(driver());
        String evidenceName = QatraParallel.uniqueEvidenceName("parallel_smoke");
        Assert.assertTrue(evidenceName.contains("parallel_smoke"));
    }


    /**
     * Keep this disabled. Enable it manually to see how QATRA reports RTL issues.
     */
    @Test(enabled = false, description = "Enable manually to verify QATRA RTL scan failures")
    public void rtlScanFailureDemo() {
        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html lang='ar'>
                          <head><title>Broken RTL Demo</title></head>
                          <body style='direction: ltr;'>
                            <h1 id='title'>مرحبا بالمستخدم</h1>
                            <p id='broken'>Ø§Ù„Ù†Øµ Ø§Ù„Ø¹Ø±Ø¨ÙŠ</p>
                          </body>
                        </html>
                        """));

        driver().rtl()
                .scanPage()
                .report()
                .failOnIssues();
    }

    /**
     * Keep this disabled. Enable it manually to prove screenshot-on-failure behavior.
     */
    @Test(enabled = false, description = "Enable manually to verify automatic screenshot on failure")
    public void screenshotOnFailureDemo() {
        driver().browser()
                .navigateTo(html("""
                        <!doctype html>
                        <html>
                          <head><title>Failure Demo</title></head>
                          <body><h1 id='message'>Actual Text</h1></body>
                        </html>
                        """))
                .assertThat()
                .element(By.id("message")).containsText("Expected Text That Does Not Exist");
    }


    private static HttpServer startLocalHtmlServer(String htmlSource) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            byte[] response = htmlSource.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(response);
            }
        });
        server.start();
        return server;
    }

    private static String html(String source) {
        return "data:text/html;charset=utf-8," + URLEncoder
                .encode(source, StandardCharsets.UTF_8)
                .replace("+", "%20");
    }
}
