# Configuration Reference

QATRA reads configuration from system properties, environment variables, profile files, `qatra.properties`, and defaults.

## Priority Order

```text
1. System properties: -Dqatra.browser=edge
2. Environment variables: QATRA_BROWSER=edge
3. Active profile file: qatra-staging.properties
4. Default file: qatra.properties
5. Built-in defaults
```

## Environment Profiles

```properties
qatra.env=local
qatra.config.validate-on-startup=true
qatra.config.required=qatra.base.url,qatra.api.base.url
```

Run:

```bash
mvn test -Dqatra.env=staging
```

## Web Settings

```properties
qatra.browser=chrome
qatra.headless=true
qatra.browser.maximize=true
qatra.base.url=https://example.com
qatra.timeout.element=10
qatra.timeout.pageload=30
qatra.timeout.implicit=0
qatra.wait.polling.ms=250
qatra.wait.page.ready=true
qatra.element.highlight=false
```

## Evidence and Reporting

```properties
qatra.screenshots.on.failure=true
qatra.screenshots.dir=target/qatra-reports/screenshots
qatra.evidence.on.failure=true
qatra.evidence.page-source.on.failure=true
qatra.evidence.browser-logs.on.failure=true
qatra.evidence.page-source.dir=target/qatra-reports/page-source
qatra.evidence.browser-logs.dir=target/qatra-reports/browser-logs
qatra.allure.results.dir=target/allure-results
```

## RTL Settings

```properties
qatra.rtl.scan.enabled=true
qatra.rtl.fail-on=errors
qatra.rtl.report.attach=true
qatra.rtl.report.export=true
qatra.rtl.report.dir=target/qatra-reports/rtl
qatra.rtl.report.formats=txt,json,html
qatra.rtl.report.filename=rtl-scan-report
qatra.rtl.report.history.enabled=true
qatra.rtl.report.history.dir=target/qatra-reports/rtl/history
qatra.rtl.report.history.index=true
qatra.rtl.scan.direction=true
qatra.rtl.scan.encoding=true
qatra.rtl.scan.placeholder=true
qatra.rtl.scan.digits=true
qatra.rtl.scan.mixed-direction=true
qatra.rtl.scan.alignment=true
qatra.rtl.scan.selector=html, body, body *
```

## RTL Baseline

```properties
qatra.rtl.baseline.enabled=true
qatra.rtl.baseline.path=target/qatra-reports/rtl/baseline/rtl-baseline.json
qatra.rtl.baseline.update=false
qatra.rtl.baseline.fail-on-new-issues=true
qatra.rtl.baseline.report.export=true
qatra.rtl.baseline.report.filename=rtl-baseline-comparison
```

## RTL Quality Gate

```properties
qatra.rtl.quality-gate.enabled=true
qatra.rtl.quality-gate.min-score=80
qatra.rtl.quality-gate.max-errors=0
qatra.rtl.quality-gate.max-warnings=5
qatra.rtl.quality-gate.fail-on-failure=true
qatra.rtl.quality-gate.report.export=true
qatra.rtl.quality-gate.report.filename=rtl-quality-gate
```

## API Settings

```properties
qatra.api.base.url=https://api.example.com
qatra.api.timeout.seconds=30
qatra.api.relaxed.https=true
qatra.api.attach.request=true
qatra.api.attach.response=true
```

## Data Settings

```properties
qatra.data.dir=src/test/resources/test-data
qatra.data.attach=true
qatra.data.csv.delimiter=,
qatra.data.csv.has-header=true
qatra.data.excel.sheet-index=0
```

## Retry and Parallel Settings

```properties
qatra.retry.enabled=false
qatra.retry.count=1
qatra.retry.attach.evidence=true
qatra.parallel.enabled=false
qatra.parallel.thread-count=2
qatra.parallel.mode=methods
qatra.stability.attempts=3
qatra.stability.delay.ms=250
qatra.stability.attach=true
```

## Secrets

Avoid printing raw tokens or passwords. Use the secrets helper:

```java
QatraSecrets.get("qatra.demo.token");
QatraSecrets.masked("qatra.demo.token");
```
