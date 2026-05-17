# Phase 3.18.6 — Advanced Healing Reports

QATRA healing reports are designed to be stronger than a simple old-locator/new-locator summary.

The goal is to make every self-healing decision reviewable by a QA engineer before the permanent Page Object locator is changed.

## What QATRA exports

When a locator is healed, QATRA exports a complete evidence package under:

```text
target/qatra-reports/healing
```

Artifacts include:

```text
healing-dashboard-latest.html
healing-report-latest.html
healing-report-latest.json
healing-report-latest.txt
human-review-checklist-latest.md
candidate-comparison-latest.csv
healing-decision-matrix-latest.json
locator-patches.json
locator-patches.md
history/index.html
history/healing-*.html
history/healing-*.json
history/healing-*.txt
```

## Why this matters

Self-healing can be dangerous when it silently uses the wrong element and hides a real bug. QATRA avoids blind healing by exporting:

- Primary locator
- Healed locator
- Confidence score
- Risk level
- Decision summary
- Candidate attempts
- Rejected candidates
- Human review checklist
- Suggested Java locator replacement
- JSON decision matrix for CI or review tools

## Human review workflow

1. QATRA detects that the primary locator failed.
2. QATRA evaluates fallback candidates using confidence and risk rules.
3. QATRA heals only when the selected mode and guardrails allow it.
4. QATRA exports a full evidence package.
5. The automation engineer reviews the checklist and patch suggestion.
6. The Page Object locator is updated only after human approval.

## Example output

```text
Primary locator: By.id: old-save-button
Resolved locator: By.cssSelector: [data-testid='save-request']
Confidence: 92%
Risk: LOW
Review: Low-risk auto-heal
```

## Permanent fix suggestion

QATRA generates locator patches in JSON and Markdown. These are intentionally not applied automatically.

```java
By.cssSelector("[data-testid='save-request']")
```

This keeps QATRA safe for government, banking, and enterprise systems where automation must not hide product defects.
