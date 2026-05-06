package io.github.qatra.web.waits.adaptive.conditions.components;

import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import io.github.qatra.web.waits.adaptive.conditions.dom.ElementStableCondition;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitEvidenceCollector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public class ModalStableCondition implements QatraCondition {

    private final ElementStableCondition stableCondition;

    public ModalStableCondition(Duration quietWindow) {
        this.stableCondition = new ElementStableCondition(quietWindow);
    }

    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        if (element == null || !element.isDisplayed()) {
            return QatraConditionResult.failed("Modal is not visible", WaitEvidenceCollector.collect(driver, element));
        }
        return stableCondition.evaluate(driver, element);
    }

    @Override
    public String description() {
        return "Modal is visible and visually stable";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.COMPONENT;
    }
}
