package io.github.qatra.web.waits;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * Backward-compatible smart wait facade.
 *
 * <p>Phase 3.11 refactors this class to delegate to {@link QatraWait}, which is
 * based on Selenium FluentWait and custom predicates instead of relying only on
 * legacy ExpectedConditions. Existing QATRA actions can keep using SmartWait,
 * while new code should prefer QatraWait directly.</p>
 */
public final class SmartWait {

    private SmartWait() {
    }

    public static WebElement untilVisible(WebDriver driver, By locator, long timeoutSeconds) {
        return QatraWait.forElement(driver, locator, options(timeoutSeconds))
                .waitUntilVisible()
                .element();
    }

    public static WebElement untilClickable(WebDriver driver, By locator, long timeoutSeconds) {
        return QatraWait.forElement(driver, locator, options(timeoutSeconds))
                .waitUntilClickable()
                .element();
    }

    public static WebElement untilPresent(WebDriver driver, By locator, long timeoutSeconds) {
        return QatraWait.forElement(driver, locator, options(timeoutSeconds))
                .waitUntilPresent()
                .element();
    }

    public static boolean untilInvisible(WebDriver driver, By locator, long timeoutSeconds) {
        QatraWait.forElement(driver, locator, options(timeoutSeconds)).waitUntilInvisible();
        return true;
    }

    public static boolean untilTextContains(WebDriver driver, By locator, String expectedText, long timeoutSeconds) {
        QatraWait.forElement(driver, locator, options(timeoutSeconds)).waitUntilTextContains(expectedText);
        return true;
    }

    public static boolean untilAttributeEquals(WebDriver driver, By locator, String attributeName, String expectedValue, long timeoutSeconds) {
        QatraWait.forElement(driver, locator, options(timeoutSeconds)).waitUntilAttributeEquals(attributeName, expectedValue);
        return true;
    }

    public static boolean untilValueEquals(WebDriver driver, By locator, String expectedValue, long timeoutSeconds) {
        return untilAttributeEquals(driver, locator, "value", expectedValue, timeoutSeconds);
    }

    public static List<WebElement> untilAtLeastOneElement(WebDriver driver, By locator, long timeoutSeconds) {
        QatraWait.forElement(driver, locator, options(timeoutSeconds)).waitUntilPresent();
        List<WebElement> elements = driver.findElements(locator);
        if (elements.isEmpty()) {
            throw new TimeoutException("QATRA SmartWait timeout: No elements were found within "
                    + timeoutSeconds + " seconds. Locator: " + locator);
        }
        return elements;
    }

    public static boolean untilPageReady(WebDriver driver, long timeoutSeconds) {
        QatraWait.forPage(driver, options(timeoutSeconds)).waitUntilPageIsFullyReady();
        return true;
    }

    /**
     * Controlled pause for demos/animations. Prefer condition-based waits.
     * Uses LockSupport instead of Thread.sleep to avoid InterruptedException plumbing.
     */
    public static void pause(long millis) {
        if (millis <= 0) {
            return;
        }
        LockSupport.parkNanos(Duration.ofMillis(millis).toNanos());
        if (Thread.currentThread().isInterrupted()) {
            throw new RuntimeException("QATRA wait was interrupted");
        }
    }

    private static WaitOptions options(long timeoutSeconds) {
        long pollingMs = QatraConfig.getInstance()
                .getIntProperty(QatraProperties.WAIT_POLLING_MS, 250);

        return WaitOptions.fromConfig()
                .toBuilder()
                .timeoutSeconds(timeoutSeconds)
                .pollingMillis(pollingMs)
                .build();
    }
}
