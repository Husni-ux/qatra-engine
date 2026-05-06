package io.github.qatra.web.waits.adaptive.conditions.dom;

import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitEvidenceCollector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementClickableCondition implements QatraCondition {
    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        if (element == null) {
            return QatraConditionResult.failed("Element is not present");
        }
        boolean clickable = element.isDisplayed() && element.isEnabled();
        return clickable
                ? QatraConditionResult.passed("Element is clickable", WaitEvidenceCollector.collect(driver, element))
                : QatraConditionResult.failed("Element is not ready for click", WaitEvidenceCollector.collect(driver, element));
    }

    @Override
    public String description() {
        return "Element is clickable";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.DOM;
    }
}
