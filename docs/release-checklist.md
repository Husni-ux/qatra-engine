# QATRA Release Checklist

Use this checklist before publishing a GitHub release or Maven Central artifact.

## 1. Version and documentation

- [ ] Confirm the release version, for example `0.1.0-alpha`.
- [ ] Update `CHANGELOG.md`.
- [ ] Update `README.md` quick start if the public API changed.
- [ ] Check `docs/configuration-management.md`.
- [ ] Check `docs/ci-cd.md`.

## 2. Local build validation

```bash
mvn -B -ntp clean test -Dsurefire.failIfNoSpecifiedTests=false
```

```bash
mvn -B -ntp -pl qatra-web -am test -Pparallel-web \
  -Dqatra.browser=chrome \
  -Dqatra.headless=true
```

```bash
mvn -B -ntp package -DskipTests
```

## 3. CI validation

- [ ] `QATRA CI` workflow passed.
- [ ] Release readiness workflow passed.
- [ ] All test evidence artifacts are uploaded.
- [ ] No secrets are printed in logs.

## 4. GitHub release preparation

- [ ] Create tag: `v0.1.0-alpha`.
- [ ] Attach generated module JARs if needed.
- [ ] Include known limitations.
- [ ] Include next roadmap section.

## 5. Maven Central preparation

- [ ] Confirm Central Portal namespace.
- [ ] Confirm final Maven coordinates.
- [ ] Confirm `central-release` profile exists.
- [ ] Confirm source JAR generation works.
- [ ] Confirm Javadoc JAR generation works.
- [ ] Confirm GPG signing works.
- [ ] Confirm Central Portal token credentials exist.
- [ ] Confirm GitHub secrets are configured.
- [ ] Build dry-run bundle with `central.skipPublishing=true`.
- [ ] Review generated bundle artifacts.
- [ ] Upload with `central.skipPublishing=false` only after dry-run passes.
- [ ] Keep `central.autoPublish=false` for the first release and publish manually from Central Portal.
