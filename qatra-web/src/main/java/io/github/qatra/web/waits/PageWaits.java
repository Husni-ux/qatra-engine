package io.github.qatra.web.waits;

import org.openqa.selenium.WebDriver;

/** Page-level wait shortcuts. */
public final class PageWaits {

    private PageWaits() {}

    public static QatraWait untilPageIsFullyReady(WebDriver driver) {
        return QatraWait.forPage(driver).waitUntilPageIsFullyReady();
    }
}
