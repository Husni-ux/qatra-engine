package io.github.qatra.web.stability;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Lightweight TestNG listener for QATRA stability and parallel execution visibility.
 */
public class QatraTestListener implements ITestListener, IExecutionListener {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    @Override
    public void onExecutionStart() {
        if (QatraConfig.getInstance().getBooleanProperty(QatraProperties.PARALLEL_ENABLED, false)) {
            LOG.info("QATRA parallel execution mode is enabled by configuration.");
        }
    }

    @Override
    public void onStart(ITestContext context) {
        String name = context != null ? context.getName() : "QATRA TestNG context";
        LOG.info("QATRA TestNG context started: {}", name);
    }

    @Override
    public void onTestStart(ITestResult result) {
        String message = "QATRA test started: " + QatraThreadInfo.testName(result)
                + " on " + QatraThreadInfo.currentThreadLabel();
        LOG.step(message);
        AllureReport.attachText("QATRA Thread", message);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("QATRA test passed: {} on {}", QatraThreadInfo.testName(result), QatraThreadInfo.currentThreadLabel());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String message = "QATRA test failed: " + QatraThreadInfo.testName(result)
                + " on " + QatraThreadInfo.currentThreadLabel();
        LOG.error(message);
        AllureReport.attachText("QATRA Failure Thread", message);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("QATRA test skipped: {} on {}", QatraThreadInfo.testName(result), QatraThreadInfo.currentThreadLabel());
    }
}
