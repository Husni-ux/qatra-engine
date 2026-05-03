package io.github.qatra.web.testng;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.core.env.QatraEnvironment;
import io.github.qatra.web.WebDriver;
import io.github.qatra.web.reports.AllureReport;
import io.github.qatra.web.reports.DiagnosticsManager;
import io.github.qatra.web.stability.QatraTestListener;
import io.github.qatra.web.stability.QatraThreadInfo;
import io.qameta.allure.testng.AllureTestNg;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.lang.reflect.Method;

/**
 * Base TestNG class for QATRA Web tests.
 *
 * <p>It handles the standard web test lifecycle:</p>
 * <ul>
 *     <li>Open browser before each test</li>
 *     <li>Bind the driver to the current thread</li>
 *     <li>Capture diagnostics automatically on failure</li>
 *     <li>Quit browser safely after each test</li>
 * </ul>
 *
 * <p>For parallel execution, prefer {@link #driver()} inside tests and page setup code.
 * The protected {@code driver} field remains available for backwards compatibility.</p>
 */
@Listeners({AllureTestNg.class, QatraTestListener.class})
public abstract class QatraBaseTest {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final ThreadLocal<WebDriver> qatraDriverHolder = new ThreadLocal<>();

    /**
     * Backward-compatible field used by existing sequential tests.
     * For parallel execution, prefer {@link #driver()}.
     */
    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void qatraSetUp(Method method) {
        String testName = QatraThreadInfo.methodName(method);
        String threadLabel = QatraThreadInfo.currentThreadLabel();

        QatraEnvironment environment = QatraEnvironment.current();

        LOG.step("QATRA test setup started: {} on {} | env={}", testName, threadLabel, environment.name());
        AllureReport.step("QATRA setup: " + testName + " on " + threadLabel + " | env=" + environment.name());
        AllureReport.attachText("QATRA Environment", environment.summary());

        WebDriver createdDriver = createDriver();
        qatraDriverHolder.set(createdDriver);
        driver = createdDriver;
    }

    @AfterMethod(alwaysRun = true)
    public void qatraTearDown(ITestResult result) {
        WebDriver currentDriver = qatraDriverHolder.get();
        try {
            if (result != null && !result.isSuccess() && currentDriver != null) {
                String testName = QatraThreadInfo.testName(result);

                AllureReport.attachText("Failed test", testName + " on " + QatraThreadInfo.currentThreadLabel());
                DiagnosticsManager.captureFailureEvidence(
                        currentDriver.getSeleniumDriver(),
                        QatraThreadInfo.uniqueName(testName + "_FAILED"),
                        result.getThrowable()
                );
            }
        } finally {
            if (currentDriver != null) {
                currentDriver.quit();
            }
            qatraDriverHolder.remove();
            if (driver == currentDriver) {
                driver = null;
            }
        }
    }

    /**
     * Thread-safe access to the QATRA driver for the current TestNG test thread.
     */
    protected WebDriver driver() {
        WebDriver currentDriver = qatraDriverHolder.get();
        if (currentDriver == null) {
            throw new IllegalStateException("No QATRA WebDriver found for the current thread. Did setup run?");
        }
        return currentDriver;
    }

    /**
     * Alias for {@link #driver()} for teams that prefer shorter syntax.
     */
    protected WebDriver qatra() {
        return driver();
    }

    /**
     * Override this method in a test class if you need custom browser creation.
     */
    protected WebDriver createDriver() {
        return new WebDriver();
    }
}
