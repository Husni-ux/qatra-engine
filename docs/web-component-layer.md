# QATRA Web Component Layer

QATRA's next web-focused enhancement is a reusable component layer for modern web applications.

This layer is not a replacement for Page Objects. It is a small set of reusable helpers for common widgets that are often unstable in enterprise Arabic/RTL systems.

## Why this matters

Raw Selenium can click and type, but modern applications often use custom components instead of standard HTML controls. Arabic government and enterprise systems commonly use:

- Custom dropdowns
- Dynamic tables
- Modal dialogs
- Toast notifications
- Loading overlays
- Arabic input fields

The QATRA component layer uses `QatraAdaptiveWait` internally, so each component can wait for web-specific readiness signals before performing the action.

## Current components

```text
io.github.qatra.web.components
├── QatraComponent
├── QatraDropdown
├── QatraTable
├── QatraModal
├── QatraToast
└── QatraLoadingOverlay
```

## Example: Arabic dropdown

```java
qatra.dropdown(By.id("city"))
        .selectArabicText("الرياض")
        .assertSelectedArabicText("الرياض");
```

## Example: Dynamic Arabic table

```java
qatra.webTable(By.id("visits-table"))
        .withRows(By.cssSelector("tbody tr"))
        .waitUntilRowsLoaded(2)
        .assertRowContainsArabicText("منشأة تجريبية");
```

## Example: Toast message

```java
qatra.toast(By.id("toast"))
        .successMessageContains("تم الحفظ بنجاح");
```

## Example: Adaptive web actions

```java
qatra.element()
        .adaptiveType(By.id("username"), "Husni")
        .adaptiveClick(By.id("save"));
```

## Design rule

The default Selenium-based fluent actions remain available. Adaptive actions are introduced as explicit methods first:

- `adaptiveClick`
- `adaptiveType`
- `waitUntilReadyForClick`
- `waitUntilArabicReady`

This prevents breaking existing users while allowing advanced web teams to opt into QATRA's diagnostic-rich adaptive waits.

## Next improvements

- Searchable select component
- Date picker component
- Grid/table filtering helpers
- Modal button matching by Arabic text and role
- Component-level screenshots on failure
- RTL-specific component readiness rules
