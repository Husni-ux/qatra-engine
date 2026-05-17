# Accessibility Tree Locator Healing

## Purpose

QATRA's Accessibility Tree Locator Healing allows self-healing locator recovery to use semantic UI signals instead of relying only on CSS, XPath, or visible text.

This is especially useful for modern Arabic/RTL applications where stable accessibility attributes often survive UI refactoring better than generated IDs or CSS classes.

## Supported Signals

QATRA can now inspect and use:

- `role`
- `aria-label`
- `aria-labelledby`
- associated `<label for="...">`
- wrapping labels
- `placeholder`
- `title`
- `alt`
- input `value`
- visible text
- inferred native role, such as `button`, `textbox`, `link`, `checkbox`, `radio`, and `combobox`

## Example

```java
QatraLocator saveButton = QatraLocator.primary(By.id("old-save-button"))
        .named("Save request button")
        .expectedRole("button")
        .expectedAccessibleName("حفظ الطلب")
        .semanticArabicAction("save")
        .fallbackRoleAndAccessibleName("button", "حفظ الطلب")
        .build();

driver()
        .element()
        .smartClick(saveButton);
```

If the primary locator fails, QATRA can find the element using accessibility identity instead:

```html
<button role="button" aria-label="حفظ الطلب">...</button>
```

## Arabic Form Example

```java
QatraLocator facilityName = QatraLocator.primary(By.id("facility-name"))
        .named("Facility name textbox")
        .expectedRole("textbox")
        .expectedAccessibleName("اسم المنشأة")
        .fallbackLabelText("اسم المنشأة")
        .build();

driver()
        .element()
        .smartType(facilityName, "منشأة تجريبية");
```

This can resolve fields like:

```html
<label for="generatedFacilityName">اسم المنشأة</label>
<input id="generatedFacilityName" />
```

## Why This Matters

Traditional locators often break when:

- IDs become generated
- CSS classes change
- DOM structure changes
- components are wrapped differently
- absolute XPath becomes invalid

Accessibility-first locators are often more stable because they describe the business purpose of the element.

## Safety

Accessibility healing still goes through QATRA's safety model:

- confidence scoring
- risk analysis
- healing modes
- guardrails
- audit reports
- patch suggestions

QATRA does not blindly heal. It explains why the accessibility candidate was selected.
