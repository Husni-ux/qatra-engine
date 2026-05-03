# Maven Central Publishing Preparation

This document explains how QATRA Engine is prepared for Maven Central publishing.

## Current coordinates

Current development coordinates:

```xml
<groupId>io.github.qatra</groupId>
<artifactId>qatra-web</artifactId>
<version>0.1.0-alpha-SNAPSHOT</version>
```

First public release target:

```xml
<groupId>io.github.qatra</groupId>
<artifactId>qatra-web</artifactId>
<version>0.1.0-alpha</version>
```

> Before the first real publish, confirm the namespace in Central Portal. If the project is published under a personal GitHub namespace, the groupId may need to be changed to a verified namespace such as `io.github.husni-ux` or another namespace approved in Central Portal.

## Modules planned for publication

The first Maven Central bundle should publish:

```text
qatra-engine   parent POM
qatra-core     core utilities
qatra-web      Selenium/Web/RTL module
qatra-api      API testing module
```

## Release profile

The parent `pom.xml` includes the profile:

```text
central-release
```

It adds:

```text
source JAR generation
Javadoc JAR generation
GPG signing
Central Portal publishing plugin
checksum generation
safe dry-run publishing by default
```

By default, the release profile uses:

```properties
central.skipPublishing=true
central.autoPublish=false
central.waitUntil=validated
```

This means the default behavior is safe: it builds the Central bundle locally without uploading it.

## Required secrets for GitHub Actions

Add these repository secrets before running a real release:

```text
CENTRAL_USERNAME
CENTRAL_PASSWORD
GPG_PRIVATE_KEY
GPG_PASSPHRASE
```

The Central username and password are token credentials generated from Central Portal.

## Local dry-run bundle

From the project root:

```bash
./scripts/prepare-release-local.sh 0.1.0-alpha
./scripts/build-central-bundle-local.sh
```

On Windows PowerShell:

```powershell
.\scripts\prepare-release-local.ps1 -Version 0.1.0-alpha
.\scripts\build-central-bundle-local.ps1
```

## Manual Maven dry-run

```bash
mvn -B -ntp org.codehaus.mojo:versions-maven-plugin:2.17.1:set \
  -DnewVersion=0.1.0-alpha \
  -DgenerateBackupPoms=false

mvn -B -ntp clean verify \
  -Dqatra.env=local \
  -Dqatra.browser=chrome \
  -Dqatra.headless=true \
  -Dsurefire.failIfNoSpecifiedTests=false

mvn -B -ntp -Pcentral-release deploy \
  -DskipTests \
  -Dcentral.skipPublishing=true \
  -Dcentral.autoPublish=false
```

## Real publishing command

Only run this after validating the dry-run bundle and Central Portal namespace:

```bash
mvn -B -ntp -Pcentral-release deploy \
  -DskipTests \
  -Dcentral.skipPublishing=false \
  -Dcentral.autoPublish=false \
  -Dcentral.waitUntil=validated
```

For the first release, keep `central.autoPublish=false` and manually review the deployment in Central Portal before publishing.

## Consumer dependency examples

After the release is available on Maven Central:

```xml
<dependency>
    <groupId>io.github.qatra</groupId>
    <artifactId>qatra-web</artifactId>
    <version>0.1.0-alpha</version>
</dependency>
```

```xml
<dependency>
    <groupId>io.github.qatra</groupId>
    <artifactId>qatra-api</artifactId>
    <version>0.1.0-alpha</version>
</dependency>
```

## Release safety checklist

Before uploading:

```text
All tests pass locally
CI pipeline passes
README is updated
CHANGELOG has release notes
LICENSE exists
CONTRIBUTING exists
POM metadata is complete
Source JARs are generated
Javadoc JARs are generated
Artifacts are signed
Central namespace is verified
Dry-run bundle is reviewed
```
