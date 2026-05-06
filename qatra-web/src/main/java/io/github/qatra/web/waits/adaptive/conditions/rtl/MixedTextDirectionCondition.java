package io.github.qatra.web.waits.adaptive.conditions.rtl;

import io.github.qatra.web.rtl.RtlEngine;
import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitEvidenceCollector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;

/**
 * This condition does not guarantee visual order, but flags the most obvious mixed text risk signals.
 */
public class MixedTextDirectionCondition implements QatraCondition {
    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        String text = ArabicTextVisibleCondition.collectText(element);
        boolean mixed = RtlEngine.containsMixedArabicAndLatin(text);
        String direction = RtlEngine.effectiveDirection(driver, element);
        if (!mixed || "rtl".equals(direction)) {
            return QatraConditionResult.passed("Mixed Arabic/Latin text has acceptable direction signal", Map.of("direction", direction));
        }
        return QatraConditionResult.failed("Mixed Arabic/Latin text found without RTL direction", Map.of(
                "direction", direction,
                "text", WaitEvidenceCollector.abbreviate(text, 180)
        ));
    }

    @Override
    public String description() {
        return "Mixed Arabic/Latin text has safe direction signal";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.RTL;
    }
}
