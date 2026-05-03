# qatra-web Stabilization Notes

This patch focuses on making the existing web test suite safer for parallel execution.

## Fixed

- Updated `SampleQatraTest` to use `driver()` instead of the backward-compatible `driver` field.
- Removed duplicate Allure listener registration from the sample parallel suite path.
- Guarded Allure attachments so DataProvider-time attachments do not print `no test is running`.
- Added a Log4j-to-SLF4J bridge for Excel/Apache POI test logging noise.

## Why

The `driver` field is kept for backwards compatibility, but `driver()` is the recommended thread-safe accessor when TestNG parallel execution is enabled.
