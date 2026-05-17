# GitHub Issues — Healing Reports & Patch Suggestions

## Issue 1 — Add HTML/JSON/TXT healing reports

**Goal:** Export evidence for every healed locator.

**Acceptance Criteria:**

- Export latest report in TXT, JSON, and HTML.
- Export timestamped history reports.
- Include selected locator, confidence, risk, decision summary, attempts, rejected candidates, and recommendation.

## Issue 2 — Add locator patch suggestion file

**Goal:** Generate human-reviewable locator replacement suggestions.

**Acceptance Criteria:**

- Create `target/qatra-reports/healing/locator-patches.json`.
- Include old locator, new locator, confidence, risk, and suggested Java code.
- Never modify source files automatically.

## Issue 3 — Add healing history index

**Goal:** Make healing reports easier to browse locally.

**Acceptance Criteria:**

- Generate `history/index.html`.
- Link to timestamped HTML healing reports.

## Issue 4 — Add reviewer workflow for locator patches

**Goal:** Move from suggestions to controlled approval.

**Future Acceptance Criteria:**

- Generate approved/rejected suggestion files.
- Support review status.
- Allow future CLI or Maven plugin integration.
