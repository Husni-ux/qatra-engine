package io.github.qatra.examples;

import io.github.qatra.web.testng.QatraBaseTest;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

/**
 * Optional external-site smoke example.
 *
 * This class intentionally does not end with "Test" to avoid running during the default Maven test suite.
 * Run it manually only when you want to demonstrate QATRA on a real Arabic/RTL website.
 */
public class QatraZatcaSmokeExample extends QatraBaseTest {

    private static final String ZATCA_HOME = "https://zatca.gov.sa/ar/Pages/default.aspx";

    @Test
    public void zatcaArabicHomepageSmokeExample() {
        driver().browser()
                .navigateTo(ZATCA_HOME)
                .waitUntilPageReady()
                .assertThat()
                .browser()
                .url()
                .contains("zatca.gov.sa");

        driver().expect(By.tagName("body"))
                .exists()
                .isVisible();

        driver().rtl()
                .assertArabicTextExists()
                .scanPage()
                .report();

        driver().screenshot("zatca_homepage_smoke_example");
        driver().diagnostics("zatca_homepage_smoke_example");
    }
}
