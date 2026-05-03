# QATRA Engine — Run Guide

## Requirements

- JDK 21
- Maven 3.9+
- Chrome installed

> In IntelliJ, set Project SDK and Maven Runner JRE to JDK 21.

## Run all tests

```powershell
mvn -U clean test
```

## Run only QATRA Web sample tests

```powershell
mvn -pl qatra-web -am clean test -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false
```

## What is included in this version?

- `qatra-core`: configuration, logger, enums, driver context
- `qatra-web`: Selenium 4 fluent wrapper
- `QatraBaseTest`: TestNG base class that opens and closes the browser automatically
- Automatic screenshot on failure
- Manual screenshots using `driver.screenshot("name")`
- RTL assertion using `.isRTL()`
- Stable sample tests using local `data:` HTML pages

## Screenshot output

Screenshots are saved by default under:

```text
target/qatra-reports/screenshots
```

You can change the folder in `qatra.properties`:

```properties
qatra.screenshots.dir=target/qatra-reports/screenshots
qatra.screenshots.on.failure=true
```

## Example test

```java
public class LoginTest extends QatraBaseTest {

    @Test
    public void userCanLogin() {
        driver.browser()
                .navigateTo("https://example.com/login")
                .element()
                .type(By.id("username"), "admin")
                .click(By.id("loginBtn"))
                .assertThat()
                .element(By.id("welcome")).isVisible()
                .element(By.tagName("body")).isRTL();
    }
}
```

---

## Allure Report

This version adds first-class Allure integration:

- QATRA actions appear as Allure steps.
- QATRA assertions appear as Allure steps.
- Manual screenshots are attached to Allure automatically.
- Failure screenshots and stack traces are attached automatically when using `QatraBaseTest`.

Run tests:

```powershell
mvn -pl qatra-web -am clean test -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false
```

Allure results are generated here:

```text
qatra-web/target/allure-results
```

To view the report, install Allure CLI, then run:

```powershell
allure serve qatra-web/target/allure-results
```

Or generate static HTML:

```powershell
allure generate qatra-web/target/allure-results -o qatra-web/target/allure-report --clean
```


## Phase 1.7 validation

Run the sample test class to validate browser actions, screenshots, Allure steps, RTL checks, and the new advanced element assertions:

```powershell
mvn -pl qatra-web -am clean test -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false
```

Expected result:

```text
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
```

The important new test is:

```text
advancedElementAssertionsTest
```

---

## Phase 1.9 validation

Run the sample tests:

```powershell
mvn -pl qatra-web -am clean test -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false
```

Expected result:

```text
BUILD SUCCESS
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
```

Check generated evidence folders:

```powershell
dir .\qatra-web\target\qatra-reports\screenshots
dir .\qatra-web\target\qatra-reports\page-source
dir .\qatra-web\target\qatra-reports\browser-logs
```

To verify failure evidence, temporarily enable this test in `SampleQatraTest`:

```java
@Test(enabled = true, description = "Enable manually to verify automatic screenshot on failure")
public void screenshotOnFailureDemo() { ... }
```

Then run the tests again. QATRA should generate screenshot, page source, browser logs, and Allure attachments for the failure.


## Phase 1.9 — Smart Waits & Better Element Actions

This version adds a centralized `SmartWait` layer and new fluent element methods:

```java
driver.element()
      .waitUntilPageReady()
      .waitUntilVisible(By.id("username"))
      .clearAndType(By.id("username"), "admin")
      .waitUntilClickable(By.id("loginBtn"))
      .click(By.id("loginBtn"));
```

New capabilities:

- Consistent timeout and polling behavior.
- Friendly timeout messages.
- Automatic handling for transient `NoSuchElementException` and `StaleElementReferenceException`.
- `waitUntilVisible`, `waitUntilClickable`, `waitUntilPresent`, `waitUntilInvisible`.
- `waitUntilTextContains`, `waitUntilAttributeEquals`, `waitUntilValueEquals`.
- Better element actions: `clearAndType`, `typeAndPress`, `selectByIndex`, `highlight`, `pause`, `getElementCount`.

Configuration:

```properties
qatra.wait.polling.ms=250
qatra.wait.page.ready=true
qatra.element.highlight=false
```

---

## Phase 2.2 validation — RTL Engine Expansion

Run the sample tests:

```powershell
mvn -pl qatra-web -am clean test -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false
```

Expected result:

```text
BUILD SUCCESS
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
```

The important new test is:

```text
rtlEngineExpansionTest
```

It validates:

- `driver.rtl()` fluent entry point.
- Page-level RTL assertion.
- Arabic text existence check.
- Broken Arabic encoding check.
- Full page RTL scan.
- Allure RTL scan report attachment.

To test RTL failure reporting manually, enable:

```java
@Test(enabled = true, description = "Enable manually to verify QATRA RTL scan failures")
public void rtlScanFailureDemo() { ... }
```

Then run the tests again. QATRA should fail the test and attach a readable RTL scan report to Allure.

---

## Phase 2.2 validation — RTL Page Scanner Enhancements

Run the sample tests:

```powershell
mvn -pl qatra-web -am clean test -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false
```

Expected result:

```text
BUILD SUCCESS
Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
```

The important new test is:

```text
rtlScannerEnhancementsTest
```

It validates typed RTL scan categories:

```text
DIRECTION
ENCODING
PLACEHOLDER
DIGITS
MIXED_DIRECTION
ALIGNMENT
```

---

## Phase 2.2 RTL Scanner Configuration

Run all sample tests:

```powershell
mvn -pl qatra-web -am clean test -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false
```

Run while changing scanner behavior from command line:

```powershell
mvn -pl qatra-web -am clean test -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false -Dqatra.rtl.fail-on=errors -Dqatra.rtl.scan.digits=false
```

Expected sample result:

```text
Tests run: 10
Failures: 0
Errors: 0
Skipped: 0
```

---

## Phase 2.3 validation — RTL Report Export

Run the sample tests:

```powershell
mvn -pl qatra-web -am clean test -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false
```

Expected sample result:

```text
Tests run: 11
Failures: 0
Errors: 0
Skipped: 0
```

The important new test is:

```text
rtlReportExportTest
```

Check exported RTL reports:

```powershell
dir .\qatra-web\target\qatra-reports\rtl
```

Expected files:

```text
rtl-scan-report.txt
rtl-scan-report.json
```

You can also override report export settings from Maven:

```powershell
mvn -pl qatra-web -am clean test -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false -Dqatra.rtl.report.export=true -Dqatra.rtl.report.formats=txt,json
```

## Phase 2.4 — Validate RTL HTML Reports and History

Run the sample suite:

```powershell
mvn -pl qatra-web -am clean test -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false
```

Expected result:

```text
Tests run: 12
Failures: 0
Errors: 0
Skipped: 0
```

Check exported RTL reports:

```powershell
dir .\qatra-web\target\qatra-reports\rtl
dir .\qatra-web\target\qatra-reports\rtl\history
```

Open the HTML summary:

```powershell
start .\qatra-web\target\qatra-reports\rtl\rtl-scan-report.html
start .\qatra-web\target\qatra-reports\rtl\history\index.html
```

## Phase 2.6 RTL Baseline Run

Run the full sample suite:

```powershell
mvn -pl qatra-web -am clean test -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false
```

Expected now:

```text
Tests run: 13
Failures: 0
Errors: 0
Skipped: 0
```

Open baseline comparison report:

```powershell
start .\qatra-web\target\qatra-reports\rtl\rtl-baseline-demo-comparison.html
```

## Phase 2.6 — RTL Quality Gate

Run the full sample suite:

```powershell
mvn -pl qatra-web -am clean test -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false
```

Open the quality gate report after the run:

```powershell
start .\qatra-web\target\qatra-reports\rtl\rtl-quality-gate-demo.html
```

---

## Run qatra-api Tests

From the project root:

```powershell
mvn -pl qatra-api -am clean test -Dtest=SampleQatraApiTest -Dsurefire.failIfNoSpecifiedTests=false
```

The API tests start a local HTTP server during the test run, so they do not depend on external internet websites.

Expected result:

```text
Tests run: 3
Failures: 0
Errors: 0
Skipped: 0
```

To run the whole project:

```powershell
mvn clean test
```

## Phase 3.1 run command

Run the expanded Selenium core coverage examples:

```powershell
mvn -pl qatra-web -am clean test -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false
```

Expected sample coverage now includes alerts, frames, windows/tabs, cookies, storage, shadow DOM, HTML tables, and HTML5 drag-and-drop.


---

## Phase 3.2 validation — Selenium Core Assertions & Helpers

Run the web sample tests:

```powershell
mvn -pl qatra-web -am clean test -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false
```

Expected result:

```text
BUILD SUCCESS
Tests run: 21, Failures: 0, Errors: 0, Skipped: 0
```

Important new tests:

```text
seleniumCoreAlertAndWindowAssertionsTest
seleniumCoreCookieStorageShadowTableAndPageHealthAssertionsTest
seleniumCoreDownloadAssertionsTest
```


---

## Phase 3.3 validation — Page Object Model Support

This version adds QATRA Page Object Model helpers:

- `QatraPage`
- `QatraElement`
- `QatraElementCollection`
- `@QatraFindBy`
- `QatraPageFactory`
- `driver.page(LoginPage.class)`

Run:

```powershell
mvn -pl qatra-web -am clean test -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false
```

Expected result:

```text
Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
```

The important new test is:

```text
pageObjectModelSupportTest
```

## Phase 3.6 — Retry / Parallel / Stability

Run normal sample tests:

```powershell
mvn -pl qatra-web -am clean test -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false
```

Run the parallel TestNG suite:

```powershell
mvn -pl qatra-web -am clean test -Pparallel-web -Dsurefire.failIfNoSpecifiedTests=false
```
