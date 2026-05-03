package io.github.qatra.web.actions;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.assertions.WebAssertions;
import io.github.qatra.web.fluent.FluentWeb;
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
 * Fluent browser alert actions for JavaScript alert/confirm/prompt dialogs.
 */
public class AlertActions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final FluentWeb parent;
    private final long timeoutSeconds;

    public AlertActions(WebDriver driver, FluentWeb parent) {
        this.driver = driver;
        this.parent = parent;
        this.timeoutSeconds = QatraConfig.getInstance()
                .getIntProperty(QatraProperties.ELEMENT_TIMEOUT, 10);
    }

    public AlertActions accept() {
        LOG.action("Accept alert");
        AllureReport.step("Accept alert");
        waitForAlert().accept();
        return this;
    }

    public AlertActions dismiss() {
        LOG.action("Dismiss alert");
        AllureReport.step("Dismiss alert");
        waitForAlert().dismiss();
        return this;
    }

    public AlertActions type(String text) {
        LOG.action("Type into prompt alert: {}", text);
        AllureReport.step("Type into prompt alert");
        waitForAlert().sendKeys(text);
        return this;
    }

    public String getText() {
        LOG.action("Get alert text");
        AllureReport.step("Get alert text");
        return waitForAlert().getText();
    }

    public boolean isPresent() {
        LOG.action("Check if alert is present");
        AllureReport.step("Check if alert is present");
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException ex) {
            return false;
        }
    }

    public AlertActions acceptIfPresent() {
        LOG.action("Accept alert if present");
        AllureReport.step("Accept alert if present");
        try {
            driver.switchTo().alert().accept();
        } catch (NoAlertPresentException ignored) {
            LOG.info("No alert present to accept");
        }
        return this;
    }

    public AlertActions dismissIfPresent() {
        LOG.action("Dismiss alert if present");
        AllureReport.step("Dismiss alert if present");
        try {
            driver.switchTo().alert().dismiss();
        } catch (NoAlertPresentException ignored) {
            LOG.info("No alert present to dismiss");
        }
        return this;
    }

    public AlertActions assertTextContains(String expectedText) {
        LOG.assertion("Assert alert text contains '{}'", expectedText);
        AllureReport.step("Assert alert text contains: " + expectedText);
        String actualText = getText();
        Assert.assertTrue(
                actualText != null && actualText.contains(expectedText),
                "Expected alert text to contain: " + expectedText + " but was: " + actualText
        );
        return this;
    }

    public AlertActions assertTextEquals(String expectedText) {
        LOG.assertion("Assert alert text equals '{}'", expectedText);
        AllureReport.step("Assert alert text equals: " + expectedText);
        Assert.assertEquals(getText(), expectedText, "Alert text mismatch");
        return this;
    }

    private Alert waitForAlert() {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.alertIsPresent());
        } catch (TimeoutException ex) {
            throw new AssertionError("Alert was not present within " + timeoutSeconds + " seconds", ex);
        }
    }

    public FluentWeb and() {
        return parent;
    }

    public BrowserActions browser() {
        return parent.browser();
    }

    public ElementActions element() {
        return parent.element();
    }

    public WebAssertions assertThat() {
        return parent.assertThat();
    }
}
