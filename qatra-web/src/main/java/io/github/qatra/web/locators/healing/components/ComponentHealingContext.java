package io.github.qatra.web.locators.healing.components;

import java.util.Objects;

/**
 * Business-readable context for component-level self-healing.
 *
 * <p>The goal is to describe the component intent, not only the selector. Examples:
 * "Arabic dropdown for المدينة", "modal containing تأكيد الحفظ", or
 * "table row containing منشأة تجريبية".</p>
 */
public final class ComponentHealingContext {

    private final String name;
    private final ComponentType type;
    private final String arabicLabel;
    private final String expectedText;
    private final String expectedAction;
    private final String optionText;
    private final String rowText;

    private ComponentHealingContext(Builder builder) {
        this.name = builder.name;
        this.type = builder.type == null ? ComponentType.UNKNOWN : builder.type;
        this.arabicLabel = builder.arabicLabel;
        this.expectedText = builder.expectedText;
        this.expectedAction = builder.expectedAction;
        this.optionText = builder.optionText;
        this.rowText = builder.rowText;
    }

    public static Builder builder(ComponentType type) {
        return new Builder(type);
    }

    public String name() { return name; }
    public ComponentType type() { return type; }
    public String arabicLabel() { return arabicLabel; }
    public String expectedText() { return expectedText; }
    public String expectedAction() { return expectedAction; }
    public String optionText() { return optionText; }
    public String rowText() { return rowText; }

    public String displayName() {
        if (notBlank(name)) return name;
        if (notBlank(arabicLabel)) return type + " for " + arabicLabel;
        if (notBlank(expectedText)) return type + " containing " + expectedText;
        return type.name();
    }

    private static boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    public static final class Builder {
        private final ComponentType type;
        private String name;
        private String arabicLabel;
        private String expectedText;
        private String expectedAction;
        private String optionText;
        private String rowText;

        private Builder(ComponentType type) {
            this.type = Objects.requireNonNull(type, "type must not be null");
        }

        public Builder named(String name) { this.name = name; return this; }
        public Builder arabicLabel(String arabicLabel) { this.arabicLabel = arabicLabel; return this; }
        public Builder expectedText(String expectedText) { this.expectedText = expectedText; return this; }
        public Builder expectedAction(String expectedAction) { this.expectedAction = expectedAction; return this; }
        public Builder optionText(String optionText) { this.optionText = optionText; return this; }
        public Builder rowText(String rowText) { this.rowText = rowText; return this; }
        public ComponentHealingContext build() { return new ComponentHealingContext(this); }
    }
}
