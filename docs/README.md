# QATRA Documentation

Welcome to the QATRA Engine documentation.

## Start Here

1. [Getting Started](getting-started.md)
2. [Architecture](architecture.md)
3. [Configuration Reference](configuration-reference.md)

## Main Guides

- [Web Testing Guide](web-testing.md)
- [RTL Testing Guide](rtl-testing.md)
- [API Testing Guide](api-testing.md)
- [Page Object Model Guide](page-object-model.md)
- [Data Driven Testing Guide](data-driven-testing.md)
- [Stability, Retry, and Parallel Execution](stability.md)

## Project and Release

- [CI/CD Guide](ci-cd.md)
- [Configuration Management](configuration-management.md)
- [Coverage Matrix](coverage-matrix.md)
- [Examples Index](examples-index.md)
- [Roadmap](roadmap.md)
- [Release Checklist](release-checklist.md)

- [Consumer Installation](consumer-installation.md)
- [Maven Central Publishing](maven-central-publishing.md)
- [Release Process](release-process.md)
- [Namespace Decision](namespace-decision.md)

## Documentation Rules

- Keep examples small and runnable.
- Prefer Java 21 syntax.
- Prefer stable locators such as `id`, `data-testid`, and accessibility-friendly attributes.
- Keep Arabic/RTL testing visible in public examples.
- Update docs whenever public APIs change.

- [Smart Wait Engine v2](smart-wait-engine-v2.md)

## Adaptive Wait Engine Docs

- [Selenium vs SHAFT Wait Analysis](selenium-shaft-wait-analysis.md)
- [QATRA Adaptive Wait Engine](qatra-adaptive-wait-engine.md)
- [Wait Capability Comparison](qatra-adaptive-wait-comparison.md)
- [Adaptive Wait Roadmap](qatra-adaptive-wait-roadmap.md)
- [Adaptive Wait GitHub Issues](github-issues-adaptive-wait.md)
- [README Adaptive Wait Draft](README-improvement-adaptive-waits.md)

- [Adaptive Element Actions](adaptive-element-actions.md)

## Self-Healing Locators

- [Self-Healing Locator Engine](self-healing-locator-engine.md)

## Healing Reports

- [Healing Confidence, Risk & Approval](healing-confidence-risk-approval.md)
- [Healing Reports & Patch Suggestions](healing-reports-patch-suggestions.md)

- [Arabic Semantic Locator Healing](arabic-semantic-locator-healing.md)


- [Locator Quality Advisor](locator-quality-advisor.md) — proactive locator scoring, risk analysis, and quality gate support.

- [Healing Modes & Bug-Safety Guardrails](healing-modes-guardrails.md)
- [Advanced Healing Reports](advanced-healing-reports.md)
- [Advanced Healing Report Issues](github-issues-advanced-healing-reports.md)

- [IDE-Free Code Patch Strategy](ide-code-patch-strategy.md)
- [GitHub Issues — Code Patch Strategy](github-issues-code-patch-strategy.md)

- [Accessibility Tree Locator Healing](accessibility-tree-locator-healing.md)


## Phase 3.18.9 — Arabic Component Self-Healing

Added business-intent based healing for Arabic dropdowns, tables, modals, toast messages, date pickers, and label-based form fields. This extends QATRA self-healing from element-level fallback into component-aware recovery for Arabic/RTL enterprise systems.
