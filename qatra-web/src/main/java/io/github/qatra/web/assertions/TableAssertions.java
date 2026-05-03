package io.github.qatra.web.assertions;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import io.github.qatra.web.waits.SmartWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Assertions for standard HTML tables.
 */
public class TableAssertions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final WebAssertions parent;
    private final By tableLocator;
    private final long timeoutSeconds;

    public TableAssertions(WebDriver driver, WebAssertions parent, By tableLocator) {
        this.driver = driver;
        this.parent = parent;
        this.tableLocator = tableLocator;
        this.timeoutSeconds = QatraConfig.getInstance()
                .getIntProperty(QatraProperties.ELEMENT_TIMEOUT, 10);
    }

    public TableAssertions hasRowCount(int expectedCount) {
        LOG.assertion("Assert table row count {}: {}", expectedCount, tableLocator);
        AllureReport.step("Assert table row count: " + expectedCount);
        Assert.assertEquals(rowCount(), expectedCount, "Table row count mismatch: " + tableLocator);
        return this;
    }

    public TableAssertions hasColumnCount(int expectedCount) {
        LOG.assertion("Assert table column count {}: {}", expectedCount, tableLocator);
        AllureReport.step("Assert table column count: " + expectedCount);
        Assert.assertEquals(columnCount(), expectedCount, "Table column count mismatch: " + tableLocator);
        return this;
    }

    public TableAssertions hasHeaders(String... expectedHeaders) {
        LOG.assertion("Assert table headers: {}", tableLocator);
        AllureReport.step("Assert table headers");
        List<String> actual = headers();
        for (String expected : expectedHeaders) {
            Assert.assertTrue(actual.stream().anyMatch(header -> header.equalsIgnoreCase(expected)),
                    "Expected table header to exist: " + expected + " but headers were: " + actual);
        }
        return this;
    }

    public TableAssertions cellEquals(int rowIndexZeroBased, int columnIndexZeroBased, String expectedText) {
        LOG.assertion("Assert table cell equals '{}': row {}, column {}", expectedText, rowIndexZeroBased, columnIndexZeroBased);
        AllureReport.step("Assert table cell equals: " + expectedText);
        Assert.assertEquals(cellText(rowIndexZeroBased, columnIndexZeroBased), expectedText, "Table cell text mismatch");
        return this;
    }

    public TableAssertions cellContains(int rowIndexZeroBased, int columnIndexZeroBased, String expectedText) {
        LOG.assertion("Assert table cell contains '{}': row {}, column {}", expectedText, rowIndexZeroBased, columnIndexZeroBased);
        AllureReport.step("Assert table cell contains: " + expectedText);
        String actual = cellText(rowIndexZeroBased, columnIndexZeroBased);
        Assert.assertTrue(actual.contains(expectedText), "Expected table cell to contain: " + expectedText + " but was: " + actual);
        return this;
    }

    public TableAssertions columnContains(String headerText, String expectedText) {
        LOG.assertion("Assert table column '{}' contains '{}'", headerText, expectedText);
        AllureReport.step("Assert table column contains: " + expectedText);
        List<String> values = columnValues(headerText);
        Assert.assertTrue(values.stream().anyMatch(value -> value.contains(expectedText)),
                "Expected column '" + headerText + "' to contain: " + expectedText + " but values were: " + values);
        return this;
    }

    public TableAssertions rowContains(int rowIndexZeroBased, String expectedText) {
        LOG.assertion("Assert table row {} contains '{}'", rowIndexZeroBased, expectedText);
        AllureReport.step("Assert table row contains: " + expectedText);
        WebElement row = rows().get(rowIndexZeroBased);
        Assert.assertTrue(row.getText().contains(expectedText), "Expected row to contain: " + expectedText + " but was: " + row.getText());
        return this;
    }

    public TableAssertions containsText(String expectedText) {
        LOG.assertion("Assert table contains text '{}': {}", expectedText, tableLocator);
        AllureReport.step("Assert table contains text: " + expectedText);
        String actual = table().getText();
        Assert.assertTrue(actual.contains(expectedText), "Expected table to contain: " + expectedText + " but was: " + actual);
        return this;
    }

    public TableAssertions columnIsSortedAscending(String headerText) {
        LOG.assertion("Assert table column '{}' is sorted ascending", headerText);
        AllureReport.step("Assert table column is sorted ascending: " + headerText);
        List<String> actual = columnValues(headerText);
        List<String> sorted = new ArrayList<>(actual);
        sorted.sort(Comparator.comparing(value -> value.toLowerCase(Locale.ROOT)));
        Assert.assertEquals(actual, sorted, "Expected column to be sorted ascending: " + headerText);
        return this;
    }

    public WebAssertions and() {
        return parent;
    }

    private WebElement table() {
        return SmartWait.untilPresent(driver, tableLocator, timeoutSeconds);
    }

    private int rowCount() {
        return rows().size();
    }

    private int columnCount() {
        List<WebElement> headerCells = table().findElements(By.cssSelector("thead tr:first-child th, thead tr:first-child td"));
        if (!headerCells.isEmpty()) {
            return headerCells.size();
        }
        List<WebElement> firstRowCells = table().findElements(By.cssSelector("tbody tr:first-child th, tbody tr:first-child td, tr:first-child th, tr:first-child td"));
        return firstRowCells.size();
    }

    private String cellText(int rowIndexZeroBased, int columnIndexZeroBased) {
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

    private List<String> columnValues(String headerText) {
        int index = headerIndex(headerText);
        List<String> values = new ArrayList<>();
        for (WebElement row : rows()) {
            List<WebElement> cells = cells(row);
            if (index >= 0 && index < cells.size()) {
                values.add(cells.get(index).getText());
            }
        }
        return values;
    }

    private List<String> headers() {
        return table().findElements(By.cssSelector("thead th, thead td, tr:first-child th"))
                .stream()
                .map(element -> element.getText().trim())
                .collect(Collectors.toList());
    }

    private int headerIndex(String headerText) {
        List<String> headers = headers();
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).equalsIgnoreCase(headerText.trim())) {
                return i;
            }
        }
        throw new AssertionError("Table header was not found: " + headerText + ". Available headers: " + headers);
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
}
