# API Testing Guide

The `qatra-api` module provides a fluent wrapper around REST Assured.

## Basic GET

```java
QatraApi
        .create()
        .baseUrl("https://api.example.com")
        .get("/health")
        .assertThat()
        .statusCode(200)
        .contentTypeContains("json")
        .bodyContains("UP")
        .jsonPath("status").equalsTo("UP");
```

## Use Environment Base URL

```java
QatraApi
        .fromEnvironment()
        .get("/health")
        .assertThat()
        .statusCode(200);
```

Set the base URL in `qatra.properties` or profile files:

```properties
qatra.api.base.url=https://api.example.com
```

## Headers and Bearer Token

```java
QatraApi
        .create()
        .baseUrl("https://api.example.com")
        .header("Accept", "application/json")
        .bearerToken("token-value")
        .get("/me")
        .assertThat()
        .statusCode(200);
```

## Query Params

```java
QatraApi
        .create()
        .baseUrl("https://api.example.com")
        .queryParam("role", "qa")
        .queryParam("active", true)
        .get("/users")
        .assertThat()
        .statusCode(200);
```

## Path Params

```java
QatraApi
        .create()
        .baseUrl("https://api.example.com")
        .pathParam("id", 10)
        .get("/users/{id}")
        .assertThat()
        .statusCode(200)
        .jsonPath("id").equalsTo(10);
```

## JSON Body

```java
QatraApi
        .create()
        .baseUrl("https://api.example.com")
        .jsonBody("""
            {
              "username": "admin",
              "password": "secret"
            }
        """)
        .post("/login")
        .assertThat()
        .statusCode(200)
        .jsonPath("token").exists();
```

## Assertions

Common assertions:

```java
.statusCode(200)
.statusCodeIsBetween(200, 299)
.contentTypeContains("json")
.bodyContains("Success")
.headerExists("X-Request-Id")
.responseTimeLessThan(1000)
.jsonPath("data.id").exists()
.jsonPath("data.name").equalsTo("Husni")
```

## Evidence

QATRA can attach request and response data to Allure.

```properties
qatra.api.attach.request=true
qatra.api.attach.response=true
qatra.api.timeout.seconds=30
qatra.api.relaxed.https=true
```

## Current Limitations

The API module is currently a starter module. Planned next improvements include:

- Reusable request specifications
- Basic Auth
- Form parameters
- Multipart upload
- Cookie support
- Request/response export to files
- JSON schema validation
