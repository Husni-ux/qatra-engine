# QATRA Healing Reports & Patch Suggestions

## Purpose

This phase turns locator healing from a runtime workaround into a maintainable engineering workflow.

When QATRA safely heals a locator, it now exports evidence that helps the tester understand what happened and decide whether the Page Object should be updated permanently.

QATRA does **not** rewrite source code automatically. It generates human-reviewable patch suggestions.

## Generated artifacts

Reports are exported under:

```text
target/qatra-reports/healing
```

Current artifacts:

```text
healing-report-latest.txt
healing-report-latest.json
healing-report-latest.html
locator-patches.json
history/index.html
history/healing-*.txt
history/healing-*.json
history/healing-*.html
```

## Report contents

The healing report includes:

- locator name
- primary locator
- resolved locator
- resolved source
- healing status
- confidence score
- risk level
- decision summary
- candidate attempts
- rejected candidates
- recommendation

## Patch suggestions

When a locator is healed with a fallback, QATRA writes a suggestion to:

```text
locator-patches.json
```

Example:

```json
{
  "locatorName": "Save request button",
  "oldLocator": "By.id: old-save-button",
  "newLocator": "By.cssSelector: [data-testid='save-request']",
  "confidence": "92%",
  "risk": "LOW",
  "suggestedJavaCode": "By.cssSelector(\"[data-testid='save-request']\")"
}
```

## Why this is important

Self-healing should not hide weak locators forever.

The right workflow is:

1. QATRA heals safely during execution.
2. QATRA exports evidence.
3. The tester reviews the generated suggestion.
4. The Page Object locator is updated intentionally.
5. The test suite becomes more stable over time.

## Recommended usage

Use auto-healing only when confidence is high and risk is low or medium.

For stricter teams, use:

```properties
qatra.healing.mode=SUGGEST_ONLY
```

or:

```properties
qatra.healing.mode=STRICT_APPROVAL
```

This makes QATRA safer for enterprise environments where false positives are more dangerous than a failed test.
