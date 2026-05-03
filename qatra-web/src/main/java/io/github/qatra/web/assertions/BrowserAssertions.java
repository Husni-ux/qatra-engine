package io.github.qatra.web.assertions;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * Fluent browser-level assertions (title, URL, etc.)
 */
public class BrowserAssertions {

    private static final QatraLogger LOG = QatraLogger.getInstance();
    private final WebDriver driver;
    private final WebAssertions parent;

    public BrowserAssertions(WebDriver driver, WebAssertions parent) {
        this.driver = driver;
        this.parent = parent;
    }

    // ─── Title ────────────────────────────────────────────────────────────────

    public TitleAssertions title() {
        return new TitleAssertions(driver.getTitle(), this);
    }

    // ─── URL ──────────────────────────────────────────────────────────────────

    public UrlAssertions url() {
        return new UrlAssertions(driver.getCurrentUrl(), this);
    }

    // ─── Chainback ────────────────────────────────────────────────────────────

    public WebAssertions and() { return parent; }

    // ══════════════════════════════════════════════════════════════════════════
    //  Inner: TitleAssertions
    // ══════════════════════════════════════════════════════════════════════════

    public static class TitleAssertions {
        private final String actualTitle;
        private final BrowserAssertions parent;

        public TitleAssertions(String actualTitle, BrowserAssertions parent) {
            this.actualTitle = actualTitle;
            this.parent = parent;
        }

        public BrowserAssertions contains(String expected) {
            LOG.assertion("Assert title contains '{}'", expected);
            AllureReport.step("Assert browser title contains [" + expected + "]");
            if (!actualTitle.contains(expected)) {
                String msg = String.format("Title mismatch.\n  Expected to contain: '%s'\n  Actual: '%s'", expected, actualTitle);
                LOG.assertionFailed(msg);
                Assert.fail(msg);
            }
            LOG.assertion("✓ Title contains '{}'", expected);
            return parent;
        }

        public BrowserAssertions equals(String expected) {
            LOG.assertion("Assert title = '{}'", expected);
            AllureReport.step("Assert browser title equals [" + expected + "]");
            Assert.assertEquals(actualTitle, expected, "Page title mismatch");
            LOG.assertion("✓ Title = '{}'", expected);
            return parent;
        }

        public BrowserAssertions startsWith(String prefix) {
            LOG.assertion("Assert title starts with '{}'", prefix);
            AllureReport.step("Assert browser title starts with [" + prefix + "]");
            Assert.assertTrue(actualTitle.startsWith(prefix),
                    String.format("Title does not start with '%s'. Actual: '%s'", prefix, actualTitle));
            return parent;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Inner: UrlAssertions
    // ══════════════════════════════════════════════════════════════════════════

    public static class UrlAssertions {
        private final String actualUrl;
        private final BrowserAssertions parent;

        public UrlAssertions(String actualUrl, BrowserAssertions parent) {
            this.actualUrl = actualUrl;
            this.parent = parent;
        }

        public BrowserAssertions contains(String expected) {
            LOG.assertion("Assert URL contains '{}'", expected);
            AllureReport.step("Assert browser URL contains [" + expected + "]");
            if (!actualUrl.contains(expected)) {
                String msg = String.format("URL mismatch.\n  Expected to contain: '%s'\n  Actual: '%s'", expected, actualUrl);
                LOG.assertionFailed(msg);
                Assert.fail(msg);
            }
            LOG.assertion("✓ URL contains '{}'", expected);
            return parent;
        }

        public BrowserAssertions equals(String expected) {
            LOG.assertion("Assert URL = '{}'", expected);
            AllureReport.step("Assert browser URL equals [" + expected + "]");
            Assert.assertEquals(actualUrl, expected, "URL mismatch");
            return parent;
        }

        public BrowserAssertions endsWith(String suffix) {
            LOG.assertion("Assert URL ends with '{}'", suffix);
            AllureReport.step("Assert browser URL ends with [" + suffix + "]");
            Assert.assertTrue(actualUrl.endsWith(suffix),
                    String.format("URL does not end with '%s'. Actual: '%s'", suffix, actualUrl));
            return parent;
        }
    }
}
