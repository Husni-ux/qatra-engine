package io.github.qatra.web.waits.adaptive.conditions.javascript;

import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;

public class DocumentReadyCondition implements QatraCondition {
    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        if (!(driver instanceof JavascriptExecutor js)) {
            return QatraConditionResult.passed("JavaScript unsupported; document readiness skipped");
        }
        String state = String.valueOf(js.executeScript("return document.readyState"));
        return "complete".equals(state)
                ? QatraConditionResult.passed("document.readyState is complete", Map.of("document.readyState", state))
                : QatraConditionResult.failed("document.readyState is not complete", Map.of("document.readyState", state));
    }

    @Override
    public String description() {
        return "Document readyState is complete";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.JAVASCRIPT;
    }
}
