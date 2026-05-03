package io.github.qatra.web.page;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the element that proves a page is loaded.
 *
 * <pre>
 * {@literal @}QatraPageLoaded(id = "page-title")
 * public class LoginPage extends QatraPage { ... }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QatraPageLoaded {
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
