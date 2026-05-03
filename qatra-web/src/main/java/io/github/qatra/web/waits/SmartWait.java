package io.github.qatra.web.waits;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Centralized smart wait utility for QATRA Web.
 *
 * <p>This class keeps wait behavior consistent across actions and assertions:
 * timeout, polling interval, ignored transient Selenium exceptions, logging,
 * Allure steps, and friendlier timeout messages.</p>
 */
public final class SmartWait {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private SmartWait() {
    }

    public static WebElement untilVisible(WebDriver driver, By locator, long timeoutSeconds) {
        String action = "Wait until element is visible: " + locator;
        LOG.action(action);
        AllureReport.step(action);
        try {
            return wait(driver, timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw friendlyTimeout("Element was not visible", locator, timeoutSeconds, e);
        }
    }

    public static WebElement untilClickable(WebDriver driver, By locator, long timeoutSeconds) {
        String action = "Wait until element is clickable: " + locator;
        LOG.action(action);
        AllureReport.step(action);
        try {
            return wait(driver, timeoutSeconds).until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            throw friendlyTimeout("Element was not clickable", locator, timeoutSeconds, e);
        }
    }

    public static WebElement untilPresent(WebDriver driver, By locator, long timeoutSeconds) {
        String action = "Wait until element is present in DOM: " + locator;
        LOG.action(action);
        AllureReport.step(action);
        try {
            return wait(driver, timeoutSeconds).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw friendlyTimeout("Element was not present in DOM", locator, timeoutSeconds, e);
        }
    }

    public static boolean untilInvisible(WebDriver driver, By locator, long timeoutSeconds) {
        String action = "Wait until element is invisible: " + locator;
        LOG.action(action);
        AllureReport.step(action);
        try {
            return wait(driver, timeoutSeconds).until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw friendlyTimeout("Element was still visible", locator, timeoutSeconds, e);
        }
    }

    public static boolean untilTextContains(WebDriver driver, By locator, String expectedText, long timeoutSeconds) {
        String action = "Wait until element text contains '" + expectedText + "': " + locator;
        LOG.action(action);
        AllureReport.step(action);
        try {
            return wait(driver, timeoutSeconds).until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
        } catch (TimeoutException e) {
            throw friendlyTimeout("Element text did not contain '" + expectedText + "'", locator, timeoutSeconds, e);
        }
    }

    public static boolean untilAttributeEquals(WebDriver driver, By locator, String attributeName, String expectedValue, long timeoutSeconds) {
        String action = "Wait until attribute '" + attributeName + "' equals '" + expectedValue + "': " + locator;
        LOG.action(action);
        AllureReport.step(action);
        try {
            return wait(driver, timeoutSeconds).until(ExpectedConditions.attributeToBe(locator, attributeName, expectedValue));
        } catch (TimeoutException e) {
            throw friendlyTimeout("Attribute '" + attributeName + "' did not become '" + expectedValue + "'", locator, timeoutSeconds, e);
        }
    }

    public static boolean untilValueEquals(WebDriver driver, By locator, String expectedValue, long timeoutSeconds) {
        return untilAttributeEquals(driver, locator, "value", expectedValue, timeoutSeconds);
    }

    public static List<WebElement> untilAtLeastOneElement(WebDriver driver, By locator, long timeoutSeconds) {
        String action = "Wait until at least one element exists: " + locator;
        LOG.action(action);
        AllureReport.step(action);
        try {
            return wait(driver, timeoutSeconds).until(webDriver -> {
                List<WebElement> elements = webDriver.findElements(locator);
                return elements.isEmpty() ? null : elements;
            });
        } catch (TimeoutException e) {
            throw friendlyTimeout("No elements were found", locator, timeoutSeconds, e);
        }
    }

    public static boolean untilPageReady(WebDriver driver, long timeoutSeconds) {
        String action = "Wait until document.readyState is complete";
        LOG.action(action);
        AllureReport.step(action);
        try {
            return wait(driver, timeoutSeconds).until(webDriver ->
                    "complete".equals(((JavascriptExecutor) webDriver).executeScript("return document.readyState"))
            );
        } catch (TimeoutException e) {
            throw new TimeoutException("QATRA SmartWait timeout: Page did not become ready within "
                    + timeoutSeconds + " seconds.", e);
        }
    }

    public static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("QATRA wait was interrupted", e);
        }
    }

    private static WebDriverWait wait(WebDriver driver, long timeoutSeconds) {
        long pollingMs = QatraConfig.getInstance()
                .getIntProperty(QatraProperties.WAIT_POLLING_MS, 250);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.pollingEvery(Duration.ofMillis(Math.max(50, pollingMs)));
        wait.ignoring(NoSuchElementException.class);
        wait.ignoring(StaleElementReferenceException.class);
        return wait;
    }

    private static TimeoutException friendlyTimeout(String reason, By locator, long timeoutSeconds, TimeoutException original) {
        return new TimeoutException("QATRA SmartWait timeout: " + reason
                + " within " + timeoutSeconds + " seconds. Locator: " + locator, original);
    }
}
