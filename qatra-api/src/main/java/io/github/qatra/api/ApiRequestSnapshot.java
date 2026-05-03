package io.github.qatra.api;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Immutable-ish snapshot of the API request sent by QATRA.
 *
 * <p>This is used for readable logs, Allure attachments, and future diagnostics.</p>
 */
public final class ApiRequestSnapshot {

    private final ApiMethod method;
    private final String baseUrl;
    private final String endpoint;
    private final Map<String, Object> headers;
    private final Map<String, Object> queryParams;
    private final Map<String, Object> pathParams;
    private final String body;
    private final LocalDateTime timestamp;

    public ApiRequestSnapshot(
            ApiMethod method,
            String baseUrl,
            String endpoint,
            Map<String, Object> headers,
            Map<String, Object> queryParams,
            Map<String, Object> pathParams,
            String body
    ) {
        this.method = method;
        this.baseUrl = baseUrl;
        this.endpoint = endpoint;
        this.headers = new LinkedHashMap<>(headers);
        this.queryParams = new LinkedHashMap<>(queryParams);
        this.pathParams = new LinkedHashMap<>(pathParams);
        this.body = body;
        this.timestamp = LocalDateTime.now();
    }

    public ApiMethod method() {
        return method;
    }

    public String baseUrl() {
        return baseUrl;
    }

    public String endpoint() {
        return endpoint;
    }

    public Map<String, Object> headers() {
        return new LinkedHashMap<>(headers);
    }

    public Map<String, Object> queryParams() {
        return new LinkedHashMap<>(queryParams);
    }

    public Map<String, Object> pathParams() {
        return new LinkedHashMap<>(pathParams);
    }

    public String body() {
        return body;
    }

    public LocalDateTime timestamp() {
        return timestamp;
    }

    public String fullUrl() {
        if (baseUrl == null || baseUrl.isBlank()) {
            return endpoint;
        }
        if (endpoint == null || endpoint.isBlank()) {
            return baseUrl;
        }
        if (baseUrl.endsWith("/") && endpoint.startsWith("/")) {
            return baseUrl + endpoint.substring(1);
        }
        if (!baseUrl.endsWith("/") && !endpoint.startsWith("/")) {
            return baseUrl + "/" + endpoint;
        }
        return baseUrl + endpoint;
    }

    public String toReadableText() {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        joiner.add("QATRA API Request");
        joiner.add("=================");
        joiner.add("Timestamp : " + timestamp);
        joiner.add("Method    : " + method);
        joiner.add("URL       : " + fullUrl());
        joiner.add("Headers   : " + headers);
        joiner.add("Query     : " + queryParams);
        joiner.add("Path      : " + pathParams);
        joiner.add("Body      : " + (body == null || body.isBlank() ? "<empty>" : body));
        return joiner.toString();
    }
}
