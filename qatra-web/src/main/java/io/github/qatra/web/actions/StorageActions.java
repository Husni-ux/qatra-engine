package io.github.qatra.web.actions;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.assertions.WebAssertions;
import io.github.qatra.web.fluent.FluentWeb;
import io.github.qatra.web.reports.AllureReport;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * Fluent localStorage and sessionStorage actions.
 */
public class StorageActions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final FluentWeb parent;

    public StorageActions(WebDriver driver, FluentWeb parent) {
        this.driver = driver;
        this.parent = parent;
    }

    public StorageActions setLocal(String key, String value) {
        LOG.action("Set localStorage item: {}", key);
        AllureReport.step("Set localStorage item: " + key);
        js("window.localStorage.setItem(arguments[0], arguments[1]);", key, value);
        return this;
    }

    public String getLocal(String key) {
        LOG.action("Get localStorage item: {}", key);
        AllureReport.step("Get localStorage item: " + key);
        Object value = js("return window.localStorage.getItem(arguments[0]);", key);
        return value == null ? null : String.valueOf(value);
    }

    public StorageActions removeLocal(String key) {
        LOG.action("Remove localStorage item: {}", key);
        AllureReport.step("Remove localStorage item: " + key);
        js("window.localStorage.removeItem(arguments[0]);", key);
        return this;
    }

    public StorageActions clearLocal() {
        LOG.action("Clear localStorage");
        AllureReport.step("Clear localStorage");
        js("window.localStorage.clear();");
        return this;
    }

    public StorageActions setSession(String key, String value) {
        LOG.action("Set sessionStorage item: {}", key);
        AllureReport.step("Set sessionStorage item: " + key);
        js("window.sessionStorage.setItem(arguments[0], arguments[1]);", key, value);
        return this;
    }

    public String getSession(String key) {
        LOG.action("Get sessionStorage item: {}", key);
        AllureReport.step("Get sessionStorage item: " + key);
        Object value = js("return window.sessionStorage.getItem(arguments[0]);", key);
        return value == null ? null : String.valueOf(value);
    }

    public StorageActions removeSession(String key) {
        LOG.action("Remove sessionStorage item: {}", key);
        AllureReport.step("Remove sessionStorage item: " + key);
        js("window.sessionStorage.removeItem(arguments[0]);", key);
        return this;
    }

    public StorageActions clearSession() {
        LOG.action("Clear sessionStorage");
        AllureReport.step("Clear sessionStorage");
        js("window.sessionStorage.clear();");
        return this;
    }

    public StorageActions assertLocalValue(String key, String expectedValue) {
        LOG.assertion("Assert localStorage '{}' value", key);
        AllureReport.step("Assert localStorage value: " + key);
        Assert.assertEquals(getLocal(key), expectedValue, "localStorage value mismatch: " + key);
        return this;
    }

    public StorageActions assertSessionValue(String key, String expectedValue) {
        LOG.assertion("Assert sessionStorage '{}' value", key);
        AllureReport.step("Assert sessionStorage value: " + key);
        Assert.assertEquals(getSession(key), expectedValue, "sessionStorage value mismatch: " + key);
        return this;
    }

    private Object js(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
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

    public CookieActions cookies() {
        return parent.cookies();
    }

    public WebAssertions assertThat() {
        return parent.assertThat();
    }
}
