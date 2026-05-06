package io.github.qatra.web.components;

/** Internal XPath escaping helper for Arabic and mixed-language text. */
final class ArabicXPath {
    private ArabicXPath() {
    }

    static String textContains(String text) {
        return "contains(normalize-space(.), " + literal(text) + ")";
    }

    static String literal(String value) {
        if (value == null) {
            return "''";
        }
        if (!value.contains("'")) {
            return "'" + value + "'";
        }
        if (!value.contains("\"")) {
            return "\"" + value + "\"";
        }
        StringBuilder concat = new StringBuilder("concat(");
        String[] parts = value.split("'");
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                concat.append(", \"'\", ");
            }
            concat.append("'").append(parts[i]).append("'");
        }
        concat.append(")");
        return concat.toString();
    }
}
