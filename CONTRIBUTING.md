# Contributing to QATRA Engine

Thank you for helping improve QATRA Engine.

## Development requirements

- Java 21 LTS
- Maven 3.9+
- Chrome, Firefox, or Edge for web tests
- Git

## Local validation

Run the full build:

```bash
mvn -B -ntp clean test -Dsurefire.failIfNoSpecifiedTests=false
```

Run only web tests:

```bash
mvn -B -ntp -pl qatra-web -am clean test \
  -Dtest=SampleQatraTest \
  -Dqatra.env=local \
  -Dqatra.browser=chrome \
  -Dqatra.headless=true \
  -Dsurefire.failIfNoSpecifiedTests=false
```

Run API tests:

```bash
mvn -B -ntp -pl qatra-api -am clean test -Dqatra.env=local
```

Run parallel web smoke:

```bash
mvn -B -ntp -pl qatra-web -am test -Pparallel-web \
  -Dqatra.browser=chrome \
  -Dqatra.headless=true
```

## Contribution rules

1. Keep the fluent API readable for testers.
2. Add tests for every new action, assertion, or utility.
3. Attach evidence to Allure when a feature produces diagnostic output.
4. Do not log secrets or tokens. Use `QatraSecrets.masked(...)` when needed.
5. Keep Arabic/RTL support as a first-class concern.

## Pull request checklist

- [ ] Code compiles with Java 21
- [ ] Relevant tests added or updated
- [ ] `mvn clean test` passes locally
- [ ] Documentation updated when public API changes
- [ ] No generated files from `target/` committed
