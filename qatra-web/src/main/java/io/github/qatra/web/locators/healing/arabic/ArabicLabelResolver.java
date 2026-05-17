package io.github.qatra.web.locators.healing.arabic;

import org.openqa.selenium.By;

/**
 * Builds Arabic label-based locators for common form and component patterns.
 */
public final class ArabicLabelResolver {
    private ArabicLabelResolver() {}

    public static By byNearbyArabicLabel(String label) {
        String literal = xpathLiteral(label);
        String xpath = "//*[self::label or self::span or self::div or self::p][contains(normalize-space(.), " + literal + ")]" +
                "/ancestor::*[self::div or self::section or self::form or self::fieldset][1]" +
                "//*[self::input or self::textarea or self::select or self::button or @role='combobox' or @role='button'][1]";
        return By.xpath(xpath);
    }

    public static By byArabicActionText(String action) {
        StringBuilder xpath = new StringBuilder("//*[(self::button or self::a or self::input or @role='button' or @role='link')]");        xpath.append("[");
        boolean first = true;
        for (String synonym : ArabicActionDictionary.synonyms(action)) {
            if (!first) xpath.append(" or ");
            first = false;
            String literal = xpathLiteral(synonym);
            xpath.append("normalize-space(.)=").append(literal)
                    .append(" or @value=").append(literal)
                    .append(" or @aria-label=").append(literal)
                    .append(" or @title=").append(literal);
        }
        xpath.append("]");
        return By.xpath(xpath.toString());
    }

    public static By byArabicActionContainsText(String action) {
        StringBuilder xpath = new StringBuilder("//*[(self::button or self::a or self::input or @role='button' or @role='link')]");        xpath.append("[");
        boolean first = true;
        for (String synonym : ArabicActionDictionary.synonyms(action)) {
            if (!first) xpath.append(" or ");
            first = false;
            String literal = xpathLiteral(synonym);
            xpath.append("contains(normalize-space(.), ").append(literal).append(")")
                    .append(" or contains(@value, ").append(literal).append(")")
                    .append(" or contains(@aria-label, ").append(literal).append(")")
                    .append(" or contains(@title, ").append(literal).append(")");
        }
        xpath.append("]");
        return By.xpath(xpath.toString());
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
        concat.append(")");
        return concat.toString();
    }
}
