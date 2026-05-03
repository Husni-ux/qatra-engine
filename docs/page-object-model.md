# Page Object Model Guide

QATRA supports Page Object Model and Component Object Model.

## Why Page Objects?

Page Objects keep tests readable and reduce locator duplication.

Instead of this:

```java
driver().element().clearAndType(By.id("username"), "admin");
driver().element().clearAndType(By.id("password"), "secret");
driver().element().click(By.id("login"));
```

Use this:

```java
driver().page(LoginPage.class).loginAs("admin", "secret");
```

## Basic Page Object

```java
public class LoginPage extends QatraPage {

    @QatraFindBy(id = "username")
    private QatraElement username;

    @QatraFindBy(id = "password")
    private QatraElement password;

    @QatraFindBy(testId = "login-button")
    private QatraElement loginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open(String url) {
        browser().navigateTo(url);
        return this;
    }

    public DashboardPage loginAs(String user, String pass) {
        username.clearAndType(user);
        password.clearAndType(pass);
        loginButton.click();
        return transitionTo(DashboardPage.class);
    }
}
```

## Page Validation

```java
@QatraPageUrl(contains = "/dashboard", titleContains = "Dashboard")
@QatraPageLoaded(id = "dashboard-title")
public class DashboardPage extends QatraPage {
    // page code
}
```

QATRA validates the page after transition.

## Components

Use components for reusable UI parts such as header, sidebar, modal, toast, or cards.

```java
public class HeaderComponent extends QatraComponent {

    @QatraFindBy(testId = "brand")
    private QatraElement brand;

    @QatraFindBy(testId = "user-chip")
    private QatraElement userChip;

    public HeaderComponent(WebDriver driver, By rootLocator) {
        super(driver, rootLocator);
    }

    public HeaderComponent assertLoggedInUser(String expectedUser) {
        userChip.assertThat().containsText(expectedUser);
        return this;
    }
}
```

Use it inside a page:

```java
public class DashboardPage extends QatraPage {

    @QatraFindBy(id = "app-header")
    private HeaderComponent header;

    public DashboardPage assertUser(String expectedUser) {
        header.assertLoggedInUser(expectedUser);
        return this;
    }
}
```

## Locator Annotation

`@QatraFindBy` supports common locator strategies such as:

```java
@QatraFindBy(id = "username")
@QatraFindBy(name = "email")
@QatraFindBy(css = ".submit")
@QatraFindBy(xpath = "//button[text()='Save']")
@QatraFindBy(testId = "save-button")
```

Prefer stable locators such as `id`, `data-testid`, and accessibility-friendly attributes.
