# QATRA Self-Healing Locator Engine

## Purpose

The Self-Healing Locator Engine helps QATRA Web tests survive controlled UI locator changes without hiding the underlying problem.

It tries a primary locator first, then falls back to explicitly configured alternatives such as:

- `data-testid`
- `data-test`
- `data-qa`
- `aria-label`
- visible Arabic text
- visible English text
- CSS fallback
- XPath fallback

The goal is not to guess randomly. The goal is to provide a readable fallback chain and a healing report so teams can improve unstable locators.

## Example

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

If `By.id("saveBtn")` fails, QATRA tries the configured fallbacks and logs the locator that was used.

## Smart Actions

The engine is integrated with new `ElementActions` methods:

```java
smartFind(QatraLocator locator)
smartClick(QatraLocator locator)
smartType(QatraLocator locator, String text)
smartClear(QatraLocator locator)
smartAppend(QatraLocator locator, String text)
smartWaitUntilReady(QatraLocator locator)
smartGetText(QatraLocator locator)
```

These actions combine self-healing locator resolution with adaptive readiness checks.

## Locator Quality Advisor

QATRA also includes a locator advisor:

```java
LocatorQualityReport report = LocatorQualityAdvisor.analyze(
        By.xpath("/html/body/div[3]/div[2]/button[1]")
);

System.out.println(report.score());
System.out.println(report.riskLevel());
System.out.println(report.recommendations());
```

It flags common risks such as:

- absolute XPath
- index-based XPath
- dynamic framework-generated classes
- class-only selectors
- brittle text-only selectors

## Business Value

This feature is useful for real enterprise automation because many test failures are caused by locator drift rather than real product defects.

QATRA helps the tester answer:

- Did the primary locator fail?
- Which fallback worked?
- Is this locator weak?
- What should the team improve?
- Should developers add a stable `data-testid`?

This moves QATRA closer to being an Arabic-first Web QA Automation and Diagnostics Engine, not just a Selenium wrapper.
