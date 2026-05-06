# Phase 3.14 — QATRA Web Assertion Engine Cleanup

## Goal

This phase keeps the focus on `qatra-web` and starts cleaning the assertion architecture without breaking the existing fluent API.

The old `WebAssertions.ElementAssertions` API remains available for backward compatibility. The new package gives QATRA a cleaner assertion engine that can grow independently:

```text
io.github.qatra.web.assertions.engine
```

## New classes

```text
QatraAssert
ElementAssert
TextAssert
RtlAssert
EncodingAssert
VisualAssert
AssertionEvidence
AssertionFailure
```

## Why this matters

QATRA should not become only a Selenium wrapper. The assertion layer is one of the places where QATRA can clearly add Arabic-first testing value:

- business-readable assertion failures
- Arabic text validation
- RTL direction validation
- mojibake and broken Arabic detection
- Unicode normalization checks
- visual state checks such as viewport and covered-element detection

## New API examples

```java
QatraAssert.that(driver, By.id("login-title"))
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
            .isInsideViewport()
            .notCovered();
```

Convenience API from the QATRA driver:

```java
driver().expect(By.id("title"))
        .exists()
        .rtl()
        .hasValidArabicRendering();
```

Bridge from the existing assertion entry point:

```java
driver().assertThat()
        .expect(By.id("username"))
        .hasValue("husni");
```

## Design decision

This phase does **not** delete or rewrite the old assertion API. That would be risky. Instead, it introduces the cleaner engine beside the old one and gives future phases a safe migration path.

## Next improvement

Phase 3.15 should integrate this engine more deeply with failure diagnostics:

- export assertion failure reports as HTML/JSON
- attach screenshots from assertion engine directly
- add assertion categories to Allure reports
- add assertion result objects for custom reporting
