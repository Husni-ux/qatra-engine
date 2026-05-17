# Adaptive Element Actions

Phase 3.16 integrates the QATRA Adaptive Wait Engine into daily web element actions.

## Goal

The Adaptive Wait Engine should not remain only as a separate low-level API. Testers should benefit from it directly when interacting with real web pages.

Instead of writing a manual wait followed by an action, QATRA provides adaptive actions that wait for real readiness before interacting with the element.

## Why this matters

Traditional Selenium actions can fail even when an element exists in the DOM. Common real-world causes include:

- Loading overlays still covering the element
- Animation or layout movement
- Element visible but not enabled yet
- Dynamic components rendering late
- Arabic text not fully rendered
- Custom dropdowns and tables not ready

Adaptive actions help reduce flaky tests by checking multiple readiness signals before performing the action.

## Public API

```java
driver().element().adaptiveClick(By.id("save"));
driver().element().adaptiveType(By.id("name"), "منشأة تجريبية");
driver().element().adaptiveClear(By.id("name"));
driver().element().adaptiveAppend(By.id("notes"), "تم التحديث");
driver().element().adaptiveTypeAndPress(By.id("search"), "منشأة", Keys.ENTER);
driver().element().adaptiveSelectByText(By.id("city"), "الرياض");
driver().element().adaptiveHover(By.id("menu"));
driver().element().adaptiveDoubleClick(By.id("row"));
driver().element().waitUntilArabicTextReady(By.id("title"), "تسجيل الدخول");
```

## Readiness model

Adaptive element actions use QATRA Adaptive Wait internally and may evaluate:

- Element visibility
- Element enabled state
- Element stability
- Overlay coverage
- Loading overlay disappearance
- Arabic text readiness where applicable

## Backward compatibility

Existing methods such as `click`, `type`, `selectByText`, and `hover` remain available.

Adaptive methods are explicit and opt-in during the alpha phase. This avoids changing existing behavior suddenly while giving teams a safer option for dynamic pages.

## Recommended usage

Use normal actions for simple static pages:

```java
driver().element().click(By.id("submit"));
```

Use adaptive actions for dynamic enterprise screens, Arabic/RTL pages, or components affected by loaders and animations:

```java
driver().element().adaptiveClick(By.id("submit"));
```

## Future direction

A future QATRA configuration option may allow teams to make adaptive actions the default strategy for all element interactions.
