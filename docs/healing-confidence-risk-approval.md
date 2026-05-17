# QATRA Healing Confidence, Risk & Approval Engine

This phase adds a safer decision layer on top of the Self-Healing Locator Engine.

The goal is not to blindly heal broken locators. The goal is to make every healing decision explainable, scored, risk-aware, and configurable.

## Why this matters

Self-healing can be dangerous if it clicks the wrong element. QATRA handles this by evaluating every fallback candidate through:

- confidence scoring
- risk classification
- semantic hints
- Arabic text evidence
- role/accessibility evidence
- healing mode configuration

## Healing modes

Supported modes:

- `OFF`
- `REPORT_ONLY`
- `SUGGEST_ONLY`
- `SAFE_AUTO_HEAL`
- `AUTO_HEAL`
- `STRICT_APPROVAL`

Default mode:

```properties
qatra.healing.mode=SAFE_AUTO_HEAL
qatra.healing.min-confidence=75
qatra.healing.max-auto-risk=MEDIUM
```

## Example

```java
QatraLocator saveButton = QatraLocator.primary(By.id("old-save-btn"))
        .named("Save request button")
        .expectedRole("button")
        .expectedArabicText("حفظ الطلب")
        .expectedAction("save")
        .fallbackDataTestId("save-request")
        .fallbackText("حفظ الطلب")
        .build();

driver().element().smartClick(saveButton);
```

## Report output

When a fallback is used, QATRA includes decision details such as:

- selected locator
- confidence score
- risk level
- visible text
- role
- CSS direction
- candidate reason
- rejected candidates
- recommendations

This positions QATRA as a controlled healing engine, not a blind locator guessing tool.
