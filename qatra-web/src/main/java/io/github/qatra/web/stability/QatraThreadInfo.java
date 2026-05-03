package io.github.qatra.web.stability;

import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Small helper for thread-safe names and test diagnostics.
 */
public final class QatraThreadInfo {

    private static final AtomicLong SEQUENCE = new AtomicLong(0);
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS", Locale.ROOT);

    private QatraThreadInfo() {
    }

    public static long threadId() {
        return Thread.currentThread().threadId();
    }

    public static String threadName() {
        return Thread.currentThread().getName();
    }

    public static String currentThreadLabel() {
        return "thread-" + threadId() + "-" + safe(threadName());
    }

    public static String testName(ITestResult result) {
        if (result == null) {
            return "unknown-test";
        }
        if (result.getMethod() != null && result.getMethod().getMethodName() != null) {
            return result.getMethod().getMethodName();
        }
        return result.getName() != null ? result.getName() : "unknown-test";
    }

    public static String methodName(Method method) {
        return method != null ? method.getName() : "unknown-test";
    }

    public static String uniqueName(String baseName) {
        return safe(baseName)
                + "_"
                + LocalDateTime.now().format(TIMESTAMP)
                + "_t"
                + threadId()
                + "_"
                + SEQUENCE.incrementAndGet();
    }

    public static String safe(String value) {
        String text = Objects.toString(value, "unknown");
        return text.replaceAll("[^a-zA-Z0-9._-]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
    }
}
