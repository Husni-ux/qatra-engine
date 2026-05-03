package io.github.qatra.web.stability;

import io.github.qatra.core.context.DriverContext;
import io.github.qatra.web.WebDriver;
import org.testng.Assert;

/**
 * Helpers that make parallel test execution easier to verify and debug.
 */
public final class QatraParallel {

    private QatraParallel() {
    }

    public static String currentThreadLabel() {
        return QatraThreadInfo.currentThreadLabel();
    }

    public static String uniqueEvidenceName(String baseName) {
        return QatraThreadInfo.uniqueName(baseName);
    }

    public static void assertDriverIsBoundToCurrentThread(WebDriver qatraDriver) {
        Assert.assertNotNull(qatraDriver, "QATRA WebDriver wrapper must not be null.");
        Assert.assertTrue(DriverContext.hasDriver(), "Raw Selenium driver should be bound to current thread.");
        Assert.assertSame(
                DriverContext.getDriver(),
                qatraDriver.getSeleniumDriver(),
                "DriverContext must point to the raw Selenium driver for the current thread."
        );
    }
}
