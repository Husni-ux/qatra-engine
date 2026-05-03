# QATRA Web Stabilization — RTL Regex Fix

This patch fixes two RTL hardening issues found during `SampleQatraTest` execution:

1. Page-level Arabic detection failed when collected page text contained line breaks.
2. Common Arabic mojibake such as `Ø§Ù„Ù†Øµ Ø§Ù„Ø¹Ø±Ø¨ÙŠ` was not always detected by the scanner.

## Technical Change

`RtlEngine` now uses `Pattern.find()` friendly regexes instead of `Pattern.matches()` with `.*` patterns. This is more reliable for whole-page text because collected page text often contains newlines from `innerText`, `textContent`, and page source.

The Arabic mojibake detector was also widened to detect repeated UTF-8/Windows-1252 style mojibake pairs beginning with characters such as `Ø`, `Ù`, `Ã`, and `Â`.

## Expected Result

The following tests should no longer fail because of RTL regex detection:

- `rtlEngineExpansionTest`
- `rtlScannerEnhancementsTest`
