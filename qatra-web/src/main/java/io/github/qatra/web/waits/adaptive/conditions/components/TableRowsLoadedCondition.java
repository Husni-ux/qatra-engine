package io.github.qatra.web.waits.adaptive.conditions.components;

import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitEvidenceCollector;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;

public class TableRowsLoadedCondition implements QatraCondition {

    private final By rowLocator;
    private final int minimumRows;

    public TableRowsLoadedCondition(By rowLocator, int minimumRows) {
        this.rowLocator = rowLocator == null ? By.cssSelector("tbody tr") : rowLocator;
        this.minimumRows = Math.max(1, minimumRows);
    }

    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        if (element == null || !element.isDisplayed()) {
            return QatraConditionResult.failed("Table root is not visible", WaitEvidenceCollector.collect(driver, element));
        }
        long rows = element.findElements(rowLocator).stream().filter(WebElement::isDisplayed).count();
        return rows >= minimumRows
                ? QatraConditionResult.passed("Table rows are loaded", Map.of("rows", String.valueOf(rows)))
                : QatraConditionResult.failed("Table rows are not loaded yet", Map.of("rows", String.valueOf(rows), "minimum", String.valueOf(minimumRows)));
    }

    @Override
    public String description() {
        return "Table rows are loaded";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.COMPONENT;
    }
}
