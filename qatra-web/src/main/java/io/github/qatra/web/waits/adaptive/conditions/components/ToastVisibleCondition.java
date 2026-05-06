package io.github.qatra.web.waits.adaptive.conditions.components;

import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitEvidenceCollector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;

public class ToastVisibleCondition implements QatraCondition {

    private final String expectedText;

    public ToastVisibleCondition(String expectedText) {
        this.expectedText = expectedText == null ? "" : expectedText;
    }

    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        if (element == null || !element.isDisplayed()) {
            return QatraConditionResult.failed("Toast is not visible", WaitEvidenceCollector.collect(driver, element));
        }
        String text = element.getText() == null ? "" : element.getText();
        if (expectedText.isBlank() || text.contains(expectedText)) {
            return QatraConditionResult.passed("Toast is visible", Map.of("text", WaitEvidenceCollector.abbreviate(text, 180)));
        }
        return QatraConditionResult.failed("Toast visible but expected text not found", Map.of("expected", expectedText, "actual", WaitEvidenceCollector.abbreviate(text, 180)));
    }

    @Override
    public String description() {
        return expectedText.isBlank() ? "Toast is visible" : "Toast contains text: " + expectedText;
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.COMPONENT;
    }
}
