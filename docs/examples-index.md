# Examples Index

The current examples live mainly inside sample tests until a dedicated `qatra-examples` module is created.

## Web Examples

File:

```text
qatra-web/src/test/java/io/github/qatra/tests/SampleQatraTest.java
```

Covered examples:

```text
browserTitleTest
elementTypingTest
manualScreenshotTest
manualDiagnosticsTest
advancedElementAssertionsTest
smartWaitsAndBetterActionsTest
rtlEngineExpansionTest
rtlScannerEnhancementsTest
rtlScannerConfigurationTest
rtlReportExportTest
rtlReportHistoryAndHtmlSummaryTest
rtlBaselineRegressionComparisonTest
rtlQualityGateTest
seleniumCoreAlertsFramesAndWindowsTest
seleniumCoreCookiesAndStorageTest
seleniumCoreShadowDomAndTableTest
seleniumCoreDragAndDropTest
seleniumCoreAlertAndWindowAssertionsTest
seleniumCoreCookieStorageShadowTableAndPageHealthAssertionsTest
seleniumCoreDownloadAssertionsTest
pageObjectModelSupportTest
pageObjectAdvancedFeaturesTest
dataDrivenCsvProviderTest
dataDrivenJsonDirectLoadTest
dataDrivenExcelDirectLoadTest
retryAnalyzerRecoversControlledFlakyTest
stabilityEventuallyHelperTest
parallelDriverContextSmokeTest
```

## API Examples

Files:

```text
qatra-api/src/test/java/io/github/qatra/api/tests/SampleQatraApiTest.java
qatra-api/src/test/java/io/github/qatra/api/tests/QatraEnvironmentProfileTest.java
```

Covered examples:

```text
GET /health
POST /login
GET /users with query parameters
Environment profile loading
Configured API base URL
```

## Data Files

```text
qatra-web/src/test/resources/test-data/login-users.csv
qatra-web/src/test/resources/test-data/api-users.json
qatra-web/src/test/resources/test-data/ui-users.xlsx
```

## Suggested Future Examples Module

Planned structure:

```text
qatra-examples/
├── web-examples
├── rtl-examples
├── api-examples
├── page-object-examples
└── data-driven-examples
```

Each example should be small, runnable, and documented.
