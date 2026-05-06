package io.github.qatra.web.assertions.engine;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Element-level assertion facade.
 *
 * <p>This class is intentionally small and delegates text, RTL, encoding and visual
 * checks to focused assertion classes. Existing WebAssertions remains backward
 * compatible, while this engine gives QATRA a cleaner architecture for new APIs.</p>
 */
public class ElementAssert {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final By locator;
    private final WebElement fixedElement;
    private Duration timeout = Duration.ofSeconds(10);

    private ElementAssert(WebDriver driver, By locator, WebElement fixedElement) {
        this.driver = driver;
        this.locator = locator;
        this.fixedElement = fixedElement;
    }

    public static ElementAssert of(WebDriver driver, By locator) {
        return new ElementAssert(driver, locator, null);
    }

    public static ElementAssert of(WebDriver driver, WebElement element) {
        return new ElementAssert(driver, null, element);
    }

    public ElementAssert withTimeout(Duration timeout) {
        if (timeout != null && !timeout.isNegative() && !timeout.isZero()) {
            this.timeout = timeout;
        }
        return this;
    }

    public ElementAssert exists() {
        LOG.assertion("[QATRA Assert] Element exists: {}", context());
        AllureReport.step("QATRA Assert: element exists - " + context());
        try {
            resolvePresent();
            return this;
        } catch (Throwable throwable) {
            fail("Element exists", "element should exist in DOM", "element was not found", null);
            return this;
        }
    }

    public ElementAssert doesNotExist() {
        LOG.assertion("[QATRA Assert] Element does not exist: {}", context());
        AllureReport.step("QATRA Assert: element does not exist - " + context());
        if (locator == null) {
            fail("Element does not exist", "no fixed WebElement", "a fixed WebElement was provided", fixedElement);
        }
        List<WebElement> elements = driver.findElements(locator);
        if (!elements.isEmpty()) {
            fail("Element does not exist", "0 matching elements", elements.size() + " matching element(s)", elements.get(0));
        }
        return this;
    }

    public ElementAssert isVisible() {
        LOG.assertion("[QATRA Assert] Element visible: {}", context());
        AllureReport.step("QATRA Assert: element visible - " + context());
        WebElement element = resolveVisible();
        if (!element.isDisplayed()) {
            fail("Element visible", "displayed=true", "displayed=false", element);
        }
        return this;
    }

    public ElementAssert isNotVisible() {
        LOG.assertion("[QATRA Assert] Element not visible: {}", context());
        AllureReport.step("QATRA Assert: element not visible - " + context());
        try {
            WebElement element = resolvePresent();
            if (element.isDisplayed()) {
                fail("Element not visible", "displayed=false", "displayed=true", element);
            }
        } catch (NoSuchElementException ignored) {
            return this;
        }
        return this;
    }

    public ElementAssert isEnabled() {
        WebElement element = resolvePresent();
        if (!element.isEnabled()) {
            fail("Element enabled", "enabled=true", "enabled=false", element);
        }
        return this;
    }

    public ElementAssert isDisabled() {
        WebElement element = resolvePresent();
        if (element.isEnabled()) {
            fail("Element disabled", "enabled=false", "enabled=true", element);
        }
        return this;
    }

    public ElementAssert isSelected() {
        WebElement element = resolvePresent();
        if (!element.isSelected()) {
            fail("Element selected", "selected=true", "selected=false", element);
        }
        return this;
    }

    public ElementAssert isNotSelected() {
        WebElement element = resolvePresent();
        if (element.isSelected()) {
            fail("Element not selected", "selected=false", "selected=true", element);
        }
        return this;
    }

    public ElementAssert hasAttribute(String attribute, String expectedValue) {
        WebElement element = resolvePresent();
        String actual = safe(element.getAttribute(attribute));
        if (!actual.equals(safe(expectedValue))) {
            fail("Element attribute", attribute + "=" + safe(expectedValue), attribute + "=" + actual, element);
        }
        return this;
    }

    public ElementAssert attributeContains(String attribute, String expectedValue) {
        WebElement element = resolvePresent();
        String actual = safe(element.getAttribute(attribute));
        if (!actual.contains(safe(expectedValue))) {
            fail("Element attribute contains", attribute + " contains " + safe(expectedValue), attribute + "=" + actual, element);
        }
        return this;
    }

    public ElementAssert hasValue(String expectedValue) {
        return hasAttribute("value", expectedValue);
    }

    public TextAssert text() {
        return new TextAssert(this);
    }

    public RtlAssert rtl() {
        return new RtlAssert(this);
    }

    public EncodingAssert encoding() {
        return new EncodingAssert(this);
    }

    public VisualAssert visual() {
        return new VisualAssert(this);
    }

    public ElementAssert and() {
        return this;
    }

    public ElementAssert element(By newLocator) {
        return ElementAssert.of(driver, newLocator).withTimeout(timeout);
    }

    public WebElement resolvePresent() {
        if (fixedElement != null) {
            return fixedElement;
        }
        return seleniumWait().until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public WebElement resolveVisible() {
        if (fixedElement != null) {
            return fixedElement;
        }
        return seleniumWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebDriver driver() {
        return driver;
    }

    public String context() {
        return locator != null ? locator.toString() : "fixed WebElement";
    }

    public Duration timeout() {
        return timeout;
    }

    void fail(String assertionName, String expected, String actual, WebElement element) {
        WebElement evidenceElement = element;
        if (evidenceElement == null) {
            try {
                evidenceElement = fixedElement != null ? fixedElement : driver.findElement(locator);
            } catch (Throwable ignored) {
                // Element not available; evidence will be minimal.
            }
        }
        String evidence = AssertionEvidence.elementSnapshot(driver, evidenceElement);
        String diagnostics = AssertionDiagnostics.capture(driver, assertionName, context(), expected, actual, evidenceElement);
        String message = new AssertionFailure(assertionName, context(), expected, actual, evidence + diagnostics).getMessage();
        LOG.assertionFailed(message);
        AllureReport.attachText("QATRA assertion failure", message);
        throw new AssertionFailure(message);
    }

    private WebDriverWait seleniumWait() {
        return new WebDriverWait(driver, timeout);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
