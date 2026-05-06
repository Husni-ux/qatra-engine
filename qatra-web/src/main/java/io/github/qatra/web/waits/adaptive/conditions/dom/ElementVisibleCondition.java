package io.github.qatra.web.waits.adaptive.conditions.dom;

import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitEvidenceCollector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementVisibleCondition implements QatraCondition {
    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        if (element == null) {
            return QatraConditionResult.failed("Element is not present");
        }
        return element.isDisplayed()
                ? QatraConditionResult.passed("Element is visible", WaitEvidenceCollector.collect(driver, element))
                : QatraConditionResult.failed("Element is present but not visible", WaitEvidenceCollector.collect(driver, element));
    }

    @Override
    public String description() {
        return "Element is visible";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.DOM;
    }
}
