package io.github.qatra.web.waits;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Text-focused wait shortcuts. */
public final class TextWaits {

    private TextWaits() {}

    public static QatraWait untilTextContains(WebDriver driver, By locator, String expectedText) {
        return QatraWait.forElement(driver, locator).waitUntilTextContains(expectedText);
    }

    public static QatraWait untilArabicTextIsVisible(WebDriver driver, By locator, String expectedArabicText) {
        return QatraWait.forElement(driver, locator).waitUntilArabicTextIsVisible(expectedArabicText);
    }

    public static QatraWait untilArabicTextRenderedCorrectly(WebDriver driver, By locator) {
        return QatraWait.forElement(driver, locator).waitUntilArabicTextRenderedCorrectly();
    }
}
