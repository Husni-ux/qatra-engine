# Consumer Installation Guide

After QATRA is published to Maven Central, users should not clone the framework source code. They can add QATRA as a dependency.

## Web testing dependency

```xml
<dependency>
    <groupId>io.github.qatra</groupId>
    <artifactId>qatra-web</artifactId>
    <version>0.1.0-alpha</version>
</dependency>
```

## API testing dependency

```xml
<dependency>
    <groupId>io.github.qatra</groupId>
    <artifactId>qatra-api</artifactId>
    <version>0.1.0-alpha</version>
</dependency>
```

## Minimal test example

```java
import io.github.qatra.web.WebDriver;
import org.testng.annotations.Test;

public class FirstQatraTest {

    @Test
    public void openExample() {
        WebDriver driver = new WebDriver();

        driver.browser()
                .navigateTo("https://example.com")
                .assertThat()
                .browser()
                .title()
                .contains("Example");

        driver.quit();
    }
}
```
