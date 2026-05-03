package io.github.qatra.web.actions;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.assertions.WebAssertions;
import io.github.qatra.web.fluent.FluentWeb;
import io.github.qatra.web.reports.AllureReport;
import io.github.qatra.web.waits.SmartWait;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;


/**
 * Fluent element-level actions with built-in smart waits.
 *
 * <pre>
 *   driver.element()
 *         .type(By.id("username"), "admin")
 *         .click(By.id("loginBtn"))
 *         .assertThat()
 *         .element(By.id("welcome")).isVisible();
 * </pre>
 */
public class ElementActions {

    private static final QatraLogger LOG = QatraLogger.getInstance();
    private final WebDriver driver;
    private final FluentWeb parent;
    private final long timeoutSeconds;

    public ElementActions(WebDriver driver, FluentWeb parent) {
        this.driver = driver;
        this.parent = parent;
        this.timeoutSeconds = QatraConfig.getInstance()
                .getIntProperty(QatraProperties.ELEMENT_TIMEOUT, 10);
    }

    // ─── Core Actions ─────────────────────────────────────────────────────────

    /**
     * Click an element. Waits for it to be clickable first.
     */
    public ElementActions click(By locator) {
        LOG.action("Click: {}", locator);
        AllureReport.step("Click element: " + locator);
        waitForClickable(locator).click();
        return this;
    }

    /**
     * Clear a field and type text into it.
     */
    public ElementActions type(By locator, String text) {
        LOG.action("Type '{}' into: {}", text, locator);
        AllureReport.step("Type text into: " + locator);
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
        return this;
    }

    /**
     * Type text without clearing first (append).
     */
    public ElementActions append(By locator, String text) {
        LOG.action("Append '{}' to: {}", text, locator);
        AllureReport.step("Append text to: " + locator);
        waitForVisible(locator).sendKeys(text);
        return this;
    }

    /**
     * Clear a field.
     */
    public ElementActions clear(By locator) {
        LOG.action("Clear: {}", locator);
        AllureReport.step("Clear element: " + locator);
        waitForVisible(locator).clear();
        return this;
    }

    /**
     * Submit a form by pressing ENTER on an element.
     */
    public ElementActions submit(By locator) {
        LOG.action("Submit via: {}", locator);
        AllureReport.step("Submit via element: " + locator);
        waitForVisible(locator).sendKeys(Keys.RETURN);
        return this;
    }

    /**
     * Hover over an element.
     */
    public ElementActions hover(By locator) {
        LOG.action("Hover over: {}", locator);
        AllureReport.step("Hover over element: " + locator);
        new Actions(driver).moveToElement(waitForVisible(locator)).perform();
        return this;
    }

    /**
     * Double-click an element.
     */
    public ElementActions doubleClick(By locator) {
        LOG.action("Double-click: {}", locator);
        AllureReport.step("Double-click element: " + locator);
        new Actions(driver).doubleClick(waitForClickable(locator)).perform();
        return this;
    }

    /**
     * Right-click (context menu) an element.
     */
    public ElementActions rightClick(By locator) {
        LOG.action("Right-click: {}", locator);
        AllureReport.step("Right-click element: " + locator);
        new Actions(driver).contextClick(waitForVisible(locator)).perform();
        return this;
    }

    /**
     * Scroll element into view and click it.
     */
    public ElementActions scrollAndClick(By locator) {
        LOG.action("Scroll & click: {}", locator);
        AllureReport.step("Scroll and click element: " + locator);
        WebElement element = waitForPresent(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        element.click();
        return this;
    }

    /**
     * Click an element via JavaScript (bypasses overlays).
     */
    public ElementActions jsClick(By locator) {
        LOG.action("JS Click: {}", locator);
        AllureReport.step("JavaScript click element: " + locator);
        WebElement element = waitForPresent(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        return this;
    }

    /**
     * Select a dropdown option by visible text.
     */
    public ElementActions selectByText(By locator, String text) {
        LOG.action("Select '{}' from: {}", text, locator);
        AllureReport.step("Select visible text from element: " + locator);
        new Select(waitForVisible(locator)).selectByVisibleText(text);
        return this;
    }

    /**
     * Select a dropdown option by value attribute.
     */
    public ElementActions selectByValue(By locator, String value) {
        LOG.action("Select by value '{}' from: {}", value, locator);
        AllureReport.step("Select value from element: " + locator);
        new Select(waitForVisible(locator)).selectByValue(value);
        return this;
    }

    /**
     * Check a checkbox (only if not already checked).
     */
    public ElementActions check(By locator) {
        LOG.action("Check: {}", locator);
        AllureReport.step("Check element: " + locator);
        WebElement element = waitForClickable(locator);
        if (!element.isSelected()) element.click();
        return this;
    }

    /**
     * Uncheck a checkbox (only if currently checked).
     */
    public ElementActions uncheck(By locator) {
        LOG.action("Uncheck: {}", locator);
        AllureReport.step("Uncheck element: " + locator);
        WebElement element = waitForClickable(locator);
        if (element.isSelected()) element.click();
        return this;
    }

    /**
     * Upload a file by providing its absolute path.
     */
    public ElementActions uploadFile(By locator, String absoluteFilePath) {
        LOG.action("Upload file: {}", absoluteFilePath);
        AllureReport.step("Upload file using element: " + locator);
        waitForPresent(locator).sendKeys(absoluteFilePath);
        return this;
    }


    /**
     * Clear a field and type text into it. Alias for type(), useful for readability.
     */
    public ElementActions clearAndType(By locator, String text) {
        return type(locator, text);
    }

    /**
     * Type text then press a keyboard key, for flows such as search + ENTER.
     */
    public ElementActions typeAndPress(By locator, String text, Keys key) {
        LOG.action("Type '{}' and press {} into: {}", text, key.name(), locator);
        AllureReport.step("Type text and press " + key.name() + " into element: " + locator);
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
        element.sendKeys(key);
        return this;
    }

    /**
     * Select a dropdown option by index.
     */
    public ElementActions selectByIndex(By locator, int index) {
        LOG.action("Select index '{}' from: {}", index, locator);
        AllureReport.step("Select index " + index + " from element: " + locator);
        new Select(waitForVisible(locator)).selectByIndex(index);
        return this;
    }

    /**
     * Highlight an element briefly by adding an outline. Useful while debugging.
     */
    public ElementActions highlight(By locator) {
        LOG.action("Highlight: {}", locator);
        AllureReport.step("Highlight element: " + locator);
        WebElement element = waitForPresent(locator);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].setAttribute('data-qatra-original-style', arguments[0].getAttribute('style') || '');" +
                "arguments[0].style.outline='3px solid #00a3ff';" +
                "arguments[0].style.outlineOffset='2px';",
                element
        );
        return this;
    }



    /**
     * Drag one element and drop it onto another element using Selenium Actions.
     */
    public ElementActions dragAndDrop(By sourceLocator, By targetLocator) {
        LOG.action("Drag and drop from {} to {}", sourceLocator, targetLocator);
        AllureReport.step("Drag and drop from " + sourceLocator + " to " + targetLocator);
        WebElement source = waitForVisible(sourceLocator);
        WebElement target = waitForVisible(targetLocator);
        new Actions(driver).dragAndDrop(source, target).perform();
        return this;
    }

    /**
     * Drag one element by offset using Selenium Actions.
     */
    public ElementActions dragByOffset(By sourceLocator, int xOffset, int yOffset) {
        LOG.action("Drag {} by offset x={}, y={}", sourceLocator, xOffset, yOffset);
        AllureReport.step("Drag element by offset: " + sourceLocator);
        WebElement source = waitForVisible(sourceLocator);
        new Actions(driver).dragAndDropBy(source, xOffset, yOffset).perform();
        return this;
    }

    /**
     * HTML5-friendly drag and drop fallback using JavaScript dataTransfer events.
     * Use this when native Selenium dragAndDrop does not trigger the application events.
     */
    public ElementActions html5DragAndDrop(By sourceLocator, By targetLocator) {
        LOG.action("HTML5 drag and drop from {} to {}", sourceLocator, targetLocator);
        AllureReport.step("HTML5 drag and drop from " + sourceLocator + " to " + targetLocator);
        WebElement source = waitForPresent(sourceLocator);
        WebElement target = waitForPresent(targetLocator);
        String script = "const source = arguments[0];" +
                "const target = arguments[1];" +
                "const dataTransfer = new DataTransfer();" +
                "source.dispatchEvent(new DragEvent('dragstart', {bubbles:true, cancelable:true, dataTransfer}));" +
                "target.dispatchEvent(new DragEvent('dragenter', {bubbles:true, cancelable:true, dataTransfer}));" +
                "target.dispatchEvent(new DragEvent('dragover', {bubbles:true, cancelable:true, dataTransfer}));" +
                "target.dispatchEvent(new DragEvent('drop', {bubbles:true, cancelable:true, dataTransfer}));" +
                "source.dispatchEvent(new DragEvent('dragend', {bubbles:true, cancelable:true, dataTransfer}));";
        ((JavascriptExecutor) driver).executeScript(script, source, target);
        return this;
    }

    /**
     * Wait until an element is present in the DOM.
     */
    public ElementActions waitUntilPresent(By locator) {
        waitForPresent(locator);
        return this;
    }

    /**
     * Wait until an element is visible.
     */
    public ElementActions waitUntilVisible(By locator) {
        waitForVisible(locator);
        return this;
    }

    /**
     * Wait until an element is clickable.
     */
    public ElementActions waitUntilClickable(By locator) {
        waitForClickable(locator);
        return this;
    }

    /**
     * Wait until an element is invisible or not present.
     */
    public ElementActions waitUntilInvisible(By locator) {
        SmartWait.untilInvisible(driver, locator, timeoutSeconds);
        return this;
    }

    /**
     * Wait until an element text contains the expected value.
     */
    public ElementActions waitUntilTextContains(By locator, String expectedText) {
        SmartWait.untilTextContains(driver, locator, expectedText, timeoutSeconds);
        return this;
    }

    /**
     * Wait until an element attribute equals the expected value.
     */
    public ElementActions waitUntilAttributeEquals(By locator, String attributeName, String expectedValue) {
        SmartWait.untilAttributeEquals(driver, locator, attributeName, expectedValue, timeoutSeconds);
        return this;
    }

    /**
     * Wait until an element value attribute equals the expected value.
     */
    public ElementActions waitUntilValueEquals(By locator, String expectedValue) {
        SmartWait.untilValueEquals(driver, locator, expectedValue, timeoutSeconds);
        return this;
    }

    /**
     * Wait until document.readyState becomes complete.
     */
    public ElementActions waitUntilPageReady() {
        SmartWait.untilPageReady(driver, timeoutSeconds);
        return this;
    }

    /**
     * Explicit short pause. Prefer smart waits; use this only for demos or controlled animations.
     */
    public ElementActions pause(long millis) {
        LOG.action("Pause for {} ms", millis);
        AllureReport.step("Pause for " + millis + " ms");
        SmartWait.pause(millis);
        return this;
    }

    /**
     * Scroll an element into view.
     */
    public ElementActions scrollTo(By locator) {
        LOG.action("Scroll to: {}", locator);
        AllureReport.step("Scroll to element: " + locator);
        WebElement element = waitForPresent(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
        return this;
    }

    /**
     * Focus an element using JavaScript.
     */
    public ElementActions focus(By locator) {
        LOG.action("Focus: {}", locator);
        AllureReport.step("Focus element: " + locator);
        WebElement element = waitForPresent(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].focus();", element);
        return this;
    }

    /**
     * Get visible text from an element.
     */
    public String getText(By locator) {
        LOG.action("Get text: {}", locator);
        AllureReport.step("Get text from element: " + locator);
        return waitForVisible(locator).getText();
    }

    /**
     * Get an attribute value from an element.
     */
    public String getAttribute(By locator, String attributeName) {
        LOG.action("Get attribute '{}' from: {}", attributeName, locator);
        AllureReport.step("Get attribute " + attributeName + " from element: " + locator);
        return waitForPresent(locator).getAttribute(attributeName);
    }

    /**
     * Get a CSS property value from an element.
     */
    public String getCssValue(By locator, String propertyName) {
        LOG.action("Get CSS '{}' from: {}", propertyName, locator);
        AllureReport.step("Get CSS " + propertyName + " from element: " + locator);
        return waitForPresent(locator).getCssValue(propertyName);
    }

    /**
     * Check if an element is displayed.
     */
    public boolean isDisplayed(By locator) {
        LOG.action("Check displayed: {}", locator);
        AllureReport.step("Check if element is displayed: " + locator);
        try {
            return waitForPresent(locator).isDisplayed();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Check if an element is enabled.
     */
    public boolean isEnabled(By locator) {
        LOG.action("Check enabled: {}", locator);
        AllureReport.step("Check if element is enabled: " + locator);
        try {
            return waitForPresent(locator).isEnabled();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Check if an element is selected.
     */
    public boolean isSelected(By locator) {
        LOG.action("Check selected: {}", locator);
        AllureReport.step("Check if element is selected: " + locator);
        try {
            return waitForPresent(locator).isSelected();
        } catch (TimeoutException | NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Get number of matching elements.
     */
    public int getElementCount(By locator) {
        LOG.action("Get element count: {}", locator);
        AllureReport.step("Get element count: " + locator);
        return driver.findElements(locator).size();
    }

    /**
     * Press a keyboard key.
     */
    public ElementActions pressKey(Keys key) {
        LOG.action("Press key: {}", key.name());
        AllureReport.step("Press key: " + key.name());
        new Actions(driver).sendKeys(key).perform();
        return this;
    }

    // ─── Wait Helpers ─────────────────────────────────────────────────────────

    private WebElement waitForVisible(By locator) {
        return SmartWait.untilVisible(driver, locator, timeoutSeconds);
    }

    private WebElement waitForClickable(By locator) {
        return SmartWait.untilClickable(driver, locator, timeoutSeconds);
    }

    private WebElement waitForPresent(By locator) {
        return SmartWait.untilPresent(driver, locator, timeoutSeconds);
    }

    // ─── Chainback ────────────────────────────────────────────────────────────

    public FluentWeb and() {
        return parent;
    }

    public BrowserActions browser() {
        return parent.browser();
    }

    public WebAssertions assertThat() {
        return parent.assertThat();
    }


    public AlertActions alert() {
        return parent.alert();
    }

    public FrameActions frame() {
        return parent.frame();
    }

    public WindowActions window() {
        return parent.window();
    }

    public ShadowDomActions shadow() {
        return parent.shadow();
    }

    public CookieActions cookies() {
        return parent.cookies();
    }

    public StorageActions storage() {
        return parent.storage();
    }

    public TableActions table(By tableLocator) {
        return parent.table(tableLocator);
    }

}
