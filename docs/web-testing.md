# Web Testing Guide

The `qatra-web` module wraps Selenium 4 with a fluent API, smart waits, assertions, diagnostics, and RTL features.

## Browser Actions

```java
driver()
        .browser()
        .navigateTo("https://example.com")
        .maximize()
        .refresh()
        .navigateBack()
        .navigateForward();
```

Use configured base URL:

```java
driver().browser().navigateToBaseUrl();
```

## Element Actions

```java
driver()
        .element()
        .click(By.id("login"))
        .clearAndType(By.id("username"), "admin")
        .typeAndPress(By.id("search"), "QATRA", Keys.ENTER)
        .hover(By.id("menu"))
        .doubleClick(By.id("item"))
        .rightClick(By.id("item"))
        .scrollTo(By.id("footer"))
        .highlight(By.id("save"));
```

## Select, Checkbox, Upload

```java
driver()
        .element()
        .selectByText(By.id("country"), "Jordan")
        .check(By.id("remember"))
        .uploadFile(By.id("attachment"), "C:/files/report.pdf");
```

## Smart Waits

```java
driver()
        .element()
        .waitUntilPageReady()
        .waitUntilVisible(By.id("username"))
        .waitUntilClickable(By.id("login"))
        .waitUntilTextContains(By.id("status"), "Ready");
```

## Alerts

```java
driver()
        .alert()
        .assertTextContains("Are you sure")
        .accept();
```

## Frames

```java
driver().frame().switchTo(By.id("payment-frame"));
driver().element().clearAndType(By.id("card"), "4111111111111111");
driver().frame().defaultContent();
```

## Windows and Tabs

```java
driver()
        .window()
        .openNewTab()
        .browser()
        .navigateTo("https://example.com")
        .window()
        .switchToTitleContains("Example");
```

## Shadow DOM

```java
driver()
        .shadow()
        .click(By.id("host"), By.cssSelector("#shadowBtn"));
```

## Cookies

```java
driver()
        .cookies()
        .add("qatra_session", "active")
        .assertExists("qatra_session")
        .assertValue("qatra_session", "active");
```

## Local and Session Storage

```java
driver()
        .storage()
        .setLocal("language", "ar")
        .setSession("role", "qa")
        .assertLocalValue("language", "ar")
        .assertSessionValue("role", "qa");
```

## Tables

```java
driver()
        .table(By.id("users"))
        .assertRowCount(2)
        .assertColumnCount(2)
        .assertColumnContains("Role", "QA");
```

## Drag and Drop

```java
driver()
        .element()
        .dragAndDrop(By.id("source"), By.id("target"))
        .html5DragAndDrop(By.id("source"), By.id("target"));
```

## Assertions

```java
driver()
        .assertThat()
        .element(By.id("message")).isVisible()
        .element(By.id("message")).containsText("Success")
        .element(By.id("submit")).isEnabled()
        .element(By.id("username")).hasAttribute("type", "text")
        .browser().url().contains("/dashboard");
```

## Page Health

```java
driver()
        .assertThat()
        .pageHealth()
        .allLinksHaveHref()
        .hasNoBrokenLinks()
        .hasNoBrokenImages();
```

## Diagnostics

Manual screenshot:

```java
driver().screenshot("login_page_loaded");
```

Manual diagnostics:

```java
driver().diagnostics("before_checkout_submit");
```

Failure diagnostics are handled by `QatraBaseTest`.
