package io.github.qatra.web.waits;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import io.github.qatra.web.rtl.RtlEngine;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * QATRA Smart Wait Engine built on Selenium FluentWait and custom predicates.
 *
 * <p>This class is intentionally different from a thin ExpectedConditions wrapper.
 * It provides Arabic-first and RTL-aware waits for dynamic enterprise and
 * government systems where content, direction, encoding, and custom widgets may
 * stabilize after the DOM is technically present.</p>
 *
 * <pre>
 * QatraWait.forElement(driver, By.id("title"))
 *         .waitUntilArabicTextIsVisible("مرحبا")
 *         .waitUntilRtlDirectionApplied()
 *         .waitUntilTextIsNotBroken();
 * </pre>
 */
public final class QatraWait {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final By locator;
    private final WaitOptions options;

    private QatraWait(WebDriver driver, By locator, WaitOptions options) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
        this.locator = locator;
        this.options = options == null ? WaitOptions.fromConfig() : options;
    }

    public static QatraWait forPage(WebDriver driver) {
        return new QatraWait(driver, null, WaitOptions.fromConfig());
    }

    public static QatraWait forPage(WebDriver driver, WaitOptions options) {
        return new QatraWait(driver, null, options);
    }

    public static QatraWait forElement(WebDriver driver, By locator) {
        Objects.requireNonNull(locator, "locator must not be null");
        return new QatraWait(driver, locator, WaitOptions.fromConfig());
    }

    public static QatraWait forElement(WebDriver driver, By locator, WaitOptions options) {
        Objects.requireNonNull(locator, "locator must not be null");
        return new QatraWait(driver, locator, options);
    }

    public QatraWait withOptions(WaitOptions newOptions) {
        return new QatraWait(driver, locator, newOptions);
    }

    public QatraWait withTimeout(Duration timeout) {
        return withOptions(options.toBuilder().timeout(timeout).build());
    }

    public QatraWait pollingEvery(Duration pollingInterval) {
        return withOptions(options.toBuilder().pollingInterval(pollingInterval).build());
    }

    public WebElement element() {
        ensureElementContext("element()");
        return driver.findElement(locator);
    }

    public QatraWait waitUntilVisible() {
        ensureElementContext("waitUntilVisible");
        waitForElement("Wait until element is visible: " + locator, webDriver -> {
            WebElement element = webDriver.findElement(locator);
            return element.isDisplayed() ? element : null;
        });
        return this;
    }

    public QatraWait waitUntilClickable() {
        ensureElementContext("waitUntilClickable");
        waitForElement("Wait until element is clickable: " + locator, webDriver -> {
            WebElement element = webDriver.findElement(locator);
            return element.isDisplayed() && element.isEnabled() ? element : null;
        });
        return this;
    }

    public QatraWait waitUntilPresent() {
        ensureElementContext("waitUntilPresent");
        waitForElement("Wait until element is present: " + locator, webDriver -> webDriver.findElement(locator));
        return this;
    }

    public QatraWait waitUntilInvisible() {
        ensureElementContext("waitUntilInvisible");
        waitForBoolean("Wait until element is invisible: " + locator, webDriver -> {
            List<WebElement> elements = webDriver.findElements(locator);
            if (elements.isEmpty()) {
                return true;
            }
            for (WebElement element : elements) {
                if (element.isDisplayed()) {
                    return null;
                }
            }
            return true;
        });
        return this;
    }

    public QatraWait waitUntilTextContains(String expectedText) {
        ensureElementContext("waitUntilTextContains");
        waitForElement("Wait until text contains '" + expectedText + "': " + locator, webDriver -> {
            WebElement element = webDriver.findElement(locator);
            String text = collectElementText(element);
            return text.contains(safe(expectedText)) ? element : null;
        });
        return this;
    }

    public QatraWait waitUntilAttributeEquals(String attributeName, String expectedValue) {
        ensureElementContext("waitUntilAttributeEquals");
        waitForElement("Wait until attribute '" + attributeName + "' equals '" + expectedValue + "': " + locator,
                webDriver -> {
                    WebElement element = webDriver.findElement(locator);
                    String actual = safe(element.getAttribute(attributeName));
                    return actual.equals(safe(expectedValue)) ? element : null;
                });
        return this;
    }

    public QatraWait waitUntilValueEquals(String expectedValue) {
        return waitUntilAttributeEquals("value", expectedValue);
    }

    public QatraWait waitUntilArabicTextIsVisible(String expectedArabicText) {
        ensureElementContext("waitUntilArabicTextIsVisible");
        waitForElement("Wait until Arabic text is visible: " + locator, webDriver -> {
            WebElement element = webDriver.findElement(locator);
            if (!element.isDisplayed()) {
                return null;
            }
            String text = collectElementText(element);
            boolean hasArabic = RtlEngine.containsArabicText(text);
            boolean expectedMatches = safe(expectedArabicText).isBlank() || text.contains(expectedArabicText);
            return hasArabic && expectedMatches ? element : null;
        });
        return this;
    }

    public QatraWait waitUntilArabicTextIsVisible() {
        return waitUntilArabicTextIsVisible("");
    }

    public QatraWait waitUntilTextIsNotBroken() {
        ensureElementContext("waitUntilTextIsNotBroken");
        waitForElement("Wait until text has no broken Arabic/encoding characters: " + locator, webDriver -> {
            WebElement element = webDriver.findElement(locator);
            String text = collectElementText(element);
            return !RtlEngine.hasBrokenArabicEncoding(text) ? element : null;
        });
        return this;
    }

    public QatraWait waitUntilRtlDirectionApplied() {
        ensureElementContext("waitUntilRtlDirectionApplied");
        waitForElement("Wait until RTL direction is applied: " + locator, webDriver -> {
            WebElement element = webDriver.findElement(locator);
            return "rtl".equals(RtlEngine.effectiveDirection(webDriver, element)) ? element : null;
        });
        return this;
    }

    public QatraWait waitUntilEncodingIsValid() {
        return waitUntilTextIsNotBroken();
    }

    public QatraWait waitUntilArabicTextRenderedCorrectly() {
        ensureElementContext("waitUntilArabicTextRenderedCorrectly");
        waitForElement("Wait until Arabic text is rendered correctly: " + locator, webDriver -> {
            WebElement element = webDriver.findElement(locator);
            if (!element.isDisplayed()) {
                return null;
            }
            String text = collectElementText(element);
            boolean ok = RtlEngine.containsArabicText(text)
                    && !RtlEngine.hasBrokenArabicEncoding(text)
                    && "rtl".equals(RtlEngine.effectiveDirection(webDriver, element));
            return ok ? element : null;
        });
        return this;
    }

    public QatraWait waitUntilElementIsStable() {
        ensureElementContext("waitUntilElementIsStable");
        AtomicReference<Rectangle> previousRect = new AtomicReference<>();
        AtomicInteger stablePolls = new AtomicInteger(0);

        waitForElement("Wait until element is visually stable: " + locator, webDriver -> {
            WebElement element = webDriver.findElement(locator);
            if (!element.isDisplayed()) {
                stablePolls.set(0);
                previousRect.set(null);
                return null;
            }

            Rectangle current = element.getRect();
            Rectangle previous = previousRect.get();
            previousRect.set(current);

            if (sameRect(previous, current)) {
                return stablePolls.incrementAndGet() >= 2 ? element : null;
            }

            stablePolls.set(0);
            return null;
        });
        return this;
    }

    public QatraWait waitUntilCustomComponentReady() {
        ensureElementContext("waitUntilCustomComponentReady");
        waitForElement("Wait until custom component is ready: " + locator, webDriver -> {
            WebElement element = webDriver.findElement(locator);
            if (!element.isDisplayed()) {
                return null;
            }

            String ariaBusy = safe(element.getAttribute("aria-busy"));
            String disabled = safe(element.getAttribute("disabled"));
            String className = safe(element.getAttribute("class")).toLowerCase();
            String dataLoading = safe(element.getAttribute("data-loading"));

            boolean busy = "true".equalsIgnoreCase(ariaBusy)
                    || "true".equalsIgnoreCase(dataLoading)
                    || "disabled".equalsIgnoreCase(disabled)
                    || className.contains("loading")
                    || className.contains("skeleton");

            return !busy ? element : null;
        });
        return this;
    }

    public QatraWait waitUntilPageIsFullyReady() {
        waitForBoolean("Wait until page is fully ready", webDriver -> pageReady(webDriver) && ajaxComplete(webDriver) ? true : null);
        return this;
    }

    public QatraWait waitUntilAjaxIsCompleted() {
        waitForBoolean("Wait until Ajax is completed", webDriver -> ajaxComplete(webDriver) ? true : null);
        return this;
    }

    WebElement waitForElement(String description, Function<WebDriver, WebElement> condition) {
        return run(description, condition);
    }

    Boolean waitForBoolean(String description, Function<WebDriver, Boolean> condition) {
        return run(description, condition);
    }

    private <T> T run(String description, Function<WebDriver, T> condition) {
        LOG.action(description);
        AllureReport.step(description);
        try {
            return fluentWait().until(condition);
        } catch (TimeoutException e) {
            throw new TimeoutException("QATRA SmartWait timeout: " + description
                    + " within " + options.timeout().toSeconds() + " seconds.", e);
        }
    }

    private FluentWait<WebDriver> fluentWait() {
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(options.timeout())
                .pollingEvery(options.pollingInterval());
        for (Class<? extends Throwable> ignoredException : options.ignoredExceptions()) {
            wait.ignoring(ignoredException);
        }
        return wait;
    }

    private void ensureElementContext(String method) {
        if (locator == null) {
            throw new IllegalStateException(method + " requires QatraWait.forElement(driver, locator). Use forPage() for page waits.");
        }
    }

    private static String collectElementText(WebElement element) {
        if (element == null) {
            return "";
        }
        return String.join(" ",
                safe(element.getText()),
                safe(element.getAttribute("textContent")),
                safe(element.getAttribute("placeholder")),
                safe(element.getAttribute("value")),
                safe(element.getAttribute("aria-label")),
                safe(element.getAttribute("title")),
                safe(element.getAttribute("alt"))
        ).trim();
    }

    private static boolean pageReady(WebDriver driver) {
        if (!(driver instanceof JavascriptExecutor js)) {
            return true;
        }
        Object readyState = js.executeScript("return document.readyState");
        return "complete".equals(String.valueOf(readyState));
    }

    private static boolean ajaxComplete(WebDriver driver) {
        if (!(driver instanceof JavascriptExecutor js)) {
            return true;
        }
        Object value = js.executeScript("""
                const jqueryActive = window.jQuery ? window.jQuery.active : 0;
                const angularReady = window.getAllAngularTestabilities
                    ? window.getAllAngularTestabilities().every(t => t.isStable())
                    : true;
                const busyElements = document.querySelectorAll('[aria-busy="true"], .loading, .spinner, .loader, .skeleton');
                return jqueryActive === 0 && angularReady && busyElements.length === 0;
                """);
        return Boolean.TRUE.equals(value);
    }

    private static boolean sameRect(Rectangle a, Rectangle b) {
        if (a == null || b == null) {
            return false;
        }
        return a.getX() == b.getX()
                && a.getY() == b.getY()
                && a.getWidth() == b.getWidth()
                && a.getHeight() == b.getHeight();
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
