package io.github.qatra.web.waits.adaptive.conditions.components;

import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitEvidenceCollector;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;

public class DropdownReadyCondition implements QatraCondition {

    private final By optionsLocator;

    public DropdownReadyCondition(By optionsLocator) {
        this.optionsLocator = optionsLocator;
    }

    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        if (element == null || !element.isDisplayed()) {
            return QatraConditionResult.failed("Dropdown root is not visible", WaitEvidenceCollector.collect(driver, element));
        }
        String busy = safe(element.getAttribute("aria-busy"));
        if ("true".equalsIgnoreCase(busy)) {
            return QatraConditionResult.failed("Dropdown is still aria-busy=true", Map.of("aria-busy", busy));
        }
        if (optionsLocator != null) {
            long options = driver.findElements(optionsLocator).stream().filter(WebElement::isDisplayed).count();
            if (options == 0) {
                return QatraConditionResult.failed("Dropdown options are not visible", Map.of("options", "0"));
            }
            return QatraConditionResult.passed("Dropdown is visible and options are loaded", Map.of("options", String.valueOf(options)));
        }
        return QatraConditionResult.passed("Dropdown root is visible and not busy");
    }

    @Override
    public String description() {
        return "Dropdown is ready";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.COMPONENT;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
