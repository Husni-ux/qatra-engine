package io.github.qatra.core.context;

import org.openqa.selenium.WebDriver;

/**
 * Thread-safe storage for the current WebDriver instance.
 * Uses ThreadLocal to support parallel test execution.
 *
 * Each test thread gets its own driver — no conflicts.
 */
public final class DriverContext {

    private static final ThreadLocal<WebDriver> driverHolder = new ThreadLocal<>();

    private DriverContext() {}

    /**
     * Store the driver for the current thread.
     */
    public static void setDriver(WebDriver driver) {
        driverHolder.set(driver);
    }

    /**
     * Get the driver for the current thread.
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverHolder.get();
        if (driver == null) {
            throw new IllegalStateException(
                "No WebDriver found for the current thread. " +
                "Did you forget to call new QATRA.GUI.WebDriver() in @BeforeMethod?"
            );
        }
        return driver;
    }

    /**
     * Check if a driver exists for the current thread.
     */
    public static boolean hasDriver() {
        return driverHolder.get() != null;
    }

    /**
     * Remove the driver reference for the current thread.
     * Always call this after the test (in @AfterMethod).
     */
    public static void removeDriver() {
        driverHolder.remove();
    }
}
