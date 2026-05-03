# RTL Parallel Configuration Isolation Fix

This patch removes JVM-wide `System.setProperty(...)` mutations from `SampleQatraTest` RTL tests.

## Why

The `parallel-web` profile runs TestNG methods concurrently. JVM system properties are global, not thread-local. When one RTL test temporarily disabled `qatra.rtl.scan.digits`, another parallel RTL scanner test could read that value and fail with:

```text
Digits issue should be detected. expected [true] but found [false]
```

## What changed

- Added `RtlScanConfig.builder()` and `RtlScanConfig#toBuilder()`.
- Updated RTL sample tests to pass local immutable scanner configs via `driver().rtl().withConfig(config)`.
- Removed global property mutation from RTL sample tests.
- Kept all production configuration support unchanged for real users.

## Benefit

RTL tests are now safer under method-level parallel execution, and advanced users can create local scanner configurations without affecting other tests in the same JVM.
