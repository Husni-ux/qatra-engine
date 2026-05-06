package io.github.qatra.web.components;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import io.github.qatra.web.waits.adaptive.QatraAdaptiveWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/** Web table helper for standard and dynamic Arabic/RTL tables. */
public final class QatraTable extends QatraComponent<QatraTable> {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private By rowsLocator;

    private QatraTable(WebDriver driver, By rootLocator) {
        super(driver, rootLocator);
        this.rowsLocator = By.cssSelector("tbody tr, [role='row']");
    }

    public static QatraTable of(WebDriver driver, By rootLocator) {
        return new QatraTable(driver, rootLocator);
    }

    public QatraTable withRows(By rowsLocator) {
        this.rowsLocator = rowsLocator;
        return this;
    }

    public QatraTable waitUntilRowsLoaded(int minimumRows) {
        LOG.action("Wait until table rows loaded: {} minimum={}", rootLocator, minimumRows);
        AllureReport.step("Wait until table rows loaded: " + rootLocator + " minimum=" + minimumRows);
        QatraAdaptiveWait.forElement(driver, rootLocator)
                .withTimeout(timeout)
                .pollingEvery(polling)
                .withQuietWindow(quietWindow)
                .untilTableRowsLoaded(rowsLocator, minimumRows);
        return this;
    }

    public int rowCount() {
        return visibleRows().size();
    }

    public List<String> rowTexts() {
        return visibleRows().stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public QatraTable assertRowContainsArabicText(String expectedArabicText) {
        return assertRowContainsText(expectedArabicText);
    }

    public QatraTable assertRowContainsText(String expectedText) {
        boolean found = rowTexts().stream().anyMatch(text -> text.contains(expectedText));
        if (!found) {
            throw new AssertionError("No visible table row contained expected text: '" + expectedText + "'. Rows: " + rowTexts());
        }
        return this;
    }

    private List<WebElement> visibleRows() {
        return driver.findElements(rowsLocator).stream()
                .filter(WebElement::isDisplayed)
                .collect(Collectors.toList());
    }
}
