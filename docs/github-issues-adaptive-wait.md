# GitHub Issues — QATRA Adaptive Wait Engine

## Epic

1. Implement QATRA Adaptive Wait Engine with diagnostic-rich FluentWait conditions

## Core

2. Add `QatraWaitOptions` for timeout, polling, quiet window, diagnostics, and ignored exceptions
3. Add `QatraCondition` and `QatraConditionResult`
4. Add `QatraWaitReport` and `QatraTimeoutException`
5. Add screenshot capture on wait timeout

## DOM / Visual

6. Add DOM readiness conditions
7. Add element stability condition using rectangle quiet window
8. Add not-covered condition using JavaScript hit testing
9. Add loading overlay gone condition

## JavaScript / Network

10. Add document ready condition
11. Add jQuery idle condition
12. Add Angular readiness condition
13. Add network idle fallback condition
14. Add mutation stability condition

## Arabic / RTL

15. Add Arabic text visible condition
16. Add Arabic text readable condition
17. Add no broken Arabic condition
18. Add no mojibake condition
19. Add RTL direction condition
20. Add mixed Arabic/Latin direction safety condition

## Components

21. Add dropdown ready condition
22. Add table rows loaded condition
23. Add modal stable condition
24. Add toast visible condition

## Tests

25. Add TestNG smoke tests for adaptive waits
26. Add negative tests for corrupted Arabic/mojibake
27. Add real website smoke example with safe scope

## Documentation

28. Add Selenium vs SHAFT wait comparison
29. Add QATRA Adaptive Wait Engine documentation
30. Add README examples for Arabic readiness waits

