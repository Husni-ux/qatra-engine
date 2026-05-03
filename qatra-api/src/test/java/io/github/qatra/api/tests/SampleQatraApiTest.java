package io.github.qatra.api.tests;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.github.qatra.api.QatraApi;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Local API tests for qatra-api.
 *
 * <p>These tests do not depend on external websites. They start a tiny local HTTP server
 * and use QATRA API fluent assertions against it.</p>
 */
public class SampleQatraApiTest {

    private HttpServer server;
    private String baseUrl;

    @BeforeClass(alwaysRun = true)
    public void startLocalApiServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);

        server.createContext("/health", exchange -> {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                return;
            }

            sendJson(exchange, 200, """
                    {
                      "status": "UP",
                      "message": "QATRA API ready",
                      "user": {
                        "name": "Husni",
                        "role": "QA Engineer"
                      },
                      "items": [1, 2, 3]
                    }
                    """);
        });

        server.createContext("/login", exchange -> {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                return;
            }

            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            if (requestBody.contains("admin") && requestBody.contains("123456")) {
                sendJson(exchange, 200, """
                        {
                          "token": "qatra-demo-token",
                          "expiresIn": 3600,
                          "user": {
                            "username": "admin"
                          }
                        }
                        """);
            } else {
                sendJson(exchange, 401, "{\"error\":\"Invalid credentials\"}");
            }
        });

        server.createContext("/users", exchange -> {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            sendJson(exchange, 200, """
                    {
                      "page": 1,
                      "query": "%s",
                      "data": [
                        {"id": 1, "name": "Husni"},
                        {"id": 2, "name": "QATRA"}
                      ]
                    }
                    """.formatted(query == null ? "" : query.replace("\"", "\\\"")));
        });

        server.start();
        baseUrl = "http://localhost:" + server.getAddress().getPort();
    }

    @AfterClass(alwaysRun = true)
    public void stopLocalApiServer() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void healthCheckApiTest() {
        QatraApi.create()
                .baseUrl(baseUrl)
                .get("/health")
                .assertThat()
                .statusCode(200)
                .contentTypeContains("json")
                .bodyContains("QATRA API ready")
                .jsonPath("status").equalsTo("UP")
                .jsonPath("user.name").equalsTo("Husni")
                .jsonPath("items").isArray()
                .jsonPath("items").hasSize(3)
                .responseTimeLessThan(3000);
    }

    @Test
    public void postLoginApiTest() {
        QatraApi.create()
                .baseUrl(baseUrl)
                .jsonBody("""
                        {
                          "username": "admin",
                          "password": "123456"
                        }
                        """)
                .post("/login")
                .assertThat()
                .statusCode(200)
                .bodyContains("qatra-demo-token")
                .jsonPath("token").exists()
                .jsonPath("user.username").equalsTo("admin")
                .jsonPath("expiresIn").equalsTo(3600);
    }

    @Test
    public void queryParamApiTest() {
        QatraApi.create()
                .baseUrl(baseUrl)
                .queryParam("role", "qa")
                .queryParam("active", true)
                .get("/users")
                .assertThat()
                .statusCode(200)
                .bodyContains("role=qa")
                .jsonPath("data").hasSize(2)
                .jsonPath("data[0].name").equalsTo("Husni");
    }

    private void sendJson(HttpExchange exchange, int statusCode, String responseBody) throws IOException {
        byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
