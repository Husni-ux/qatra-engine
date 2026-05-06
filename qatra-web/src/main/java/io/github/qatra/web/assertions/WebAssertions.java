package io.github.qatra.web.assertions;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.actions.BrowserActions;
import io.github.qatra.web.actions.ElementActions;
import io.github.qatra.web.fluent.FluentWeb;
import io.github.qatra.web.reports.AllureReport;
import io.github.qatra.web.reports.DiagnosticsManager;
import io.github.qatra.web.assertions.engine.ElementAssert;
import io.github.qatra.web.assertions.engine.QatraAssert;
import io.github.qatra.web.rtl.RtlEngine;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * Fluent assertions for web testing.
 *
 * <p>Entry point for all assertions:</p>
 * <pre>
 *   driver.assertThat()
 *         .element(By.id("msg")).isVisible().containsText("Success")
 *         .and()
 *         .browser().title().contains("Dashboard");
 * </pre>
 */
public class WebAssertions {

    private static final QatraLogger LOG = QatraLogger.getInstance();
    private final WebDriver driver;
    private final FluentWeb parent;
    private final long timeoutSeconds;

    public WebAssertions(WebDriver driver, FluentWeb parent, long timeoutSeconds) {
        this.driver = driver;
        this.parent = parent;
        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * Start element-level assertions.
     */
    public ElementAssertions element(By locator) {
        return new ElementAssertions(locator, driver, this, timeoutSeconds);
    }


    /**
     * Start the cleaner QATRA Web Assertion Engine while keeping the legacy
     * WebAssertions API backward-compatible.
     */
    public ElementAssert expect(By locator) {
        return QatraAssert.that(driver, locator, Duration.ofSeconds(timeoutSeconds));
    }

    /**
     * Start browser-level assertions.
     */
    public BrowserAssertions browser() {
        return new BrowserAssertions(driver, this);
    }

    /**
     * Start alert-level assertions.
     */
    public AlertAssertions alert() {
        return new AlertAssertions(driver, this);
    }

    /**
     * Start window/tab assertions.
     */
    public WindowAssertions window() {
        return new WindowAssertions(driver, this);
    }

    /**
     * Start cookie assertions.
     */
    public CookieAssertions cookies() {
        return new CookieAssertions(driver, this);
    }

    /**
     * Start localStorage/sessionStorage assertions.
     */
    public StorageAssertions storage() {
        return new StorageAssertions(driver, this);
    }

    /**
     * Start Shadow DOM assertions for an element inside an open shadow root.
     */
    public ShadowDomAssertions shadow(By hostLocator, By shadowLocator) {
        return new ShadowDomAssertions(driver, this, hostLocator, shadowLocator);
    }

    /**
     * Start standard HTML table assertions.
     */
    public TableAssertions table(By tableLocator) {
        return new TableAssertions(driver, this, tableLocator);
    }

    /**
     * Start page health assertions such as broken links and broken images.
     */
    public PageHealthAssertions pageHealth() {
        return new PageHealthAssertions(driver, this);
    }

    /**
     * Start download-folder assertions.
     */
    public DownloadAssertions downloads(Path downloadDirectory) {
        return new DownloadAssertions(downloadDirectory, this);
    }

    public FluentWeb and() {
        return parent;
    }

    public ElementActions element() {
        return parent.element();
    }

    public BrowserActions browserActions() {
        return parent.browser();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ElementAssertions — element-level assertions
    // ══════════════════════════════════════════════════════════════════════════

    public static class ElementAssertions {

        private final By locator;
        private final WebDriver driver;
        private final WebAssertions parent;
        private final long timeoutSeconds;

        public ElementAssertions(By locator, WebDriver driver, WebAssertions parent, long timeoutSeconds) {
            this.locator = locator;
            this.driver = driver;
            this.parent = parent;
            this.timeoutSeconds = timeoutSeconds;
        }

        // ─── Presence / Visibility ───────────────────────────────────────────

        /** Assert element exists in the DOM. */
        public ElementAssertions exists() {
            LOG.assertion("Assert element exists: {}", locator);
            AllureReport.step("Assert element exists: " + locator);
            try {
                waitForPresent();
                LOG.assertion("✓ Element exists: {}", locator);
            } catch (TimeoutException e) {
                fail("Element does NOT exist in DOM: " + locator);
            }
            return this;
        }

        /** Assert element does not exist in the DOM. */
        public ElementAssertions doesNotExist() {
            LOG.assertion("Assert element does not exist: {}", locator);
            AllureReport.step("Assert element does not exist: " + locator);
            try {
                createWait().until(ExpectedConditions.numberOfElementsToBe(locator, 0));
                LOG.assertion("✓ Element does not exist: {}", locator);
            } catch (TimeoutException e) {
                fail("Element exists but should NOT exist: " + locator + " | count=" + count());
            }
            return this;
        }

        /** Assert element is visible on the page. */
        public ElementAssertions isVisible() {
            LOG.assertion("Assert visible: {}", locator);
            AllureReport.step("Assert element is visible: " + locator);
            try {
                waitForVisible();
                LOG.assertion("✓ Element is visible: {}", locator);
            } catch (TimeoutException e) {
                fail("Element is NOT visible within " + timeoutSeconds + " seconds: " + locator);
            }
            return this;
        }

        /** Assert element is NOT visible, or not present at all. */
        public ElementAssertions isNotVisible() {
            LOG.assertion("Assert NOT visible: {}", locator);
            AllureReport.step("Assert element is not visible: " + locator);
            try {
                createWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
                LOG.assertion("✓ Element is not visible: {}", locator);
            } catch (TimeoutException e) {
                fail("Element IS visible but should NOT be visible: " + locator);
            }
            return this;
        }

        // ─── Text Assertions ─────────────────────────────────────────────────

        /** Assert element text contains the given substring. */
        public ElementAssertions containsText(String expectedText) {
            LOG.assertion("Assert text contains '{}': {}", expectedText, locator);
            AllureReport.step("Assert element text contains [" + expectedText + "]: " + locator);
            WebElement element = waitForPresent();
            String actual = element.getText();
            if (!safe(actual).contains(safe(expectedText))) {
                fail(String.format("Text mismatch on %s.%n  Expected to contain: '%s'%n  Actual: '%s'", locator, expectedText, actual));
            }
            LOG.assertion("✓ Text contains '{}': {}", expectedText, locator);
            return this;
        }

        /** Assert element text exactly equals the given value after trimming both sides. */
        public ElementAssertions hasText(String expectedText) {
            LOG.assertion("Assert exact text '{}': {}", expectedText, locator);
            AllureReport.step("Assert element text equals [" + expectedText + "]: " + locator);
            String actual = waitForPresent().getText().trim();
            String expected = safe(expectedText).trim();
            if (!actual.equals(expected)) {
                fail(String.format("Text mismatch on %s.%n  Expected: '%s'%n  Actual: '%s'", locator, expectedText, actual));
            }
            LOG.assertion("✓ Exact text '{}': {}", expectedText, locator);
            return this;
        }

        /** Alias for hasText for more readable tests. */
        public ElementAssertions hasExactText(String expectedText) {
            return hasText(expectedText);
        }

        // ─── State Assertions ────────────────────────────────────────────────

        /** Assert element is enabled/interactable. */
        public ElementAssertions isEnabled() {
            LOG.assertion("Assert enabled: {}", locator);
            AllureReport.step("Assert element is enabled: " + locator);
            boolean enabled = waitForPresent().isEnabled();
            if (!enabled) {
                fail("Element is NOT enabled: " + locator);
            }
            LOG.assertion("✓ Element is enabled: {}", locator);
            return this;
        }

        /** Assert element is disabled. */
        public ElementAssertions isDisabled() {
            LOG.assertion("Assert disabled: {}", locator);
            AllureReport.step("Assert element is disabled: " + locator);
            boolean enabled = waitForPresent().isEnabled();
            if (enabled) {
                fail("Element IS enabled but should be disabled: " + locator);
            }
            LOG.assertion("✓ Element is disabled: {}", locator);
            return this;
        }

        /** Assert checkbox/radio/option is selected. */
        public ElementAssertions isSelected() {
            LOG.assertion("Assert selected: {}", locator);
            AllureReport.step("Assert element is selected: " + locator);
            boolean selected = waitForPresent().isSelected();
            if (!selected) {
                fail("Element is NOT selected: " + locator);
            }
            LOG.assertion("✓ Element is selected: {}", locator);
            return this;
        }

        /** Assert checkbox/radio/option is not selected. */
        public ElementAssertions isNotSelected() {
            LOG.assertion("Assert not selected: {}", locator);
            AllureReport.step("Assert element is not selected: " + locator);
            boolean selected = waitForPresent().isSelected();
            if (selected) {
                fail("Element IS selected but should NOT be selected: " + locator);
            }
            LOG.assertion("✓ Element is not selected: {}", locator);
            return this;
        }

        /** Alias for isSelected, useful for checkbox naming. */
        public ElementAssertions isChecked() {
            return isSelected();
        }

        /** Alias for isNotSelected, useful for checkbox naming. */
        public ElementAssertions isUnchecked() {
            return isNotSelected();
        }

        // ─── Attribute / Value / CSS Assertions ──────────────────────────────

        /** Assert element attribute equals the given value. */
        public ElementAssertions hasAttribute(String attribute, String expectedValue) {
            LOG.assertion("Assert attribute '{}' = '{}': {}", attribute, expectedValue, locator);
            AllureReport.step("Assert element attribute " + attribute + " equals [" + expectedValue + "]: " + locator);
            String actual = waitForPresent().getAttribute(attribute);
            if (!safe(actual).equals(safe(expectedValue))) {
                fail(String.format("Attribute '%s' mismatch on %s.%n  Expected: '%s'%n  Actual: '%s'", attribute, locator, expectedValue, actual));
            }
            LOG.assertion("✓ Attribute '{}' = '{}': {}", attribute, expectedValue, locator);
            return this;
        }

        /** Assert element attribute contains the given value. */
        public ElementAssertions attributeContains(String attribute, String expectedSubstring) {
            LOG.assertion("Assert attribute '{}' contains '{}': {}", attribute, expectedSubstring, locator);
            AllureReport.step("Assert element attribute " + attribute + " contains [" + expectedSubstring + "]: " + locator);
            String actual = waitForPresent().getAttribute(attribute);
            if (!safe(actual).contains(safe(expectedSubstring))) {
                fail(String.format("Attribute '%s' mismatch on %s.%n  Expected to contain: '%s'%n  Actual: '%s'", attribute, locator, expectedSubstring, actual));
            }
            LOG.assertion("✓ Attribute '{}' contains '{}': {}", attribute, expectedSubstring, locator);
            return this;
        }

        /** Assert input value equals the given value. */
        public ElementAssertions hasValue(String expectedValue) {
            return hasAttribute("value", expectedValue);
        }

        /** Assert input value contains the given value. */
        public ElementAssertions valueContains(String expectedSubstring) {
            return attributeContains("value", expectedSubstring);
        }

        /** Assert CSS property equals the given value. */
        public ElementAssertions hasCssValue(String propertyName, String expectedValue) {
            LOG.assertion("Assert CSS '{}' = '{}': {}", propertyName, expectedValue, locator);
            AllureReport.step("Assert element CSS " + propertyName + " equals [" + expectedValue + "]: " + locator);
            String actual = waitForPresent().getCssValue(propertyName);
            if (!safe(actual).equals(safe(expectedValue))) {
                fail(String.format("CSS property '%s' mismatch on %s.%n  Expected: '%s'%n  Actual: '%s'", propertyName, locator, expectedValue, actual));
            }
            LOG.assertion("✓ CSS '{}' = '{}': {}", propertyName, expectedValue, locator);
            return this;
        }

        /** Assert CSS property contains the given value. */
        public ElementAssertions cssValueContains(String propertyName, String expectedSubstring) {
            LOG.assertion("Assert CSS '{}' contains '{}': {}", propertyName, expectedSubstring, locator);
            AllureReport.step("Assert element CSS " + propertyName + " contains [" + expectedSubstring + "]: " + locator);
            String actual = waitForPresent().getCssValue(propertyName);
            if (!safe(actual).contains(safe(expectedSubstring))) {
                fail(String.format("CSS property '%s' mismatch on %s.%n  Expected to contain: '%s'%n  Actual: '%s'", propertyName, locator, expectedSubstring, actual));
            }
            LOG.assertion("✓ CSS '{}' contains '{}': {}", propertyName, expectedSubstring, locator);
            return this;
        }

        /** Assert element class attribute contains a class name as a token. */
        public ElementAssertions hasClass(String expectedClassName) {
            LOG.assertion("Assert class contains token '{}': {}", expectedClassName, locator);
            AllureReport.step("Assert element has class [" + expectedClassName + "]: " + locator);
            String classes = safe(waitForPresent().getAttribute("class"));
            boolean found = List.of(classes.split("\\s+")).contains(expectedClassName);
            if (!found) {
                fail(String.format("Class token not found on %s.%n  Expected class: '%s'%n  Actual classes: '%s'", locator, expectedClassName, classes));
            }
            LOG.assertion("✓ Class contains token '{}': {}", expectedClassName, locator);
            return this;
        }

        /** Assert element tag name equals expected tag name. */
        public ElementAssertions hasTagName(String expectedTagName) {
            LOG.assertion("Assert tag name '{}': {}", expectedTagName, locator);
            AllureReport.step("Assert element tag name is [" + expectedTagName + "]: " + locator);
            String actual = waitForPresent().getTagName();
            if (!safe(actual).equalsIgnoreCase(safe(expectedTagName))) {
                fail(String.format("Tag name mismatch on %s.%n  Expected: '%s'%n  Actual: '%s'", locator, expectedTagName, actual));
            }
            LOG.assertion("✓ Tag name '{}': {}", expectedTagName, locator);
            return this;
        }

        // ─── Count Assertions ────────────────────────────────────────────────

        /** Assert the locator matches exactly the expected number of elements. */
        public ElementAssertions hasCount(int expectedCount) {
            LOG.assertion("Assert element count {}: {}", expectedCount, locator);
            AllureReport.step("Assert element count is [" + expectedCount + "]: " + locator);
            int actual = count();
            if (actual != expectedCount) {
                fail(String.format("Element count mismatch on %s.%n  Expected: %d%n  Actual: %d", locator, expectedCount, actual));
            }
            LOG.assertion("✓ Element count {}: {}", expectedCount, locator);
            return this;
        }

        /** Assert the locator matches at least the expected number of elements. */
        public ElementAssertions hasMinimumCount(int minimumCount) {
            LOG.assertion("Assert element minimum count {}: {}", minimumCount, locator);
            AllureReport.step("Assert element minimum count is [" + minimumCount + "]: " + locator);
            int actual = count();
            if (actual < minimumCount) {
                fail(String.format("Element count mismatch on %s.%n  Expected at least: %d%n  Actual: %d", locator, minimumCount, actual));
            }
            LOG.assertion("✓ Element minimum count {}: {}", minimumCount, locator);
            return this;
        }

        // ─── Arabic / RTL Assertions ─────────────────────────────────────────

        /** Assert CSS/HTML direction is RTL. */
        public ElementAssertions isRTL() {
            LOG.rtl("Assert RTL direction: {}", locator);
            AllureReport.step("Assert element direction is RTL: " + locator);
            assertDirection("rtl");
            LOG.rtl("✓ Element is RTL: {}", locator);
            return this;
        }

        /** Assert CSS/HTML direction is LTR. */
        public ElementAssertions isLTR() {
            LOG.assertion("Assert LTR direction: {}", locator);
            AllureReport.step("Assert element direction is LTR: " + locator);
            assertDirection("ltr");
            LOG.assertion("✓ Element is LTR: {}", locator);
            return this;
        }

        /** Alias for isRTL for readability in Arabic/RTL test scenarios. */
        public ElementAssertions hasDirectionRTL() {
            return isRTL();
        }

        /** Alias for isLTR for readability in bilingual test scenarios. */
        public ElementAssertions hasDirectionLTR() {
            return isLTR();
        }

        /** Assert element text contains Arabic letters. */
        public ElementAssertions hasArabicText() {
            LOG.rtl("Assert Arabic text exists: {}", locator);
            AllureReport.step("Assert element has Arabic text: " + locator);
            String text = waitForPresent().getText();
            if (!text.matches(".*[\\u0600-\\u06FF].*")) {
                fail(String.format("Arabic text not found on %s.%n  Actual text: '%s'", locator, text));
            }
            LOG.rtl("✓ Arabic text exists: {}", locator);
            return this;
        }

        /** Assert element text has Arabic-Indic digits such as ١٢٣. */
        public ElementAssertions hasArabicDigits() {
            LOG.rtl("Assert Arabic digits exist: {}", locator);
            AllureReport.step("Assert element has Arabic digits: " + locator);
            String text = waitForPresent().getText();
            if (!text.matches(".*[\\u0660-\\u0669].*")) {
                fail(String.format("Arabic-Indic digits not found on %s.%n  Actual text: '%s'", locator, text));
            }
            LOG.rtl("✓ Arabic digits exist: {}", locator);
            return this;
        }

        /** Assert element text has English digits such as 123. */
        public ElementAssertions hasEnglishDigits() {
            LOG.assertion("Assert English digits exist: {}", locator);
            AllureReport.step("Assert element has English digits: " + locator);
            String text = waitForPresent().getText();
            if (!text.matches(".*[0-9].*")) {
                fail(String.format("English digits not found on %s.%n  Actual text: '%s'", locator, text));
            }
            LOG.assertion("✓ English digits exist: {}", locator);
            return this;
        }

        /**
         * Assert text does not contain common broken Arabic encoding indicators.
         *
         * <p>This catches common visible symptoms like replacement characters,
         * repeated question marks, or mojibake fragments such as Ø/Ù/Ã/Â.</p>
         */
        public ElementAssertions hasNoBrokenArabicCharacters() {
            LOG.rtl("Assert no broken Arabic characters: {}", locator);
            AllureReport.step("Assert element has no broken Arabic characters: " + locator);
            String text = collectReadableContent(waitForPresent());

            if (RtlEngine.hasBrokenArabicEncoding(text)) {
                fail(String.format("Possible broken Arabic encoding detected on %s.%n  Actual content: '%s'", locator, text));
            }
            LOG.rtl("✓ No broken Arabic characters found: {}", locator);
            return this;
        }

        /** Alias for hasNoBrokenArabicCharacters; easier for non-Arabic users to understand. */
        public ElementAssertions hasNoEncodingIssues() {
            return hasNoBrokenArabicCharacters();
        }

        /**
         * Assert Arabic content exists, has no common encoding issues, and is rendered in RTL direction.
         */
        public ElementAssertions hasValidArabicRendering() {
            LOG.rtl("Assert valid Arabic rendering: {}", locator);
            AllureReport.step("Assert element has valid Arabic rendering: " + locator);
            WebElement element = waitForPresent();
            String content = collectReadableContent(element);

            if (!RtlEngine.containsArabicText(content)) {
                fail(String.format("Arabic content not found on %s.%n  Actual content: '%s'", locator, content));
            }

            if (RtlEngine.hasBrokenArabicEncoding(content)) {
                fail(String.format("Possible broken Arabic encoding detected on %s.%n  Actual content: '%s'", locator, content));
            }

            String direction = RtlEngine.effectiveDirection(driver, element);
            if (!"rtl".equalsIgnoreCase(direction)) {
                fail(String.format("Arabic content is not rendered as RTL on %s.%n  Expected direction: rtl%n  Actual direction: '%s'%n  Content: '%s'",
                        locator, direction, content));
            }

            LOG.rtl("✓ Valid Arabic rendering: {}", locator);
            return this;
        }

        /** Assert placeholder text contains Arabic letters. */
        public ElementAssertions hasArabicPlaceholder() {
            LOG.rtl("Assert Arabic placeholder: {}", locator);
            AllureReport.step("Assert element has Arabic placeholder: " + locator);
            String placeholder = safe(waitForPresent().getAttribute("placeholder"));
            if (!RtlEngine.containsArabicText(placeholder)) {
                fail(String.format("Arabic placeholder not found on %s.%n  Actual placeholder: '%s'", locator, placeholder));
            }
            LOG.rtl("✓ Arabic placeholder exists: {}", locator);
            return this;
        }

        /** Assert an input or field with placeholder text is rendered in RTL direction. */
        public ElementAssertions hasPlaceholderDirectionRTL() {
            LOG.rtl("Assert placeholder direction is RTL: {}", locator);
            AllureReport.step("Assert element placeholder direction is RTL: " + locator);
            WebElement element = waitForPresent();
            String direction = RtlEngine.effectiveDirection(driver, element);
            if (!"rtl".equalsIgnoreCase(direction)) {
                fail(String.format("Placeholder direction is not RTL on %s.%n  Expected: rtl%n  Actual: '%s'%n  Placeholder: '%s'",
                        locator, direction, safe(element.getAttribute("placeholder"))));
            }
            LOG.rtl("✓ Placeholder direction is RTL: {}", locator);
            return this;
        }

        /** Assert Arabic content is present and the element direction is RTL. */
        public ElementAssertions usesRtlDirectionWhenArabic() {
            LOG.rtl("Assert Arabic content uses RTL direction: {}", locator);
            AllureReport.step("Assert Arabic content uses RTL direction: " + locator);
            WebElement element = waitForPresent();
            String content = collectReadableContent(element);
            if (!RtlEngine.containsArabicText(content)) {
                fail(String.format("Arabic content not found on %s.%n  Actual content: '%s'", locator, content));
            }
            String direction = RtlEngine.effectiveDirection(driver, element);
            if (!"rtl".equalsIgnoreCase(direction)) {
                fail(String.format("Arabic content exists but direction is not RTL on %s.%n  Expected: rtl%n  Actual: '%s'%n  Content: '%s'",
                        locator, direction, content));
            }
            LOG.rtl("✓ Arabic content uses RTL direction: {}", locator);
            return this;
        }

        // ─── Chainback ────────────────────────────────────────────────────────

        public WebAssertions and() {
            return parent;
        }

        public ElementAssertions element(By newLocator) {
            return parent.element(newLocator);
        }

        // ─── Internal Helpers ────────────────────────────────────────────────

        private WebDriverWait createWait() {
            return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        }

        private WebElement waitForPresent() {
            return createWait().until(ExpectedConditions.presenceOfElementLocated(locator));
        }

        private WebElement waitForVisible() {
            return createWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
        }

        private int count() {
            return driver.findElements(locator).size();
        }

        private void assertDirection(String expectedDirection) {
            WebElement element = waitForPresent();
            String dir = element.getAttribute("dir");
            String cssDir = element.getCssValue("direction");
            String actual = !safe(dir).isBlank() ? dir : cssDir;

            boolean matches = expectedDirection.equalsIgnoreCase(safe(dir))
                    || expectedDirection.equalsIgnoreCase(safe(cssDir));

            if (!matches) {
                fail(String.format(Locale.ROOT,
                        "Element direction mismatch on %s.%n  Expected: '%s'%n  Actual dir attribute: '%s'%n  Actual CSS direction: '%s'%n  Effective actual: '%s'",
                        locator, expectedDirection, dir, cssDir, actual));
            }
        }

        private String collectReadableContent(WebElement element) {
            return String.join(" ",
                    safe(element.getText()),
                    safe(element.getAttribute("placeholder")),
                    safe(element.getAttribute("value")),
                    safe(element.getAttribute("aria-label"))
            ).replaceAll("\\s+", " ").trim();
        }

        private void fail(String message) {
            LOG.assertionFailed(message);
            AllureReport.attachText("Assertion failure", message);
            DiagnosticsManager.attachBrowserState(driver, "Browser state at assertion failure");
            Assert.fail(message);
        }

        private static String safe(String value) {
            return value == null ? "" : value;
        }
    }
}
