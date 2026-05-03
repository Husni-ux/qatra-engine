# QATRA CI/CD Guide

This phase adds GitHub Actions automation for validating QATRA Engine on every pull request and push.

## Workflows

### `.github/workflows/ci.yml`

Runs the standard engineering checks:

1. Validate Maven project structure.
2. Run `qatra-api` tests.
3. Run `qatra-web` tests in Chrome headless mode.
4. Run the `parallel-web` profile as a smoke check.
5. Package JAR artifacts.
6. Upload test reports, Allure results, and QATRA evidence.

### `.github/workflows/release-readiness.yml`

Manual workflow used before tagging a release candidate. It checks that key release files exist and runs a full Maven verification.

## Default CI environment

The CI workflow sets:

```text
Java: 21
Browser: Chrome
Headless: true
QATRA environment: local by default
```

You can run the same behavior locally:

```bash
mvn -B -ntp -pl qatra-web -am clean test \
  -Dtest=SampleQatraTest \
  -Dqatra.env=local \
  -Dqatra.browser=chrome \
  -Dqatra.headless=true \
  -Dsurefire.failIfNoSpecifiedTests=false
```

## Artifacts uploaded by CI

The workflow uploads:

```text
qatra-api/target/surefire-reports
qatra-api/target/allure-results
qatra-web/target/surefire-reports
qatra-web/target/allure-results
qatra-web/target/qatra-reports
**/target/*.jar
```

These artifacts help reviewers debug failures without rerunning the pipeline locally.

## Recommended branch protection

For a public repository, require these checks before merge:

```text
Validate project structure
API module tests
Web module tests
Package artifacts
```

The parallel web job is useful as a quality signal. Keep it required only after it is stable across browsers and runners.

## Maven Central release workflow

### `.github/workflows/maven-central-release.yml`

Manual workflow used to prepare or upload a Maven Central release.

Recommended first run:

```text
dry_run=true
auto_publish=false
```

This creates and validates the release flow without publishing.

Real upload after successful dry-run:

```text
dry_run=false
auto_publish=false
```

Keep `auto_publish=false` for the first public release so the deployment can be reviewed manually in Central Portal before publishing.

Required GitHub secrets:

```text
CENTRAL_USERNAME
CENTRAL_PASSWORD
GPG_PRIVATE_KEY
GPG_PASSPHRASE
```

The release workflow uses the parent Maven profile:

```text
central-release
```

That profile attaches source and Javadoc JARs, signs artifacts, and uses the Central Portal publishing plugin.
