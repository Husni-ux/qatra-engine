# Getting Started with QATRA Engine

This guide helps you run QATRA locally and write your first web and API tests.

## Requirements

- Java 21 LTS
- Maven 3.9+
- Chrome, Edge, or Firefox
- Git

Check your Java version:

```bash
java -version
```

Expected major version:

```text
21
```

## Project Structure

```text
qatra-engine/
├── qatra-core
├── qatra-web
├── qatra-api
├── docs
├── scripts
└── pom.xml
```

Run Maven commands from the folder that contains the parent `pom.xml`.

## Run All Tests

```bash
mvn clean test -Dsurefire.failIfNoSpecifiedTests=false
```

## Run Web Tests

```bash
mvn -pl qatra-web -am clean test \
  -Dtest=SampleQatraTest \
  -Dqatra.env=local \
  -Dqatra.browser=chrome \
  -Dqatra.headless=true \
  -Dsurefire.failIfNoSpecifiedTests=false
```

## Run API Tests

```bash
mvn -pl qatra-api -am clean test -Dqatra.env=local
```

## Basic Web Test

```java
import io.github.qatra.web.testng.QatraBaseTest;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

public class FirstQatraWebTest extends QatraBaseTest {

    @Test
    public void pageTitleShouldBeCorrect() {
        driver()
                .browser()
                .navigateTo("https://example.com")
                .assertThat()
                .browser()
                .title()
                .contains("Example");
    }
}
```

Use `driver()` instead of the field `driver` when possible. It is the recommended parallel-safe access method.

## Basic API Test

```java
import io.github.qatra.api.QatraApi;
import org.testng.annotations.Test;

public class FirstQatraApiTest {

    @Test
    public void healthShouldBeUp() {
        QatraApi
                .create()
                .baseUrl("https://api.example.com")
                .get("/health")
                .assertThat()
                .statusCode(200);
    }
}
```

## Configuration

Create or update `src/test/resources/qatra.properties`:

```properties
qatra.browser=chrome
qatra.headless=true
qatra.timeout.element=10
qatra.timeout.pageload=30
qatra.screenshots.on.failure=true
qatra.evidence.on.failure=true
qatra.api.base.url=https://api.example.com
```

For environment-specific settings, use profiles:

```text
qatra-local.properties
qatra-dev.properties
qatra-staging.properties
qatra-prod.properties
```

Run with:

```bash
mvn test -Dqatra.env=staging
```

## Where Reports Are Saved

```text
target/surefire-reports
target/allure-results
target/qatra-reports/screenshots
target/qatra-reports/page-source
target/qatra-reports/browser-logs
target/qatra-reports/rtl
```

## Common First Issues

### Maven says there is no POM

You are probably running from the wrong folder. Move into the folder containing the parent `pom.xml`.

### Tests fail because no tests match the pattern

Use:

```bash
-Dsurefire.failIfNoSpecifiedTests=false
```

### Browser opens visibly in CI

Set:

```bash
-Dqatra.headless=true
```
