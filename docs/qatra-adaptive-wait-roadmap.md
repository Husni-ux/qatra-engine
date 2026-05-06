# QATRA Adaptive Wait Implementation Roadmap

## Phase 1 — Review Current Waits

- Identify all wait usage in qatra-web.
- Identify any direct `Thread.sleep` usage.
- Identify old ExpectedConditions dependency points.
- Map existing waits to QATRA adaptive conditions.

## Phase 2 — Core Adaptive Wait API

- `QatraWaitOptions`
- `QatraCondition`
- `QatraConditionResult`
- `QatraWaitReport`
- `QatraTimeoutException`
- `QatraAdaptiveWait`

## Phase 3 — DOM Conditions

- exists
- visible
- enabled
- clickable
- stable
- not covered

## Phase 4 — JavaScript and Network Conditions

- document ready
- jQuery idle
- Angular ready
- network idle fallback
- mutation stability

## Phase 5 — Arabic/RTL Conditions

- Arabic text visible
- Arabic text readable
- no broken Arabic
- no mojibake
- RTL direction applied
- mixed text direction safe

## Phase 6 — Component Conditions

- dropdown ready
- table rows loaded
- modal stable
- toast visible
- loading overlay gone

## Phase 7 — Diagnostics

- collect attempts
- capture screenshot on timeout
- include current text, CSS direction, rectangle, last exception
- export report to Allure and QATRA reports

## Phase 8 — Integration

- gradually replace old `SmartWait` internals with adaptive wait conditions
- keep public APIs backward compatible
- document migration path

## Phase 9 — Tests

- unit tests for Arabic/encoding logic
- TestNG browser tests for adaptive waits
- negative tests for mojibake
- component tests for dropdown/table/modal/toast

## Phase 10 — Documentation and Release

- README examples
- comparison with Selenium/SHAFT
- GitHub issues
- release notes

