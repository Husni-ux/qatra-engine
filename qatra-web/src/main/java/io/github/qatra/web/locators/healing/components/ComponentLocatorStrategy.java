package io.github.qatra.web.locators.healing.components;

import io.github.qatra.web.locators.QatraLocator;
import io.github.qatra.web.locators.healing.accessibility.AccessibilityLocatorStrategy;
import org.openqa.selenium.By;

/**
 * Component-intent based locator strategies for Arabic/RTL web applications.
 *
 * <p>These strategies are intentionally business-readable. They prefer stable
 * accessibility and label signals, then fallback to Arabic text and common component
 * patterns used by enterprise UI libraries.</p>
 */
public final class ComponentLocatorStrategy {

    private ComponentLocatorStrategy() {}

    public static QatraLocator dropdownByArabicLabel(String label) {
        String name = "Arabic dropdown: " + label;
        return QatraLocator.primary(AccessibilityLocatorStrategy.byRoleAndAccessibleName("combobox", label))
                .named(name)
                .expectedRole("combobox")
                .expectedAccessibleName(label)
                .expectedArabicText(label)
                .fallback("component dropdown by label association", AccessibilityLocatorStrategy.byLabelText(label))
                .fallback("component dropdown following Arabic label", followingLabel(label,
                        "self::select or @role='combobox' or @aria-haspopup='listbox' or contains(@class,'dropdown') or contains(@class,'select')"))
                .fallback("component dropdown by aria-label", By.xpath("//*[@aria-label=" + xpathLiteral(label) + " and (@role='combobox' or @aria-haspopup='listbox')]") )
                .fallback("component dropdown contains Arabic label", By.xpath("//*[contains(@class,'dropdown') or contains(@class,'select') or @role='combobox'][" + textContains(label) + "]"))
                .build();
    }

    public static QatraLocator tableContainingArabicText(String expectedText) {
        String name = "Arabic table containing: " + expectedText;
        return QatraLocator.primary(By.xpath("//*[self::table or @role='table' or @role='grid'][" + textContains(expectedText) + "]"))
                .named(name)
                .expectedRole("table")
                .expectedArabicText(expectedText)
                .fallback("component table/grid containing Arabic text", By.xpath("//*[contains(@class,'table') or contains(@class,'grid') or @role='rowgroup'][" + textContains(expectedText) + "]"))
                .fallback("component table row containing Arabic text", By.xpath("//*[@role='row' or self::tr][" + textContains(expectedText) + "]/ancestor::*[self::table or @role='table' or @role='grid' or contains(@class,'table')][1]"))
                .build();
    }

    public static QatraLocator modalContainingArabicText(String expectedText) {
        String name = "Arabic modal containing: " + expectedText;
        return QatraLocator.primary(By.xpath("//*[@role='dialog' or @aria-modal='true' or contains(@class,'modal')][" + textContains(expectedText) + "]"))
                .named(name)
                .expectedRole("dialog")
                .expectedArabicText(expectedText)
                .fallback("component modal by accessible name", AccessibilityLocatorStrategy.byRoleAndAccessibleName("dialog", expectedText))
                .fallback("component visible modal containing text", By.xpath("//*[contains(@class,'dialog') or contains(@class,'popup') or contains(@class,'modal')][" + textContains(expectedText) + "]"))
                .build();
    }

    public static QatraLocator toastContainingArabicText(String expectedText) {
        String name = "Arabic toast containing: " + expectedText;
        return QatraLocator.primary(By.xpath("//*[@role='alert' or @role='status' or contains(@class,'toast') or contains(@class,'notification') or contains(@class,'alert')][" + textContains(expectedText) + "]"))
                .named(name)
                .expectedRole("status")
                .expectedArabicText(expectedText)
                .fallback("component toast by visible Arabic text", By.xpath("//*[contains(@class,'message') or contains(@class,'snackbar') or contains(@class,'toast')][" + textContains(expectedText) + "]"))
                .build();
    }

    public static QatraLocator inputByArabicLabel(String label) {
        String name = "Arabic input: " + label;
        return QatraLocator.primary(AccessibilityLocatorStrategy.byRoleAndAccessibleName("textbox", label))
                .named(name)
                .expectedRole("textbox")
                .expectedAccessibleName(label)
                .expectedArabicText(label)
                .fallback("component input by label text", AccessibilityLocatorStrategy.byLabelText(label))
                .fallback("component input following Arabic label", followingLabel(label, "self::input or self::textarea or @role='textbox'"))
                .fallback("component input by placeholder", AccessibilityLocatorStrategy.byPlaceholder(label))
                .build();
    }

    public static QatraLocator datePickerByArabicLabel(String label) {
        String name = "Arabic date picker: " + label;
        return QatraLocator.primary(AccessibilityLocatorStrategy.byRoleAndAccessibleName("textbox", label))
                .named(name)
                .expectedRole("textbox")
                .expectedAccessibleName(label)
                .expectedArabicText(label)
                .fallback("date picker by Arabic label", AccessibilityLocatorStrategy.byLabelText(label))
                .fallback("date picker following Arabic label", followingLabel(label,
                        "self::input or @role='textbox' or contains(@class,'date') or contains(@class,'calendar')"))
                .fallback("date picker placeholder", AccessibilityLocatorStrategy.byPlaceholder(label))
                .build();
    }

    public static QatraLocator modalButtonByArabicAction(String modalText, String actionText) {
        String name = "Arabic modal button: " + actionText;
        return QatraLocator.primary(By.xpath("//*[@role='dialog' or contains(@class,'modal')][" + textContains(modalText) + "]//*[self::button or @role='button'][" + textContains(actionText) + "]"))
                .named(name)
                .expectedRole("button")
                .expectedArabicText(actionText)
                .fallback("modal action button by aria-label", By.xpath("//*[@role='dialog' or contains(@class,'modal')]//*[self::button or @role='button' or @aria-label=" + xpathLiteral(actionText) + "][" + textContains(actionText) + " or @aria-label=" + xpathLiteral(actionText) + "]"))
                .build();
    }

    private static By followingLabel(String label, String componentPredicate) {
        return By.xpath("//*[self::label or self::span or self::div][normalize-space(.)=" + xpathLiteral(label) + "]/following::*[" + componentPredicate + "][1]");
    }

    private static String textContains(String value) {
        String literal = xpathLiteral(value);
        return "contains(normalize-space(.), " + literal + ") or contains(@aria-label, " + literal + ") or contains(@placeholder, " + literal + ") or contains(@title, " + literal + ") or contains(@value, " + literal + ")";
    }

    private static String xpathLiteral(String value) {
        if (value == null) return "''";
        if (!value.contains("'")) return "'" + value + "'";
        if (!value.contains("\"")) return "\"" + value + "\"";
        String[] parts = value.split("'");
        StringBuilder concat = new StringBuilder("concat(");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) concat.append(", \"'\", ");
            concat.append("'").append(parts[i]).append("'");
        }
        return concat.append(")").toString();
    }
}
