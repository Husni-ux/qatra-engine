# QATRA Smart Wait Engine v2

## Purpose

QATRA Smart Wait Engine v2 moves wait logic away from thin Selenium `ExpectedConditions` usage and toward custom `FluentWait` predicates designed for Arabic/RTL websites, dynamic widgets, and evidence-rich automation.

This phase directly addresses the framework review concern that QATRA should not become only another Selenium wrapper. The wait engine now adds Arabic-first behavior that raw Selenium does not provide out of the box.

## Main Classes

```text
qatra-web/src/main/java/io/github/qatra/web/waits
├── WaitOptions.java
├── QatraWait.java
├── TextWaits.java
├── RtlWaits.java
├── EncodingWaits.java
├── ComponentWaits.java
├── PageWaits.java
├── AjaxWaits.java
└── SmartWait.java          # Backward-compatible facade
```

## Design Principles

- Use Selenium `FluentWait` with custom predicates.
- Keep timeout, polling interval, and ignored exceptions configurable.
- Avoid `Thread.sleep` in condition-based waits.
- Preserve backward compatibility through `SmartWait`.
- Add business-readable wait methods for Arabic/RTL systems.

## Example: Page Wait Fluent Chain

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

## Example: Arabic/RTL Wait

```java
QatraWait.forElement(driver().getSeleniumDriver(), By.id("title"))
        .waitUntilArabicTextIsVisible("مرحبا")
        .waitUntilTextIsNotBroken()
        .waitUntilRtlDirectionApplied()
        .waitUntilArabicTextRenderedCorrectly();
```

## Example: Custom Component Wait

```java
QatraWait.forElement(driver().getSeleniumDriver(), By.id("city-widget"))
        .waitUntilCustomComponentReady()
        .waitUntilElementIsStable()
        .waitUntilArabicTextIsVisible("الرياض");
```

## Supported Wait Methods

```text
waitUntilArabicTextIsVisible
waitUntilTextIsNotBroken
waitUntilRtlDirectionApplied
waitUntilElementIsStable
waitUntilEncodingIsValid
waitUntilCustomComponentReady
waitUntilPageIsFullyReady
waitUntilAjaxIsCompleted
waitUntilArabicTextRenderedCorrectly
```

## Business Value

Arabic and RTL systems often include dynamic government/enterprise UI components that are technically present in the DOM but not visually or linguistically ready. QATRA waits for content quality, direction, encoding, and component readiness rather than only presence or visibility.

This reduces flaky tests and makes automation more aligned with what a real Arabic-speaking user actually sees.

## Backward Compatibility

Existing QATRA code using `SmartWait` continues to work. Internally, `SmartWait` now delegates to `QatraWait`.

## Known Limitations

- Full network-idle detection is browser/CDP-specific and is not included yet.
- Custom component readiness is based on common signals like `aria-busy`, `data-loading`, `.loading`, `.spinner`, `.loader`, and `.skeleton`.
- Visual Arabic rendering validation is still logical/textual; future work may add screenshot or multimodal validation.
