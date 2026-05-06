package io.github.qatra.web.waits.adaptive.conditions.dom;

import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitEvidenceCollector;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ElementNotCoveredCondition implements QatraCondition {
    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        if (element == null) {
            return QatraConditionResult.failed("Element is not present");
        }
        if (!(driver instanceof JavascriptExecutor js)) {
            return QatraConditionResult.passed("Driver does not support JavaScript hit-testing; condition skipped");
        }
        Rectangle rect = element.getRect();
        int x = rect.getX() + rect.getWidth() / 2;
        int y = rect.getY() + rect.getHeight() / 2;
        Object result = js.executeScript("""
                const el = arguments[0];
                const x = arguments[1];
                const y = arguments[2];
                const top = document.elementFromPoint(x, y);
                return top === el || el.contains(top);
                """, element, x, y);
        boolean notCovered = Boolean.TRUE.equals(result);
        return notCovered
                ? QatraConditionResult.passed("Element center is not covered", WaitEvidenceCollector.collect(driver, element))
                : QatraConditionResult.failed("Element appears to be covered by another element", WaitEvidenceCollector.collect(driver, element));
    }

    @Override
    public String description() {
        return "Element is not covered by overlay";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.VISUAL;
    }
}
