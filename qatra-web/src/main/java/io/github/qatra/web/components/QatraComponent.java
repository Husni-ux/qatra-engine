package io.github.qatra.web.components;

import io.github.qatra.web.waits.adaptive.QatraAdaptiveWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Objects;

/**
 * Base class for QATRA web component objects.
 *
 * <p>This component layer is intentionally focused on web UI automation. It is not a
 * Page Object replacement; it is a small reusable layer for dynamic widgets that are
 * common in modern Arabic/RTL applications.</p>
 */
public abstract class QatraComponent<T extends QatraComponent<T>> {

    protected final WebDriver driver;
    protected final By rootLocator;
    protected Duration timeout = Duration.ofSeconds(10);
    protected Duration polling = Duration.ofMillis(100);
    protected Duration quietWindow = Duration.ofMillis(300);

    protected QatraComponent(WebDriver driver, By rootLocator) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
        this.rootLocator = Objects.requireNonNull(rootLocator, "rootLocator must not be null");
    }

    public T withTimeout(Duration timeout) {
        this.timeout = timeout == null ? this.timeout : timeout;
        return self();
    }

    public T pollingEvery(Duration polling) {
        this.polling = polling == null ? this.polling : polling;
        return self();
    }

    public T withQuietWindow(Duration quietWindow) {
        this.quietWindow = quietWindow == null ? this.quietWindow : quietWindow;
        return self();
    }

    public T waitUntilReady() {
        waitForRoot()
                .require()
                .visible()
                .enabled()
                .stable()
                .noLoadingOverlay()
                .untilReady();
        return self();
    }

    public WebElement root() {
        return waitForRoot()
                .require()
                .visible()
                .untilReady()
                .element();
    }

    protected QatraAdaptiveWait waitForRoot() {
        return QatraAdaptiveWait.forElement(driver, rootLocator)
                .withTimeout(timeout)
                .pollingEvery(polling)
                .withQuietWindow(quietWindow);
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }
}
