package io.github.qatra.web.page;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Lightweight Page Object locator annotation for QATRA pages.
 *
 * <pre>
 * {@literal @}QatraFindBy(id = "username")
 * private QatraElement username;
 *
 * {@literal @}QatraFindBy(testId = "login-button")
 * private QatraElement loginButton;
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface QatraFindBy {
    String id() default "";
    String name() default "";
    String css() default "";
    String xpath() default "";
    String tagName() default "";
    String className() default "";
    String linkText() default "";
    String partialLinkText() default "";
    String testId() default "";
    String dataTestId() default "";
}
