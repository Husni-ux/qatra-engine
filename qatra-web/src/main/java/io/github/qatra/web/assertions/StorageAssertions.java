package io.github.qatra.web.assertions;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * Assertions for localStorage and sessionStorage.
 */
public class StorageAssertions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final WebAssertions parent;

    public StorageAssertions(WebDriver driver, WebAssertions parent) {
        this.driver = driver;
        this.parent = parent;
    }

    public StorageAssertions localExists(String key) {
        LOG.assertion("Assert localStorage key exists: {}", key);
        AllureReport.step("Assert localStorage key exists: " + key);
        Assert.assertNotNull(local(key), "Expected localStorage key to exist: " + key);
        return this;
    }

    public StorageAssertions localDoesNotExist(String key) {
        LOG.assertion("Assert localStorage key does not exist: {}", key);
        AllureReport.step("Assert localStorage key does not exist: " + key);
        Assert.assertNull(local(key), "Expected localStorage key not to exist: " + key);
        return this;
    }

    public StorageAssertions localValue(String key, String expectedValue) {
        LOG.assertion("Assert localStorage '{}' value", key);
        AllureReport.step("Assert localStorage value: " + key);
        Assert.assertEquals(local(key), expectedValue, "localStorage value mismatch: " + key);
        return this;
    }

    public StorageAssertions sessionExists(String key) {
        LOG.assertion("Assert sessionStorage key exists: {}", key);
        AllureReport.step("Assert sessionStorage key exists: " + key);
        Assert.assertNotNull(session(key), "Expected sessionStorage key to exist: " + key);
        return this;
    }

    public StorageAssertions sessionDoesNotExist(String key) {
        LOG.assertion("Assert sessionStorage key does not exist: {}", key);
        AllureReport.step("Assert sessionStorage key does not exist: " + key);
        Assert.assertNull(session(key), "Expected sessionStorage key not to exist: " + key);
        return this;
    }

    public StorageAssertions sessionValue(String key, String expectedValue) {
        LOG.assertion("Assert sessionStorage '{}' value", key);
        AllureReport.step("Assert sessionStorage value: " + key);
        Assert.assertEquals(session(key), expectedValue, "sessionStorage value mismatch: " + key);
        return this;
    }

    public WebAssertions and() {
        return parent;
    }

    private String local(String key) {
        Object value = ((JavascriptExecutor) driver).executeScript("return window.localStorage.getItem(arguments[0]);", key);
        return value == null ? null : String.valueOf(value);
    }

    private String session(String key) {
        Object value = ((JavascriptExecutor) driver).executeScript("return window.sessionStorage.getItem(arguments[0]);", key);
        return value == null ? null : String.valueOf(value);
    }
}
