package io.github.qatra.web.fluent;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * The central coordinator of QATRA's fluent web API.
 *
 * All action classes hold a reference to FluentWeb so they can
 * chain back to any other action group.
 *
 * Think of it as the "hub" in a hub-and-spoke model.
 */
public class FluentWeb {

    private final WebDriver driver;
    private final long timeout;

    private final BrowserActions browserActions;
    private final ElementActions elementActions;
    private final AlertActions alertActions;
    private final FrameActions frameActions;
    private final WindowActions windowActions;
    private final ShadowDomActions shadowDomActions;
    private final CookieActions cookieActions;
    private final StorageActions storageActions;
    private final WebAssertions assertions;

    public FluentWeb(WebDriver driver) {
        this.driver = driver;
        this.timeout = QatraConfig.getInstance()
                .getIntProperty(QatraProperties.ELEMENT_TIMEOUT, 10);

        this.browserActions = new BrowserActions(driver, this);
        this.elementActions = new ElementActions(driver, this);
        this.alertActions = new AlertActions(driver, this);
        this.frameActions = new FrameActions(driver, this);
        this.windowActions = new WindowActions(driver, this);
        this.shadowDomActions = new ShadowDomActions(driver, this);
        this.cookieActions = new CookieActions(driver, this);
        this.storageActions = new StorageActions(driver, this);
        this.assertions = new WebAssertions(driver, this, timeout);
    }

    public BrowserActions browser() {
        return browserActions;
    }

    public ElementActions element() {
        return elementActions;
    }

    public AlertActions alert() {
        return alertActions;
    }

    public FrameActions frame() {
        return frameActions;
    }

    public WindowActions window() {
        return windowActions;
    }

    public ShadowDomActions shadow() {
        return shadowDomActions;
    }

    public CookieActions cookies() {
        return cookieActions;
    }

    public StorageActions storage() {
        return storageActions;
    }

    public TableActions table(By tableLocator) {
        return new TableActions(driver, this, tableLocator);
    }

    public WebAssertions assertThat() {
        return assertions;
    }

    public WebDriver getDriver() {
        return driver;
    }
}
