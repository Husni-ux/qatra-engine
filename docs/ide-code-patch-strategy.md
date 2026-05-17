# Phase 3.18.7 — IDE-Free Code Patch Strategy

QATRA now generates reviewable locator patch workflow artifacts without requiring an IntelliJ plugin.

The goal is not to modify source code automatically. The goal is to help automation engineers review healed locators, approve safe changes, and then update Page Objects intentionally.

## Why this matters

Self-healing is useful at runtime, but permanent maintainability requires updating weak or broken locators in the source code. A direct IDE plugin is useful later, but the safer first step is to generate patch suggestions as reviewable artifacts.

## Generated artifacts

When QATRA heals a locator, it can export:

```text
target/qatra-reports/healing/patch-workflow/
  qatra-healing-suggestions.json
  qatra-healing-suggestions.md
  qatra-healing-locator-patches.diff
  qatra-healing-approval-template.json
  qatra-healing-review.html
```

## Human-review workflow

1. A locator fails.
2. QATRA heals it using confidence, risk, guardrails, and Arabic/RTL validation.
3. QATRA generates a patch suggestion.
4. The automation engineer reviews the suggestion.
5. The Page Object is updated manually only if the suggestion is valid.
6. Future versions may consume the approval file or support an IntelliJ plugin.

## Safety principle

QATRA does not auto-change code.

Patch files are suggestions only. They must be reviewed because self-healing can hide real bugs if applied blindly.

## Future direction

- Map healed locators to Page Object fields.
- Generate safer diffs with source context.
- Support approved locator files.
- Add CLI commands to list and review suggestions.
- Prepare an IntelliJ plugin later using the same generated artifacts.
