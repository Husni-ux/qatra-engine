package io.github.qatra.web.waits.adaptive.conditions.dom;

import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitEvidenceCollector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementEnabledCondition implements QatraCondition {
    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        if (element == null) {
            return QatraConditionResult.failed("Element is not present");
        }
        return element.isEnabled()
                ? QatraConditionResult.passed("Element is enabled", WaitEvidenceCollector.collect(driver, element))
                : QatraConditionResult.failed("Element is visible but disabled", WaitEvidenceCollector.collect(driver, element));
    }

    @Override
    public String description() {
        return "Element is enabled";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.DOM;
    }
}
