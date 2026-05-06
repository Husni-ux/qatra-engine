# Phase 3.14.2 — Missing Component Conditions Compile Fix

## Problem
`QatraAdaptiveWait.java` imports two component wait conditions:

- `LoadingOverlayGoneCondition`
- `TableRowsLoadedCondition`

On the user's machine, Maven failed because these two classes were not present in the extracted project source tree.

## Fix
This phase ensures both classes are included under:

```text
qatra-web/src/main/java/io/github/qatra/web/waits/adaptive/conditions/components
```

## Validation command

```powershell
mvn -pl qatra-web -am clean test "-Dtest=QatraWebAssertionEngineTest" "-Dqatra.env=local" "-Dqatra.browser=chrome" "-Dqatra.headless=true" "-Dsurefire.failIfNoSpecifiedTests=false"
```
