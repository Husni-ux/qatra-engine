# QATRA Web Assertion Failure Diagnostics

QATRA Web assertions are designed to be readable for testers and useful for debugging.
When a focused assertion fails, QATRA now builds a diagnostic package instead of only throwing a short assertion message.

## What gets captured

For focused assertion engine failures, QATRA captures:

- Assertion name
- Locator
- Expected value
- Actual rendered value
- Detected issue explanation
- Current URL
- Page title
- Element text and attributes
- Effective direction and CSS direction
- Text alignment
- Displayed/enabled state
- Viewport and overlay coverage state
- Element rectangle
- Screenshot
- Page source
- Browser console logs
- Text diagnostic report under `target/qatra-reports/assertions`

## Example failure message

```text
QATRA Assertion Failed: No mojibake
Locator: By.id: broken
Expected: UTF-8-readable Arabic text
Actual: ØªØ³Ø¬ÙŠÙ„ Ø§Ù„Ø¯Ø®ÙˆÙ„

QATRA Assertion Diagnostics
----------------------------------------
Detected issue: Mojibake / encoding corruption detected in rendered text.
Evidence files
- Screenshot   : target/qatra-reports/screenshots/...
- Page source  : target/qatra-reports/page-source/...
- Browser logs : target/qatra-reports/browser-logs/...
```

## Why this matters

For Arabic and RTL systems, a failed assertion is often not enough. A tester needs to know whether the problem is text mismatch, RTL direction, mojibake, broken characters, viewport/overlay behavior, or DOM state.

This feature moves QATRA Web closer to being a QA evidence engine, not just a Selenium wrapper.
