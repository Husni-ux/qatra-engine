package io.github.qatra.web.components;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import io.github.qatra.web.waits.adaptive.QatraAdaptiveWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;

/** Toast/message component with Arabic text support. */
public final class QatraToast extends QatraComponent<QatraToast> {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private QatraToast(WebDriver driver, By rootLocator) {
        super(driver, rootLocator);
    }

    public static QatraToast waitFor(WebDriver driver) {
        return new QatraToast(driver, By.cssSelector(".toast, .alert, .notification, [role='alert'], [role='status']"));
    }

    public static QatraToast of(WebDriver driver, By rootLocator) {
        return new QatraToast(driver, rootLocator);
    }

    public QatraToast visibleMessageContains(String expectedText) {
        LOG.action("Wait for toast containing '{}'", expectedText);
        AllureReport.step("Wait for toast containing '" + expectedText + "'");
        QatraAdaptiveWait.forElement(driver, rootLocator)
                .withTimeout(timeout)
                .pollingEvery(polling)
                .require()
                .visible()
                .toastVisible(expectedText)
                .untilReady();
        return this;
    }

    public QatraToast successMessageContains(String expectedText) {
        return visibleMessageContains(expectedText);
    }

    public QatraToast assertArabicMessageContains(String expectedArabicText) {
        return visibleMessageContains(expectedArabicText);
    }

    public QatraToast waitUntilGone() {
        new FluentWait<>(driver)
                .withTimeout(timeout)
                .pollingEvery(polling)
                .until(currentDriver -> currentDriver.findElements(rootLocator).stream().noneMatch(WebElement::isDisplayed));
        return this;
    }
}
