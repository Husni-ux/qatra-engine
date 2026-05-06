package io.github.qatra.web.waits.adaptive.conditions.rtl;

import io.github.qatra.web.rtl.RtlEngine;
import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitEvidenceCollector;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;

public class ArabicTextVisibleCondition implements QatraCondition {

    private final String expectedText;

    public ArabicTextVisibleCondition(String expectedText) {
        this.expectedText = expectedText == null ? "" : expectedText;
    }

    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        if (element == null || !element.isDisplayed()) {
            return QatraConditionResult.failed("Arabic text element is not visible", WaitEvidenceCollector.collect(driver, element));
        }
        String text = collectText(element);
        boolean hasArabic = RtlEngine.containsArabicText(text);
        boolean expectedMatches = expectedText.isBlank() || text.contains(expectedText);
        if (hasArabic && expectedMatches) {
            return QatraConditionResult.passed("Arabic text is visible", Map.of("text", WaitEvidenceCollector.abbreviate(text, 180)));
        }
        return QatraConditionResult.failed("Arabic text is missing or expected text was not found", Map.of(
                "expected", expectedText,
                "actual", WaitEvidenceCollector.abbreviate(text, 180)
        ));
    }

    @Override
    public String description() {
        return expectedText.isBlank() ? "Arabic text is visible" : "Arabic text is visible: " + expectedText;
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.RTL;
    }

    static String collectText(WebElement element) {
        if (element == null) return "";
        return String.join(" ",
                safe(element.getText()),
                safe(element.getAttribute("textContent")),
                safe(element.getAttribute("placeholder")),
                safe(element.getAttribute("value")),
                safe(element.getAttribute("aria-label")),
                safe(element.getAttribute("title")),
                safe(element.getAttribute("alt"))
        ).trim();
    }

    static String safe(String value) {
        return value == null ? "" : value;
    }
}
