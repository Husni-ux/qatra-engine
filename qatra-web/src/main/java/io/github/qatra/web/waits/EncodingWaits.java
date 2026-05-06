package io.github.qatra.web.waits;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Encoding-focused waits for Arabic content quality. */
public final class EncodingWaits {

    private EncodingWaits() {}

    public static QatraWait untilEncodingIsValid(WebDriver driver, By locator) {
        return QatraWait.forElement(driver, locator).waitUntilEncodingIsValid();
    }

    public static QatraWait untilTextIsNotBroken(WebDriver driver, By locator) {
        return QatraWait.forElement(driver, locator).waitUntilTextIsNotBroken();
    }
}
