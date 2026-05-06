# Suggested GitHub Issues for QATRA Engine

This list translates the enhancement roadmap into practical open-source issues.

## Stabilization

1. **Stabilize qatra-web alpha candidate**
   - Verify `mvn clean verify`
   - Keep qatra-web feature-frozen except bug fixes
   - Confirm parallel tests pass

2. **Fix fluent chaining consistency across browser and element actions**
   - Ensure all fluent action methods return the expected action object
   - Add tests for chained browser waits

3. **Review public API naming before 0.1.0-alpha release**
   - Remove confusing duplicates
   - Add JavaDoc for public entry points

## Smart Wait Engine

4. **Implement FluentWait-based QatraWait engine**
   - Add `QatraWait`, `WaitOptions`, page/element waits
   - Avoid direct reliance on legacy ExpectedConditions

5. **Add Arabic/RTL smart waits**
   - Arabic text visible
   - RTL direction applied
   - Encoding valid
   - Arabic text rendered correctly

6. **Add custom component readiness waits**
   - `aria-busy`
   - `.loading`, `.spinner`, `.loader`, `.skeleton`
   - element stability

## Arabic/RTL Assertion Engine

7. **Extract Arabic assertion logic into dedicated assertion classes**
   - `ArabicTextAssert`
   - `RtlAssert`
   - `EncodingAssert`
   - `ArabicNumberAssert`

8. **Add Unicode normalization assertions**
   - NFC/NFD checks
   - replacement character checks
   - reversed text detection starter

## Custom Components

9. **Add QatraDropdown for custom/select2-like widgets**
10. **Add QatraTable for dynamic Arabic tables**
11. **Add QatraToast and QatraModal helpers**
12. **Add QatraLoadingOverlay helper**

## Quality Gates

13. **Add unit tests for RTL detection rules**
14. **Add tests for mojibake and broken Arabic detection**
15. **Add GitHub Actions matrix for Java 21 and multiple browsers**

## Documentation and Community

16. **Add CODE_OF_CONDUCT.md**
17. **Add first-contribution guide**
18. **Add comparison page: QATRA vs raw Selenium**
19. **Add real-world smoke testing ethics guide**
20. **Prepare SHAFT contribution candidate utilities**
