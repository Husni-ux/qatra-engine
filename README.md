# QATRA Engine 🌊

## Quality Automation Testing & RTL Architecture

**Arabic-first Java automation framework focused on Web testing, RTL validation, smart/adaptive waits, diagnostics, and fluent QA APIs.**

[![Java 21](https://img.shields.io/badge/Java-21-orange?style=for-the-badge)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-Multi--Module-blue?style=for-the-badge)](https://maven.apache.org/)
[![Selenium 4](https://img.shields.io/badge/Selenium-4-brightgreen?style=for-the-badge)](https://www.selenium.dev/)
[![TestNG](https://img.shields.io/badge/TestNG-Ready-red?style=for-the-badge)](https://testng.org/)
[![Allure](https://img.shields.io/badge/Allure-Reports-purple?style=for-the-badge)](https://qameta.io/allure-report/)
[![License](https://img.shields.io/badge/License-MIT-indigo?style=for-the-badge)](LICENSE)

> QATRA is not another Selenium wrapper. QATRA is an Arabic-first quality automation engine designed for readable tests, smarter waits, professional evidence, and RTL quality validation.

---

## Table of Contents

- [What is QATRA?](#what-is-qatra)
- [Why QATRA?](#why-qatra)
- [Current Status](#current-status)
- [Project Modules](#project-modules)
- [Main Features](#main-features)
- [Requirements](#requirements)
- [Quick Start](#quick-start)
- [Web Testing Example](#web-testing-example)
- [Smart Wait Engine](#smart-wait-engine)
- [Adaptive Wait Engine](#adaptive-wait-engine)
- [Arabic / RTL Testing](#arabic--rtl-testing)
- [Web Assertion Engine](#web-assertion-engine)
- [Assertion Failure Diagnostics](#assertion-failure-diagnostics)
- [Web Component Layer](#web-component-layer)
- [Page Object Model](#page-object-model)
- [Configuration](#configuration)
- [Reports and Evidence](#reports-and-evidence)
- [Run Tests](#run-tests)
- [Documentation](#documentation)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)

---

## What is QATRA?

QATRA Engine is a Java-based test automation framework built on top of Selenium 4 and TestNG.

It helps QA engineers write cleaner, more readable, and more maintainable web automation tests while adding features that are commonly needed in real enterprise testing projects:

- Fluent browser and element actions
- Smart and adaptive waits
- Arabic and RTL assertions
- Broken Arabic / encoding checks
- Web component helpers
- Screenshots, browser logs, page source, and diagnostics
- Allure integration
- Page Object Model support
- Data-driven testing utilities
- Retry and parallel execution helpers

QATRA uses Selenium as the automation foundation and adds a higher-level quality layer around it.

---

## Why QATRA?

Most automation frameworks help you click, type, and assert.

QATRA does that, but it also treats **Arabic, RTL, readability, diagnostics, and evidence** as first-class testing concerns.

QATRA is designed for teams that test systems where the following issues matter:

- Arabic text may appear broken or incorrectly encoded
- RTL layout may render incorrectly
- Arabic and English mixed content may cause direction issues
- Dynamic pages need more than basic Selenium waits
- Test failures need clear evidence, not only stack traces
- Test code should be readable by QA engineers, developers, and reviewers

---

## Current Status

QATRA is currently in:

```text
0.1.0-alpha-SNAPSHOT
```

This version is suitable for:

- Learning and experimentation
- Internal pilots
- Web automation proof of concepts
- Arabic / RTL testing exploration
- Open-source feedback and contribution

The public API may still change before `1.0.0`.

> Current engineering focus: **qatra-web**. API, mobile, database, and visual testing are planned or experimental and should not distract from stabilizing the web module.

---

## Project Modules

```text
qatra-engine/
├── qatra-core/       # Configuration, logging, environment profiles, secrets, driver context, enums
├── qatra-web/        # Selenium fluent API, smart waits, adaptive waits, RTL engine, assertions, diagnostics
├── qatra-api/        # Starter REST API testing module
├── docs/             # Documentation and technical guides
├── scripts/          # Local CI and release helper scripts
└── .github/          # GitHub Actions, issue templates, Dependabot
```

Planned future modules:

```text
qatra-mobile/         # Appium-based mobile testing
qatra-db/             # Database validations
qatra-rtl/            # Dedicated Arabic / RTL quality module when the RTL engine matures
qatra-examples/       # Standalone examples project
```

---

## Main Features

| Area | Status | Description |
|---|---:|---|
| Browser lifecycle | ✅ | Browser creation, navigation, refresh, quit |
| Element actions | ✅ | Click, type, clear, hover, upload, JS helpers |
| Smart waits | ✅ | FluentWait-based waits for pages, text, Arabic, RTL, Ajax, components |
| Adaptive waits | ✅ Foundation | Multi-signal waits for DOM, JS, RTL, stability, overlays, components |
| Web assertions | ✅ | Legacy fluent assertions + focused assertion engine |
| Assertion diagnostics | ✅ Foundation | Expected/actual, locator, URL, element state, screenshot, source, logs |
| Arabic / RTL checks | ✅ | Arabic text, RTL direction, digits, mixed content, encoding safety |
| RTL scanner | ✅ | TXT, JSON, HTML reports, history, baseline, quality gate |
| Web components | ✅ Foundation | Dropdown, table, modal, toast, loading overlay |
| Screenshots and diagnostics | ✅ | Screenshot, browser logs, page source, Allure attachments |
| Page Object Model | ✅ | Page and component object support |
| Data-driven testing | ✅ | CSV, JSON, Excel readers |
| Stability helpers | ✅ | Retry, parallel execution, listener support |
| CI/CD | ✅ | GitHub Actions and local CI scripts |
| API testing | Starter | REST Assured-based fluent API module |
| Mobile / DB | Planned | Future modules |

---

## Requirements

- Java 21 LTS
- Maven 3.9+
- Chrome, Edge, or Firefox
- TestNG
- Git

Recommended local setup:

```text
Java: 21 LTS
Maven: 3.9+
Browser: Chrome / Edge / Firefox
OS: Windows, Linux, or macOS
```

---

## Quick Start

Clone the repository:

```bash
git clone https://github.com/Husni-ux/qatra-engine.git
cd qatra-engine
```

Run a clean build:

```bash
mvn clean test -Dsurefire.failIfNoSpecifiedTests=false
```

Run web tests only:

```bash
mvn -pl qatra-web -am clean test \
  -Dqatra.env=local \
  -Dqatra.browser=chrome \
  -Dqatra.headless=true \
  -Dsurefire.failIfNoSpecifiedTests=false
```

Windows PowerShell:

```powershell
mvn -pl qatra-web -am clean test "-Dqatra.env=local" "-Dqatra.browser=chrome" "-Dqatra.headless=true" "-Dsurefire.failIfNoSpecifiedTests=false"
```

> In PowerShell, keep `-D` Maven properties inside quotes to avoid parsing issues.

---

## Web Testing Example

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
                .element(By.id("welcome"))
                    .isVisible()
                    .containsText("Welcome")
                    .and()
                .browser()
                    .url()
                    .contains("/dashboard");
    }
}
```

`QatraBaseTest` handles setup, teardown, thread-safe driver access, and failure diagnostics.

---

## Smart Wait Engine

QATRA includes a Smart Wait Engine built on Selenium FluentWait.

It is designed for real dynamic pages where basic visibility is not always enough.

Smart wait examples include:

- Wait until page is ready
- Wait until element is visible or clickable
- Wait until Arabic text is visible
- Wait until text is not broken
- Wait until RTL direction is applied
- Wait until encoding is valid
- Wait until custom component is ready
- Wait until Ajax is completed

Example:

```java
import io.github.qatra.web.waits.QatraWait;
import org.openqa.selenium.By;

QatraWait.forElement(driver().getSeleniumDriver(), By.id("title"))
        .waitUntilArabicTextIsVisible("مرحبا")
        .waitUntilTextIsNotBroken()
        .waitUntilRtlDirectionApplied()
        .waitUntilEncodingIsValid()
        .waitUntilArabicTextRenderedCorrectly();
```

Browser-level example:

```java
driver()
        .browser()
        .navigateTo("https://example.com")
        .waitUntilPageReady()
        .assertThat()
        .browser()
        .url()
        .contains("example.com");
```

Documentation: [Smart Wait Engine v2](docs/smart-wait-engine-v2.md)

---

## Adaptive Wait Engine

QATRA Adaptive Wait Engine goes beyond traditional Selenium `ExpectedConditions`.

Instead of waiting for one technical condition, it can combine several readiness signals:

- DOM readiness
- Element existence, visibility, enabled state, and clickability
- Element visual stability
- Overlay coverage detection
- JavaScript readiness
- jQuery idle signal
- Angular readiness signal
- Network idle fallback
- DOM mutation stability
- Arabic text readiness
- RTL direction readiness
- Mojibake and broken Arabic detection
- Dropdown, modal, table, toast, and loading overlay readiness
- Timeout diagnostics and evidence collection

Example:

```java
import io.github.qatra.web.waits.adaptive.QatraAdaptiveWait;
import org.openqa.selenium.By;

QatraAdaptiveWait.forElement(driver().getSeleniumDriver(), By.id("login-title"))
        .untilArabicTextReady("تسجيل الدخول");
```

Advanced readiness chain:

```java
QatraAdaptiveWait.forElement(driver().getSeleniumDriver(), By.id("submit"))
        .require()
            .visible()
            .enabled()
            .stable()
            .notCovered()
            .noLoadingOverlay()
        .untilReady();
```

Page readiness:

```java
QatraAdaptiveWait.forPage(driver().getSeleniumDriver())
        .untilPageFullyReady();
```

Documentation:

- [QATRA Adaptive Wait Engine](docs/qatra-adaptive-wait-engine.md)
- [Selenium / SHAFT Wait Analysis](docs/selenium-shaft-wait-analysis.md)
- [Adaptive Wait Comparison](docs/qatra-adaptive-wait-comparison.md)
- [Adaptive Wait Roadmap](docs/qatra-adaptive-wait-roadmap.md)

---

## Arabic / RTL Testing

QATRA is designed with Arabic and RTL testing in mind.

It supports checks for:

- Arabic text existence
- Arabic text readability
- RTL direction
- CSS direction
- Mixed Arabic / English content
- Arabic digits and English digits
- Broken Arabic characters
- Mojibake / encoding corruption
- Unicode replacement characters
- Reversed Arabic text patterns
- Full-page RTL scanning
- RTL scan reports and history
- RTL baseline comparison
- RTL quality gate

Element-level example:

```java
driver()
        .assertThat()
        .element(By.id("arabic-title"))
            .hasArabicText()
            .hasNoBrokenArabicCharacters()
            .isRTL();
```

Full-page scan:

```java
driver()
        .rtl()
        .assertArabicTextExists()
        .scanPage()
        .report();
```

Quality gate example:

```java
driver()
        .rtl()
        .scanPage()
        .qualityGate()
        .failOnConfiguredQualityGate();
```

Documentation: [RTL Testing Guide](docs/rtl-testing.md)

---

## Web Assertion Engine

QATRA includes a focused Web Assertion Engine for cleaner, maintainable, and diagnostics-friendly web assertions.

Example:

```java
driver()
        .expect(By.id("login-title"))
        .exists()
        .isVisible()
        .text()
            .contains("تسجيل الدخول")
            .containsArabic()
            .and()
        .rtl()
            .hasArabicText()
            .hasReadableArabicText()
            .hasRtlDirection()
            .and()
        .encoding()
            .isUtf8SafeContent()
            .and()
        .visual()
            .isDisplayed()
            .isInsideViewport()
            .notCovered();
```

The legacy assertion API is still available:

```java
driver()
        .assertThat()
        .element(By.id("message"))
            .isVisible()
            .containsText("Success");
```

Documentation: [Web Assertion Engine](docs/web-assertion-engine.md)

---

## Assertion Failure Diagnostics

QATRA does not only fail an assertion; it aims to explain the failure.

When a focused web assertion fails, QATRA can collect diagnostic evidence such as:

- Assertion name
- Locator
- Expected value
- Actual rendered value
- Current URL
- Page title
- Element text
- CSS direction
- Text alignment
- Displayed / enabled state
- Viewport state
- Overlay coverage state
- Element rectangle
- Screenshot path
- Page source path
- Browser logs path
- Detected issue summary
- Assertion diagnostic report path

Example:

```java
driver()
        .expect(By.id("broken-arabic"))
        .encoding()
        .hasNoMojibake();
```

If rendered Arabic text is corrupted, QATRA can report the problem with evidence instead of showing only a generic assertion error.

Reports are saved under:

```text
target/qatra-reports/assertions
target/qatra-reports/screenshots
target/qatra-reports/page-source
target/qatra-reports/browser-logs
```

Documentation: [Assertion Failure Diagnostics](docs/assertion-failure-diagnostics.md)

---

## Web Component Layer

QATRA includes a web-focused component layer for dynamic UI widgets commonly found in enterprise and Arabic/RTL systems.

Supported component helpers include:

- Dropdown
- Table
- Modal
- Toast
- Loading overlay

Examples:

```java
driver()
        .dropdown(By.id("city"))
        .selectArabicText("الرياض")
        .assertSelectedArabicText("الرياض");
```

```java
driver()
        .webTable(By.id("visits-table"))
        .withRows(By.cssSelector("tbody tr"))
        .waitUntilRowsLoaded(2)
        .assertRowContainsArabicText("منشأة تجريبية");
```

```java
driver()
        .toast(By.id("toast"))
        .successMessageContains("تم الحفظ بنجاح");
```

Documentation: [Web Component Layer](docs/web-component-layer.md)

---

## Page Object Model

QATRA supports Page Object and Component Object patterns.

```java
import io.github.qatra.web.WebDriver;
import io.github.qatra.web.page.QatraElement;
import io.github.qatra.web.page.QatraFindBy;
import io.github.qatra.web.page.QatraPage;
import io.github.qatra.web.page.QatraPageLoaded;
import io.github.qatra.web.page.QatraPageUrl;

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

Usage:

```java
DashboardPage dashboard = driver().page(DashboardPage.class);
dashboard.assertUser("Husni");
```

Documentation: [Page Object Model](docs/page-object-model.md)

---

## Configuration

QATRA supports environment profiles:

```text
qatra-local.properties
qatra-dev.properties
qatra-staging.properties
qatra-prod.properties
qatra.properties
```

Configuration priority:

```text
System properties → Environment variables → Active profile file → qatra.properties → Defaults
```

Run with a profile:

```bash
mvn -pl qatra-web -am clean test -Dqatra.env=staging
```

Windows PowerShell:

```powershell
mvn -pl qatra-web -am clean test "-Dqatra.env=staging"
```

Common properties:

```properties
qatra.browser=chrome
qatra.headless=true
qatra.timeout.seconds=30
qatra.base.url=https://example.com
qatra.screenshot.on.failure=true
qatra.diagnostics.on.failure=true
```

Documentation:

- [Configuration Management](docs/configuration-management.md)
- [Configuration Reference](docs/configuration-reference.md)

---

## Data-Driven Testing

QATRA includes utilities for test data from:

- CSV
- JSON
- Excel

Example resources:

```text
qatra-web/src/test/resources/test-data/login-users.csv
qatra-web/src/test/resources/test-data/api-users.json
qatra-web/src/test/resources/test-data/ui-users.xlsx
```

Documentation: [Data Driven Testing](docs/data-driven-testing.md)

---

## API Testing Module

QATRA includes a starter API testing module based on REST Assured.

Example:

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

> Current focus remains `qatra-web`. The API module is available as a starter module and will mature later.

Documentation: [API Testing Guide](docs/api-testing.md)

---

## Reports and Evidence

QATRA can generate and attach different types of testing evidence:

```text
target/qatra-reports/screenshots
target/qatra-reports/page-source
target/qatra-reports/browser-logs
target/qatra-reports/rtl
target/qatra-reports/assertions
allure-results
```

Evidence may include:

- Screenshots
- Page source
- Browser console logs
- Browser state
- RTL scan reports
- Assertion failure diagnostics
- Allure steps and attachments

This helps testers investigate failures faster and makes reports more useful for developers, leads, and stakeholders.

---

## Run Tests

Run everything:

```bash
mvn clean test -Dsurefire.failIfNoSpecifiedTests=false
```

Run web tests:

```bash
mvn -pl qatra-web -am clean test \
  -Dqatra.env=local \
  -Dqatra.browser=chrome \
  -Dqatra.headless=true \
  -Dsurefire.failIfNoSpecifiedTests=false
```

Run a specific web test:

```bash
mvn -pl qatra-web -am clean test \
  -Dtest=QatraWebAssertionEngineTest \
  -Dqatra.env=local \
  -Dqatra.browser=chrome \
  -Dqatra.headless=true \
  -Dsurefire.failIfNoSpecifiedTests=false
```

Windows PowerShell:

```powershell
mvn -pl qatra-web -am clean test "-Dtest=QatraWebAssertionEngineTest" "-Dqatra.env=local" "-Dqatra.browser=chrome" "-Dqatra.headless=true" "-Dsurefire.failIfNoSpecifiedTests=false"
```

Run local CI script:

```bash
./scripts/run-ci-local.sh
```

Windows PowerShell:

```powershell
.\scripts\run-ci-local.ps1
```

---

## Maven Central Readiness

QATRA is prepared for Maven Central publishing through a safe release profile.

Current development version:

```xml
<version>0.1.0-alpha-SNAPSHOT</version>
```

Target first public release:

```xml
<version>0.1.0-alpha</version>
```

After the first public release, users will be able to install modules like this:

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
    <artifactId>qatra-core</artifactId>
    <version>0.1.0-alpha</version>
</dependency>
```

Release documentation:

- [Maven Central Publishing](docs/maven-central-publishing.md)
- [Release Process](docs/release-process.md)
- [Release Checklist](docs/release-checklist.md)
- [Consumer Installation](docs/consumer-installation.md)
- [Namespace Decision](docs/namespace-decision.md)

---

## Documentation

Start here:

- [Getting Started](docs/getting-started.md)
- [Architecture](docs/architecture.md)
- [Web Testing Guide](docs/web-testing.md)
- [Smart Wait Engine v2](docs/smart-wait-engine-v2.md)
- [QATRA Adaptive Wait Engine](docs/qatra-adaptive-wait-engine.md)
- [Selenium / SHAFT Wait Analysis](docs/selenium-shaft-wait-analysis.md)
- [Web Assertion Engine](docs/web-assertion-engine.md)
- [Assertion Failure Diagnostics](docs/assertion-failure-diagnostics.md)
- [Web Component Layer](docs/web-component-layer.md)
- [RTL Testing Guide](docs/rtl-testing.md)
- [Page Object Model](docs/page-object-model.md)
- [Data Driven Testing](docs/data-driven-testing.md)
- [Stability, Retry, and Parallel Execution](docs/stability.md)
- [Configuration Management](docs/configuration-management.md)
- [Configuration Reference](docs/configuration-reference.md)
- [CI/CD](docs/ci-cd.md)
- [Coverage Matrix](docs/coverage-matrix.md)
- [Examples Index](docs/examples-index.md)
- [Roadmap](docs/roadmap.md)
- [Web Focused Roadmap](docs/web-focused-roadmap.md)

---

## Roadmap

Near-term focus:

```text
Phase 3.15  Web assertion diagnostics foundation
Phase 3.16  Integrate Adaptive Wait Engine into ElementActions
Phase 3.17  Stronger assertion failure reports
Phase 3.18  Advanced web components
Phase 3.19  Examples and README cleanup
Phase 3.20  First web alpha release
```

Future direction:

- Stable `qatra-web` alpha release
- More real-world Arabic/RTL examples
- Better reports and dashboards
- More component helpers
- Cleaner public API contracts
- Maven Central publishing
- Standalone examples project
- API module maturity
- Future mobile and database modules

---

## Design Principles

1. **Keep tests readable**  
   Test code should be clear for QA engineers, developers, and reviewers.

2. **Build on Selenium, do not replace it**  
   QATRA extends Selenium for real-world testing needs.

3. **Treat Arabic and RTL as first-class quality concerns**  
   Arabic systems deserve dedicated testing support.

4. **Make failures explainable**  
   A failed assertion should provide useful evidence, not only a stack trace.

5. **Keep the API practical**  
   Every feature should help testers solve real testing problems.

---

## Contributing

Contributions are welcome.

You can help by:

- Trying QATRA on real web applications
- Opening issues
- Suggesting API improvements
- Improving documentation
- Adding examples
- Reporting Arabic / RTL bugs
- Contributing code

Before contributing, please read:

- [CONTRIBUTING.md](CONTRIBUTING.md)
- [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)

Core contribution principles:

1. Keep the API readable.
2. Keep Arabic / RTL quality as a first-class concern.
3. Add useful diagnostics for failures.
4. Avoid logging secrets.
5. Add tests and docs for public API changes.

---

## Repository

GitHub:

```text
https://github.com/Husni-ux/qatra-engine
```

Suggested repository description:

```text
Arabic-first Java Selenium automation framework focused on Web testing, RTL validation, smart waits, diagnostics, and fluent QA APIs.
```

Suggested topics:

```text
java selenium testng test-automation qa-automation web-testing rtl arabic-testing fluent-api open-source
```

---

## License

MIT © QATRA Community
