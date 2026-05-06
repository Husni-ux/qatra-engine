package io.github.qatra.web;

import io.github.qatra.core.context.DriverContext;
import io.github.qatra.core.enums.BrowserType;
import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.actions.AlertActions;
import io.github.qatra.web.actions.BrowserActions;
import io.github.qatra.web.actions.CookieActions;
import io.github.qatra.web.actions.ElementActions;
import io.github.qatra.web.actions.FrameActions;
import io.github.qatra.web.actions.ShadowDomActions;
import io.github.qatra.web.actions.StorageActions;
import io.github.qatra.web.actions.TableActions;
import io.github.qatra.web.actions.WindowActions;
import io.github.qatra.web.assertions.WebAssertions;
import io.github.qatra.web.assertions.engine.ElementAssert;
import io.github.qatra.web.assertions.engine.QatraAssert;
import io.github.qatra.web.factory.WebDriverFactory;
import io.github.qatra.web.fluent.FluentWeb;
import io.github.qatra.web.page.QatraComponent;
import io.github.qatra.web.page.QatraPage;
import io.github.qatra.web.page.QatraPageFactory;
import io.github.qatra.web.reports.ScreenshotManager;
import io.github.qatra.web.components.QatraDropdown;
import io.github.qatra.web.components.QatraLoadingOverlay;
import io.github.qatra.web.components.QatraModal;
import io.github.qatra.web.components.QatraTable;
import io.github.qatra.web.components.QatraToast;
import io.github.qatra.web.reports.DiagnosticsManager;
import io.github.qatra.web.waits.SmartWait;
import io.github.qatra.web.rtl.RtlActions;

import org.openqa.selenium.By;

import java.nio.file.Path;
import java.util.Optional;

/**
 * ╔══════════════════════════════════════════════╗
 * ║  QATRA Web Driver — Main Entry Point         ║
 * ╚══════════════════════════════════════════════╝
 *
 * This is the class your tests will use.
 *
 * <pre>
 * // TestNG Example:
 * public class LoginTest {
 *
 *     QATRA.GUI.WebDriver driver;
 *
 *     {@literal @}BeforeMethod
 *     public void setUp() {
 *         driver = new QATRA.GUI.WebDriver();
 *     }
 *
 *     {@literal @}Test
 *     public void testLogin() {
 *         driver.browser().navigateTo("https://myapp.com/login")
 *               .element().type(By.id("username"), "admin")
 *               .element().type(By.id("password"), "secret")
 *               .element().click(By.id("loginBtn"))
 *               .assertThat().element(By.id("welcome")).isVisible()
 *               .and().browser().url().contains("/dashboard");
 *     }
 *
 *     {@literal @}AfterMethod
 *     public void tearDown() {
 *         driver.quit();
 *     }
 * }
 * </pre>
 */
public class WebDriver {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final org.openqa.selenium.WebDriver seleniumDriver;
    private final FluentWeb fluent;

    // ─── Constructors ─────────────────────────────────────────────────────────

    /** Create a driver using the browser configured in qatra.properties. */
    public WebDriver() {
        this.seleniumDriver = WebDriverFactory.create();
        this.fluent = new FluentWeb(seleniumDriver);
        DriverContext.setDriver(seleniumDriver);
        LOG.info("QATRA WebDriver initialized ✓");
    }

    /** Create a driver for a specific browser type. */
    public WebDriver(BrowserType browser) {
        this.seleniumDriver = WebDriverFactory.create(browser);
        this.fluent = new FluentWeb(seleniumDriver);
        DriverContext.setDriver(seleniumDriver);
        LOG.info("QATRA WebDriver initialized ({}) ✓", browser);
    }

    // ─── Fluent API Entry Points ──────────────────────────────────────────────

    /**
     * Start browser-level actions (navigate, refresh, resize, etc.)
     */
    public BrowserActions browser() {
        return fluent.browser();
    }

    /**
     * Start element-level actions (click, type, hover, etc.)
     */
    public ElementActions element() {
        return fluent.element();
    }

    /**
     * Start assertions (isVisible, hasText, isRTL, etc.)
     */
    public WebAssertions assertThat() {
        return fluent.assertThat();
    }



    /**
     * Start the cleaner QATRA Web Assertion Engine for one element.
     *
     * <pre>
     * driver.expect(By.id("title"))
     *       .exists()
     *       .rtl().hasArabicText().hasRtlDirection();
     * </pre>
     */
    public ElementAssert expect(By locator) {
        return QatraAssert.that(seleniumDriver, locator);
    }

    /**
     * Start JavaScript alert/confirm/prompt actions.
     */
    public AlertActions alert() {
        return fluent.alert();
    }

    /**
     * Start frame/iframe switching actions.
     */
    public FrameActions frame() {
        return fluent.frame();
    }

    /**
     * Start browser window/tab actions.
     */
    public WindowActions window() {
        return fluent.window();
    }

    /**
     * Start Shadow DOM actions for open shadow roots.
     */
    public ShadowDomActions shadow() {
        return fluent.shadow();
    }

    /**
     * Start cookie management actions.
     */
    public CookieActions cookies() {
        return fluent.cookies();
    }

    /**
     * Start localStorage/sessionStorage actions.
     */
    public StorageActions storage() {
        return fluent.storage();
    }

    /**
     * Start standard HTML table helpers.
     */
    public TableActions table(By tableLocator) {
        return fluent.table(tableLocator);
    }

    /**
     * Start a web component dropdown helper for native or custom dropdown widgets.
     */
    public QatraDropdown dropdown(By rootLocator) {
        return QatraDropdown.of(seleniumDriver, rootLocator);
    }

    /**
     * Start a dynamic web table helper focused on readable table assertions.
     */
    public QatraTable webTable(By rootLocator) {
        return QatraTable.of(seleniumDriver, rootLocator);
    }

    /**
     * Start a modal dialog helper.
     */
    public QatraModal modal(By rootLocator) {
        return QatraModal.of(seleniumDriver, rootLocator);
    }

    /**
     * Start a toast/notification helper.
     */
    public QatraToast toast(By rootLocator) {
        return QatraToast.of(seleniumDriver, rootLocator);
    }

    /**
     * Start a loading overlay helper.
     */
    public QatraLoadingOverlay loadingOverlay() {
        return QatraLoadingOverlay.of(seleniumDriver);
    }


    /**
     * Start Arabic/RTL validation and scanning features.
     */
    public RtlActions rtl() {
        return new RtlActions(seleniumDriver);
    }



    /**
     * Create a Page Object bound to this QATRA driver.
     *
     * <pre>
     * LoginPage page = driver.page(LoginPage.class);
     * page.open().loginAs("admin", "secret");
     * </pre>
     */
    public <T extends QatraPage> T page(Class<T> pageClass) {
        return QatraPageFactory.create(pageClass, this);
    }

    /**
     * Create a reusable Component Object bound to a root locator.
     *
     * <pre>
     * Header header = driver.component(Header.class, By.id("header"));
     * header.assertLoggedInUser("Husni");
     * </pre>
     */
    public <T extends QatraComponent> T component(Class<T> componentClass, By rootLocator) {
        return QatraPageFactory.createComponent(componentClass, this, rootLocator);
    }

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    /**
     * Quit the browser and clean up.
     * Always call this in @AfterMethod.
     */
    public void quit() {
        if (seleniumDriver != null) {
            LOG.action("Quitting browser...");
            seleniumDriver.quit();
            DriverContext.removeDriver();
            LOG.info("Browser closed ✓");
        }
    }


    /**
     * Capture a screenshot manually.
     *
     * @param name logical screenshot name
     * @return screenshot path when capture succeeds
     */
    public Optional<Path> screenshot(String name) {
        return ScreenshotManager.capture(seleniumDriver, name);
    }


    /**
     * Wait until the current page document.readyState is complete.
     */
    public WebDriver waitUntilPageReady() {
        SmartWait.untilPageReady(seleniumDriver, 30);
        return this;
    }

    /**
     * Capture manual diagnostic evidence: browser state, page source, and browser console logs.
     * Useful when you want to attach debugging information even when the test passes.
     *
     * @param name logical evidence name
     */
    public void diagnostics(String name) {
        DiagnosticsManager.attachBrowserState(seleniumDriver, "Browser state - " + name);
        DiagnosticsManager.capturePageSource(seleniumDriver, name);
        DiagnosticsManager.captureBrowserConsoleLogs(seleniumDriver, name);
    }

    /**
     * Get the current page title.
     */
    public String getTitle() {
        return seleniumDriver.getTitle();
    }

    /**
     * Get the current page URL.
     */
    public String getCurrentUrl() {
        return seleniumDriver.getCurrentUrl();
    }

    /**
     * Get the raw Selenium WebDriver (escape hatch for advanced use cases).
     */
    public org.openqa.selenium.WebDriver getSeleniumDriver() {
        return seleniumDriver;
    }
}
