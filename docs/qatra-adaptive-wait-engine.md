# QATRA Adaptive Wait Engine

## Goal

The QATRA Adaptive Wait Engine is a FluentWait-based waiting system that evaluates multiple readiness signals instead of relying on a single generic expected condition.

It is designed for Arabic/RTL web applications, government portals, enterprise systems, and modern dynamic UI frameworks.

## Readiness Layers

### 1. DOM Readiness

- Element exists.
- Element visible.
- Element enabled.
- Element clickable.
- Element not stale.

### 2. JavaScript and Network Readiness

- `document.readyState` is complete.
- jQuery active requests are zero when jQuery exists.
- Angular testabilities are stable when Angular exposes them.
- QATRA network idle signal when instrumentation is available.
- DOM mutation signature is stable for a quiet window.

### 3. Visual Stability

- Element rectangle is stable.
- Element is not covered by overlay.
- Loading overlay is gone.
- Component is not busy.

### 4. Arabic/RTL Readiness

- Arabic text is visible.
- Arabic text is readable.
- No broken Arabic characters.
- No mojibake.
- RTL direction is applied.
- Mixed Arabic/Latin content has a safe direction signal.

### 5. Custom Component Readiness

- Dropdown options loaded.
- Table rows loaded.
- Modal visible and stable.
- Toast visible and text validated.
- Loading overlays gone.

### 6. Diagnostic Layer

On timeout, QATRA should collect:

- Locator/context.
- Elapsed time.
- Polling interval.
- Failed condition.
- Last element text.
- CSS direction.
- Bounding rectangle.
- Last exception.
- Screenshot path.

## API Examples

### Arabic readiness

```java
QatraAdaptiveWait.forElement(driver, By.id("login-title"))
        .withTimeout(Duration.ofSeconds(15))
        .pollingEvery(Duration.ofMillis(100))
        .untilArabicTextReady("تسجيل الدخول");
```

### Click readiness

```java
QatraAdaptiveWait.forElement(driver, By.id("submit"))
        .untilReadyForClick();
```

### Advanced requirement builder

```java
QatraAdaptiveWait.forElement(driver, By.id("login-title"))
        .withTimeout(Duration.ofSeconds(15))
        .pollingEvery(Duration.ofMillis(100))
        .require()
            .visible()
            .enabled()
            .stable()
            .notCovered()
            .rtlDirection()
            .arabicTextReadable()
            .noMojibake()
        .untilReady();
```

### Page readiness

```java
QatraAdaptiveWait.forPage(driver)
        .withQuietWindow(Duration.ofMillis(500))
        .untilPageFullyReady();
```

### Component readiness

```java
QatraAdaptiveWait.forElement(driver, By.id("city-dropdown"))
        .untilDropdownReady(By.cssSelector("[role='option']"));
```

## Timeout Example

```text
QatraWait Timeout: readiness requirements were not satisfied for element By.id: login-title
Last failed condition: No mojibake encoding corruption
Expected: readable Arabic text
Actual: ØªØ³Ø¬ÙŠÙ„ Ø§Ù„Ø¯Ø®ÙˆÙ„
CSS direction: rtl
Element stable: true
Network idle: true
Timeout: 10 seconds
Polling: 100ms
Screenshot: target/qatra-reports/waits/screenshots/adaptive_wait_timeout.png
```

## Phase 3.12.1 Stability Note

Adaptive wait tests that use `data:` URLs must encode spaces as `%20`, not `+`. `URLEncoder` is designed for form encoding, and browsers can treat `+` literally inside a `data:text/html` URL. This can corrupt inline HTML test fixtures and make valid elements impossible to locate.

The adaptive wait engine now also records element lookup failures as wait attempts, so timeout reports show the locator/context and the last lookup exception instead of reporting zero attempts.

