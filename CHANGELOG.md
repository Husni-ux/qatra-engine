
## Phase 3.18.9 — Arabic Component Self-Healing

- Added component-intent based locator healing for Arabic/RTL web applications.
- Added `QatraComponentHealing` as a driver entry point through `driver().componentHealing()` and `driver().components()`.
- Added component locator strategies for Arabic dropdowns, tables, modals, toasts, date pickers, and label-based input fields.
- Added business-readable component resolution such as `dropdown("المدينة")`, `tableContaining("منشأة تجريبية")`, and `modalButton("تأكيد الحفظ", "تأكيد")`.
- Integrated component healing with the existing confidence, risk, guardrail, reporting, and patch suggestion workflow.
- Added TestNG coverage for Arabic dropdown, table, modal button, input label, and toast component healing.
- Added documentation and GitHub issue suggestions for Arabic component self-healing.


## Phase 3.18.8 — Accessibility Tree Locator Healing

- Added accessibility-tree based locator healing support.
- Added practical accessible-name resolver using ARIA, labels, placeholder, title, alt, value, and visible text.
- Added role + accessible-name fallback strategies.
- Added label text and placeholder fallback strategies for Arabic forms.
- Integrated accessibility evidence into healing confidence scoring.
- Added TestNG coverage for Arabic accessibility locator healing.


## Phase 3.18.6 — Advanced Healing Reports

- Enhanced the self-healing locator report into a full QA evidence package.
- Added `healing-dashboard-latest.html`.
- Added `human-review-checklist-latest.md`.
- Added `candidate-comparison-latest.csv`.
- Added `healing-decision-matrix-latest.json`.
- Added Markdown locator patch suggestions in `locator-patches.md`.
- Improved HTML report structure with candidate comparison and review checklist sections.
- Added documentation for advanced healing reports and suggested follow-up GitHub issues.


## Phase 3.18.5 — Healing Modes & Bug-Safety Guardrails

- Added mode-aware healing policy engine.
- Added OFF, REPORT_ONLY, SUGGEST_ONLY, SAFE_AUTO_HEAL, AUTO_HEAL, and STRICT_APPROVAL behavior.
- Added bug-safety guardrails to block hidden, disabled, or ambiguous candidates.
- Added strict approval file support for controlled healing.
- Added healing mode audit reports under `target/qatra-reports/healing/modes`.
- Added TestNG coverage for report-only mode, ambiguous candidate blocking, and strict approval.


## Phase 3.18.3 — Arabic Semantic Locator Healing

- Added Arabic action dictionary for common business actions.
- Added Arabic text normalization and semantic similarity utilities.
- Added Arabic semantic action fallback locators.
- Added Arabic nearby-label fallback for form fields.
- Integrated Arabic semantic matching into healing confidence scoring.
- Added tests for Arabic action healing and label-based field resolution.
- Added documentation for Arabic semantic locator healing.


## Phase 3.18 — Self-Healing Locator Engine

- Added `QatraLocator` for business-readable locator fallback chains.
- Added `QatraLocatorEngine` for primary and fallback locator resolution.
- Added locator healing diagnostics and resolution reports.
- Added locator quality advisor for fragile selectors.
- Added smart element actions: `smartClick`, `smartType`, `smartClear`, `smartAppend`, `smartFind`, `smartGetText`.
- Added TestNG coverage for Arabic text fallback, data-testid fallback, and locator quality scoring.
- Added documentation for self-healing locator strategy.

## Phase 3.16 - Adaptive Element Actions Integration

### Added
- Added explicit adaptive element actions powered by `QatraAdaptiveWait`.
- Added `adaptiveClear`, `adaptiveAppend`, `adaptiveSubmit`, `adaptiveHover`, `adaptiveDoubleClick`, `adaptiveRightClick`, `adaptiveScrollAndClick`, `adaptiveSelectByText`, `adaptiveSelectByValue`, `adaptiveSelectByIndex`, `adaptiveCheck`, `adaptiveUncheck`, and `adaptiveUploadFile`.
- Added `waitUntilAdaptiveReady` and `waitUntilArabicTextReady` as tester-friendly readiness APIs.
- Added `QatraAdaptiveElementActionsTest` smoke tests.
- Added documentation for adaptive element actions under `docs/adaptive-element-actions.md`.

### Improved
- Reduced duplicated adaptive wait logic in `ElementActions` by adding internal adaptive helper methods.
- Kept classic element actions backward-compatible while making adaptive behavior explicit and opt-in.


## Phase 3.15 - Web Assertion Failure Diagnostics

### Added
- Rich diagnostic reports for focused QATRA Web assertion failures.
- Automatic screenshot, page source, browser console logs, and assertion text report capture.
- Failure reason detection for mojibake, replacement characters, question-mark corruption, RTL/direction mismatch, text mismatch, attribute mismatch, and visual state mismatch.
- Documentation for assertion diagnostics under `docs/assertion-failure-diagnostics.md`.

### Improved
- Assertion failure messages now include expected value, actual rendered value, locator, browser context, element context, CSS direction, viewport state, overlay coverage state, and evidence file paths.


## Phase 3.14.1 - Web Assertion Engine Compile Fix

### Fixed
- Renamed the private helper method in `ElementAssert` from `wait()` to `seleniumWait()` because `Object.wait()` is final in Java and cannot be overridden or hidden by a zero-argument method.
- Kept the public assertion API unchanged.

# Changelog

## Phase 3.18.1 — Healing Confidence, Risk & Approval Engine

- Added configurable healing modes.
- Added fallback locator confidence scoring.
- Added healing risk classification.
- Added semantic hints to `QatraLocator`.
- Added Arabic text and role evidence in healing decisions.
- Enhanced locator healing reports with confidence, risk, and rejected candidates.
- Added tests for safe auto-healing and suggest-only mode.



## Phase 3.14 — Web Assertion Engine Cleanup

- Added focused QATRA Web Assertion Engine under `io.github.qatra.web.assertions.engine`.
- Added `QatraAssert`, `ElementAssert`, `TextAssert`, `RtlAssert`, `EncodingAssert`, and `VisualAssert`.
- Added `driver.expect(By locator)` as a cleaner assertion entry point.
- Added `driver.assertThat().expect(By locator)` bridge while preserving backward compatibility.
- Added assertion evidence and business-readable assertion failure messages.
- Added smoke tests for Arabic/RTL assertions, visual checks, and mojibake detection.

All notable changes to QATRA Engine will be documented in this file.

## 0.1.0-alpha — In progress

### Added

- `qatra-core` module for configuration, logging, enums, and driver context.
- `qatra-web` module built on Selenium 4 with fluent browser and element actions.
- Advanced web assertions.
- Screenshot capture and diagnostics evidence.
- Allure integration.
- Smart waits.
- Arabic/RTL assertions, scanner, report export, history, baseline comparison, and quality gate.
- Selenium core coverage: alerts, frames, windows, Shadow DOM, cookies, storage, tables, and drag/drop helpers.
- Page Object Model and Component Object Model support.
- Data-driven testing support for CSV, JSON, and Excel.
- Retry, parallel execution, and stability utilities.
- Environment profiles and configuration management.
- `qatra-api` starter module for REST API testing.
- GitHub Actions CI/CD workflow and release readiness workflow.
- Public README polish and structured documentation set for getting started, architecture, web, RTL, API, data, stability, configuration, examples, coverage, and roadmap.


- Maven Central publishing preparation with `central-release` profile.
- Source JAR, Javadoc JAR, GPG signing, and Central Portal bundle configuration.
- GitHub Actions workflow for manual Maven Central release preparation.
- Local release preparation scripts for Windows, Linux, and macOS.
- Maven Central publishing, release process, namespace decision, and consumer installation documentation.

- Smart Wait Engine v2 using Selenium `FluentWait` with custom predicates.
- Arabic/RTL-aware waits for Arabic text visibility, mojibake-free text, RTL direction, encoding validity, custom component readiness, Ajax completion, page readiness, and element stability.
- Browser-level `waitUntilPageReady()` now preserves fluent chaining.
- `CODE_OF_CONDUCT.md` and Smart Wait Engine v2 documentation.

### Notes

This is still an alpha-quality framework. Public APIs may change before `1.0.0`.

## Phase 3.12 — QATRA Adaptive Wait Engine Design & Initial Implementation

### Added

- Added `QatraAdaptiveWait` as a diagnostic-rich wait API built on Selenium FluentWait.
- Added adaptive wait options: timeout, polling interval, quiet window, screenshot-on-failure, diagnostics flag, ignored exceptions.
- Added condition model: `QatraCondition`, `QatraConditionResult`, `QatraWaitSignal`, `QatraConditionGroup`.
- Added diagnostics model: `WaitAttempt`, `WaitFailureReason`, `WaitEvidenceCollector`, `WaitScreenshotCapture`, `WaitDebugReport`.
- Added DOM/visual readiness conditions.
- Added JavaScript/network readiness conditions.
- Added Arabic/RTL readiness conditions.
- Added custom component readiness conditions.
- Added TestNG smoke tests for QATRA adaptive waits.
- Added documentation comparing Selenium, SHAFT, and QATRA wait strategies.

### Strategic Direction

QATRA should remain powered by Selenium while specializing in Arabic/RTL readiness, encoding safety, custom Arabic component behavior, and evidence-based timeout diagnostics.
## Phase 3.12.1 - Adaptive Wait Data URL and Diagnostics Fix

- Fixed adaptive wait smoke tests by encoding spaces as `%20` in `data:` URLs instead of `+`, which browsers treat literally in data URL HTML.
- Improved adaptive wait timeout diagnostics when element lookup fails before condition evaluation.
- Added the detailed wait debug report directly into `QatraTimeoutException` messages.

## Phase 3.12.2 — Test Data URL Encoding Stabilization

- Fixed remaining `data:text/html` test fixtures that used `URLEncoder` without converting `+` to `%20`.
- Stabilized `QatraWaitEngineTest` browser title, Arabic wait, and custom component readiness tests.
- Kept the Adaptive Wait Engine implementation unchanged; the failure was caused by malformed in-memory HTML test pages, not by Maven or Selenium.

## Phase 3.13 - Web Component Layer

### Added
- Added `io.github.qatra.web.components` package focused on reusable web UI widgets.
- Added `QatraDropdown` with Arabic text selection support for native and custom dropdowns.
- Added `QatraTable` for dynamic table row readiness and Arabic row assertions.
- Added `QatraModal` for modal open/close stability checks.
- Added `QatraToast` for Arabic toast/notification validation.
- Added `QatraLoadingOverlay` helper powered by the Adaptive Wait Engine.
- Added WebDriver shortcuts: `dropdown`, `webTable`, `modal`, `toast`, and `loadingOverlay`.
- Added explicit adaptive element methods: `adaptiveClick`, `adaptiveType`, `waitUntilReadyForClick`, and `waitUntilArabicReady`.
- Added web component smoke tests.

### Notes
- Default element actions remain backward-compatible.
- Adaptive actions are opt-in while the component layer matures.

## Phase 3.17 — Real-World Web Smoke Examples

### Added

- Added local real-world style web smoke examples for Arabic/RTL workflows.
- Added `QatraWebRealWorldExamplesTest` covering Arabic login, RTL page scan, and component-based Arabic workflow examples.
- Added optional external website smoke example `QatraZatcaSmokeExample`, intentionally excluded from default Maven test execution by class naming.
- Added documentation for real-world web smoke examples and safe public website testing rules.
- Added a practical web pilot plan for introducing QATRA in real teams.

### Notes

- External public website examples are meant for light smoke validation only.
- No load testing, security testing, crawling, or production form submissions should be performed without authorization.

## Phase 3.18.2 — Healing Reports & Patch Suggestions

### Added

- Added human-reviewable healing report export for self-healing locators.
- Added TXT, JSON, and HTML healing report outputs.
- Added timestamped healing history reports and history index.
- Added `locator-patches.json` for suggested permanent locator replacements.
- Added `HealingReportExporter`, `HealingPatchSuggestion`, and `HealingReportArtifacts`.
- Added tests for healing report and patch suggestion generation.

### Notes

QATRA does not automatically rewrite source code. It exports evidence and patch suggestions so testers can review locator changes intentionally.


## Phase 3.18.4 — Proactive Locator Quality Advisor

- Added proactive locator quality analysis before runtime failure.
- Added advanced locator scoring, risk levels, issue codes, and recommendations.
- Added QatraLocator chain analysis including fallback candidate quality.
- Added locator quality reports in HTML, JSON, and TXT formats.
- Added LocatorQualityGate for CI/test enforcement.
- Added `driver().locatorAdvisor()` fluent entry point.
- Added documentation and tests for locator quality advisor.

## Phase 3.18.7 — IDE-Free Code Patch Strategy

- Added reviewable locator patch workflow artifacts.
- Added `qatra-healing-suggestions.json` and Markdown suggestions.
- Added unified diff locator patch suggestions.
- Added approval template JSON for future human-approved healing.
- Added HTML review page for patch suggestions.
- Added lightweight Page Object source scanner foundation.
- Added tests for the patch workflow exporter.
