package io.github.qatra.web.waits;

import org.openqa.selenium.WebDriver;

/** Ajax/framework stability wait shortcuts. */
public final class AjaxWaits {

    private AjaxWaits() {}

    public static QatraWait untilAjaxIsCompleted(WebDriver driver) {
        return QatraWait.forPage(driver).waitUntilAjaxIsCompleted();
    }
}
