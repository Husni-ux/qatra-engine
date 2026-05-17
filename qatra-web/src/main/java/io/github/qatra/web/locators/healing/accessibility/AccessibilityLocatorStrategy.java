package io.github.qatra.web.locators.healing.accessibility;

import org.openqa.selenium.By;

/**
 * Accessibility-first locator strategies for self-healing.
 */
public final class AccessibilityLocatorStrategy {

    private AccessibilityLocatorStrategy() {}

    public static By byRole(String role) {
        String r = xpathLiteral(role);
        String normalized = role == null ? "" : role.trim().toLowerCase();
        String nativeRole = switch (normalized) {
            case "button" -> "self::button or (@type='button' or @type='submit' or @type='reset')";
            case "link" -> "self::a";
            case "textbox" -> "self::input or self::textarea";
            case "combobox" -> "self::select or @role='combobox'";
            case "checkbox" -> "@type='checkbox'";
            case "radio" -> "@type='radio'";
            default -> "false()";
        };
        return By.xpath("//*[translate(@role,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')=" + r + " or " + nativeRole + "]");
    }

    public static By byAccessibleName(String accessibleName) {
        String literal = xpathLiteral(accessibleName);
        return By.xpath("//*[(normalize-space(.)=" + literal + ")" +
                " or @aria-label=" + literal +
                " or @title=" + literal +
                " or @placeholder=" + literal +
                " or @alt=" + literal +
                " or @value=" + literal +
                " or @id=//label[normalize-space(.)=" + literal + "]/@for" +
                " or @aria-labelledby=//label[normalize-space(.)=" + literal + "]/@id]");
    }

    public static By byRoleAndAccessibleName(String role, String accessibleName) {
        String r = role == null ? "" : role.trim().toLowerCase();
        String literal = xpathLiteral(accessibleName);
        String rolePredicate = switch (r) {
            case "button" -> "(@role='button' or self::button or @type='button' or @type='submit' or @type='reset')";
            case "link" -> "(@role='link' or self::a)";
            case "textbox" -> "(@role='textbox' or self::input or self::textarea)";
            case "combobox" -> "(@role='combobox' or self::select)";
            case "checkbox" -> "(@role='checkbox' or @type='checkbox')";
            case "radio" -> "(@role='radio' or @type='radio')";
            default -> "(@role=" + xpathLiteral(role) + ")";
        };
        String namePredicate = "(normalize-space(.)=" + literal +
                " or @aria-label=" + literal +
                " or @title=" + literal +
                " or @placeholder=" + literal +
                " or @alt=" + literal +
                " or @value=" + literal +
                " or @id=//label[normalize-space(.)=" + literal + "]/@for" +
                " or @aria-labelledby=//label[normalize-space(.)=" + literal + "]/@id)";
        return By.xpath("//*[" + rolePredicate + " and " + namePredicate + "]");
    }

    public static By byPlaceholder(String placeholder) {
        return By.xpath("//*[@placeholder=" + xpathLiteral(placeholder) + "]");
    }

    public static By byAriaLabel(String ariaLabel) {
        return By.xpath("//*[@aria-label=" + xpathLiteral(ariaLabel) + "]");
    }

    public static By byLabelText(String labelText) {
        String literal = xpathLiteral(labelText);
        return By.xpath("//*[@id=//label[normalize-space(.)=" + literal + "]/@for]" +
                " | //label[normalize-space(.)=" + literal + "]//input" +
                " | //label[normalize-space(.)=" + literal + "]//textarea" +
                " | //label[normalize-space(.)=" + literal + "]//select" +
                " | //label[normalize-space(.)=" + literal + "]/following::*[self::input or self::textarea or self::select][1]");
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
