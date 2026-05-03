package io.github.qatra.web.page;

import io.github.qatra.web.WebDriver;
import io.github.qatra.web.assertions.WebAssertions;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Objects;

/**
 * Page Object friendly wrapper around a Selenium locator.
 *
 * <p>QatraElement keeps page classes readable while still using the same QATRA
 * actions, smart waits, assertions, Allure steps, and diagnostics underneath.</p>
 */
public class QatraElement {

    private final WebDriver driver;
    private final By locator;
    private final By scopeLocator;

    public QatraElement(WebDriver driver, By locator) {
        this(driver, locator, null);
    }

    public QatraElement(WebDriver driver, By locator, By scopeLocator) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
        this.locator = Objects.requireNonNull(locator, "locator must not be null");
        this.scopeLocator = scopeLocator;
    }

    public QatraElement click() {
        if (isScoped()) {
            raw().click();
        } else {
            driver.element().click(locator);
        }
        return this;
    }

    public QatraElement type(String text) {
        if (isScoped()) {
            WebElement element = raw();
            element.clear();
            element.sendKeys(text);
        } else {
            driver.element().type(locator, text);
        }
        return this;
    }

    public QatraElement clearAndType(String text) {
        return type(text);
    }

    public QatraElement append(String text) {
        if (isScoped()) {
            raw().sendKeys(text);
        } else {
            driver.element().append(locator, text);
        }
        return this;
    }

    public QatraElement clear() {
        if (isScoped()) {
            raw().clear();
        } else {
            driver.element().clear(locator);
        }
        return this;
    }

    public QatraElement submit() {
        if (isScoped()) {
            raw().sendKeys(Keys.RETURN);
        } else {
            driver.element().submit(locator);
        }
        return this;
    }

    public QatraElement hover() {
        driver.element().hover(locator);
        return this;
    }

    public QatraElement doubleClick() {
        driver.element().doubleClick(locator);
        return this;
    }

    public QatraElement rightClick() {
        driver.element().rightClick(locator);
        return this;
    }

    public QatraElement scrollTo() {
        driver.element().scrollTo(locator);
        return this;
    }

    public QatraElement focus() {
        driver.element().focus(locator);
        return this;
    }

    public QatraElement highlight() {
        driver.element().highlight(locator);
        return this;
    }

    public QatraElement check() {
        if (isScoped()) {
            WebElement element = raw();
            if (!element.isSelected()) element.click();
        } else {
            driver.element().check(locator);
        }
        return this;
    }

    public QatraElement uncheck() {
        if (isScoped()) {
            WebElement element = raw();
            if (element.isSelected()) element.click();
        } else {
            driver.element().uncheck(locator);
        }
        return this;
    }

    public QatraElement selectByText(String text) {
        driver.element().selectByText(locator, text);
        return this;
    }

    public QatraElement selectByValue(String value) {
        driver.element().selectByValue(locator, value);
        return this;
    }

    public QatraElement selectByIndex(int index) {
        driver.element().selectByIndex(locator, index);
        return this;
    }

    public QatraElement uploadFile(String absoluteFilePath) {
        if (isScoped()) {
            raw().sendKeys(absoluteFilePath);
        } else {
            driver.element().uploadFile(locator, absoluteFilePath);
        }
        return this;
    }

    public QatraElement typeAndPress(String text, Keys key) {
        if (isScoped()) {
            WebElement element = raw();
            element.clear();
            element.sendKeys(text);
            element.sendKeys(key);
        } else {
            driver.element().typeAndPress(locator, text, key);
        }
        return this;
    }

    public String text() {
        return isScoped() ? raw().getText() : driver.element().getText(locator);
    }

    public String attribute(String attributeName) {
        return isScoped() ? raw().getAttribute(attributeName) : driver.element().getAttribute(locator, attributeName);
    }

    public String cssValue(String propertyName) {
        return isScoped() ? raw().getCssValue(propertyName) : driver.element().getCssValue(locator, propertyName);
    }

    public boolean isDisplayed() {
        return isScoped() ? raw().isDisplayed() : driver.element().isDisplayed(locator);
    }

    public boolean isEnabled() {
        return isScoped() ? raw().isEnabled() : driver.element().isEnabled(locator);
    }

    public boolean isSelected() {
        return isScoped() ? raw().isSelected() : driver.element().isSelected(locator);
    }

    public int count() {
        return rawAll().size();
    }

    public WebElement raw() {
        if (scopeLocator == null) {
            return driver.getSeleniumDriver().findElement(locator);
        }
        return driver.getSeleniumDriver().findElement(scopeLocator).findElement(locator);
    }

    public List<WebElement> rawAll() {
        if (scopeLocator == null) {
            return driver.getSeleniumDriver().findElements(locator);
        }
        return driver.getSeleniumDriver().findElement(scopeLocator).findElements(locator);
    }

    public WebAssertions.ElementAssertions assertThat() {
        return driver.assertThat().element(locator);
    }

    public By locator() {
        return locator;
    }

    public By scopeLocator() {
        return scopeLocator;
    }

    public WebDriver driver() {
        return driver;
    }

    public boolean isScoped() {
        return scopeLocator != null;
    }

    @Override
    public String toString() {
        return "QatraElement{" + (scopeLocator == null ? "" : "scope=" + scopeLocator + ", ") + locator + '}';
    }
}
