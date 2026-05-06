package io.github.qatra.web.waits.adaptive.conditions.javascript;

import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;

public class JQueryIdleCondition implements QatraCondition {
    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        if (!(driver instanceof JavascriptExecutor js)) {
            return QatraConditionResult.passed("JavaScript unsupported; jQuery readiness skipped");
        }
        Object active = js.executeScript("return window.jQuery ? window.jQuery.active : 0");
        long count = parseLong(active);
        return count == 0
                ? QatraConditionResult.passed("jQuery active requests are zero", Map.of("jQuery.active", String.valueOf(count)))
                : QatraConditionResult.failed("jQuery requests are still active", Map.of("jQuery.active", String.valueOf(count)));
    }

    @Override
    public String description() {
        return "jQuery active requests are zero";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.NETWORK;
    }

    private static long parseLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
