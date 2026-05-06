package io.github.qatra.web.waits.adaptive.conditions.dom;

import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitEvidenceCollector;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.time.Instant;

/**
 * Detects visual stability by requiring the element rectangle to stop changing for a quiet window.
 */
public class ElementStableCondition implements QatraCondition {

    private final Duration quietWindow;
    private Rectangle lastRect;
    private Instant stableSince;

    public ElementStableCondition(Duration quietWindow) {
        this.quietWindow = quietWindow == null ? Duration.ofMillis(500) : quietWindow;
    }

    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        if (element == null) {
            reset();
            return QatraConditionResult.failed("Element is not present");
        }
        Rectangle current = element.getRect();
        if (!sameRect(lastRect, current)) {
            lastRect = current;
            stableSince = Instant.now();
            return QatraConditionResult.failed("Element rectangle is still changing", WaitEvidenceCollector.collect(driver, element));
        }
        Duration stableFor = Duration.between(stableSince, Instant.now());
        if (stableFor.compareTo(quietWindow) >= 0) {
            return QatraConditionResult.passed("Element rectangle is stable for " + stableFor.toMillis() + " ms", WaitEvidenceCollector.collect(driver, element));
        }
        return QatraConditionResult.failed("Element is stable but quiet window not reached yet", WaitEvidenceCollector.collect(driver, element));
    }

    @Override
    public String description() {
        return "Element rectangle is visually stable";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.VISUAL;
    }

    private void reset() {
        lastRect = null;
        stableSince = null;
    }

    private static boolean sameRect(Rectangle a, Rectangle b) {
        if (a == null || b == null) {
            return false;
        }
        return a.getX() == b.getX()
                && a.getY() == b.getY()
                && a.getWidth() == b.getWidth()
                && a.getHeight() == b.getHeight();
    }
}
