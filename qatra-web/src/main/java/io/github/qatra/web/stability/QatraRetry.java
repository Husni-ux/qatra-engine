package io.github.qatra.web.stability;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional method-level retry configuration for TestNG tests.
 *
 * <pre>
 * {@literal @}Test(retryAnalyzer = QatraRetryAnalyzer.class)
 * {@literal @}QatraRetry(count = 2)
 * public void sometimesFlakyTest() { ... }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface QatraRetry {

    /** Number of retry attempts after the original failure. */
    int count() default -1;

    /** Short business reason shown in logs and reports. */
    String reason() default "";
}
