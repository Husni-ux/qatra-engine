package io.github.qatra.web.assertions;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

/**
 * Assertion helpers for JavaScript alert, confirm, and prompt dialogs.
 */
public class AlertAssertions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final WebAssertions parent;
    private final long timeoutSeconds;

    public AlertAssertions(WebDriver driver, WebAssertions parent) {
        this.driver = driver;
        this.parent = parent;
        this.timeoutSeconds = QatraConfig.getInstance()
                .getIntProperty(QatraProperties.ELEMENT_TIMEOUT, 10);
    }

    public AlertAssertions isPresent() {
        LOG.assertion("Assert alert is present");
        AllureReport.step("Assert alert is present");
        try {
            waitForAlert();
            return this;
        } catch (TimeoutException ex) {
            Assert.fail("Expected an alert to be present within " + timeoutSeconds + " seconds.");
            return this;
        }
    }

    public AlertAssertions isNotPresent() {
        LOG.assertion("Assert alert is not present");
        AllureReport.step("Assert alert is not present");
        try {
            driver.switchTo().alert();
            Assert.fail("Expected no alert to be present, but an alert was open.");
        } catch (NoAlertPresentException ignored) {
            // Expected path.
        }
        return this;
    }

    public AlertAssertions textContains(String expectedText) {
        LOG.assertion("Assert alert text contains '{}'", expectedText);
        AllureReport.step("Assert alert text contains: " + expectedText);
        String actual = waitForAlert().getText();
        Assert.assertTrue(
                actual != null && actual.contains(expectedText),
                "Expected alert text to contain: " + expectedText + " but was: " + actual
        );
        return this;
    }

    public AlertAssertions textEquals(String expectedText) {
        LOG.assertion("Assert alert text equals '{}'", expectedText);
        AllureReport.step("Assert alert text equals: " + expectedText);
        Assert.assertEquals(waitForAlert().getText(), expectedText, "Alert text mismatch");
        return this;
    }

    public WebAssertions and() {
        return parent;
    }

    private Alert waitForAlert() {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.alertIsPresent());
    }
}
