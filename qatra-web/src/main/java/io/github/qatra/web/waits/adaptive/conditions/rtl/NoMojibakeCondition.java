package io.github.qatra.web.waits.adaptive.conditions.rtl;

import io.github.qatra.web.rtl.RtlEngine;
import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitEvidenceCollector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;

public class NoMojibakeCondition implements QatraCondition {
    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        String text = ArabicTextVisibleCondition.collectText(element);
        boolean mojibake = RtlEngine.hasBrokenArabicEncoding(text);
        return !mojibake
                ? QatraConditionResult.passed("No mojibake/encoding corruption detected")
                : QatraConditionResult.failed("Mojibake/encoding corruption detected", Map.of("text", WaitEvidenceCollector.abbreviate(text, 180)));
    }

    @Override
    public String description() {
        return "No mojibake encoding corruption";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.ENCODING;
    }
}
