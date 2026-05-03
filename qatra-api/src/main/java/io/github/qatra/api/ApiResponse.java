package io.github.qatra.api;

import io.github.qatra.api.assertions.ApiAssertions;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

/**
 * Wrapper around REST Assured Response with QATRA fluent assertions.
 */
public final class ApiResponse {

    private final Response response;
    private final ApiRequestSnapshot requestSnapshot;
    private final long responseTimeMs;

    public ApiResponse(Response response, ApiRequestSnapshot requestSnapshot) {
        this.response = response;
        this.requestSnapshot = requestSnapshot;
        this.responseTimeMs = response == null ? -1 : response.time();
    }

    public ApiAssertions assertThat() {
        return new ApiAssertions(this);
    }

    public Response raw() {
        return response;
    }

    public ApiRequestSnapshot request() {
        return requestSnapshot;
    }

    public int statusCode() {
        return response.statusCode();
    }

    public String bodyAsString() {
        return response.body().asString();
    }

    public String header(String name) {
        return response.header(name);
    }

    public String contentType() {
        return response.contentType();
    }

    public long responseTimeMs() {
        return responseTimeMs;
    }

    public <T> T jsonPath(String path) {
        return response.jsonPath().get(path);
    }

    public JsonPath jsonPath() {
        return response.jsonPath();
    }

    public String toReadableText() {
        return "QATRA API Response" + System.lineSeparator()
                + "==================" + System.lineSeparator()
                + "Status        : " + statusCode() + System.lineSeparator()
                + "Content-Type  : " + contentType() + System.lineSeparator()
                + "Response Time : " + responseTimeMs + " ms" + System.lineSeparator()
                + "Body          : " + System.lineSeparator()
                + bodyAsString();
    }
}
