package io.github.qatra.web.waits.adaptive.conditions.rtl;

import io.github.qatra.web.rtl.RtlEngine;
import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitEvidenceCollector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;

public class RtlDirectionCondition implements QatraCondition {
    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        String direction = RtlEngine.effectiveDirection(driver, element);
        return "rtl".equals(direction)
                ? QatraConditionResult.passed("RTL direction is applied", Map.of("direction", direction))
                : QatraConditionResult.failed("RTL direction is not applied", Map.of("direction", direction));
    }

    @Override
    public String description() {
        return "RTL direction is applied";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.RTL;
    }
}
