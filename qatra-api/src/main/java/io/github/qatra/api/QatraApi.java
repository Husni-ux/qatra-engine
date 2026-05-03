package io.github.qatra.api;

import io.github.qatra.api.reports.ApiAllureReport;
import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.logger.QatraLogger;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Main entry point for QATRA API testing.
 *
 * <p>Example:</p>
 * <pre>
 * QatraApi.create()
 *         .baseUrl("https://api.example.com")
 *         .header("Accept", "application/json")
 *         .get("/health")
 *         .assertThat()
 *         .statusCode(200)
 *         .jsonPath("$.status").equalsTo("UP");
 * </pre>
 */
public final class QatraApi {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final QatraConfig config;
    private final Map<String, Object> headers = new LinkedHashMap<>();
    private final Map<String, Object> queryParams = new LinkedHashMap<>();
    private final Map<String, Object> pathParams = new LinkedHashMap<>();

    private String baseUrl;
    private String body;
    private boolean relaxedHttps;

    private QatraApi() {
        this.config = QatraConfig.getInstance();
        this.baseUrl = config.getProperty(QatraProperties.API_BASE_URL, "");
        this.relaxedHttps = config.getBooleanProperty(QatraProperties.API_RELAXED_HTTPS, true);
        this.headers.put("Accept", "application/json");
    }

    public static QatraApi create() {
        return new QatraApi();
    }

    public static QatraApi start() {
        return create();
    }

    /**
     * Explicit alias for teams that want to show they are using the active
     * environment profile. The constructor already loads qatra.api.base.url.
     */
    public static QatraApi fromEnvironment() {
        return create();
    }

    public QatraApi useConfiguredBaseUrl() {
        this.baseUrl = config.getProperty(QatraProperties.API_BASE_URL, "");
        return this;
    }

    public QatraApi baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public QatraApi relaxedHttps(boolean relaxedHttps) {
        this.relaxedHttps = relaxedHttps;
        return this;
    }

    public QatraApi header(String name, Object value) {
        this.headers.put(name, value);
        return this;
    }

    public QatraApi headers(Map<String, ?> headers) {
        if (headers != null) {
            this.headers.putAll(headers);
        }
        return this;
    }

    public QatraApi contentTypeJson() {
        this.headers.put("Content-Type", "application/json");
        return this;
    }

    public QatraApi bearerToken(String token) {
        this.headers.put("Authorization", "Bearer " + token);
        return this;
    }

    public QatraApi queryParam(String name, Object value) {
        this.queryParams.put(name, value);
        return this;
    }

    public QatraApi queryParams(Map<String, ?> queryParams) {
        if (queryParams != null) {
            this.queryParams.putAll(queryParams);
        }
        return this;
    }

    public QatraApi pathParam(String name, Object value) {
        this.pathParams.put(name, value);
        return this;
    }

    public QatraApi pathParams(Map<String, ?> pathParams) {
        if (pathParams != null) {
            this.pathParams.putAll(pathParams);
        }
        return this;
    }

    public QatraApi body(String body) {
        this.body = body;
        return this;
    }

    public QatraApi jsonBody(String body) {
        return contentTypeJson().body(body);
    }

    public ApiResponse get(String endpoint) {
        return execute(ApiMethod.GET, endpoint);
    }

    public ApiResponse post(String endpoint) {
        return execute(ApiMethod.POST, endpoint);
    }

    public ApiResponse put(String endpoint) {
        return execute(ApiMethod.PUT, endpoint);
    }

    public ApiResponse patch(String endpoint) {
        return execute(ApiMethod.PATCH, endpoint);
    }

    public ApiResponse delete(String endpoint) {
        return execute(ApiMethod.DELETE, endpoint);
    }

    public QatraApi clearHeaders() {
        this.headers.clear();
        return this;
    }

    public QatraApi clearQueryParams() {
        this.queryParams.clear();
        return this;
    }

    public QatraApi clearPathParams() {
        this.pathParams.clear();
        return this;
    }

    public QatraApi clearBody() {
        this.body = null;
        return this;
    }

    private ApiResponse execute(ApiMethod method, String endpoint) {
        ApiRequestSnapshot snapshot = new ApiRequestSnapshot(
                method,
                baseUrl,
                endpoint,
                headers,
                queryParams,
                pathParams,
                body
        );

        String step = "API " + method + " " + snapshot.fullUrl();
        LOG.action(step);
        ApiAllureReport.step(step);

        if (config.getBooleanProperty(QatraProperties.API_ATTACH_REQUEST, true)) {
            ApiAllureReport.attachText("API Request - " + method + " " + endpoint, snapshot.toReadableText());
        }

        RequestSpecification request = RestAssured.given();

        if (relaxedHttps) {
            request.relaxedHTTPSValidation();
        }

        int timeoutSeconds = config.getIntProperty(QatraProperties.API_TIMEOUT_SECONDS, 30);
        request.config(RestAssured.config().httpClient(
                io.restassured.config.HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", timeoutSeconds * 1000)
                        .setParam("http.socket.timeout", timeoutSeconds * 1000)
        ));

        if (baseUrl != null && !baseUrl.isBlank()) {
            request.baseUri(baseUrl);
        }

        if (!headers.isEmpty()) {
            request.headers(headers);
        }

        if (!queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }

        if (!pathParams.isEmpty()) {
            request.pathParams(pathParams);
        }

        if (body != null) {
            request.body(body);
            if (!headers.containsKey("Content-Type")) {
                request.contentType(ContentType.JSON);
            }
        }

        Response rawResponse = switch (method) {
            case GET -> request.get(endpoint);
            case POST -> request.post(endpoint);
            case PUT -> request.put(endpoint);
            case PATCH -> request.patch(endpoint);
            case DELETE -> request.delete(endpoint);
        };

        ApiResponse apiResponse = new ApiResponse(rawResponse, snapshot);
        LOG.action("API response: status={} time={}ms", apiResponse.statusCode(), apiResponse.responseTimeMs());

        if (config.getBooleanProperty(QatraProperties.API_ATTACH_RESPONSE, true)) {
            String responseText = apiResponse.toReadableText();
            String contentType = apiResponse.contentType() == null ? "" : apiResponse.contentType().toLowerCase();
            if (contentType.contains("json")) {
                ApiAllureReport.attachJson("API Response - " + method + " " + endpoint, apiResponse.bodyAsString());
            } else {
                ApiAllureReport.attachText("API Response - " + method + " " + endpoint, responseText);
            }
        }

        return apiResponse;
    }
}
