package io.github.qatra.web.page;

import io.github.qatra.web.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.List;
import java.util.Objects;

/**
 * Page Object wrapper for repeated elements such as cards, rows, menu items, or search results.
 */
public class QatraElementCollection {

    private final WebDriver driver;
    private final By locator;
    private final By scopeLocator;

    public QatraElementCollection(WebDriver driver, By locator) {
        this(driver, locator, null);
    }

    public QatraElementCollection(WebDriver driver, By locator, By scopeLocator) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
        this.locator = Objects.requireNonNull(locator, "locator must not be null");
        this.scopeLocator = scopeLocator;
    }

    public int count() {
        return rawAll().size();
    }

    public WebElement raw(int index) {
        return rawAll().get(index);
    }

    public List<WebElement> rawAll() {
        if (scopeLocator == null) {
            return driver.getSeleniumDriver().findElements(locator);
        }
        return driver.getSeleniumDriver().findElement(scopeLocator).findElements(locator);
    }

    public List<String> texts() {
        return rawAll().stream()
                .map(WebElement::getText)
                .toList();
    }

    public List<String> attributes(String attributeName) {
        return rawAll().stream()
                .map(element -> element.getAttribute(attributeName))
                .toList();
    }

    public QatraElementCollection assertCount(int expectedCount) {
        Assert.assertEquals(count(), expectedCount,
                "Unexpected element count for collection: " + locator);
        return this;
    }

    public QatraElementCollection assertMinimumCount(int minimumCount) {
        Assert.assertTrue(count() >= minimumCount,
                "Expected collection " + locator + " to have at least " + minimumCount + " elements, but found " + count());
        return this;
    }

    public QatraElementCollection assertContainsText(String expectedText) {
        boolean found = texts().stream().anyMatch(text -> text != null && text.contains(expectedText));
        Assert.assertTrue(found,
                "Expected collection " + locator + " to contain text: " + expectedText + " | actual=" + texts());
        return this;
    }

    public By locator() {
        return locator;
    }

    public By scopeLocator() {
        return scopeLocator;
    }

    @Override
    public String toString() {
        return "QatraElementCollection{" + (scopeLocator == null ? "" : "scope=" + scopeLocator + ", ") + locator + '}';
    }
}
