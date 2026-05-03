package io.github.qatra.web.actions;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.assertions.WebAssertions;
import io.github.qatra.web.fluent.FluentWeb;
import io.github.qatra.web.reports.AllureReport;
import io.github.qatra.web.waits.SmartWait;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Fluent Shadow DOM actions for open shadow roots.
 */
public class ShadowDomActions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final FluentWeb parent;
    private final long timeoutSeconds;

    public ShadowDomActions(WebDriver driver, FluentWeb parent) {
        this.driver = driver;
        this.parent = parent;
        this.timeoutSeconds = QatraConfig.getInstance()
                .getIntProperty(QatraProperties.ELEMENT_TIMEOUT, 10);
    }

    public WebElement find(By hostLocator, By shadowLocator) {
        LOG.action("Find shadow element. Host: {}, Element: {}", hostLocator, shadowLocator);
        AllureReport.step("Find shadow element: " + shadowLocator + " inside host: " + hostLocator);
        return shadowRoot(hostLocator).findElement(shadowLocator);
    }

    public ShadowDomActions click(By hostLocator, By shadowLocator) {
        LOG.action("Click shadow element. Host: {}, Element: {}", hostLocator, shadowLocator);
        AllureReport.step("Click shadow element");
        find(hostLocator, shadowLocator).click();
        return this;
    }

    public ShadowDomActions type(By hostLocator, By shadowLocator, String text) {
        LOG.action("Type into shadow element. Host: {}, Element: {}", hostLocator, shadowLocator);
        AllureReport.step("Type into shadow element");
        WebElement element = find(hostLocator, shadowLocator);
        element.clear();
        element.sendKeys(text);
        return this;
    }

    public String getText(By hostLocator, By shadowLocator) {
        LOG.action("Get shadow element text. Host: {}, Element: {}", hostLocator, shadowLocator);
        AllureReport.step("Get shadow element text");
        return find(hostLocator, shadowLocator).getText();
    }

    public String getAttribute(By hostLocator, By shadowLocator, String attributeName) {
        LOG.action("Get shadow element attribute '{}'. Host: {}, Element: {}", attributeName, hostLocator, shadowLocator);
        AllureReport.step("Get shadow element attribute: " + attributeName);
        return find(hostLocator, shadowLocator).getAttribute(attributeName);
    }

    public boolean isDisplayed(By hostLocator, By shadowLocator) {
        try {
            return find(hostLocator, shadowLocator).isDisplayed();
        } catch (RuntimeException ex) {
            return false;
        }
    }

    private SearchContext shadowRoot(By hostLocator) {
        WebElement host = SmartWait.untilPresent(driver, hostLocator, timeoutSeconds);
        return host.getShadowRoot();
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
