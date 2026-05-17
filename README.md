# QATRA Engine 🌊

### Arabic-first Java Test Automation Engine for Selenium, RTL Applications, Locator Intelligence, and Safe Self-Healing

QATRA Engine helps QA teams build readable, stable, and evidence-rich automation for modern web applications — with first-class support for Arabic, RTL quality, smart/adaptive waits, accessibility signals, locator intelligence, diagnostics, and safe self-healing.

<p align="center">
  <img alt="Java 21" src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge">
  <img alt="Maven" src="https://img.shields.io/badge/Maven-Multi--Module-blue?style=for-the-badge">
  <img alt="Selenium 4" src="https://img.shields.io/badge/Selenium-4-brightgreen?style=for-the-badge">
  <img alt="TestNG" src="https://img.shields.io/badge/TestNG-Ready-red?style=for-the-badge">
  <img alt="Allure" src="https://img.shields.io/badge/Allure-Reports-purple?style=for-the-badge">
  <img alt="License" src="https://img.shields.io/badge/License-MIT-indigo?style=for-the-badge">
</p>

> QATRA is not just another Selenium wrapper.  
> It is a quality automation engine for real-world Arabic/RTL systems, dynamic enterprise UIs, weak locator strategies, and evidence-driven debugging.

---

## Table of Contents

- [Why QATRA?](#why-qatra)
- [Current Status](#current-status)
- [Feature Highlights](#feature-highlights)
- [Project Structure](#project-structure)
- [Requirements](#requirements)
- [Quick Start](#quick-start)
- [First Web Test](#first-web-test)
- [Arabic / RTL Testing](#arabic--rtl-testing)
- [Smart and Adaptive Waits](#smart-and-adaptive-waits)
- [Adaptive Element Actions](#adaptive-element-actions)
- [Web Assertion Engine](#web-assertion-engine)
- [Assertion Failure Diagnostics](#assertion-failure-diagnostics)
- [Locator Intelligence](#locator-intelligence)
- [Safe Self-Healing Locators](#safe-self-healing-locators)
- [Healing Modes and Guardrails](#healing-modes-and-guardrails)
- [Arabic Semantic Locator Healing](#arabic-semantic-locator-healing)
- [Accessibility Locator Healing](#accessibility-locator-healing)
- [Arabic Component Self-Healing](#arabic-component-self-healing)
- [Healing Reports and Patch Suggestions](#healing-reports-and-patch-suggestions)
- [Web Component Layer](#web-component-layer)
- [Real-World Web Smoke Examples](#real-world-web-smoke-examples)
- [Page Object Model](#page-object-model)
- [Configuration](#configuration)
- [Data-Driven Testing](#data-driven-testing)
- [API Testing Module](#api-testing-module)
- [Reports and Evidence](#reports-and-evidence)
- [Run Tests](#run-tests)
- [Documentation](#documentation)
- [Maven Central Readiness](#maven-central-readiness)
- [Roadmap](#roadmap)
- [Design Principles](#design-principles)
- [Contributing](#contributing)
- [License](#license)

---

## Why QATRA?

Most automation frameworks help you click, type, and assert.

QATRA does that, but it also focuses on problems that QA teams face every day in Arabic and enterprise systems:

- Arabic text may render incorrectly or become corrupted.
- RTL layout may break without obvious functional failure.
- Arabic and English mixed content may cause direction issues.
- Dynamic pages may look visible but still not be ready for interaction.
- Locators may change frequently during UI development.
- Automation failures often lack useful evidence.
- Self-healing can hide real product bugs if it is not controlled.

QATRA is designed to make automation more readable, safer, and more explainable.

---

## Current Status

```text
0.1.0-alpha-SNAPSHOT
```

This version is suitable for:

- Learning and experimentation
- Internal pilots
- Web automation proof of concepts
- Arabic / RTL testing exploration
- Smoke and regression subsets
- Open-source feedback and contribution

The public API may still change before `1.0.0`.

> Current engineering focus: stabilizing `qatra-web`. API, mobile, database, and reporting modules are planned or experimental.

---

## Feature Highlights

| Area | Status | What QATRA Provides |
|---|---:|---|
| Fluent Web Automation | ✅ | Browser lifecycle, navigation, element actions, assertions, Page Object support |
| Selenium 4 Foundation | ✅ | Built on Selenium WebDriver with a higher-level QA layer |
| Smart Waits | ✅ | FluentWait-based waits for pages, elements, Arabic text, RTL, Ajax, and components |
| Adaptive Waits | ✅ | Multi-signal readiness checks for DOM, JS, overlays, stability, and components |
| Adaptive Element Actions | ✅ | Safer click, type, clear, select, hover, double-click, and keyboard actions |
| Arabic / RTL Assertions | ✅ | Arabic text, RTL direction, mixed content, digits, encoding, and mojibake checks |
| RTL Scanner | ✅ | Full-page RTL scanning, history, baseline comparison, and quality gates |
| Assertion Diagnostics | ✅ | Screenshots, page source, browser logs, expected/actual values, and element evidence |
| Web Components | ✅ Foundation | Dropdown, table, modal, toast, loading overlay, and component object support |
| Locator Quality Advisor | ✅ | Detects fragile locators before they break during regression |
| Self-Healing Locators | ✅ Alpha | Primary locator + controlled fallback locator strategy |
| Healing Guardrails | ✅ | Blocks ambiguous, hidden, disabled, covered, or risky healing candidates |
| Arabic Semantic Healing | ✅ Alpha | Uses Arabic action intent like save, approve, reject, search, submit |
| Accessibility Healing | ✅ Alpha | Uses role, accessible name, labels, placeholders, aria attributes, and native roles |
| Arabic Component Healing | ✅ Alpha | Resolves common Arabic UI components by business intent |
| Healing Reports | ✅ | HTML, JSON, TXT reports, patch suggestions, evidence, and review artifacts |
| API Testing | Starter | REST Assured-based starter module |
| Mobile / DB | Planned | Future modules |

---

## Project Structure

```text
qatra-engine/
├── qatra-core/       # Configuration, logging, environment profiles, shared utilities
├── qatra-web/        # Selenium fluent API, waits, assertions, RTL, healing, diagnostics
├── qatra-api/        # Starter REST API testing module
├── docs/             # Technical documentation and feature guides
├── scripts/          # Local CI and release helper scripts
└── .github/          # GitHub Actions, issue templates, Dependabot
```

Planned future modules:

```text
qatra-mobile/         # Appium-based mobile testing
qatra-db/             # Database validation support
qatra-rtl/            # Dedicated Arabic / RTL quality module
qatra-reporting/      # Unified reporting layer
qatra-examples/       # Standalone example projects
```

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

> In PowerShell, keep Maven `-D` properties inside quotes to avoid parsing issues.

---

## First Web Test

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
- RTL quality gates

Element-level example:

```java
driver()
        .assertThat()
        .element(By.id("arabic-title"))
            .hasArabicText()
            .hasNoBrokenArabicCharacters()
            .isRTL();
```

Focused assertion example:

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

Full-page scan:

```java
driver()
        .rtl()
        .assertArabicTextExists()
        .scanPage()
        .report();
```

Quality gate:

```java
driver()
        .rtl()
        .scanPage()
        .qualityGate()
        .failOnConfiguredQualityGate();
```

Documentation: [RTL Testing Guide](docs/rtl-testing.md)

---

## Smart and Adaptive Waits

QATRA provides two levels of waiting support.

### Smart Waits

Smart waits are built on Selenium `FluentWait` and cover common dynamic page scenarios:

- Wait until page is ready
- Wait until element is visible or clickable
- Wait until Arabic text is visible
- Wait until text is not broken
- Wait until RTL direction is applied
- Wait until encoding is valid
- Wait until custom component is ready
- Wait until Ajax is completed

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

### Adaptive Waits

Adaptive waits combine several readiness signals instead of waiting for one technical condition.

```java
import io.github.qatra.web.waits.adaptive.QatraAdaptiveWait;
import org.openqa.selenium.By;

QatraAdaptiveWait.forElement(driver().getSeleniumDriver(), By.id("submit"))
        .require()
            .visible()
            .enabled()
            .stable()
            .notCovered()
            .noLoadingOverlay()
        .untilReady();
```

Adaptive waits can use:

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

Documentation:

- [Smart Wait Engine v2](docs/smart-wait-engine-v2.md)
- [QATRA Adaptive Wait Engine](docs/qatra-adaptive-wait-engine.md)
- [Selenium / SHAFT Wait Analysis](docs/selenium-shaft-wait-analysis.md)
- [Adaptive Wait Comparison](docs/qatra-adaptive-wait-comparison.md)
- [Adaptive Wait Roadmap](docs/qatra-adaptive-wait-roadmap.md)

---

## Adaptive Element Actions

QATRA exposes adaptive element actions that use the Adaptive Wait Engine internally.

These methods are designed for dynamic enterprise web applications where a normal Selenium action may fail because the element is visible but not truly ready yet.

```java
driver()
        .element()
        .adaptiveType(By.id("arabic-name"), "منشأة تجريبية")
        .adaptiveClick(By.id("save"));
```

Additional adaptive actions:

```java
driver().element().adaptiveClear(By.id("name"));
driver().element().adaptiveAppend(By.id("notes"), "تم التحديث");
driver().element().adaptiveTypeAndPress(By.id("search"), "منشأة", Keys.ENTER);
driver().element().adaptiveSelectByText(By.id("city"), "الرياض");
driver().element().adaptiveHover(By.id("menu"));
driver().element().adaptiveDoubleClick(By.id("row"));
driver().element().waitUntilArabicTextReady(By.id("title"), "تسجيل الدخول");
```

Documentation: [Adaptive Element Actions](docs/adaptive-element-actions.md)

---

## Web Assertion Engine

QATRA includes a focused Web Assertion Engine for cleaner, maintainable, and diagnostics-friendly web assertions.

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

Reports are saved under:

```text
target/qatra-reports/assertions
target/qatra-reports/screenshots
target/qatra-reports/page-source
target/qatra-reports/browser-logs
```

Documentation: [Assertion Failure Diagnostics](docs/assertion-failure-diagnostics.md)

---

## Locator Intelligence

QATRA helps teams improve locator quality before failures happen.

```java
LocatorAdvisorReport report = ProactiveLocatorQualityAdvisor.analyze(
        "Save request button",
        By.xpath("/html/body/div[3]/div[2]/button[1]")
);

System.out.println(report.score());
System.out.println(report.riskLevel());
System.out.println(report.recommendations());
```

QATRA can detect locator risks such as:

- Absolute XPath
- Index-based selectors
- Generated framework classes
- Dynamic IDs
- Text-only primary locators
- Missing `data-testid` or semantic attributes
- Weak fallback locator chains

Locator quality reports can be exported under:

```text
target/qatra-reports/locators
```

Documentation: [Locator Quality Advisor](docs/locator-quality-advisor.md)

---

## Safe Self-Healing Locators

QATRA includes a controlled self-healing locator engine for real-world UI changes.

The engine tries the primary locator first, then evaluates explicit fallback locators such as `data-testid`, `aria-label`, Arabic visible text, English visible text, CSS, or XPath fallbacks.

```java
QatraLocator saveButton = QatraLocator.primary(By.id("saveBtn"))
        .named("Save button")
        .fallbackDataTestId("save-button")
        .fallbackText("حفظ")
        .fallbackText("Save")
        .build();

driver()
        .element()
        .smartClick(saveButton);
```

QATRA evaluates fallback candidates using:

- Confidence scoring
- Risk classification
- Visible text evidence
- Arabic text evidence
- Accessibility role hints
- Semantic expectations
- Configurable healing modes
- Guardrails against unsafe healing

Documentation: [Self-Healing Locator Engine](docs/self-healing-locator-engine.md)

---

## Healing Modes and Guardrails

QATRA self-healing is designed to be controlled and reviewable, not blind.

Supported healing modes:

```text
OFF
REPORT_ONLY
SUGGEST_ONLY
SAFE_AUTO_HEAL
AUTO_HEAL
STRICT_APPROVAL
```

```java
QatraHealingOptions options = QatraHealingOptions.builder()
        .mode(HealingMode.SAFE_AUTO_HEAL)
        .safeAutoHealMinimumConfidence(90)
        .maximumAutoHealRisk(HealingRiskLevel.LOW)
        .failOnAmbiguousCandidates(true)
        .blockDisabledCandidates(true)
        .build();
```

QATRA blocks healing when a fallback candidate may hide a real product bug, such as:

- Ambiguous matches
- Hidden elements
- Disabled buttons
- Covered elements
- Missing semantic evidence
- Risky candidate selection

> Healing should help teams investigate locator weakness. It should not silently hide real product bugs.

Documentation: [Healing Modes and Guardrails](docs/healing-modes-guardrails.md)

---

## Arabic Semantic Locator Healing

QATRA can use Arabic business intent as part of its locator recovery strategy.

Supported action intent examples:

```text
save     → حفظ، حفظ الطلب، حفظ البيانات
submit   → إرسال، إرسال الطلب، تقديم الطلب
approve  → اعتماد، موافقة، قبول
reject   → رفض، إرجاع، عدم الموافقة
cancel   → إلغاء، تراجع، إغلاق
search   → بحث، استعلام
```

```java
QatraLocator saveButton = QatraLocator.primary(By.id("old-save-btn"))
        .named("Save request button")
        .expectedRole("button")
        .semanticArabicAction("save")
        .fallbackDataTestId("submit-request")
        .fallbackArabicAction("save")
        .build();

driver()
        .element()
        .smartClick(saveButton);
```

Arabic label-based healing for fields:

```java
QatraLocator facilityName = QatraLocator.primary(By.id("facility-name"))
        .named("Facility name field")
        .expectedRole("textbox")
        .expectedArabicText("اسم المنشأة")
        .fallbackArabicLabel("اسم المنشأة")
        .build();
```

Documentation: [Arabic Semantic Locator Healing](docs/arabic-semantic-locator-healing.md)

---

## Accessibility Locator Healing

QATRA can use accessibility identity as part of locator recovery.

It can use stable semantic signals such as:

- `role`
- `aria-label`
- `aria-labelledby`
- Associated labels
- Placeholders
- Titles
- Native element roles
- Arabic accessible names

```java
QatraLocator saveButton = QatraLocator.primary(By.id("old-save-button"))
        .named("Save request button")
        .expectedRole("button")
        .expectedAccessibleName("حفظ الطلب")
        .fallbackRoleAndAccessibleName("button", "حفظ الطلب")
        .build();

driver()
        .element()
        .smartClick(saveButton);
```

Documentation: [Accessibility Tree Locator Healing](docs/accessibility-tree-locator-healing.md)

---

## Arabic Component Self-Healing

QATRA can resolve common Arabic/RTL web components by business intent instead of relying only on fragile selectors.

```java
driver()
        .componentHealing()
        .dropdown("المدينة")
        .selectArabicText("الرياض");

driver()
        .componentHealing()
        .tableContaining("منشأة تجريبية")
        .assertRowContainsArabicText("منشأة تجريبية");

driver()
        .componentHealing()
        .modalButton("تأكيد الحفظ", "تأكيد")
        .click();
```

Supported component intents include:

- Arabic dropdowns
- Tables
- Modals
- Toast / status messages
- Date pickers
- Label-based input fields

Documentation: [Arabic Component Self-Healing](docs/arabic-component-self-healing.md)

---

## Healing Reports and Patch Suggestions

QATRA turns healing decisions into reviewable QA evidence.

When a fallback locator is safely used, QATRA can export:

```text
target/qatra-reports/healing/healing-report-latest.html
target/qatra-reports/healing/healing-report-latest.json
target/qatra-reports/healing/healing-report-latest.txt
target/qatra-reports/healing/locator-patches.json
target/qatra-reports/healing/history/index.html
target/qatra-reports/healing/patch-workflow/qatra-healing-review.html
```

Reports may include:

- Primary locator
- Resolved locator
- Confidence score
- Risk level
- Decision summary
- Candidate attempts
- Rejected candidates
- Suggested permanent locator replacement
- Human review checklist
- Patch suggestions

Example patch suggestion:

```json
{
  "locatorName": "Save request button",
  "oldLocator": "By.id: old-save-button",
  "newLocator": "By.cssSelector: [data-testid='save-request']",
  "confidence": "92%",
  "risk": "LOW",
  "suggestedJavaCode": "By.cssSelector(\"[data-testid='save-request']\")"
}
```

QATRA intentionally does not rewrite source code automatically. It provides human-reviewable suggestions so teams can improve Page Object locators safely.

Documentation:

- [Healing Reports and Patch Suggestions](docs/healing-reports-patch-suggestions.md)
- [Advanced Healing Reports](docs/advanced-healing-reports.md)
- [IDE-Free Code Patch Strategy](docs/ide-code-patch-strategy.md)

---

## Web Component Layer

QATRA includes a web-focused component layer for dynamic UI widgets commonly found in enterprise and Arabic/RTL systems.

Supported component helpers include:

- Dropdown
- Table
- Modal
- Toast
- Loading overlay

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

## Real-World Web Smoke Examples

QATRA includes practical web smoke examples that show how to use the framework in realistic Arabic/RTL scenarios without depending on external systems.

The examples demonstrate:

- Arabic login page smoke testing
- Adaptive actions with loading overlays
- Arabic/RTL assertion checks
- RTL full-page scanning and reporting
- Dropdown, table, and toast component workflows
- Screenshot and diagnostics evidence capture

Run the local examples:

```bash
mvn -pl qatra-web -am clean test -Dtest=QatraWebRealWorldExamplesTest -Dqatra.env=local -Dqatra.browser=chrome -Dqatra.headless=true -Dsurefire.failIfNoSpecifiedTests=false
```

External-site examples should remain safe and lightweight:

- Do not run load testing.
- Do not run security testing.
- Do not submit production forms.
- Do not crawl heavily.
- Use smoke validation, RTL scan, screenshots, and diagnostics only.

Documentation: [Real-World Web Smoke Examples](docs/real-world-web-smoke-examples.md)

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

QATRA can collect evidence that helps teams investigate failures faster:

```text
target/qatra-reports/screenshots
target/qatra-reports/page-source
target/qatra-reports/browser-logs
target/qatra-reports/rtl
target/qatra-reports/assertions
target/qatra-reports/healing
target/qatra-reports/locators
allure-results
```

Evidence may include:

- Screenshots
- Page source
- Browser console logs
- Browser state
- RTL scan reports
- Assertion failure diagnostics
- Healing decision reports
- Locator patch suggestions
- Allure steps and attachments

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

## Documentation

Start here:

| Topic | Guide |
|---|---|
| Getting Started | [docs/getting-started.md](docs/getting-started.md) |
| Architecture | [docs/architecture.md](docs/architecture.md) |
| Web Testing | [docs/web-testing.md](docs/web-testing.md) |
| Smart Waits | [docs/smart-wait-engine-v2.md](docs/smart-wait-engine-v2.md) |
| Adaptive Waits | [docs/qatra-adaptive-wait-engine.md](docs/qatra-adaptive-wait-engine.md) |
| Adaptive Actions | [docs/adaptive-element-actions.md](docs/adaptive-element-actions.md) |
| Web Assertions | [docs/web-assertion-engine.md](docs/web-assertion-engine.md) |
| Assertion Diagnostics | [docs/assertion-failure-diagnostics.md](docs/assertion-failure-diagnostics.md) |
| RTL Testing | [docs/rtl-testing.md](docs/rtl-testing.md) |
| Web Components | [docs/web-component-layer.md](docs/web-component-layer.md) |
| Page Object Model | [docs/page-object-model.md](docs/page-object-model.md) |
| Data Driven Testing | [docs/data-driven-testing.md](docs/data-driven-testing.md) |
| Locator Quality Advisor | [docs/locator-quality-advisor.md](docs/locator-quality-advisor.md) |
| Self-Healing Locators | [docs/self-healing-locator-engine.md](docs/self-healing-locator-engine.md) |
| Arabic Semantic Healing | [docs/arabic-semantic-locator-healing.md](docs/arabic-semantic-locator-healing.md) |
| Accessibility Healing | [docs/accessibility-tree-locator-healing.md](docs/accessibility-tree-locator-healing.md) |
| Healing Modes | [docs/healing-modes-guardrails.md](docs/healing-modes-guardrails.md) |
| Healing Reports | [docs/healing-reports-patch-suggestions.md](docs/healing-reports-patch-suggestions.md) |
| Advanced Healing Reports | [docs/advanced-healing-reports.md](docs/advanced-healing-reports.md) |
| IDE-Free Patch Strategy | [docs/ide-code-patch-strategy.md](docs/ide-code-patch-strategy.md) |
| Arabic Component Healing | [docs/arabic-component-self-healing.md](docs/arabic-component-self-healing.md) |
| Real-World Examples | [docs/real-world-web-smoke-examples.md](docs/real-world-web-smoke-examples.md) |
| CI/CD | [docs/ci-cd.md](docs/ci-cd.md) |
| Coverage Matrix | [docs/coverage-matrix.md](docs/coverage-matrix.md) |
| Roadmap | [docs/roadmap.md](docs/roadmap.md) |

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

## Roadmap

Near-term focus:

```text
Phase 3.19  README and documentation cleanup
Phase 3.20  First web alpha release
Phase 3.21  API stabilization for locator intelligence
Phase 3.22  More real-world Arabic/RTL examples
Phase 3.23  Report dashboard improvements
```

Future direction:

- Stable `qatra-web` alpha release
- Maven Central publishing
- Standalone examples project
- More component helpers
- More Arabic/RTL quality rules
- Reporting module
- API module maturity
- Mobile and database modules
- AI-assisted testing features

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

5. **Keep healing safe**  
   Self-healing should be explainable, reviewable, and protected by guardrails.

6. **Keep the API practical**  
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

## Repository Topics

Suggested GitHub topics:

```text
java selenium testng test-automation qa-automation web-testing rtl arabic-testing fluent-api self-healing-locators accessibility-testing open-source
```

Suggested repository description:

```text
Arabic-first Java Selenium automation engine for Web testing, RTL validation, smart waits, locator intelligence, diagnostics, and safe self-healing.
```

---

## License

MIT © QATRA Community

