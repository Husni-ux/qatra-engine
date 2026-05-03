package io.github.qatra.web.page;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares expected URL/title characteristics for a QATRA Page Object.
 *
 * <pre>
 * {@literal @}QatraPageUrl(contains = "/login", titleContains = "Login")
 * public class LoginPage extends QatraPage { ... }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QatraPageUrl {
    /** Exact URL expected for the page. Leave blank when not needed. */
    String exact() default "";

    /** URL fragment that should be contained in the current URL. */
    String contains() default "";

    /** Page title fragment that should be contained in the current title. */
    String titleContains() default "";
}
