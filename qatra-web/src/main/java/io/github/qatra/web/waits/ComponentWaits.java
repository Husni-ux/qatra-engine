package io.github.qatra.web.waits;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Wait helpers for custom/dynamic UI components. */
public final class ComponentWaits {

    private ComponentWaits() {}

    public static QatraWait untilElementIsStable(WebDriver driver, By locator) {
        return QatraWait.forElement(driver, locator).waitUntilElementIsStable();
    }

    public static QatraWait untilCustomComponentReady(WebDriver driver, By locator) {
        return QatraWait.forElement(driver, locator).waitUntilCustomComponentReady();
    }
}
