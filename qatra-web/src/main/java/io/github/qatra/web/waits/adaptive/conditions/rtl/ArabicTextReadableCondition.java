package io.github.qatra.web.waits.adaptive.conditions.rtl;

import io.github.qatra.web.rtl.RtlEngine;
import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitEvidenceCollector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;

public class ArabicTextReadableCondition implements QatraCondition {
    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        String text = ArabicTextVisibleCondition.collectText(element);
        boolean readable = RtlEngine.containsArabicText(text) && !RtlEngine.hasBrokenArabicEncoding(text);
        return readable
                ? QatraConditionResult.passed("Arabic text is readable", Map.of("text", WaitEvidenceCollector.abbreviate(text, 180)))
                : QatraConditionResult.failed("Arabic text is missing or not readable", Map.of("text", WaitEvidenceCollector.abbreviate(text, 180)));
    }

    @Override
    public String description() {
        return "Arabic text is readable";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.RTL;
    }
}
