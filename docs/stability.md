# Stability, Retry, and Parallel Execution

QATRA includes utilities to reduce flaky tests and support parallel execution.

## Parallel-Safe Driver Access

Prefer:

```java
driver().browser().navigateTo("https://example.com");
```

instead of directly relying on a shared driver field.

## Retry Analyzer

Use retry only for controlled flaky behavior. It should not hide real bugs.

```java
@Test(retryAnalyzer = QatraRetryAnalyzer.class)
@QatraRetry(count = 1, reason = "Known temporary async delay")
public void testWithControlledRetry() {
    // test steps
}
```

Configuration:

```properties
qatra.retry.enabled=false
qatra.retry.count=1
qatra.retry.attach.evidence=true
```

## Stability Helper

Avoid random `Thread.sleep()` calls. Use `QatraStability.eventually`:

```java
QatraStability.eventually(
        "async counter becomes ready",
        3,
        Duration.ofMillis(250),
        () -> someConditionIsTrue()
);
```

## Parallel Helper

```java
QatraParallel.assertDriverIsBoundToCurrentThread(driver());
```

## Parallel TestNG Suite

File:

```text
qatra-web/src/test/resources/testng-parallel.xml
```

Run:

```bash
mvn -pl qatra-web -am clean test -Pparallel-web -Dsurefire.failIfNoSpecifiedTests=false
```

## Configuration

```properties
qatra.parallel.enabled=false
qatra.parallel.thread-count=2
qatra.parallel.mode=methods
qatra.stability.attempts=3
qatra.stability.delay.ms=250
qatra.stability.attach=true
```

## Best Practices

- Keep tests independent.
- Do not share mutable data between parallel tests.
- Use unique test data when possible.
- Capture evidence on failure.
- Fix real flakiness instead of increasing retry count.
