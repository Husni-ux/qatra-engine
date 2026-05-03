package io.github.qatra.api.assertions;

import io.github.qatra.api.ApiResponse;
import io.github.qatra.api.reports.ApiAllureReport;
import io.github.qatra.core.logger.QatraLogger;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Fluent JSONPath assertions for API responses.
 */
public final class JsonPathAssertions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final ApiResponse response;
    private final ApiAssertions parent;
    private final String path;

    public JsonPathAssertions(ApiResponse response, ApiAssertions parent, String path) {
        this.response = response;
        this.parent = parent;
        this.path = path;
    }

    public ApiAssertions exists() {
        String step = "Assert JSONPath exists: " + path;
        LOG.assertion(step);
        ApiAllureReport.step(step);

        Object actual = readValue();
        if (actual == null) {
            fail("Expected JSONPath <" + path + "> to exist but value was null/missing.");
        }

        LOG.assertion("✓ JSONPath exists: {}", path);
        return parent;
    }

    public ApiAssertions doesNotExist() {
        String step = "Assert JSONPath does not exist: " + path;
        LOG.assertion(step);
        ApiAllureReport.step(step);

        Object actual = readValue();
        if (actual != null) {
            fail("Expected JSONPath <" + path + "> not to exist but value was <" + actual + ">.");
        }

        LOG.assertion("✓ JSONPath does not exist: {}", path);
        return parent;
    }

    public ApiAssertions isNotNull() {
        return exists();
    }

    public ApiAssertions equalsTo(Object expectedValue) {
        String step = "Assert JSONPath '" + path + "' = '" + expectedValue + "'";
        LOG.assertion(step);
        ApiAllureReport.step(step);

        Object actual = readValue();
        if (!Objects.equals(String.valueOf(actual), String.valueOf(expectedValue))) {
            fail("Expected JSONPath <" + path + "> to be <" + expectedValue + "> but was <" + actual + ">.");
        }

        LOG.assertion("✓ JSONPath '{}' = '{}'", path, expectedValue);
        return parent;
    }

    public ApiAssertions contains(String expectedText) {
        String step = "Assert JSONPath '" + path + "' contains '" + expectedText + "'";
        LOG.assertion(step);
        ApiAllureReport.step(step);

        Object actual = readValue();
        if (actual == null || !String.valueOf(actual).contains(expectedText)) {
            fail("Expected JSONPath <" + path + "> to contain <" + expectedText + "> but was <" + actual + ">.");
        }

        LOG.assertion("✓ JSONPath '{}' contains '{}'", path, expectedText);
        return parent;
    }

    public ApiAssertions isArray() {
        String step = "Assert JSONPath is array/list: " + path;
        LOG.assertion(step);
        ApiAllureReport.step(step);

        Object actual = readValue();
        if (!(actual instanceof Collection<?>)) {
            fail("Expected JSONPath <" + path + "> to be an array/list but was <" + typeOf(actual) + ">.");
        }

        LOG.assertion("✓ JSONPath is array/list: {}", path);
        return parent;
    }

    public ApiAssertions isObject() {
        String step = "Assert JSONPath is object/map: " + path;
        LOG.assertion(step);
        ApiAllureReport.step(step);

        Object actual = readValue();
        if (!(actual instanceof Map<?, ?>)) {
            fail("Expected JSONPath <" + path + "> to be an object/map but was <" + typeOf(actual) + ">.");
        }

        LOG.assertion("✓ JSONPath is object/map: {}", path);
        return parent;
    }

    public ApiAssertions hasSize(int expectedSize) {
        String step = "Assert JSONPath '" + path + "' size = " + expectedSize;
        LOG.assertion(step);
        ApiAllureReport.step(step);

        Object actual = readValue();
        int actualSize;
        if (actual instanceof Collection<?> collection) {
            actualSize = collection.size();
        } else if (actual instanceof Map<?, ?> map) {
            actualSize = map.size();
        } else {
            fail("Expected JSONPath <" + path + "> to have size but it was <" + typeOf(actual) + ">.");
            return parent;
        }

        if (actualSize != expectedSize) {
            fail("Expected JSONPath <" + path + "> size to be <" + expectedSize + "> but was <" + actualSize + ">.");
        }

        LOG.assertion("✓ JSONPath '{}' size = {}", path, expectedSize);
        return parent;
    }

    public String asString() {
        Object actual = readValue();
        return actual == null ? null : String.valueOf(actual);
    }

    public Object value() {
        return readValue();
    }

    private Object readValue() {
        try {
            return response.jsonPath(path);
        } catch (Exception e) {
            return null;
        }
    }

    private String typeOf(Object value) {
        return value == null ? "null" : value.getClass().getName();
    }

    private void fail(String message) {
        LOG.assertionFailed(message);
        ApiAllureReport.attachText("API JSONPath assertion failure", message);
        ApiAllureReport.attachText("API request", response.request().toReadableText());
        ApiAllureReport.attachText("API response", response.toReadableText());
        throw new AssertionError(message);
    }
}
