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

## Phase 3.15 — Real Website Smoke Pack

Goal: provide disabled-by-default examples for safe smoke testing on real Arabic websites.

Rules:

- No crawling
- No load testing
- No form submission on public websites
- No security testing without permission
- Only smoke, RTL scan, screenshots, and diagnostics

## Phase 3.16 — Web Documentation Polish

Goal: make the web module ready for open-source users.

Deliverables:

- Web quick-start
- Web component examples
- RTL testing examples
- Smart/adaptive wait examples
- Page Object examples
