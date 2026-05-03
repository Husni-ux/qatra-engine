package io.github.qatra.web.page;

import io.github.qatra.web.WebDriver;
import io.github.qatra.web.actions.*;
import io.github.qatra.web.assertions.WebAssertions;
import io.github.qatra.web.rtl.RtlActions;
import org.openqa.selenium.By;
import org.testng.Assert;

import java.util.Objects;

/**
 * Base class for QATRA Page Object Model support.
 *
 * <p>Page classes should extend QatraPage and expose business-readable methods.
 * The page can use annotated fields, reusable components, and page-level load validation.</p>
 */
public abstract class QatraPage {

    protected final WebDriver driver;
    protected final org.openqa.selenium.WebDriver seleniumDriver;

    protected QatraPage(WebDriver driver) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
        this.seleniumDriver = driver.getSeleniumDriver();
        QatraPageFactory.initElements(this);
    }

    protected QatraElement element(By locator) {
        return new QatraElement(driver, locator);
    }

    protected QatraElementCollection elements(By locator) {
        return new QatraElementCollection(driver, locator);
    }

    protected <T extends QatraComponent> T component(Class<T> componentClass, By rootLocator) {
        return QatraPageFactory.createComponent(componentClass, driver, rootLocator);
    }

    protected <T extends QatraComponent> QatraComponentCollection<T> components(Class<T> componentClass, By rootLocator) {
        return new QatraComponentCollection<>(driver, rootLocator, componentClass);
    }

    @SuppressWarnings("unchecked")
    protected <T extends QatraPage> T navigateTo(String url) {
        driver.browser().navigateTo(url);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    protected <T extends QatraPage> T open(String url) {
        driver.browser().navigateTo(url);
        verifyPageLoaded();
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    protected <T extends QatraPage> T waitUntilPageReady() {
        driver.waitUntilPageReady();
        return (T) this;
    }

    /**
     * Verify the current page using @QatraPageUrl and @QatraPageLoaded annotations when present.
     */
    @SuppressWarnings("unchecked")
    public <T extends QatraPage> T verifyPageLoaded() {
        waitUntilPageReady();
        verifyPageUrlAnnotation();
        verifyPageLoadedAnnotation();
        onVerifyPageLoaded();
        return (T) this;
    }

    /** Hook for page classes that need custom load checks. */
    protected void onVerifyPageLoaded() {
        // Override in page classes when needed.
    }

    protected void verifyVisible(By locator) {
        driver.assertThat().element(locator).isVisible();
    }

    @SuppressWarnings("unchecked")
    protected <T extends QatraPage> T assertUrlContains(String expectedUrlPart) {
        Assert.assertTrue(driver.getCurrentUrl().contains(expectedUrlPart),
                "Expected URL to contain: " + expectedUrlPart + " | actual=" + driver.getCurrentUrl());
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    protected <T extends QatraPage> T assertUrlEquals(String expectedUrl) {
        Assert.assertEquals(driver.getCurrentUrl(), expectedUrl,
                "Unexpected page URL.");
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    protected <T extends QatraPage> T assertTitleContains(String expectedTitlePart) {
        Assert.assertTrue(driver.getTitle().contains(expectedTitlePart),
                "Expected title to contain: " + expectedTitlePart + " | actual=" + driver.getTitle());
        return (T) this;
    }

    /**
     * Create the next Page Object after a user action and immediately validate it.
     */
    protected <T extends QatraPage> T goTo(Class<T> nextPageClass) {
        T page = driver.page(nextPageClass);
        page.verifyPageLoaded();
        return page;
    }

    /**
     * Alias for goTo(), useful after clicking submit/navigation buttons.
     */
    protected <T extends QatraPage> T transitionTo(Class<T> nextPageClass) {
        return goTo(nextPageClass);
    }

    /**
     * Open a URL and return a validated target page object.
     */
    protected <T extends QatraPage> T openAs(String url, Class<T> pageClass) {
        driver.browser().navigateTo(url);
        T page = driver.page(pageClass);
        page.verifyPageLoaded();
        return page;
    }

    private void verifyPageUrlAnnotation() {
        QatraPageUrl url = getClass().getAnnotation(QatraPageUrl.class);
        if (url == null) {
            return;
        }

        if (!url.exact().isBlank()) {
            Assert.assertEquals(driver.getCurrentUrl(), url.exact(),
                    "Page URL did not match @QatraPageUrl exact value for " + getClass().getSimpleName());
        }

        if (!url.contains().isBlank()) {
            Assert.assertTrue(driver.getCurrentUrl().contains(url.contains()),
                    "Page URL did not contain @QatraPageUrl value for " + getClass().getSimpleName() +
                            ". Expected to contain: " + url.contains() + " | actual=" + driver.getCurrentUrl());
        }

        if (!url.titleContains().isBlank()) {
            Assert.assertTrue(driver.getTitle().contains(url.titleContains()),
                    "Page title did not contain @QatraPageUrl title value for " + getClass().getSimpleName() +
                            ". Expected to contain: " + url.titleContains() + " | actual=" + driver.getTitle());
        }
    }

    private void verifyPageLoadedAnnotation() {
        QatraPageLoaded loaded = getClass().getAnnotation(QatraPageLoaded.class);
        if (loaded == null) {
            return;
        }
        By locator = QatraPageFactory.buildBy(loaded);
        driver.assertThat().element(locator).isVisible();
    }

    public WebDriver qatraDriver() {
        return driver;
    }

    public org.openqa.selenium.WebDriver seleniumDriver() {
        return seleniumDriver;
    }

    public BrowserActions browser() {
        return driver.browser();
    }

    public ElementActions elementActions() {
        return driver.element();
    }

    public WebAssertions assertThat() {
        return driver.assertThat();
    }

    public RtlActions rtl() {
        return driver.rtl();
    }

    public AlertActions alert() {
        return driver.alert();
    }

    public FrameActions frame() {
        return driver.frame();
    }

    public WindowActions window() {
        return driver.window();
    }

    public ShadowDomActions shadow() {
        return driver.shadow();
    }

    public CookieActions cookies() {
        return driver.cookies();
    }

    public StorageActions storage() {
        return driver.storage();
    }

    public TableActions table(By tableLocator) {
        return driver.table(tableLocator);
    }
}
