package io.github.qatra.web.page;

import io.github.qatra.web.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.List;
import java.util.Objects;

/**
 * Base class for reusable UI components inside Page Objects.
 *
 * <p>Use this for repeated or independent UI blocks such as header, sidebar,
 * cards, modals, toast messages, search panels, and profile menus.</p>
 */
public abstract class QatraComponent {

    protected final WebDriver driver;
    protected final org.openqa.selenium.WebDriver seleniumDriver;
    protected final By rootLocator;

    protected QatraComponent(WebDriver driver, By rootLocator) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
        this.seleniumDriver = driver.getSeleniumDriver();
        this.rootLocator = Objects.requireNonNull(rootLocator, "rootLocator must not be null");
        QatraPageFactory.initElements(this);
    }

    protected QatraElement element(By locator) {
        return new QatraElement(driver, locator, rootLocator);
    }

    protected QatraElementCollection elements(By locator) {
        return new QatraElementCollection(driver, locator, rootLocator);
    }

    public QatraComponent assertVisible() {
        Assert.assertTrue(root().isDisplayed(), "Expected component to be visible: " + rootLocator);
        return this;
    }

    public QatraComponent assertContainsText(String expectedText) {
        Assert.assertTrue(text().contains(expectedText),
                "Expected component " + rootLocator + " to contain text: " + expectedText + " | actual=" + text());
        return this;
    }

    public String text() {
        return root().getText();
    }

    public WebElement root() {
        return seleniumDriver.findElement(rootLocator);
    }

    public List<WebElement> rawAll(By childLocator) {
        return root().findElements(childLocator);
    }

    public By rootLocator() {
        return rootLocator;
    }

    public WebDriver qatraDriver() {
        return driver;
    }

    public org.openqa.selenium.WebDriver seleniumDriver() {
        return seleniumDriver;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" + rootLocator + '}';
    }
}
