package io.github.qatra.web.locators.healing;

import io.github.qatra.web.locators.LocatorCandidate;
import io.github.qatra.web.locators.QatraLocator;
import io.github.qatra.web.locators.healing.accessibility.AccessibilitySignal;
import io.github.qatra.web.locators.healing.accessibility.AccessibleNameResolver;
import io.github.qatra.web.locators.healing.arabic.ArabicSemanticMatcher;
import io.github.qatra.web.locators.healing.arabic.ArabicTextSimilarity;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Locale;

/** Snapshot of evidence used to score a healing candidate. */
public final class HealingEvidence {
    private final String visibleText;
    private final String tagName;
    private final String role;
    private final String cssDirection;
    private final boolean displayed;
    private final boolean enabled;
    private final int matchCount;
    private final boolean semanticMatch;
    private final boolean expectedRoleMatch;
    private final boolean expectedTextMatch;
    private final boolean expectedArabicTextMatch;
    private final boolean expectedActionMatch;
    private final boolean arabicSemanticMatch;
    private final boolean expectedAccessibleNameMatch;
    private final String matchedArabicPhrase;
    private final String compositeText;
    private final AccessibilitySignal accessibility;

    private HealingEvidence(String visibleText, String tagName, String role, String cssDirection,
                            boolean displayed, boolean enabled, int matchCount,
                            boolean semanticMatch, boolean expectedRoleMatch,
                            boolean expectedTextMatch, boolean expectedArabicTextMatch,
                            boolean expectedActionMatch, boolean arabicSemanticMatch,
                            boolean expectedAccessibleNameMatch,
                            String matchedArabicPhrase, String compositeText,
                            AccessibilitySignal accessibility) {
        this.visibleText = visibleText;
        this.tagName = tagName;
        this.role = role;
        this.cssDirection = cssDirection;
        this.displayed = displayed;
        this.enabled = enabled;
        this.matchCount = matchCount;
        this.semanticMatch = semanticMatch;
        this.expectedRoleMatch = expectedRoleMatch;
        this.expectedTextMatch = expectedTextMatch;
        this.expectedArabicTextMatch = expectedArabicTextMatch;
        this.expectedActionMatch = expectedActionMatch;
        this.arabicSemanticMatch = arabicSemanticMatch;
        this.expectedAccessibleNameMatch = expectedAccessibleNameMatch;
        this.matchedArabicPhrase = matchedArabicPhrase;
        this.compositeText = compositeText;
        this.accessibility = accessibility;
    }

    public static HealingEvidence collect(WebDriver driver, WebElement element, LocatorCandidate candidate,
                                          QatraLocator locator, int matchCount) {
        String text = safe(() -> element.getText());
        String tag = safe(() -> element.getTagName());
        String ariaLabel = safe(() -> element.getAttribute("aria-label"));
        String value = safe(() -> element.getAttribute("value"));
        String title = safe(() -> element.getAttribute("title"));
        String placeholder = safe(() -> element.getAttribute("placeholder"));
        String dataTestId = firstNonBlank(safe(() -> element.getAttribute("data-testid")),
                firstNonBlank(safe(() -> element.getAttribute("data-test")), safe(() -> element.getAttribute("data-qa"))));
        AccessibilitySignal accessibility = AccessibleNameResolver.resolve(driver, element);
        String compositeText = String.join(" ", text, ariaLabel, value, title, placeholder, dataTestId,
                accessibility.accessibleName(), accessibility.labelText(), accessibility.ariaLabel()).trim();
        String role = firstNonBlank(accessibility.role(), firstNonBlank(safe(() -> element.getAttribute("role")), inferRole(tag, safe(() -> element.getAttribute("type")))));
        String direction = safe(() -> element.getCssValue("direction"));
        if ((direction == null || direction.isBlank()) && driver instanceof JavascriptExecutor js) {
            direction = String.valueOf(js.executeScript("return window.getComputedStyle(arguments[0]).direction;", element));
        }
        boolean displayed = safeBoolean(element::isDisplayed);
        boolean enabled = safeBoolean(element::isEnabled);

        String expectedRole = normalize(locator.expectedRole());
        String expectedText = normalize(locator.expectedText());
        String expectedArabicText = normalize(locator.expectedArabicText());
        String expectedAccessibleName = normalize(locator.expectedAccessibleName());
        String expectedAction = locator.expectedAction();
        String normalizedText = normalize(compositeText);
        String normalizedRole = normalize(role);
        String normalizedAccessibleName = normalize(accessibility.accessibleName());

        boolean roleMatch = expectedRole != null && expectedRole.equals(normalizedRole);
        boolean textMatch = expectedText != null && normalizedText != null && normalizedText.contains(expectedText);
        boolean accessibleNameMatch = expectedAccessibleName != null && normalizedAccessibleName != null &&
                (normalizedAccessibleName.equals(expectedAccessibleName) || normalizedAccessibleName.contains(expectedAccessibleName));
        boolean arabicTextMatch = expectedArabicText != null && ArabicSemanticMatcher.matchesExpectedArabicText(locator.expectedArabicText(), compositeText);
        boolean actionMatch = expectedAction != null && !expectedAction.isBlank() && ArabicSemanticMatcher.matchesAction(expectedAction, compositeText);
        String matchedPhrase = actionMatch ? ArabicSemanticMatcher.bestMatchingActionLabel(expectedAction, compositeText) : "";
        boolean arabicSemanticMatch = arabicTextMatch || actionMatch || (expectedArabicText != null && ArabicTextSimilarity.similarityPercent(compositeText, locator.expectedArabicText()) >= 82);
        String source = candidate.source().toLowerCase(Locale.ROOT);
        boolean accessibilitySource = source.contains("accessibility") || source.contains("aria") || source.contains("label") || source.contains("placeholder");
        boolean semanticMatch = roleMatch || textMatch || accessibleNameMatch || arabicSemanticMatch || source.contains("data-testid") || accessibilitySource;

        return new HealingEvidence(text, tag, role, direction, displayed, enabled, matchCount,
                semanticMatch, roleMatch, textMatch, arabicTextMatch,
                actionMatch, arabicSemanticMatch, accessibleNameMatch,
                matchedPhrase, compositeText, accessibility);
    }

    public String visibleText() { return visibleText; }
    public String tagName() { return tagName; }
    public String role() { return role; }
    public String cssDirection() { return cssDirection; }
    public boolean displayed() { return displayed; }
    public boolean enabled() { return enabled; }
    public int matchCount() { return matchCount; }
    public boolean semanticMatch() { return semanticMatch; }
    public boolean expectedRoleMatch() { return expectedRoleMatch; }
    public boolean expectedTextMatch() { return expectedTextMatch; }
    public boolean expectedArabicTextMatch() { return expectedArabicTextMatch; }
    public boolean expectedActionMatch() { return expectedActionMatch; }
    public boolean arabicSemanticMatch() { return arabicSemanticMatch; }
    public boolean expectedAccessibleNameMatch() { return expectedAccessibleNameMatch; }
    public String matchedArabicPhrase() { return matchedArabicPhrase; }
    public String compositeText() { return compositeText; }
    public AccessibilitySignal accessibility() { return accessibility; }

    private static String inferRole(String tagName, String type) {
        String tag = normalize(tagName);
        if ("button".equals(tag)) return "button";
        if ("a".equals(tag)) return "link";
        if ("select".equals(tag)) return "combobox";
        if ("textarea".equals(tag)) return "textbox";
        if ("input".equals(tag)) {
            String t = normalize(type);
            if ("checkbox".equals(t)) return "checkbox";
            if ("radio".equals(t)) return "radio";
            if ("button".equals(t) || "submit".equals(t) || "reset".equals(t)) return "button";
            return "textbox";
        }
        return null;
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String firstNonBlank(String first, String second) {
        return first != null && !first.isBlank() ? first : second;
    }

    private interface SupplierWithException<T> { T get() throws Exception; }
    private static String safe(SupplierWithException<String> supplier) {
        try { return supplier.get(); } catch (Exception ignored) { return ""; }
    }
    private interface BooleanSupplierWithException { boolean get() throws Exception; }
    private static boolean safeBoolean(BooleanSupplierWithException supplier) {
        try { return supplier.get(); } catch (Exception ignored) { return false; }
    }
}
