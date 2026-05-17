package io.github.qatra.web.locators.healing.patches;

import java.nio.file.Path;

/** Optional source-code location that may contain the locator to update. */
public final class HealingPatchTarget {
    private final Path file;
    private final Integer lineNumber;
    private final String fieldName;
    private final String oldExpression;

    private HealingPatchTarget(Path file, Integer lineNumber, String fieldName, String oldExpression) {
        this.file = file;
        this.lineNumber = lineNumber;
        this.fieldName = fieldName;
        this.oldExpression = oldExpression;
    }

    public static HealingPatchTarget unknown() {
        return new HealingPatchTarget(null, null, null, null);
    }

    public static HealingPatchTarget of(Path file, Integer lineNumber, String fieldName, String oldExpression) {
        return new HealingPatchTarget(file, lineNumber, fieldName, oldExpression);
    }

    public Path file() { return file; }
    public Integer lineNumber() { return lineNumber; }
    public String fieldName() { return fieldName; }
    public String oldExpression() { return oldExpression; }

    public boolean known() {
        return file != null;
    }

    public String displayName() {
        if (file == null) return "Unknown source location";
        StringBuilder builder = new StringBuilder(file.toString());
        if (lineNumber != null) builder.append(":").append(lineNumber);
        if (fieldName != null && !fieldName.isBlank()) builder.append(" (").append(fieldName).append(")");
        return builder.toString();
    }
}
