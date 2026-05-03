package io.github.qatra.web.actions;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.assertions.WebAssertions;
import io.github.qatra.web.fluent.FluentWeb;
import io.github.qatra.web.reports.AllureReport;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.util.Set;

/**
 * Fluent cookie management actions.
 */
public class CookieActions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final FluentWeb parent;

    public CookieActions(WebDriver driver, FluentWeb parent) {
        this.driver = driver;
        this.parent = parent;
    }

    public CookieActions add(String name, String value) {
        LOG.action("Add cookie: {}", name);
        AllureReport.step("Add cookie: " + name);
        driver.manage().addCookie(new Cookie(name, value));
        return this;
    }

    public CookieActions add(Cookie cookie) {
        LOG.action("Add cookie: {}", cookie.getName());
        AllureReport.step("Add cookie: " + cookie.getName());
        driver.manage().addCookie(cookie);
        return this;
    }

    public Cookie get(String name) {
        LOG.action("Get cookie: {}", name);
        AllureReport.step("Get cookie: " + name);
        return driver.manage().getCookieNamed(name);
    }

    public String valueOf(String name) {
        Cookie cookie = get(name);
        return cookie == null ? null : cookie.getValue();
    }

    public boolean exists(String name) {
        return get(name) != null;
    }

    public Set<Cookie> all() {
        LOG.action("Get all cookies");
        AllureReport.step("Get all cookies");
        return driver.manage().getCookies();
    }

    public CookieActions delete(String name) {
        LOG.action("Delete cookie: {}", name);
        AllureReport.step("Delete cookie: " + name);
        driver.manage().deleteCookieNamed(name);
        return this;
    }

    public CookieActions deleteAll() {
        LOG.action("Delete all cookies");
        AllureReport.step("Delete all cookies");
        driver.manage().deleteAllCookies();
        return this;
    }

    public CookieActions assertExists(String name) {
        LOG.assertion("Assert cookie exists: {}", name);
        AllureReport.step("Assert cookie exists: " + name);
        Assert.assertNotNull(get(name), "Expected cookie to exist: " + name);
        return this;
    }

    public CookieActions assertNotExists(String name) {
        LOG.assertion("Assert cookie does not exist: {}", name);
        AllureReport.step("Assert cookie does not exist: " + name);
        Assert.assertNull(get(name), "Expected cookie not to exist: " + name);
        return this;
    }

    public CookieActions assertValue(String name, String expectedValue) {
        LOG.assertion("Assert cookie '{}' value", name);
        AllureReport.step("Assert cookie value: " + name);
        Assert.assertEquals(valueOf(name), expectedValue, "Cookie value mismatch: " + name);
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

    public StorageActions storage() {
        return parent.storage();
    }

    public WebAssertions assertThat() {
        return parent.assertThat();
    }
}
