package io.github.qatra.web.locators;

import io.github.qatra.web.locators.healing.arabic.ArabicActionDictionary;
import io.github.qatra.web.locators.healing.arabic.ArabicLabelResolver;
import io.github.qatra.web.locators.healing.accessibility.AccessibilityLocatorStrategy;
import io.github.qatra.web.locators.healing.components.ComponentLocatorStrategy;
import io.github.qatra.web.locators.advisor.LocatorAdvisorReport;
import io.github.qatra.web.locators.advisor.ProactiveLocatorQualityAdvisor;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Business-readable self-healing locator chain.
 *
 * <p>Use a stable primary locator first, then add safe fallbacks such as data-testid,
 * aria-label, visible Arabic text, English text, or controlled XPath/CSS alternatives.</p>
 */
public final class QatraLocator {

    private final String name;
    private final List<LocatorCandidate> candidates;
    private final String expectedRole;
    private final String expectedText;
    private final String expectedArabicText;
    private final String expectedAction;
    private final String expectedAccessibleName;

    private QatraLocator(String name, List<LocatorCandidate> candidates,
                         String expectedRole, String expectedText,
                         String expectedArabicText, String expectedAction, String expectedAccessibleName) {
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalArgumentException("QatraLocator requires at least one candidate");
        }
        this.name = name;
        this.candidates = new ArrayList<>(candidates);
        this.expectedRole = expectedRole;
        this.expectedText = expectedText;
        this.expectedArabicText = expectedArabicText;
        this.expectedAction = expectedAction;
        this.expectedAccessibleName = expectedAccessibleName;
    }

    public static Builder primary(By locator) {
        return new Builder(locator);
    }

    public static Builder by(By locator) {
        return primary(locator);
    }

    public String name() {
        return name;
    }

    public By primaryLocator() {
        return candidates.get(0).locator();
    }

    public List<LocatorCandidate> candidates() {
        return Collections.unmodifiableList(candidates);
    }

    public String expectedRole() {
        return expectedRole;
    }

    public String expectedText() {
        return expectedText;
    }

    public String expectedArabicText() {
        return expectedArabicText;
    }

    public String expectedAction() {
        return expectedAction;
    }

    public String expectedAccessibleName() {
        return expectedAccessibleName;
    }

    public boolean hasSemanticHints() {
        return notBlank(expectedRole) || notBlank(expectedText) || notBlank(expectedArabicText) || notBlank(expectedAction) || notBlank(expectedAccessibleName);
    }

    private static boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    public LocatorQualityReport qualityReport() {
        return LocatorQualityAdvisor.analyze(primaryLocator());
    }

    /**
     * Runs the advanced proactive locator advisor against this locator chain.
     */
    public LocatorAdvisorReport advisorReport() {
        return ProactiveLocatorQualityAdvisor.analyze(this);
    }

    @Override
    public String toString() {
        return "QatraLocator{" +
                "name='" + name + '\'' +
                ", candidates=" + candidates +
                ", expectedRole='" + expectedRole + '\'' +
                ", expectedText='" + expectedText + '\'' +
                ", expectedArabicText='" + expectedArabicText + '\'' +
                ", expectedAction='" + expectedAction + '\'' +
                ", expectedAccessibleName='" + expectedAccessibleName + '\'' +
                '}';
    }

    public static final class Builder {
        private String name;
        private String expectedRole;
        private String expectedText;
        private String expectedArabicText;
        private String expectedAction;
        private String expectedAccessibleName;
        private final List<LocatorCandidate> candidates = new ArrayList<>();

        private Builder(By primary) {
            candidates.add(new LocatorCandidate(primary, "primary", 0));
        }

        public Builder named(String name) {
            this.name = name;
            return this;
        }

        public Builder expectedRole(String role) {
            this.expectedRole = role;
            return this;
        }

        public Builder role(String role) {
            return expectedRole(role);
        }

        public Builder expectedText(String text) {
            this.expectedText = text;
            return this;
        }

        public Builder expectedArabicText(String text) {
            this.expectedArabicText = text;
            return this;
        }

        public Builder expectedAction(String action) {
            this.expectedAction = action;
            return this;
        }

        public Builder expectedAccessibleName(String accessibleName) {
            this.expectedAccessibleName = accessibleName;
            return this;
        }

        public Builder accessibleName(String accessibleName) {
            return expectedAccessibleName(accessibleName);
        }

        /**
         * Sets an expected business action and adds Arabic semantic fallbacks for common UI labels.
         *
         * <p>Example: semanticArabicAction("save") adds fallbacks such as حفظ, حفظ الطلب,
         * حفظ البيانات, and related action labels from the Arabic action dictionary.</p>
         */
        public Builder semanticArabicAction(String action) {
            this.expectedAction = action;
            fallbackArabicAction(action);
            return fallbackArabicActionContains(action);
        }

        public Builder fallbackArabicAction(String action) {
            if (action == null || action.isBlank()) {
                return this;
            }
            if (this.expectedAction == null || this.expectedAction.isBlank()) {
                this.expectedAction = action;
            }
            return fallback("arabic semantic action fallback: " + ArabicActionDictionary.normalizeAction(action),
                    ArabicLabelResolver.byArabicActionText(action));
        }

        public Builder fallbackArabicActionContains(String action) {
            if (action == null || action.isBlank()) {
                return this;
            }
            if (this.expectedAction == null || this.expectedAction.isBlank()) {
                this.expectedAction = action;
            }
            return fallback("arabic semantic action contains fallback: " + ArabicActionDictionary.normalizeAction(action),
                    ArabicLabelResolver.byArabicActionContainsText(action));
        }

        public Builder fallbackArabicLabel(String label) {
            return fallback("arabic nearby label fallback", ArabicLabelResolver.byNearbyArabicLabel(label));
        }

        public Builder expectedArabicAction(String action) {
            return semanticArabicAction(action);
        }

        public Builder fallback(By locator) {
            candidates.add(new LocatorCandidate(locator, "fallback", candidates.size()));
            return this;
        }

        public Builder fallback(String source, By locator) {
            candidates.add(new LocatorCandidate(locator, source, candidates.size()));
            return this;
        }

        public Builder fallbackCss(String cssSelector) {
            return fallback("css fallback", By.cssSelector(cssSelector));
        }

        public Builder fallbackXpath(String xpath) {
            return fallback("xpath fallback", By.xpath(xpath));
        }

        public Builder fallbackDataTestId(String testId) {
            String escaped = cssEscape(testId);
            return fallback("data-testid fallback", By.cssSelector("[data-testid='" + escaped + "'],[data-test='" + escaped + "'],[data-qa='" + escaped + "']"));
        }

        public Builder fallbackAriaLabel(String label) {
            return fallback("aria-label fallback", By.xpath("//*[@aria-label=" + xpathLiteral(label) + "]"));
        }

        public Builder fallbackAccessibleName(String accessibleName) {
            return fallback("accessibility accessible-name fallback", AccessibilityLocatorStrategy.byAccessibleName(accessibleName));
        }

        public Builder fallbackRole(String role) {
            return fallback("accessibility role fallback", AccessibilityLocatorStrategy.byRole(role));
        }

        public Builder fallbackRoleAndAccessibleName(String role, String accessibleName) {
            if (this.expectedRole == null || this.expectedRole.isBlank()) {
                this.expectedRole = role;
            }
            if (this.expectedAccessibleName == null || this.expectedAccessibleName.isBlank()) {
                this.expectedAccessibleName = accessibleName;
            }
            return fallback("accessibility role + accessible-name fallback", AccessibilityLocatorStrategy.byRoleAndAccessibleName(role, accessibleName));
        }

        public Builder fallbackPlaceholder(String placeholder) {
            return fallback("accessibility placeholder fallback", AccessibilityLocatorStrategy.byPlaceholder(placeholder));
        }

        public Builder fallbackLabelText(String labelText) {
            return fallback("accessibility label text fallback", AccessibilityLocatorStrategy.byLabelText(labelText));
        }


        public Builder fallbackArabicDropdown(String label) {
            return fallback("component Arabic dropdown fallback", ComponentLocatorStrategy.dropdownByArabicLabel(label).primaryLocator());
        }

        public Builder fallbackArabicInputLabel(String label) {
            return fallback("component Arabic input label fallback", ComponentLocatorStrategy.inputByArabicLabel(label).primaryLocator());
        }

        public Builder fallbackArabicDatePicker(String label) {
            return fallback("component Arabic date picker fallback", ComponentLocatorStrategy.datePickerByArabicLabel(label).primaryLocator());
        }

        public Builder fallbackArabicModalText(String expectedText) {
            return fallback("component Arabic modal fallback", ComponentLocatorStrategy.modalContainingArabicText(expectedText).primaryLocator());
        }

        public Builder fallbackArabicToastText(String expectedText) {
            return fallback("component Arabic toast fallback", ComponentLocatorStrategy.toastContainingArabicText(expectedText).primaryLocator());
        }

        public Builder fallbackArabicTableText(String expectedText) {
            return fallback("component Arabic table fallback", ComponentLocatorStrategy.tableContainingArabicText(expectedText).primaryLocator());
        }

        public Builder fallbackText(String visibleText) {
            String literal = xpathLiteral(visibleText);
            return fallback("visible text fallback", By.xpath("//*[self::button or self::a or self::label or self::span or self::div or self::input][normalize-space(.)=" + literal + " or @value=" + literal + " or @aria-label=" + literal + "]"));
        }

        public Builder fallbackContainsText(String partialText) {
            String literal = xpathLiteral(partialText);
            return fallback("partial text fallback", By.xpath("//*[contains(normalize-space(.), " + literal + ") or contains(@aria-label, " + literal + ") or contains(@value, " + literal + ")]"));
        }

        public QatraLocator build() {
            return new QatraLocator(name, candidates, expectedRole, expectedText, expectedArabicText, expectedAction, expectedAccessibleName);
        }

        private static String cssEscape(String value) {
            return value == null ? "" : value.replace("\\", "\\\\").replace("'", "\\'");
        }

        private static String xpathLiteral(String value) {
            if (value == null) {
                return "''";
            }
            if (!value.contains("'")) {
                return "'" + value + "'";
            }
            if (!value.contains("\"")) {
                return "\"" + value + "\"";
            }
            String[] parts = value.split("'");
            StringBuilder concat = new StringBuilder("concat(");
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
}
