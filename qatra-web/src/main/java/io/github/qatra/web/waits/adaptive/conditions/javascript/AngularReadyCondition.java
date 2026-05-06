package io.github.qatra.web.waits.adaptive.conditions.javascript;

import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AngularReadyCondition implements QatraCondition {
    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        if (!(driver instanceof JavascriptExecutor js)) {
            return QatraConditionResult.passed("JavaScript unsupported; Angular readiness skipped");
        }
        Object ready = js.executeScript("""
                if (!window.getAllAngularTestabilities) return true;
                return window.getAllAngularTestabilities().every(t => t.isStable());
                """);
        return Boolean.TRUE.equals(ready)
                ? QatraConditionResult.passed("Angular testabilities are stable")
                : QatraConditionResult.failed("Angular testabilities are not stable");
    }

    @Override
    public String description() {
        return "Angular testabilities are stable";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.JAVASCRIPT;
    }
}
