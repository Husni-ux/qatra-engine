# QATRA Configuration Management

Phase 3.7 introduces environment profiles so teams can run the same tests against local, dev, staging, and production-like environments without changing Java code.

## Profile Selection

QATRA reads the active environment from:

1. System property: `-Dqatra.env=staging`
2. Environment variable: `QATRA_ENV=staging`
3. `qatra.properties`
4. Built-in default: `local`

## Profile Files

If the active environment is `staging`, QATRA automatically attempts to load:

```text
qatra-staging.properties
```

The default pattern is configurable:

```properties
qatra.config.profile-file-pattern=qatra-%s.properties
```

## Priority Order

The final value of a property is resolved in this order:

1. Java system properties, for example `-Dqatra.browser=edge`
2. Environment variables, for example `QATRA_BROWSER=edge`
3. Active profile file, for example `qatra-staging.properties`
4. Base `qatra.properties`
5. Built-in defaults

## Running with a Profile

```powershell
mvn -pl qatra-web -am clean test -Dqatra.env=staging -Dtest=SampleQatraTest -Dsurefire.failIfNoSpecifiedTests=false
```

For API tests:

```powershell
mvn -pl qatra-api -am clean test -Dqatra.env=dev
```

## Required Configuration

You can require keys per environment:

```properties
qatra.config.required=qatra.base.url,qatra.api.base.url
qatra.config.validate-on-startup=true
```

If a required key is missing, QATRA fails early with a clear error.

## Safe Secrets

Use environment variables or CI secrets for sensitive values:

```powershell
$env:QATRA_CLOUD_USERNAME="my-user"
$env:QATRA_CLOUD_ACCESSKEY="secret-value"
```

QATRA masks secret-like keys when printed through safe APIs.
