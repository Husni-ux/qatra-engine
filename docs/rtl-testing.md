# RTL and Arabic Testing Guide

QATRA's biggest differentiator is Arabic and RTL testing support.

Selenium can read text, attributes, and CSS values, but it does not provide a built-in Arabic/RTL quality engine. QATRA adds this layer.

## Element-Level Arabic Assertions

```java
driver()
        .assertThat()
        .element(By.tagName("body")).isRTL()
        .element(By.id("arabic-title")).hasArabicText()
        .element(By.id("arabic-title")).hasNoBrokenArabicCharacters()
        .element(By.id("price")).hasArabicDigits()
        .element(By.id("name")).hasArabicPlaceholder()
        .element(By.id("name")).hasPlaceholderDirectionRTL();
```

## What QATRA Checks

QATRA can detect issues such as:

- Arabic text rendered while the element is LTR
- Arabic placeholder without RTL direction
- Broken encoding such as mojibake
- Repeated question marks instead of Arabic text
- Arabic/English digit mismatch
- Mixed direction warnings
- Alignment warnings

## Page-Level RTL Scan

```java
driver()
        .rtl()
        .scanPage()
        .report()
        .result();
```

## Fail Based on Configuration

```java
driver()
        .rtl()
        .scanPage()
        .report()
        .failOnConfiguredLevel();
```

Configuration:

```properties
qatra.rtl.scan.enabled=true
qatra.rtl.fail-on=errors
qatra.rtl.scan.direction=true
qatra.rtl.scan.encoding=true
qatra.rtl.scan.placeholder=true
qatra.rtl.scan.digits=true
qatra.rtl.scan.mixed-direction=true
qatra.rtl.scan.alignment=true
```

Supported `qatra.rtl.fail-on` values:

```text
none
errors
warnings
issues
```

## RTL Reports

QATRA can export:

```text
target/qatra-reports/rtl/rtl-scan-report.txt
target/qatra-reports/rtl/rtl-scan-report.json
target/qatra-reports/rtl/rtl-scan-report.html
```

Enable export:

```properties
qatra.rtl.report.export=true
qatra.rtl.report.formats=txt,json,html
qatra.rtl.report.dir=target/qatra-reports/rtl
```

## RTL History

```properties
qatra.rtl.report.history.enabled=true
qatra.rtl.report.history.dir=target/qatra-reports/rtl/history
qatra.rtl.report.history.index=true
```

Output:

```text
target/qatra-reports/rtl/history/index.html
```

## Baseline Regression Comparison

Save a baseline:

```java
driver()
        .rtl()
        .scanPage()
        .saveBaseline();
```

Compare with baseline:

```java
driver()
        .rtl()
        .scanPage()
        .compareWithBaseline()
        .failOnNewBaselineIssues();
```

QATRA can classify:

```text
New issues
Existing issues
Resolved issues
```

## Quality Gate

```java
driver()
        .rtl()
        .scanPage()
        .report()
        .qualityGate()
        .failOnConfiguredQualityGate();
```

Configuration:

```properties
qatra.rtl.quality-gate.enabled=true
qatra.rtl.quality-gate.min-score=80
qatra.rtl.quality-gate.max-errors=0
qatra.rtl.quality-gate.max-warnings=5
qatra.rtl.quality-gate.fail-on-failure=true
```

Use quality gates in CI to prevent RTL regressions from reaching production.
