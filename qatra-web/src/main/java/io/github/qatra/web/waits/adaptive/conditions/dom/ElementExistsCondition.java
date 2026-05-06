package io.github.qatra.web.waits.adaptive.conditions.dom;

import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitEvidenceCollector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementExistsCondition implements QatraCondition {
    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        return element != null
                ? QatraConditionResult.passed("Element exists", WaitEvidenceCollector.collect(driver, element))
                : QatraConditionResult.failed("Element does not exist");
    }

    @Override
    public String description() {
        return "Element exists";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.DOM;
    }
}
