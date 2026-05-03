package io.github.qatra.web.assertions;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import io.github.qatra.web.waits.SmartWait;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

/**
 * Assertions for open Shadow DOM elements.
 */
public class ShadowDomAssertions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final WebAssertions parent;
    private final By hostLocator;
    private final By shadowLocator;
    private final long timeoutSeconds;

    public ShadowDomAssertions(WebDriver driver, WebAssertions parent, By hostLocator, By shadowLocator) {
        this.driver = driver;
        this.parent = parent;
        this.hostLocator = hostLocator;
        this.shadowLocator = shadowLocator;
        this.timeoutSeconds = QatraConfig.getInstance()
                .getIntProperty(QatraProperties.ELEMENT_TIMEOUT, 10);
    }

    public ShadowDomAssertions exists() {
        LOG.assertion("Assert shadow element exists. Host: {}, Element: {}", hostLocator, shadowLocator);
        AllureReport.step("Assert shadow element exists: " + shadowLocator);
        Assert.assertNotNull(find(), "Expected shadow element to exist: " + shadowLocator + " inside host: " + hostLocator);
        return this;
    }

    public ShadowDomAssertions isVisible() {
        LOG.assertion("Assert shadow element is visible. Host: {}, Element: {}", hostLocator, shadowLocator);
        AllureReport.step("Assert shadow element is visible: " + shadowLocator);
        Assert.assertTrue(find().isDisplayed(), "Expected shadow element to be visible: " + shadowLocator);
        return this;
    }

    public ShadowDomAssertions containsText(String expectedText) {
        LOG.assertion("Assert shadow element text contains '{}'", expectedText);
        AllureReport.step("Assert shadow element text contains: " + expectedText);
        String actual = find().getText();
        Assert.assertTrue(actual != null && actual.contains(expectedText),
                "Expected shadow element text to contain: " + expectedText + " but was: " + actual);
        return this;
    }

    public ShadowDomAssertions hasExactText(String expectedText) {
        LOG.assertion("Assert shadow element text equals '{}'", expectedText);
        AllureReport.step("Assert shadow element text equals: " + expectedText);
        Assert.assertEquals(find().getText(), expectedText, "Shadow element text mismatch");
        return this;
    }

    public ShadowDomAssertions hasAttribute(String attributeName, String expectedValue) {
        LOG.assertion("Assert shadow element attribute '{}' equals '{}'", attributeName, expectedValue);
        AllureReport.step("Assert shadow element attribute: " + attributeName);
        Assert.assertEquals(find().getAttribute(attributeName), expectedValue, "Shadow element attribute mismatch: " + attributeName);
        return this;
    }

    public WebAssertions and() {
        return parent;
    }

    private WebElement find() {
        WebElement host = SmartWait.untilPresent(driver, hostLocator, timeoutSeconds);
        SearchContext root = host.getShadowRoot();
        return root.findElement(shadowLocator);
    }
}
