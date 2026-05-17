# Real-World Web Smoke Examples

QATRA is designed to be useful beyond synthetic Selenium demos. This guide shows how to use QATRA as a practical smoke-testing layer for Arabic and RTL web applications.

## Goal

Real-world smoke tests should answer a small set of high-value questions:

- Can the page open successfully?
- Is the core Arabic content rendered?
- Is RTL direction applied correctly?
- Are common dynamic components ready before interaction?
- Can the framework capture evidence when something fails?

The goal is not to replace full regression testing. The goal is to create a fast and reliable confidence layer.

## Recommended Starting Scope

Start with 5 to 10 high-value pages:

- Login page
- Dashboard
- Search page
- Create request page
- Approval page
- Arabic report page
- Details page
- Table/list page
- Notification/toast flow
- Settings page

## Example: Arabic Login Smoke

```java
@Test
public void arabicLoginSmoke() {
    driver().browser().navigateTo("https://your-system/login");

    driver().element()
            .waitUntilArabicTextReady(By.id("login-title"), "تسجيل الدخول")
            .adaptiveType(By.id("username"), "demo-user")
            .adaptiveType(By.id("password"), "secret")
            .adaptiveClick(By.id("login"));

    driver().expect(By.id("welcome"))
            .exists()
            .isVisible()
            .text()
                .containsArabic();

    driver().screenshot("arabic_login_smoke");
    driver().diagnostics("arabic_login_smoke");
}
```

## Example: RTL Page Scan

```java
driver().rtl()
        .assertArabicTextExists()
        .scanPage()
        .report();
```

## Optional Public Website Example

The project includes an optional class:

```text
qatra-web/src/test/java/io/github/qatra/examples/QatraZatcaSmokeExample.java
```

It is intentionally not named with the `*Test` suffix so Maven Surefire does not run it by default.

Use it only for light smoke validation:

- Open page
- Assert page loaded
- Check Arabic text exists
- Generate RTL report
- Capture screenshot and diagnostics

Do not use it for:

- Load testing
- Crawling
- Security testing
- Form submission
- Automated traffic at scale

## Local Example Tests

The project includes stable local examples under:

```text
qatra-web/src/test/java/io/github/qatra/tests/examples/QatraWebRealWorldExamplesTest.java
```

Run them with:

```bash
mvn -pl qatra-web -am clean test -Dtest=QatraWebRealWorldExamplesTest -Dqatra.env=local -Dqatra.browser=chrome -Dqatra.headless=true -Dsurefire.failIfNoSpecifiedTests=false
```

## Business Value

These examples help teams introduce QATRA gradually:

- Start with smoke testing
- Prove Arabic/RTL value
- Show better failure evidence
- Reduce flaky interactions using adaptive actions
- Create confidence before scaling automation coverage

## Definition of Done

A real-world smoke test should provide:

- Clear scenario name
- Stable page navigation
- One or two business-critical assertions
- Arabic/RTL validation where relevant
- Screenshot or diagnostics evidence
- No heavy traffic or risky actions
