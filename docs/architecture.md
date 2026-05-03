# QATRA Engine Architecture

QATRA is a multi-module Java test automation framework.

## Design Goals

1. Provide a readable fluent API.
2. Keep Selenium and REST Assured power available without exposing unnecessary boilerplate.
3. Treat Arabic/RTL quality as a first-class automation concern.
4. Capture useful evidence automatically.
5. Stay modular enough to support future mobile and database modules.

## Module Overview

```text
qatra-core
  ├── Configuration loading
  ├── Environment profiles
  ├── Secrets masking
  ├── Logging
  ├── Driver context
  └── Shared enums

qatra-web
  ├── Selenium WebDriver factory
  ├── Fluent browser and element API
  ├── Core Selenium actions
  ├── Assertions
  ├── Smart waits
  ├── RTL engine
  ├── Page Object Model
  ├── Data-driven testing utilities
  ├── Retry / parallel / stability utilities
  └── Reports and diagnostics

qatra-api
  ├── REST Assured wrapper
  ├── Fluent request builder
  ├── Response object
  ├── API assertions
  └── Allure request/response attachments
```

## Fluent API Flow

```text
QATRA WebDriver
    ↓
FluentWeb hub
    ↓
BrowserActions / ElementActions / AlertActions / FrameActions / WindowActions
    ↓
Assertions / Reports / Waits / RTL Engine
```

Example:

```java
driver()
        .browser()
        .navigateTo("https://example.com")
        .element()
        .click(By.id("login"))
        .assertThat()
        .element(By.id("welcome")).isVisible();
```

## Evidence Architecture

When a failure happens, QATRA can capture:

- Screenshot
- Page source
- Browser console logs
- Current URL
- Page title
- Stack trace
- Allure attachments

The goal is to make failures explainable without rerunning the test immediately.

## Configuration Priority

```text
1. System properties
2. Environment variables
3. Active profile file
4. qatra.properties
5. Built-in defaults
```

This means the CI pipeline can override local settings safely.

## Extension Strategy

QATRA should grow by adding new fluent groups instead of overloading one class.

Good pattern:

```java
driver().downloads().waitFor("report.pdf");
driver().accessibility().scanPage();
driver().visual().compare("dashboard");
```

Avoid turning `ElementActions` into a huge class that owns every responsibility.
