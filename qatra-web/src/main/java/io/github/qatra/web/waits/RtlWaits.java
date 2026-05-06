package io.github.qatra.web.waits;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** RTL-specific wait shortcuts for Arabic interfaces. */
public final class RtlWaits {

    private RtlWaits() {}

    public static QatraWait untilRtlDirectionApplied(WebDriver driver, By locator) {
        return QatraWait.forElement(driver, locator).waitUntilRtlDirectionApplied();
    }

    public static QatraWait untilArabicTextRenderedCorrectly(WebDriver driver, By locator) {
        return QatraWait.forElement(driver, locator).waitUntilArabicTextRenderedCorrectly();
    }
}
