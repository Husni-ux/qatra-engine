# QATRA Engine 🌊

### Quality Automation Testing & RTL Architecture

**Arabic-first test automation for Web and API testing, built with Java 21, Selenium 4, REST Assured, TestNG, and Allure.**

[![Java 21](https://img.shields.io/badge/Java-21-orange?style=for-the-badge)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-Multi--Module-blue?style=for-the-badge)](https://maven.apache.org/)
[![Selenium 4](https://img.shields.io/badge/Selenium-4-brightgreen?style=for-the-badge)](https://www.selenium.dev/)
[![REST Assured](https://img.shields.io/badge/API-REST%20Assured-blueviolet?style=for-the-badge)](https://rest-assured.io/)
[![License](https://img.shields.io/badge/License-MIT-indigo?style=for-the-badge)](LICENSE)

> QATRA is not another Selenium wrapper. It is an Arabic-first quality automation engine designed for readable tests, professional evidence, and RTL quality validation.

---

## Why QATRA?

Most automation frameworks help you click, type, and assert. QATRA does that, but it also treats Arabic and RTL quality as a first-class testing concern.

QATRA helps teams write tests that are:

- **Readable** — fluent API designed for testers and developers.
- **Evidence-rich** — screenshots, page source, browser logs, Allure steps, and exported reports.
- **RTL-aware** — Arabic text checks, direction validation, RTL scanner, baseline comparison, and quality gates.
- **Project-ready** — configuration profiles, retry support, parallel execution, Page Object Model, and CI/CD workflows.

---

## Current Status

QATRA is currently in **`0.1.0-alpha`**.

The framework is usable for experiments, learning, internal pilots, and early open-source feedback. Public APIs may still change before `1.0.0`.

---

## Modules

```text
qatra-engine/
├── qatra-core/      # Config, logging, environment profiles, secrets, context, enums
├── qatra-web/       # Selenium fluent API, RTL engine, Page Objects, diagnostics, stability
├── qatra-api/       # REST API testing fluent module
├── docs/            # User and contributor documentation
├── scripts/         # Local CI scripts
└── .github/         # GitHub Actions, issue templates, Dependabot
```

Planned modules:

```text
qatra-mobile/        # Appium mobile testing
qatra-db/            # Database validations
qatra-rtl/           # Dedicated Arabic/RTL module once the RTL engine matures
qatra-examples/      # Standalone examples project
```

---

## Quick Web Example

```java
import io.github.qatra.web.testng.QatraBaseTest;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class LoginTest extends QatraBaseTest {

    @Test
    public void userCanLogin() {
        driver()
                .browser()
                .navigateTo("https://example.com/login")
                .element()
                .clearAndType(By.id("username"), "admin")
                .clearAndType(By.id("password"), "secret")
                .click(By.id("loginBtn"))
                .assertThat()
                .element(By.id("welcome")).isVisible()
                .element(By.id("welcome")).containsText("Welcome")
                .browser().url().contains("/dashboard");
    }
}
```

`QatraBaseTest` handles browser setup, teardown, diagnostics, and parallel-safe driver access.

---

## RTL / Arabic Quality Example

```java
driver()
        .assertThat()
        .element(By.tagName("body")).isRTL()
        .element(By.id("arabic-title")).hasArabicText()
        .element(By.id("arabic-title")).hasNoBrokenArabicCharacters()
        .element(By.id("price")).hasArabicDigits();
```

Full-page RTL scan:

```java
driver()
        .rtl()
        .scanPage()
        .report()
        .qualityGate()
        .failOnConfiguredQualityGate();
```

QATRA can export RTL reports as TXT, JSON, HTML, maintain history, compare against a baseline, and fail builds using a configurable quality gate.

---

## API Testing Example

```java
import io.github.qatra.api.QatraApi;
import org.testng.annotations.Test;

public class HealthApiTest {

    @Test
    public void healthEndpointIsUp() {
        QatraApi
                .fromEnvironment()
                .get("/health")
                .assertThat()
                .statusCode(200)
                .contentTypeContains("json")
                .jsonPath("status").equalsTo("UP");
    }
}
```

---

## Page Object Example

```java
@QatraPageUrl(contains = "/dashboard", titleContains = "Dashboard")
@QatraPageLoaded(id = "dashboard-title")
public class DashboardPage extends QatraPage {

    @QatraFindBy(testId = "user-chip")
    private QatraElement userChip;

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public DashboardPage assertUser(String expectedUser) {
        userChip.assertThat().containsText(expectedUser);
        return this;
    }
}
```

---

## Configuration Profiles

QATRA supports environment profiles:

```text
qatra-local.properties
qatra-dev.properties
qatra-staging.properties
qatra-prod.properties
```

Run with a profile:

```bash
mvn -pl qatra-web -am clean test -Dqatra.env=staging
```

Configuration priority:

```text
System properties → Environment variables → Active profile file → qatra.properties → Defaults
```

---

## Run Locally

Requirements:

- Java 21 LTS
- Maven 3.9+
- Chrome, Edge, or Firefox

Run all tests:

```bash
mvn clean test -Dsurefire.failIfNoSpecifiedTests=false
```

Run web tests:

```bash
mvn -pl qatra-web -am clean test \
  -Dtest=SampleQatraTest \
  -Dqatra.env=local \
  -Dqatra.browser=chrome \
  -Dqatra.headless=true \
  -Dsurefire.failIfNoSpecifiedTests=false
```

Run API tests:

```bash
mvn -pl qatra-api -am clean test -Dqatra.env=local
```

Run the local CI script:

```bash
./scripts/run-ci-local.sh
```

Windows PowerShell:

```powershell
.\scripts\run-ci-local.ps1
```

---

## Maven Central Readiness

QATRA is now prepared for Maven Central publishing using a safe release profile.

Current development version:

```xml
<version>0.1.0-alpha-SNAPSHOT</version>
```

Target first public release:

```xml
<version>0.1.0-alpha</version>
```

After the first release is published, users will be able to install QATRA like this:

```xml
<dependency>
    <groupId>io.github.qatra</groupId>
    <artifactId>qatra-web</artifactId>
    <version>0.1.0-alpha</version>
</dependency>
```

```xml
<dependency>
    <groupId>io.github.qatra</groupId>
    <artifactId>qatra-api</artifactId>
    <version>0.1.0-alpha</version>
</dependency>
```

Release preparation docs:

- [Maven Central Publishing](docs/maven-central-publishing.md)
- [Release Process](docs/release-process.md)
- [Namespace Decision](docs/namespace-decision.md)
- [Consumer Installation](docs/consumer-installation.md)

---

## Documentation

Start here:

- [Getting Started](docs/getting-started.md)
- [Architecture](docs/architecture.md)
- [Web Testing Guide](docs/web-testing.md)
- [RTL Testing Guide](docs/rtl-testing.md)
- [API Testing Guide](docs/api-testing.md)
- [Page Object Model](docs/page-object-model.md)
- [Data Driven Testing](docs/data-driven-testing.md)
- [Stability, Retry, and Parallel Execution](docs/stability.md)
- [Configuration Management](docs/configuration-management.md)
- [Configuration Reference](docs/configuration-reference.md)
- [CI/CD](docs/ci-cd.md)
- [Coverage Matrix](docs/coverage-matrix.md)
- [Examples Index](docs/examples-index.md)
- [Roadmap](docs/roadmap.md)
- [Release Checklist](docs/release-checklist.md)

---

## Current Feature Coverage

| Area | Status |
|---|---:|
| Browser lifecycle | ✅ |
| Element actions | ✅ |
| Smart waits | ✅ |
| Assertions | ✅ |
| Alerts, frames, windows | ✅ |
| Shadow DOM, cookies, storage | ✅ |
| Tables and drag/drop helpers | ✅ |
| Screenshots and diagnostics | ✅ |
| Allure evidence | ✅ |
| Arabic/RTL checks | ✅ |
| RTL scan reports, history, baseline, quality gate | ✅ |
| Page Object / Component Object | ✅ |
| CSV / JSON / Excel data | ✅ |
| Retry / parallel / stability helpers | ✅ |
| Environment profiles | ✅ |
| API fluent testing | ✅ Starter |
| Mobile / database modules | Planned |

---

## Contributing

Contributions are welcome. Please read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a pull request.

Core principles:

1. Keep the API readable.
2. Keep Arabic/RTL quality as a first-class concern.
3. Attach useful evidence for failures.
4. Avoid logging secrets.
5. Add tests and docs for public API changes.

---

## License

MIT © QATRA Community
