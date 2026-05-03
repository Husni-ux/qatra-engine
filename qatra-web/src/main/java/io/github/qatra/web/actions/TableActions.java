package io.github.qatra.web.actions;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.assertions.WebAssertions;
import io.github.qatra.web.fluent.FluentWeb;
import io.github.qatra.web.reports.AllureReport;
import io.github.qatra.web.waits.SmartWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fluent helpers and assertions for standard HTML tables.
 */
public class TableActions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final FluentWeb parent;
    private final By tableLocator;
    private final long timeoutSeconds;

    public TableActions(WebDriver driver, FluentWeb parent, By tableLocator) {
        this.driver = driver;
        this.parent = parent;
        this.tableLocator = tableLocator;
        this.timeoutSeconds = QatraConfig.getInstance()
                .getIntProperty(QatraProperties.ELEMENT_TIMEOUT, 10);
    }

    public int rowCount() {
        LOG.action("Get table row count: {}", tableLocator);
        AllureReport.step("Get table row count: " + tableLocator);
        return rows().size();
    }

    public int columnCount() {
        LOG.action("Get table column count: {}", tableLocator);
        AllureReport.step("Get table column count: " + tableLocator);
        List<WebElement> headerCells = table().findElements(By.cssSelector("thead tr:first-child th, thead tr:first-child td"));
        if (!headerCells.isEmpty()) {
            return headerCells.size();
        }
        List<WebElement> firstRowCells = table().findElements(By.cssSelector("tbody tr:first-child th, tbody tr:first-child td, tr:first-child th, tr:first-child td"));
        return firstRowCells.size();
    }

    public String cellText(int rowIndexZeroBased, int columnIndexZeroBased) {
        LOG.action("Get table cell text: row {}, column {}", rowIndexZeroBased, columnIndexZeroBased);
        AllureReport.step("Get table cell text");
        List<WebElement> rowElements = rows();
        if (rowIndexZeroBased < 0 || rowIndexZeroBased >= rowElements.size()) {
            throw new IllegalArgumentException("Invalid row index: " + rowIndexZeroBased + ". Row count: " + rowElements.size());
        }
        List<WebElement> cells = cells(rowElements.get(rowIndexZeroBased));
        if (columnIndexZeroBased < 0 || columnIndexZeroBased >= cells.size()) {
            throw new IllegalArgumentException("Invalid column index: " + columnIndexZeroBased + ". Column count: " + cells.size());
        }
        return cells.get(columnIndexZeroBased).getText();
    }

    public List<String> columnValues(int columnIndexZeroBased) {
        LOG.action("Get table column values by index: {}", columnIndexZeroBased);
        AllureReport.step("Get table column values by index: " + columnIndexZeroBased);
        List<String> values = new ArrayList<>();
        for (WebElement row : rows()) {
            List<WebElement> cells = cells(row);
            if (columnIndexZeroBased >= 0 && columnIndexZeroBased < cells.size()) {
                values.add(cells.get(columnIndexZeroBased).getText());
            }
        }
        return values;
    }

    public List<String> columnValues(String headerText) {
        int index = headerIndex(headerText);
        return columnValues(index);
    }

    public TableActions assertRowCount(int expectedCount) {
        LOG.assertion("Assert table row count {}: {}", expectedCount, tableLocator);
        AllureReport.step("Assert table row count: " + expectedCount);
        Assert.assertEquals(rowCount(), expectedCount, "Table row count mismatch: " + tableLocator);
        return this;
    }

    public TableActions assertColumnCount(int expectedCount) {
        LOG.assertion("Assert table column count {}: {}", expectedCount, tableLocator);
        AllureReport.step("Assert table column count: " + expectedCount);
        Assert.assertEquals(columnCount(), expectedCount, "Table column count mismatch: " + tableLocator);
        return this;
    }

    public TableActions assertCellContains(int rowIndexZeroBased, int columnIndexZeroBased, String expectedText) {
        LOG.assertion("Assert table cell contains '{}': row {}, column {}", expectedText, rowIndexZeroBased, columnIndexZeroBased);
        AllureReport.step("Assert table cell contains: " + expectedText);
        String actual = cellText(rowIndexZeroBased, columnIndexZeroBased);
        Assert.assertTrue(actual.contains(expectedText), "Expected cell text to contain: " + expectedText + " but was: " + actual);
        return this;
    }

    public TableActions assertColumnContains(String headerText, String expectedText) {
        LOG.assertion("Assert table column '{}' contains '{}'", headerText, expectedText);
        AllureReport.step("Assert table column contains: " + expectedText);
        List<String> values = columnValues(headerText);
        Assert.assertTrue(values.stream().anyMatch(value -> value.contains(expectedText)),
                "Expected column '" + headerText + "' to contain: " + expectedText + " but values were: " + values);
        return this;
    }

    public TableActions assertContainsText(String expectedText) {
        LOG.assertion("Assert table contains text '{}': {}", expectedText, tableLocator);
        AllureReport.step("Assert table contains text: " + expectedText);
        String tableText = table().getText();
        Assert.assertTrue(tableText.contains(expectedText), "Expected table to contain: " + expectedText + " but was: " + tableText);
        return this;
    }

    private int headerIndex(String headerText) {
        List<WebElement> headers = table().findElements(By.cssSelector("thead th, thead td, tr:first-child th"));
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).getText().trim().equalsIgnoreCase(headerText.trim())) {
                return i;
            }
        }
        throw new AssertionError("Table header was not found: " + headerText + ". Available headers: " +
                headers.stream().map(WebElement::getText).collect(Collectors.toList()));
    }

    private WebElement table() {
        return SmartWait.untilPresent(driver, tableLocator, timeoutSeconds);
    }

    private List<WebElement> rows() {
        List<WebElement> bodyRows = table().findElements(By.cssSelector("tbody tr"));
        if (!bodyRows.isEmpty()) {
            return bodyRows;
        }
        return table().findElements(By.cssSelector("tr"));
    }

    private List<WebElement> cells(WebElement row) {
        return row.findElements(By.cssSelector("th, td"));
    }

    public FluentWeb and() {
        return parent;
    }

    public BrowserActions browser() {
        return parent.browser();
    }

    public ElementActions element() {
        return parent.element();
    }

    public WebAssertions assertThat() {
        return parent.assertThat();
    }
}
