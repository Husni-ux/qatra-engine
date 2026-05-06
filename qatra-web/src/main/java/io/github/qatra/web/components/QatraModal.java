package io.github.qatra.web.components;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;

/** Modal dialog component for dynamic web applications. */
public final class QatraModal extends QatraComponent<QatraModal> {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private QatraModal(WebDriver driver, By rootLocator) {
        super(driver, rootLocator);
    }

    public static QatraModal of(WebDriver driver, By rootLocator) {
        return new QatraModal(driver, rootLocator);
    }

    public QatraModal waitUntilOpen() {
        LOG.action("Wait until modal is open and stable: {}", rootLocator);
        AllureReport.step("Wait until modal is open and stable: " + rootLocator);
        waitForRoot()
                .require()
                .visible()
                .modalStable()
                .noLoadingOverlay()
                .untilReady();
        return this;
    }

    public QatraModal waitUntilClosed() {
        LOG.action("Wait until modal is closed: {}", rootLocator);
        AllureReport.step("Wait until modal is closed: " + rootLocator);
        new FluentWait<>(driver)
                .withTimeout(timeout)
                .pollingEvery(polling)
                .until(currentDriver -> currentDriver.findElements(rootLocator).stream().noneMatch(WebElement::isDisplayed));
        return this;
    }

    public QatraModal clickButtonByText(String text) {
        waitUntilOpen();
        By button = By.xpath(".//*[self::button or @role='button'][" + ArabicXPath.textContains(text) + "]");
        root().findElement(button).click();
        return this;
    }

    public QatraModal assertContainsArabicText(String expectedArabicText) {
        return assertContainsText(expectedArabicText);
    }

    public QatraModal assertContainsText(String expectedText) {
        String actual = root().getText();
        if (!actual.contains(expectedText)) {
            throw new AssertionError("Modal text mismatch. Expected to contain: '" + expectedText + "' but actual was: '" + actual + "'");
        }
        return this;
    }
}
