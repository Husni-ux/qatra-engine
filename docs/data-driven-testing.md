# Data Driven Testing Guide

QATRA supports data-driven testing with CSV, JSON, and Excel files.

## Data Package

```text
io.github.qatra.web.data
```

Main classes:

```text
QatraData
QatraDataSet
QatraDataRecord
QatraDataProvider
QatraDataFile
```

## TestNG DataProvider with CSV

```java
@Test(dataProvider = "qatraData", dataProviderClass = QatraDataProvider.class)
@QatraDataFile(path = "test-data/login-users.csv")
public void loginWithCsvData(QatraDataRecord user) {
    driver()
            .browser()
            .navigateTo("https://example.com/login")
            .element()
            .clearAndType(By.id("username"), user.required("username"))
            .clearAndType(By.id("password"), user.required("password"))
            .click(By.id("login"));
}
```

## Load JSON Directly

```java
QatraDataSet users = QatraData.fromJson("test-data/api-users.json");
QatraDataRecord first = users.first();

String username = first.required("username");
boolean active = first.getBoolean("active");
```

## Load Excel Directly

```java
QatraDataSet users = QatraData.fromExcel("test-data/ui-users.xlsx", "Users");
```

## Useful Record Methods

```java
record.get("username")
record.required("username")
record.getBoolean("active")
record.getInt("age")
record.containsKey("role")
```

## Configuration

```properties
qatra.data.dir=src/test/resources/test-data
qatra.data.attach=true
qatra.data.csv.delimiter=,
qatra.data.csv.has-header=true
qatra.data.excel.sheet-index=0
```

When `qatra.data.attach=true`, QATRA can attach a summary of test data to Allure.
