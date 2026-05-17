# QATRA Locator Quality Advisor

The Locator Quality Advisor is a proactive quality layer for Selenium locators and QATRA self-healing locator chains.

Its goal is simple: **detect fragile locators before they break during regression**.

## Why this matters

Most UI automation failures are caused by brittle selectors:

- absolute XPath
- index-based selectors
- framework-generated CSS classes
- dynamic IDs
- text-only locators used as primary selectors
- missing stable attributes such as `data-testid`

Traditional self-healing fixes the problem after the locator breaks. QATRA goes one step earlier by warning the team before the locator becomes a failure.

## Basic usage

```java
LocatorAdvisorReport report = ProactiveLocatorQualityAdvisor.analyze(
        "Save request button",
        By.xpath("/html/body/div[3]/div[2]/button[1]")
);

System.out.println(report.score());
System.out.println(report.riskLevel());
System.out.println(report.recommendations());
```

## Fluent usage from QATRA WebDriver

```java
driver()
        .locatorAdvisor()
        .analyze("Save request button", By.cssSelector("[data-testid='save-request']"));
```

## QatraLocator chain analysis

```java
QatraLocator saveButton = QatraLocator.primary(By.id("old-save-btn"))
        .named("Save Arabic request button")
        .expectedRole("button")
        .expectedArabicText("حفظ الطلب")
        .semanticArabicAction("save")
        .fallbackDataTestId("save-request")
        .fallbackText("حفظ الطلب")
        .build();

LocatorAdvisorReport report = saveButton.advisorReport();
```

## Quality Gate

You can use the advisor as a quality gate in tests or CI:

```java
LocatorQualityGate.withOptions(
        LocatorAdvisorOptions.builder()
                .minimumScore(80)
                .failOnCritical(true)
                .build()
).require(report);
```

## Reports

QATRA exports proactive locator quality reports to:

```text
target/qatra-reports/locators
```

Generated files include:

```text
locator-quality-latest.html
locator-quality-latest.json
locator-quality-latest.txt
history/index.html
```

## What QATRA checks

- absolute XPath risk
- index-based selector risk
- dynamic IDs
- generated Angular/React/MUI/CSS-in-JS tokens
- class-only locators
- text-only primary locators
- long selectors
- missing business-readable signal
- presence of stable fallbacks
- Arabic semantic hints
- expected role and expected Arabic text

## Recommended strategy

Use this order when possible:

1. `data-testid`
2. stable `id`
3. `aria-label` / accessible name
4. semantic role + accessible name
5. nearby Arabic label
6. Arabic visible text as controlled fallback
7. short relative XPath only when needed

Avoid:

- absolute XPath
- generated classes
- layout indexes
- primary selectors based only on visible text
