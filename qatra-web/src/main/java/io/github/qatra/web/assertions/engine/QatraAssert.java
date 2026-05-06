package io.github.qatra.web.assertions.engine;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;

/**
 * Static entry point for the QATRA Web Assertion Engine.
 *
 * <pre>
 * QatraAssert.that(driver, By.id("title"))
 *        .exists()
 *        .isVisible()
 *        .rtl().hasArabicText().hasRtlDirection();
 * </pre>
 */
public final class QatraAssert {

    private QatraAssert() {
    }

    public static ElementAssert that(WebDriver driver, By locator) {
        return ElementAssert.of(driver, locator);
    }

    public static ElementAssert that(WebDriver driver, By locator, Duration timeout) {
        return ElementAssert.of(driver, locator).withTimeout(timeout);
    }

    public static ElementAssert that(WebDriver driver, WebElement element) {
        return ElementAssert.of(driver, element);
    }
}
