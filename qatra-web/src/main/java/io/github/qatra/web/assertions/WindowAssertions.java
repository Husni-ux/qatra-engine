package io.github.qatra.web.assertions;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * Assertions for browser windows and tabs.
 */
public class WindowAssertions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final WebAssertions parent;

    public WindowAssertions(WebDriver driver, WebAssertions parent) {
        this.driver = driver;
        this.parent = parent;
    }

    public WindowAssertions hasCount(int expectedCount) {
        LOG.assertion("Assert window/tab count is {}", expectedCount);
        AllureReport.step("Assert window/tab count is " + expectedCount);
        Assert.assertEquals(driver.getWindowHandles().size(), expectedCount, "Window/tab count mismatch");
        return this;
    }

    public WindowAssertions currentTitleContains(String expectedTitlePart) {
        LOG.assertion("Assert current window title contains '{}'", expectedTitlePart);
        AllureReport.step("Assert current window title contains: " + expectedTitlePart);
        String actual = driver.getTitle();
        Assert.assertTrue(actual != null && actual.contains(expectedTitlePart),
                "Expected current window title to contain: " + expectedTitlePart + " but was: " + actual);
        return this;
    }

    public WindowAssertions currentUrlContains(String expectedUrlPart) {
        LOG.assertion("Assert current window URL contains '{}'", expectedUrlPart);
        AllureReport.step("Assert current window URL contains: " + expectedUrlPart);
        String actual = driver.getCurrentUrl();
        Assert.assertTrue(actual != null && actual.contains(expectedUrlPart),
                "Expected current window URL to contain: " + expectedUrlPart + " but was: " + actual);
        return this;
    }

    public WindowAssertions hasWindowWithTitleContaining(String expectedTitlePart) {
        LOG.assertion("Assert any window title contains '{}'", expectedTitlePart);
        AllureReport.step("Assert any window title contains: " + expectedTitlePart);
        String original = driver.getWindowHandle();
        boolean found = false;
        try {
            for (String handle : driver.getWindowHandles()) {
                driver.switchTo().window(handle);
                String title = driver.getTitle();
                if (title != null && title.contains(expectedTitlePart)) {
                    found = true;
                    break;
                }
            }
        } finally {
            driver.switchTo().window(original);
        }
        Assert.assertTrue(found, "No window/tab found with title containing: " + expectedTitlePart);
        return this;
    }

    public WindowAssertions hasWindowWithUrlContaining(String expectedUrlPart) {
        LOG.assertion("Assert any window URL contains '{}'", expectedUrlPart);
        AllureReport.step("Assert any window URL contains: " + expectedUrlPart);
        String original = driver.getWindowHandle();
        boolean found = false;
        try {
            for (String handle : driver.getWindowHandles()) {
                driver.switchTo().window(handle);
                String url = driver.getCurrentUrl();
                if (url != null && url.contains(expectedUrlPart)) {
                    found = true;
                    break;
                }
            }
        } finally {
            driver.switchTo().window(original);
        }
        Assert.assertTrue(found, "No window/tab found with URL containing: " + expectedUrlPart);
        return this;
    }

    public WebAssertions and() {
        return parent;
    }
}
