package io.github.qatra.web.actions;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.assertions.WebAssertions;
import io.github.qatra.web.fluent.FluentWeb;
import io.github.qatra.web.reports.AllureReport;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Fluent frame and iframe switching actions.
 */
public class FrameActions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final FluentWeb parent;
    private final long timeoutSeconds;

    public FrameActions(WebDriver driver, FluentWeb parent) {
        this.driver = driver;
        this.parent = parent;
        this.timeoutSeconds = QatraConfig.getInstance()
                .getIntProperty(QatraProperties.ELEMENT_TIMEOUT, 10);
    }

    public FrameActions switchTo(By frameLocator) {
        LOG.action("Switch to frame: {}", frameLocator);
        AllureReport.step("Switch to frame: " + frameLocator);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator));
            return this;
        } catch (TimeoutException ex) {
            throw new AssertionError("Frame was not available within " + timeoutSeconds + " seconds: " + frameLocator, ex);
        }
    }

    public FrameActions switchTo(String nameOrId) {
        LOG.action("Switch to frame by name/id: {}", nameOrId);
        AllureReport.step("Switch to frame by name/id: " + nameOrId);
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                    .until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(nameOrId));
            return this;
        } catch (TimeoutException ex) {
            throw new AssertionError("Frame was not available within " + timeoutSeconds + " seconds: " + nameOrId, ex);
        }
    }

    public FrameActions switchTo(int index) {
        LOG.action("Switch to frame index: {}", index);
        AllureReport.step("Switch to frame index: " + index);
        driver.switchTo().frame(index);
        return this;
    }

    public FrameActions switchTo(WebElement frameElement) {
        LOG.action("Switch to frame WebElement");
        AllureReport.step("Switch to frame WebElement");
        driver.switchTo().frame(frameElement);
        return this;
    }

    public FrameActions parentFrame() {
        LOG.action("Switch to parent frame");
        AllureReport.step("Switch to parent frame");
        driver.switchTo().parentFrame();
        return this;
    }

    public FrameActions defaultContent() {
        LOG.action("Switch to default content");
        AllureReport.step("Switch to default content");
        driver.switchTo().defaultContent();
        return this;
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
