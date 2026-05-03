package io.github.qatra.web.assertions;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * Assertions for browser cookies.
 */
public class CookieAssertions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final WebAssertions parent;

    public CookieAssertions(WebDriver driver, WebAssertions parent) {
        this.driver = driver;
        this.parent = parent;
    }

    public CookieAssertions exists(String name) {
        LOG.assertion("Assert cookie exists: {}", name);
        AllureReport.step("Assert cookie exists: " + name);
        Assert.assertNotNull(cookie(name), "Expected cookie to exist: " + name);
        return this;
    }

    public CookieAssertions doesNotExist(String name) {
        LOG.assertion("Assert cookie does not exist: {}", name);
        AllureReport.step("Assert cookie does not exist: " + name);
        Assert.assertNull(cookie(name), "Expected cookie not to exist: " + name);
        return this;
    }

    public CookieAssertions hasValue(String name, String expectedValue) {
        LOG.assertion("Assert cookie '{}' value", name);
        AllureReport.step("Assert cookie value: " + name);
        Cookie cookie = cookie(name);
        Assert.assertNotNull(cookie, "Expected cookie to exist: " + name);
        Assert.assertEquals(cookie.getValue(), expectedValue, "Cookie value mismatch: " + name);
        return this;
    }

    public CookieAssertions countAtLeast(int minimumCount) {
        LOG.assertion("Assert cookie count at least {}", minimumCount);
        AllureReport.step("Assert cookie count at least: " + minimumCount);
        Assert.assertTrue(driver.manage().getCookies().size() >= minimumCount,
                "Expected cookie count to be at least " + minimumCount + " but was " + driver.manage().getCookies().size());
        return this;
    }

    public WebAssertions and() {
        return parent;
    }

    private Cookie cookie(String name) {
        return driver.manage().getCookieNamed(name);
    }
}
