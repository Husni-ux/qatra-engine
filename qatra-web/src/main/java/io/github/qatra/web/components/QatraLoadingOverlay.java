package io.github.qatra.web.components;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import io.github.qatra.web.waits.adaptive.QatraAdaptiveWait;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.Objects;

/** Helper for waiting until common loading overlays/spinners disappear. */
public final class QatraLoadingOverlay {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private Duration timeout = Duration.ofSeconds(10);
    private Duration polling = Duration.ofMillis(100);

    private QatraLoadingOverlay(WebDriver driver) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
    }

    public static QatraLoadingOverlay of(WebDriver driver) {
        return new QatraLoadingOverlay(driver);
    }

    public static QatraLoadingOverlay gone(WebDriver driver) {
        return of(driver).waitUntilGone();
    }

    public QatraLoadingOverlay withTimeout(Duration timeout) {
        this.timeout = timeout == null ? this.timeout : timeout;
        return this;
    }

    public QatraLoadingOverlay pollingEvery(Duration polling) {
        this.polling = polling == null ? this.polling : polling;
        return this;
    }

    public QatraLoadingOverlay waitUntilGone() {
        LOG.action("Wait until loading overlays are gone");
        AllureReport.step("Wait until loading overlays are gone");
        QatraAdaptiveWait.forPage(driver)
                .withTimeout(timeout)
                .pollingEvery(polling)
                .require()
                .noLoadingOverlay()
                .untilReady();
        return this;
    }
}
