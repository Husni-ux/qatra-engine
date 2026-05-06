package io.github.qatra.web.waits.adaptive.conditions.javascript;

import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * Lightweight React/Vue-friendly DOM stability signal based on document text length and element count.
 */
public class MutationStableCondition implements QatraCondition {

    private final Duration quietWindow;
    private String lastSignature;
    private Instant stableSince;

    public MutationStableCondition(Duration quietWindow) {
        this.quietWindow = quietWindow == null ? Duration.ofMillis(500) : quietWindow;
    }

    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        if (!(driver instanceof JavascriptExecutor js)) {
            return QatraConditionResult.passed("JavaScript unsupported; mutation stability skipped");
        }
        String signature = String.valueOf(js.executeScript("""
                return document.body ? (document.body.innerText.length + ':' + document.querySelectorAll('*').length) : '0:0';
                """));
        if (!signature.equals(lastSignature)) {
            lastSignature = signature;
            stableSince = Instant.now();
            return QatraConditionResult.failed("DOM signature changed", Map.of("signature", signature));
        }
        Duration stableFor = Duration.between(stableSince, Instant.now());
        return stableFor.compareTo(quietWindow) >= 0
                ? QatraConditionResult.passed("DOM signature stable for quiet window", Map.of("stableForMs", String.valueOf(stableFor.toMillis())))
                : QatraConditionResult.failed("DOM signature stable but quiet window not reached", Map.of("stableForMs", String.valueOf(stableFor.toMillis())));
    }

    @Override
    public String description() {
        return "DOM mutation signature is stable";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.JAVASCRIPT;
    }
}
