package io.github.qatra.web.locators.healing.accessibility;

/**
 * Small immutable snapshot of accessibility-related identity signals for a web element.
 */
public final class AccessibilitySignal {
    private final String role;
    private final String accessibleName;
    private final String ariaLabel;
    private final String labelText;
    private final String placeholder;
    private final String title;

    public AccessibilitySignal(String role, String accessibleName, String ariaLabel,
                               String labelText, String placeholder, String title) {
        this.role = blankToEmpty(role);
        this.accessibleName = blankToEmpty(accessibleName);
        this.ariaLabel = blankToEmpty(ariaLabel);
        this.labelText = blankToEmpty(labelText);
        this.placeholder = blankToEmpty(placeholder);
        this.title = blankToEmpty(title);
    }

    public String role() { return role; }
    public String accessibleName() { return accessibleName; }
    public String ariaLabel() { return ariaLabel; }
    public String labelText() { return labelText; }
    public String placeholder() { return placeholder; }
    public String title() { return title; }

    public boolean hasAccessibleName() {
        return !accessibleName.isBlank();
    }

    public String summary() {
        return "role=" + role + ", accessibleName=" + accessibleName +
                ", ariaLabel=" + ariaLabel + ", labelText=" + labelText +
                ", placeholder=" + placeholder + ", title=" + title;
    }

    private static String blankToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
