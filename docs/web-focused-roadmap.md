# Web-Focused Roadmap

QATRA will continue focusing on `qatra-web` before expanding heavily into mobile, API, database, visual, or multimodal testing.

## Phase 3.13 — Web Component Layer

Goal: add reusable components for dynamic Arabic/RTL web applications.

Delivered:

- `QatraDropdown`
- `QatraTable`
- `QatraModal`
- `QatraToast`
- `QatraLoadingOverlay`
- Adaptive action methods in `ElementActions`
- WebDriver entry points: `dropdown`, `webTable`, `modal`, `toast`, `loadingOverlay`
- Component smoke tests

## Phase 3.14 — Web Assertion Engine Cleanup

Goal: split large web assertion classes into focused assertion objects without breaking the public API.

Candidate classes:

- `ElementAssert`
- `TextAssert`
- `RtlAssert`
- `EncodingAssert`
- `VisualAssert`

## Phase 3.15 — Assertion Failure Reports & Web Diagnostics

Goal: make assertion failures explain themselves with screenshots, page source, browser logs, element state, expected/actual values, RTL state, and diagnostic reports.

Delivered:

- `AssertionDiagnostics`
- Assertion evidence files
- Richer assertion failure context
- Documentation for assertion diagnostics

## Phase 3.16 — Adaptive Element Actions

Goal: integrate the Adaptive Wait Engine into daily element actions so testers can use safer interactions without manually writing wait code.

Delivered:

- `adaptiveClick`
- `adaptiveType`
- `adaptiveClear`
- `adaptiveAppend`
- `adaptiveTypeAndPress`
- `adaptiveSelectByText` / `adaptiveSelectByValue` / `adaptiveSelectByIndex`
- `adaptiveHover`, `adaptiveDoubleClick`, `adaptiveRightClick`
- `waitUntilAdaptiveReady`
- `waitUntilArabicTextReady`
- Browser smoke tests for adaptive element actions

## Phase 3.17 — Real Website Smoke Pack

Goal: provide disabled-by-default examples for safe smoke testing on real Arabic websites.

Rules:

- No crawling
- No load testing
- No form submission on public websites
- No security testing without permission
- Only smoke, RTL scan, screenshots, and diagnostics

## Phase 3.18 — Web Documentation Polish

Goal: make the web module ready for open-source users.

Deliverables:

- Web quick-start
- Web component examples
- RTL testing examples
- Smart/adaptive wait examples
- Page Object examples

## Phase 3.17 — Real-World Web Smoke Examples

Goal: provide safe, practical examples that show how QATRA can be used in real Arabic/RTL web projects.

Delivered:

- Local real-world smoke examples
- Arabic login smoke scenario
- RTL scan example
- Component workflow example
- Optional public website smoke example excluded from default test runs
- Real-world smoke testing guide
- Web pilot plan for teams

## Phase 3.18 — Web Examples & README Polish

Goal: continue improving onboarding so new contributors can understand QATRA in less than 10 minutes.

Candidate work:

- Add more examples for Page Object Model
- Add troubleshooting guide
- Add selectors strategy guide
- Add diagnostics screenshots to docs

## Self-Healing Locators

- [Self-Healing Locator Engine](self-healing-locator-engine.md)

## Phase 3.18.2 — Healing Reports & Patch Suggestions

Goal: convert runtime locator healing into a maintainable review workflow.

Delivered:

- HTML, JSON, and TXT healing reports
- Healing history index
- `locator-patches.json` for human-reviewed locator updates
- Evidence-rich decision summaries
- Test coverage for report generation


### Phase 3.18.4 — Proactive Locator Quality Advisor

Detect fragile locators before failure using score, risk, recommendations, HTML/JSON/TXT reports, and optional quality gates.

## Phase 3.18.6 — Advanced Healing Reports

Status: Added advanced healing reports, candidate comparison, decision matrix, patch suggestions, human review checklist, and dashboard artifacts.

## Phase 3.18.7 — IDE-Free Code Patch Strategy

Generate human-reviewable locator patch suggestions, approval templates, unified diff artifacts, and a future-ready workflow for IDE plugin integration without modifying source code automatically.


## Phase 3.18.8 — Accessibility Tree Locator Healing

Use role, aria-label, aria-labelledby, labels, placeholder, and accessible names for safer semantic locator healing.


## Phase 3.18.9 — Arabic Component Self-Healing

Added business-intent based healing for Arabic dropdowns, tables, modals, toast messages, date pickers, and label-based form fields. This extends QATRA self-healing from element-level fallback into component-aware recovery for Arabic/RTL enterprise systems.
