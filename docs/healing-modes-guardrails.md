# QATRA Healing Modes & Bug-Safety Guardrails

QATRA self-healing is intentionally designed to avoid hiding real product bugs.
A locator fallback should not be used blindly just because it matched an element.

## Supported Modes

| Mode | Behavior | Recommended Use |
|---|---|---|
| `OFF` | Healing is disabled. Only the primary locator is used. | Strict release validation |
| `REPORT_ONLY` | QATRA evaluates candidates and writes reports, but does not use fallbacks. | First pilot phase |
| `SUGGEST_ONLY` | QATRA suggests a fallback, but the test still fails. | Human-reviewed locator maintenance |
| `SAFE_AUTO_HEAL` | QATRA auto-heals only when confidence is high, risk is low, and guardrails pass. | Default safe mode |
| `AUTO_HEAL` | QATRA auto-heals based on configured confidence and risk thresholds. | Mature teams with stable policies |
| `STRICT_APPROVAL` | QATRA auto-heals only candidates approved in an approval file. | Regulated or high-risk systems |

## Guardrails

QATRA blocks healing when a fallback candidate may hide a real bug:

- Candidate is hidden
- Candidate is disabled
- Candidate matches multiple elements
- Candidate risk is above the configured threshold
- Semantic hints were provided but did not match
- Strict approval is required but missing

## Example Configuration

```java
QatraHealingOptions options = QatraHealingOptions.builder()
        .mode(HealingMode.SAFE_AUTO_HEAL)
        .safeAutoHealMinimumConfidence(90)
        .maximumAutoHealRisk(HealingRiskLevel.LOW)
        .failOnAmbiguousCandidates(true)
        .maximumMatchesForAutoHeal(1)
        .blockHiddenCandidates(true)
        .blockDisabledCandidates(true)
        .build();
```

## Strict Approval File

```json
{
  "approvals": [
    {
      "locatorName": "Save request button",
      "oldLocator": "By.id: old-save-button",
      "newLocator": "By.cssSelector: [data-testid='save-request']",
      "minConfidence": 90,
      "risk": "LOW"
    }
  ]
}
```

## Reports

Mode decisions are written to:

```text
target/qatra-reports/healing/modes/healing-mode-audit-latest.json
target/qatra-reports/healing/modes/healing-mode-audit-latest.txt
```

## Why This Matters

Self-healing can become dangerous when it silently clicks the wrong element or hides a disabled-button business rule.
QATRA's direction is evidence-based healing:

1. Score the candidate.
2. Analyze risk.
3. Apply the selected healing mode.
4. Enforce guardrails.
5. Export evidence for review.
