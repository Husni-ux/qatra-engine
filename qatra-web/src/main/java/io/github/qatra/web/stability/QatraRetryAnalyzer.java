package io.github.qatra.web.stability;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TestNG retry analyzer for controlled flaky-test retry handling.
 *
 * <p>Important: retry is a stability safety net, not a replacement for fixing flaky tests.
 * QATRA reports every retry attempt so instability remains visible.</p>
 */
public class QatraRetryAnalyzer implements IRetryAnalyzer {

    private static final QatraLogger LOG = QatraLogger.getInstance();
    private static final Map<String, AtomicInteger> ATTEMPTS = new ConcurrentHashMap<>();

    @Override
    public boolean retry(ITestResult result) {
        if (!isRetryEnabled(result)) {
            return false;
        }

        int maxRetries = maxRetries(result);
        if (maxRetries <= 0) {
            return false;
        }

        String key = retryKey(result);
        int attempt = ATTEMPTS.computeIfAbsent(key, ignored -> new AtomicInteger(0)).incrementAndGet();

        if (attempt <= maxRetries) {
            String testName = QatraThreadInfo.testName(result);
            String message = "Retrying QATRA test '" + testName + "' attempt " + attempt + " of " + maxRetries
                    + " on " + QatraThreadInfo.currentThreadLabel();
            LOG.warn(message);
            AllureReport.attachText("QATRA Retry Attempt", message + failureSummary(result));
            return true;
        }

        ATTEMPTS.remove(key);
        return false;
    }

    private boolean isRetryEnabled(ITestResult result) {
        QatraRetry annotation = retryAnnotation(result);
        if (annotation != null && annotation.count() >= 0) {
            return annotation.count() > 0;
        }
        return QatraConfig.getInstance().getBooleanProperty(QatraProperties.RETRY_ENABLED, false);
    }

    private int maxRetries(ITestResult result) {
        QatraRetry annotation = retryAnnotation(result);
        if (annotation != null && annotation.count() >= 0) {
            return annotation.count();
        }
        return QatraConfig.getInstance().getIntProperty(QatraProperties.RETRY_COUNT, 1);
    }

    private QatraRetry retryAnnotation(ITestResult result) {
        Method method = result != null && result.getMethod() != null
                ? result.getMethod().getConstructorOrMethod().getMethod()
                : null;
        return method != null ? method.getAnnotation(QatraRetry.class) : null;
    }

    private String retryKey(ITestResult result) {
        String className = result != null && result.getTestClass() != null
                ? result.getTestClass().getName()
                : "unknown-class";
        String methodName = QatraThreadInfo.testName(result);
        String paramsHash = result != null && result.getParameters() != null
                ? String.valueOf(java.util.Arrays.deepHashCode(result.getParameters()))
                : "0";
        return className + "#" + methodName + "#" + paramsHash;
    }

    private String failureSummary(ITestResult result) {
        if (result == null || result.getThrowable() == null) {
            return "";
        }
        Throwable throwable = result.getThrowable();
        return "\nFailure: " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
    }
}
