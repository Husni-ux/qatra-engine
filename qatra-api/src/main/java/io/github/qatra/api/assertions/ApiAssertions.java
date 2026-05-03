package io.github.qatra.api.assertions;

import io.github.qatra.api.ApiResponse;
import io.github.qatra.api.reports.ApiAllureReport;
import io.github.qatra.core.logger.QatraLogger;

import java.util.Arrays;
import java.util.Objects;

/**
 * Fluent assertions for API responses.
 */
public final class ApiAssertions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final ApiResponse response;

    public ApiAssertions(ApiResponse response) {
        this.response = response;
    }

    public ApiAssertions statusCode(int expectedStatusCode) {
        String step = "Assert API status code = " + expectedStatusCode;
        LOG.assertion(step);
        ApiAllureReport.step(step);

        int actual = response.statusCode();
        if (actual != expectedStatusCode) {
            fail("Expected API status code <" + expectedStatusCode + "> but was <" + actual + ">.");
        }

        LOG.assertion("✓ API status code = {}", expectedStatusCode);
        return this;
    }

    public ApiAssertions statusCodeIn(int... expectedStatusCodes) {
        String step = "Assert API status code is one of " + Arrays.toString(expectedStatusCodes);
        LOG.assertion(step);
        ApiAllureReport.step(step);

        int actual = response.statusCode();
        for (int expected : expectedStatusCodes) {
            if (actual == expected) {
                LOG.assertion("✓ API status code {} is allowed", actual);
                return this;
            }
        }

        fail("Expected API status code to be one of " + Arrays.toString(expectedStatusCodes) + " but was <" + actual + ">.");
        return this;
    }

    public ApiAssertions statusCodeBetween(int minInclusive, int maxInclusive) {
        String step = "Assert API status code between " + minInclusive + " and " + maxInclusive;
        LOG.assertion(step);
        ApiAllureReport.step(step);

        int actual = response.statusCode();
        if (actual < minInclusive || actual > maxInclusive) {
            fail("Expected API status code between <" + minInclusive + "> and <" + maxInclusive + "> but was <" + actual + ">.");
        }

        LOG.assertion("✓ API status code {} is within range", actual);
        return this;
    }

    public ApiAssertions bodyContains(String expectedText) {
        String step = "Assert API response body contains '" + expectedText + "'";
        LOG.assertion(step);
        ApiAllureReport.step(step);

        String body = response.bodyAsString();
        if (body == null || !body.contains(expectedText)) {
            fail("Expected response body to contain <" + expectedText + "> but body was:" + System.lineSeparator() + body);
        }

        LOG.assertion("✓ API response body contains '{}'", expectedText);
        return this;
    }

    public ApiAssertions bodyNotContains(String unexpectedText) {
        String step = "Assert API response body does not contain '" + unexpectedText + "'";
        LOG.assertion(step);
        ApiAllureReport.step(step);

        String body = response.bodyAsString();
        if (body != null && body.contains(unexpectedText)) {
            fail("Expected response body not to contain <" + unexpectedText + "> but it was found.");
        }

        LOG.assertion("✓ API response body does not contain '{}'", unexpectedText);
        return this;
    }

    public ApiAssertions headerExists(String headerName) {
        String step = "Assert API response header exists: " + headerName;
        LOG.assertion(step);
        ApiAllureReport.step(step);

        String value = response.header(headerName);
        if (value == null) {
            fail("Expected response header <" + headerName + "> to exist but it was missing.");
        }

        LOG.assertion("✓ API response header exists: {}", headerName);
        return this;
    }

    public ApiAssertions headerEquals(String headerName, String expectedValue) {
        String step = "Assert API response header '" + headerName + "' = '" + expectedValue + "'";
        LOG.assertion(step);
        ApiAllureReport.step(step);

        String actual = response.header(headerName);
        if (!Objects.equals(actual, expectedValue)) {
            fail("Expected response header <" + headerName + "> to be <" + expectedValue + "> but was <" + actual + ">.");
        }

        LOG.assertion("✓ API response header '{}' = '{}'", headerName, expectedValue);
        return this;
    }

    public ApiAssertions contentTypeContains(String expectedContentTypePart) {
        String step = "Assert API response content type contains '" + expectedContentTypePart + "'";
        LOG.assertion(step);
        ApiAllureReport.step(step);

        String actual = response.contentType();
        if (actual == null || !actual.toLowerCase().contains(expectedContentTypePart.toLowerCase())) {
            fail("Expected content type to contain <" + expectedContentTypePart + "> but was <" + actual + ">.");
        }

        LOG.assertion("✓ API response content type contains '{}'", expectedContentTypePart);
        return this;
    }

    public ApiAssertions responseTimeLessThan(long maxMilliseconds) {
        String step = "Assert API response time < " + maxMilliseconds + " ms";
        LOG.assertion(step);
        ApiAllureReport.step(step);

        long actual = response.responseTimeMs();
        if (actual >= maxMilliseconds) {
            fail("Expected response time to be less than <" + maxMilliseconds + " ms> but was <" + actual + " ms>.");
        }

        LOG.assertion("✓ API response time {} ms is under {} ms", actual, maxMilliseconds);
        return this;
    }

    public JsonPathAssertions jsonPath(String path) {
        return new JsonPathAssertions(response, this, path);
    }

    public ApiResponse response() {
        return response;
    }

    private void fail(String message) {
        LOG.assertionFailed(message);
        ApiAllureReport.attachText("API assertion failure", message);
        ApiAllureReport.attachText("API request", response.request().toReadableText());
        ApiAllureReport.attachText("API response", response.toReadableText());
        throw new AssertionError(message);
    }
}
