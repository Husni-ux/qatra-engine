package io.github.qatra.web.actions;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.assertions.WebAssertions;
import io.github.qatra.web.fluent.FluentWeb;
import io.github.qatra.web.reports.AllureReport;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Fluent browser-level actions.
 *
 * <pre>
 *   driver.browser()
 *         .navigateTo("https://example.com")
 *         .and()
 *         .element()
 *         .click(...)
 *         .assertThat()
 *         .element(...)
 *         .isVisible();
 * </pre>
 */
public class BrowserActions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final FluentWeb parent;

    public BrowserActions(WebDriver driver, FluentWeb parent) {
        this.driver = driver;
        this.parent = parent;
    }

    /** Navigate to a full URL. */
    public BrowserActions navigateTo(String url) {
        LOG.action("Navigate to: {}", url);
        AllureReport.step("Navigate to: " + url);
        driver.get(url);
        return this;
    }

    /** Navigate to the configured qatra.base.url for the active environment. */
    public BrowserActions navigateToBaseUrl() {
        String baseUrl = QatraConfig.getInstance()
                .getProperty(QatraProperties.BASE_URL, "");

        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("qatra.base.url is not configured for the active QATRA environment.");
        }

        LOG.action("Navigate to configured base URL: {}", baseUrl);
        AllureReport.step("Navigate to configured base URL: " + baseUrl);
        driver.get(baseUrl);
        return this;
    }

    /** Navigate to a path relative to the configured base URL. */
    public BrowserActions navigateToRelativeUrl(String path) {
        String baseUrl = QatraConfig.getInstance()
                .getProperty(QatraProperties.BASE_URL, "");

        String fullUrl;

        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            fullUrl = baseUrl + path.substring(1);
        } else if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            fullUrl = baseUrl + "/" + path;
        } else {
            fullUrl = baseUrl + path;
        }

        LOG.action("Navigate to relative URL: {}", fullUrl);
        AllureReport.step("Navigate to relative URL: " + fullUrl);
        driver.get(fullUrl);
        return this;
    }

    /** Refresh the current page. */
    public BrowserActions refresh() {
        LOG.action("Refresh page");
        AllureReport.step("Refresh page");
        driver.navigate().refresh();
        return this;
    }

    /** Navigate back in browser history. */
    public BrowserActions navigateBack() {
        LOG.action("Navigate back");
        AllureReport.step("Navigate back");
        driver.navigate().back();
        return this;
    }

    /** Navigate forward in browser history. */
    public BrowserActions navigateForward() {
        LOG.action("Navigate forward");
        AllureReport.step("Navigate forward");
        driver.navigate().forward();
        return this;
    }

    /** Maximize the browser window. */
    public BrowserActions maximize() {
        LOG.action("Maximize browser window");
        AllureReport.step("Maximize browser window");
        driver.manage().window().maximize();
        return this;
    }

    /** Scroll to the top of the page. */
    public BrowserActions scrollToTop() {
        LOG.action("Scroll to top");
        AllureReport.step("Scroll to top");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
        return this;
    }

    /** Scroll to the bottom of the page. */
    public BrowserActions scrollToBottom() {
        LOG.action("Scroll to bottom");
        AllureReport.step("Scroll to bottom");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        return this;
    }

    /** Switch to a browser tab by index. Index starts from 0. */
    public BrowserActions switchToTab(int index) {
        LOG.action("Switch to tab index: {}", index);
        AllureReport.step("Switch to tab index: " + index);

        var handles = driver.getWindowHandles().stream().toList();

        if (index < 0 || index >= handles.size()) {
            throw new IllegalArgumentException(
                    "Invalid tab index: " + index + ". Available tabs: " + handles.size()
            );
        }

        driver.switchTo().window(handles.get(index));
        return this;
    }

    /** Close current tab and switch back to the first available tab. */
    public BrowserActions closeCurrentTab() {
        LOG.action("Close current tab");
        AllureReport.step("Close current tab");

        driver.close();

        var handles = driver.getWindowHandles().stream().toList();

        if (!handles.isEmpty()) {
            driver.switchTo().window(handles.get(0));
        }

        return this;
    }


    /**
     * Wait until the current page is fully ready.
     * Returns BrowserActions to preserve fluent chaining:
     * driver.browser().navigateTo(url).waitUntilPageReady().assertThat()...
     */
    public BrowserActions waitUntilPageReady() {
        int timeoutSeconds = QatraConfig.getInstance()
                .getIntProperty(QatraProperties.PAGE_LOAD_TIMEOUT, 30);
        io.github.qatra.web.waits.QatraWait
                .forPage(driver)
                .withTimeout(java.time.Duration.ofSeconds(timeoutSeconds))
                .waitUntilPageIsFullyReady();
        return this;
    }

    /** Wait until common Ajax/custom loading signals are completed. */
    public BrowserActions waitUntilAjaxCompleted() {
        int timeoutSeconds = QatraConfig.getInstance()
                .getIntProperty(QatraProperties.ELEMENT_TIMEOUT, 10);
        io.github.qatra.web.waits.QatraWait
                .forPage(driver)
                .withTimeout(java.time.Duration.ofSeconds(timeoutSeconds))
                .waitUntilAjaxIsCompleted();
        return this;
    }

    /** Return to the parent FluentWeb object. */
    public FluentWeb and() {
        return parent;
    }

    /** Switch from browser actions to element actions. */
    public ElementActions element() {
        return parent.element();
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

    public TableActions table(org.openqa.selenium.By tableLocator) {
        return parent.table(tableLocator);
    }

    /**
     * Start assertions from the general WebAssertions entry point.
     *
     * This intentionally returns WebAssertions, not BrowserAssertions, so both
     * .assertThat().element(...)
     * and
     * .assertThat().browser().title()
     * are valid chains.
     */
    public WebAssertions assertThat() {
        return parent.assertThat();
    }
}
