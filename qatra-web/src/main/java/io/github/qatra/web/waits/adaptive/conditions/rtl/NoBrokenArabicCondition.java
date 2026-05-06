package io.github.qatra.web.waits.adaptive.conditions.rtl;

import io.github.qatra.web.rtl.RtlEngine;
import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitEvidenceCollector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;

public class NoBrokenArabicCondition implements QatraCondition {
    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        String text = ArabicTextVisibleCondition.collectText(element);
        boolean broken = RtlEngine.hasBrokenArabicEncoding(text);
        return !broken
                ? QatraConditionResult.passed("No broken Arabic characters detected")
                : QatraConditionResult.failed("Broken Arabic characters were detected", Map.of("text", WaitEvidenceCollector.abbreviate(text, 180)));
    }

    @Override
    public String description() {
        return "No broken Arabic characters";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.ENCODING;
    }
}
