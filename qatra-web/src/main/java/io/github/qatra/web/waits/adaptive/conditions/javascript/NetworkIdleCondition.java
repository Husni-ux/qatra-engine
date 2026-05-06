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
 * Conservative network-idle signal. If no instrumentation exists, it falls back to jQuery/Angular/busy DOM checks.
 */
public class NetworkIdleCondition implements QatraCondition {

    private final Duration quietWindow;
    private Instant idleSince;

    public NetworkIdleCondition(Duration quietWindow) {
        this.quietWindow = quietWindow == null ? Duration.ofMillis(500) : quietWindow;
    }

    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        if (!(driver instanceof JavascriptExecutor js)) {
            return QatraConditionResult.passed("JavaScript unsupported; network idle skipped");
        }
        Object countValue = js.executeScript("""
                const jqueryActive = window.jQuery ? window.jQuery.active : 0;
                const qatraActive = window.__qatraActiveRequests || 0;
                const busyDom = document.querySelectorAll('[aria-busy="true"], .loading, .spinner, .loader, .skeleton').length;
                return jqueryActive + qatraActive + busyDom;
                """);
        long active = parseLong(countValue);
        if (active > 0) {
            idleSince = null;
            return QatraConditionResult.failed("Network/DOM busy signals are still active", Map.of("activeSignals", String.valueOf(active)));
        }
        if (idleSince == null) {
            idleSince = Instant.now();
            return QatraConditionResult.failed("Network is idle but quiet window has just started", Map.of("activeSignals", "0"));
        }
        Duration idleFor = Duration.between(idleSince, Instant.now());
        return idleFor.compareTo(quietWindow) >= 0
                ? QatraConditionResult.passed("Network/DOM busy signals idle for quiet window", Map.of("idleForMs", String.valueOf(idleFor.toMillis())))
                : QatraConditionResult.failed("Network idle quiet window not reached yet", Map.of("idleForMs", String.valueOf(idleFor.toMillis())));
    }

    @Override
    public String description() {
        return "Network/DOM busy signals are idle";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.NETWORK;
    }

    private static long parseLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
