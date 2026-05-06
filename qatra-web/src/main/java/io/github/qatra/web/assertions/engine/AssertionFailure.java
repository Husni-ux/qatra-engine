package io.github.qatra.web.assertions.engine;

/**
 * AssertionError used by the QATRA Web Assertion Engine.
 */
public class AssertionFailure extends AssertionError {

    public AssertionFailure(String message) {
        super(message);
    }

    public AssertionFailure(String assertionName, String locator, String expected, String actual, String evidence) {
        super(format(assertionName, locator, expected, actual, evidence));
    }

    private static String format(String assertionName, String locator, String expected, String actual, String evidence) {
        return "QATRA Assertion Failed: " + safe(assertionName) + System.lineSeparator()
                + "Locator: " + safe(locator) + System.lineSeparator()
                + "Expected: " + safe(expected) + System.lineSeparator()
                + "Actual: " + safe(actual) + System.lineSeparator()
                + safe(evidence);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
