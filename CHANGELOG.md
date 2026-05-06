
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
