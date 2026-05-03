package io.github.qatra.web.page;

import io.github.qatra.web.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;

/**
 * Collection helper for repeated components such as cards, menu items, or rows.
 */
public class QatraComponentCollection<T extends QatraComponent> {

    private final WebDriver driver;
    private final By rootLocator;
    private final Class<T> componentClass;

    public QatraComponentCollection(WebDriver driver, By rootLocator, Class<T> componentClass) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
        this.rootLocator = Objects.requireNonNull(rootLocator, "rootLocator must not be null");
        this.componentClass = Objects.requireNonNull(componentClass, "componentClass must not be null");
    }

    public int count() {
        return rawAll().size();
    }

    public List<WebElement> rawAll() {
        return driver.getSeleniumDriver().findElements(rootLocator);
    }

    public List<String> texts() {
        return rawAll().stream().map(WebElement::getText).toList();
    }

    public QatraComponentCollection<T> assertCount(int expectedCount) {
        Assert.assertEquals(count(), expectedCount, "Unexpected component count for: " + rootLocator);
        return this;
    }

    public QatraComponentCollection<T> assertMinimumCount(int minimumCount) {
        Assert.assertTrue(count() >= minimumCount,
                "Expected at least " + minimumCount + " components for " + rootLocator + ", but found " + count());
        return this;
    }

    public QatraComponentCollection<T> assertAnyContainsText(String expectedText) {
        boolean found = texts().stream().anyMatch(text -> text != null && text.contains(expectedText));
        Assert.assertTrue(found,
                "Expected at least one component " + rootLocator + " to contain text: " + expectedText + " | actual=" + texts());
        return this;
    }

    /**
     * Create a component instance bound to the same component locator.
     *
     * <p>This is most useful for collections where the test only needs shared
     * behavior or count/text assertions. For index-specific DOM scoping, use rawAll().get(index).</p>
     */
    public T componentPrototype() {
        try {
            Constructor<T> constructor = componentClass.getDeclaredConstructor(WebDriver.class, By.class);
            constructor.setAccessible(true);
            return constructor.newInstance(driver, rootLocator);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Failed to create component: " + componentClass.getName() +
                            ". Component must define constructor(WebDriver, By).",
                    e
            );
        }
    }

    public By rootLocator() {
        return rootLocator;
    }
}
