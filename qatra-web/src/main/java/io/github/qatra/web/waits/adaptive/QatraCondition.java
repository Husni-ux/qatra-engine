package io.github.qatra.web.waits.adaptive;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * A condition evaluated repeatedly by QATRA adaptive waits.
 */
@FunctionalInterface
public interface QatraCondition {

    QatraConditionResult evaluate(WebDriver driver, WebElement element);

    default String description() {
        return getClass().getSimpleName();
    }

    default QatraWaitSignal signal() {
        return QatraWaitSignal.DIAGNOSTIC;
    }
}
