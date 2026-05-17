# Arabic Component Self-Healing

QATRA Arabic Component Self-Healing resolves UI components by **business intent** instead of relying only on brittle CSS, XPath, or generated IDs.

This is designed for Arabic/RTL enterprise systems where dropdowns, tables, modals, toasts, date pickers, and input fields are often rendered as custom components by modern UI frameworks.

## Why this matters

Traditional self-healing usually works at the element level:

```text
Find element → primary locator fails → try fallback locator
```

QATRA adds a component-aware layer:

```text
Find Arabic dropdown for المدينة
Find table containing منشأة تجريبية
Find modal containing تأكيد الحفظ
Find toast containing تم الحفظ بنجاح
Find input field labeled اسم المنشأة
```

This makes the healing decision closer to the tester's business intent.

## API Examples

### Arabic Dropdown

```java
driver()
        .componentHealing()
        .dropdown("المدينة")
        .selectArabicText("الرياض")
        .assertSelectedArabicText("الرياض");
```

### Arabic Table

```java
driver()
        .componentHealing()
        .tableContaining("منشأة تجريبية")
        .withRows(By.cssSelector("tbody tr"))
        .waitUntilRowsLoaded(2)
        .assertRowContainsArabicText("منشأة تجريبية");
```

### Arabic Modal Action Button

```java
driver()
        .componentHealing()
        .modalButton("تأكيد الحفظ", "تأكيد")
        .click();
```

### Arabic Input Field by Label

```java
WebElement input = driver()
        .componentHealing()
        .inputByArabicLabel("اسم المنشأة");

input.sendKeys("منشأة تجريبية");
```

## Supported Component Intents

- Arabic dropdowns
- Arabic input fields
- Arabic date pickers
- Arabic dynamic tables
- Arabic modal dialogs
- Arabic modal action buttons
- Arabic toast/status messages

## Signals Used

QATRA uses several locator signals to resolve components:

- `role`
- `aria-label`
- `aria-labelledby`
- associated labels
- placeholder
- title
- visible Arabic text
- common component classes
- nearby Arabic labels
- table row text
- modal title/content
- toast/status message content

## Safety

Component healing still goes through the existing QATRA healing pipeline:

- fallback scoring
- confidence calculation
- risk classification
- healing modes
- guardrails
- reports
- patch suggestions

This avoids unsafe healing that may hide real bugs.

## Main Classes

```text
io.github.qatra.web.locators.healing.components.ComponentLocatorStrategy
io.github.qatra.web.locators.healing.components.QatraComponentHealing
io.github.qatra.web.locators.healing.components.ComponentHealingContext
io.github.qatra.web.locators.healing.components.ComponentType
```

## Recommended Use

Use component healing when selectors are unstable but the business intent is stable.

Good examples:

```text
المدينة dropdown
اسم المنشأة input
تأكيد الحفظ modal
تم الحفظ بنجاح toast
منشأة تجريبية table row
```

Avoid using component healing to bypass real product bugs. If a component is disabled, hidden, or blocked by an overlay, QATRA guardrails should stop unsafe healing.
