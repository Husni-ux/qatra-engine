package io.github.qatra.web.locators.advisor;

import io.github.qatra.web.locators.LocatorCandidate;
import io.github.qatra.web.locators.LocatorRiskLevel;
import io.github.qatra.web.locators.QatraLocator;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Proactive locator quality advisor.
 *
 * <p>Unlike self-healing, this class is designed to detect locator risk before the locator breaks.
 * It scores the primary locator, inspects fallback candidates, and recommends more stable locator
 * strategies such as data-testid, aria-label, semantic role, nearby Arabic labels, and accessible names.</p>
 */
public final class ProactiveLocatorQualityAdvisor {

    private ProactiveLocatorQualityAdvisor() {
    }

    public static LocatorAdvisorReport analyze(By locator) {
        return analyze("Selenium locator", locator, List.of(), null, null, null, null);
    }

    public static LocatorAdvisorReport analyze(String name, By locator) {
        return analyze(name, locator, List.of(), null, null, null, null);
    }

    public static LocatorAdvisorReport analyze(QatraLocator locator) {
        if (locator == null) {
            throw new IllegalArgumentException("QatraLocator cannot be null");
        }
        List<LocatorCandidate> candidates = locator.candidates();
        List<By> fallbackLocators = new ArrayList<>();
        List<String> notes = new ArrayList<>();
        for (LocatorCandidate candidate : candidates) {
            if (candidate.primary()) {
                continue;
            }
            fallbackLocators.add(candidate.locator());
            notes.add("Fallback [" + candidate.source() + "]: " + candidate.locator());
        }
        return analyze(locator.name(), locator.primaryLocator(), fallbackLocators, notes,
                locator.expectedRole(), locator.expectedArabicText(), locator.expectedAction());
    }

    private static LocatorAdvisorReport analyze(String name,
                                                By primary,
                                                List<By> fallbackLocators,
                                                List<String> candidateNotes,
                                                String expectedRole,
                                                String expectedArabicText,
                                                String expectedAction) {
        if (primary == null) {
            throw new IllegalArgumentException("Locator cannot be null");
        }

        List<LocatorQualityIssue> issues = new ArrayList<>();
        List<String> strengths = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        int score = 82;

        String raw = String.valueOf(primary);
        String lower = raw.toLowerCase(Locale.ROOT);

        if (isStableId(lower)) {
            score += 12;
            strengths.add("Uses a stable-looking id locator.");
        }
        if (usesTestAttribute(lower)) {
            score += 20;
            strengths.add("Uses a test-specific attribute such as data-testid/data-test/data-qa.");
        }
        if (usesAriaOrAccessibility(lower)) {
            score += 12;
            strengths.add("Uses an accessibility-oriented attribute such as aria-label or role.");
        }
        if (containsArabic(raw)) {
            score += 4;
            strengths.add("Locator contains Arabic text, which can be readable for Arabic UI flows.");
            recommendations.add("Use Arabic text locators as controlled fallbacks, not as the only locator when text changes frequently.");
        }

        if (isAbsoluteXpath(lower)) {
            score -= 55;
            issues.add(issue(LocatorRiskLevel.CRITICAL, "ABSOLUTE_XPATH",
                    "Absolute XPath is very fragile and tightly coupled to the DOM hierarchy.", -55,
                    "Replace it with data-testid, stable id, aria-label, accessible role, or a short relative XPath."));
            recommendations.add("Ask developers to add a stable data-testid for this element.");
        }
        if (isIndexBased(lower)) {
            score -= 25;
            issues.add(issue(LocatorRiskLevel.HIGH, "INDEX_BASED_SELECTOR",
                    "Locator depends on element position such as [1], [2], nth-child, or nth-of-type.", -25,
                    "Anchor the selector to a label, semantic attribute, accessible name, or component root."));
        }
        if (usesGeneratedFrameworkTokens(lower)) {
            score -= 30;
            issues.add(issue(LocatorRiskLevel.HIGH, "GENERATED_FRAMEWORK_TOKEN",
                    "Locator appears to depend on generated framework classes or attributes.", -30,
                    "Avoid generated Angular/React/MUI/CSS-in-JS classes. Prefer semantic attributes."));
        }
        if (usesClassOnly(lower)) {
            score -= 15;
            issues.add(issue(LocatorRiskLevel.MEDIUM, "CLASS_ONLY",
                    "Class-name locators are often styling-oriented and may change without business meaning.", -15,
                    "Use class only when it is stable and business-specific; otherwise prefer data-testid."));
        }
        if (usesTextOnly(lower)) {
            score -= 8;
            issues.add(issue(LocatorRiskLevel.MEDIUM, "TEXT_ONLY",
                    "Text-only locators are readable but may change with localization or content updates.", -8,
                    "Use text as a fallback together with a stable primary locator."));
        }
        if (isTooLong(raw)) {
            score -= 10;
            issues.add(issue(LocatorRiskLevel.MEDIUM, "LONG_SELECTOR",
                    "Locator is long and harder to maintain.", -10,
                    "Prefer a shorter locator scoped by component root or stable attribute."));
        }
        if (looksDynamicId(lower)) {
            score -= 25;
            issues.add(issue(LocatorRiskLevel.HIGH, "DYNAMIC_ID",
                    "ID value looks generated or dynamic.", -25,
                    "Ask for a stable data-testid or use a semantic fallback chain."));
        }
        if (hasNoBusinessSignal(lower)) {
            score -= 10;
            issues.add(issue(LocatorRiskLevel.MEDIUM, "NO_BUSINESS_SIGNAL",
                    "Locator has no clear business-readable signal.", -10,
                    "Prefer locators that communicate intent: data-testid='save-request', aria-label='حفظ الطلب', role='button'."));
        }

        if (fallbackLocators != null && !fallbackLocators.isEmpty()) {
            score += Math.min(10, fallbackLocators.size() * 3);
            strengths.add("Has fallback locator candidates for controlled self-healing.");
            for (By fallback : fallbackLocators) {
                String fallbackRaw = String.valueOf(fallback);
                String fallbackLower = fallbackRaw.toLowerCase(Locale.ROOT);
                if (isAbsoluteXpath(fallbackLower) || isIndexBased(fallbackLower)) {
                    issues.add(issue(LocatorRiskLevel.MEDIUM, "WEAK_FALLBACK",
                            "One fallback locator is also fragile: " + fallbackRaw, -5,
                            "Keep fallback candidates safe, semantic, and low-risk."));
                    score -= 5;
                }
            }
        } else {
            recommendations.add("Add safe fallback locators for important business elements, especially data-testid, aria-label, Arabic text, or nearby Arabic label.");
        }

        if (notBlank(expectedRole)) {
            score += 4;
            strengths.add("Defines expected role: " + expectedRole);
        }
        if (notBlank(expectedArabicText)) {
            score += 4;
            strengths.add("Defines expected Arabic text: " + expectedArabicText);
        }
        if (notBlank(expectedAction)) {
            score += 4;
            strengths.add("Defines expected business action: " + expectedAction);
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Locator looks acceptable. Keep it stable, short, semantic, and backed by tests.");
        }

        score = Math.max(0, Math.min(100, score));
        LocatorRiskLevel risk = riskFor(score, issues);
        String suggested = suggestedStableLocator(name, expectedAction, expectedArabicText, lower);
        return new LocatorAdvisorReport(name, primary, score, risk, issues, strengths, recommendations,
                candidateNotes == null ? List.of() : candidateNotes, suggested);
    }

    private static LocatorQualityIssue issue(LocatorRiskLevel severity, String code, String message, int impact, String recommendation) {
        return new LocatorQualityIssue(severity, code, message, impact, recommendation);
    }

    private static boolean isStableId(String lower) {
        return lower.contains("by.id") && !looksDynamicId(lower);
    }

    private static boolean usesTestAttribute(String lower) {
        return lower.contains("data-testid") || lower.contains("data-test") || lower.contains("data-qa");
    }

    private static boolean usesAriaOrAccessibility(String lower) {
        return lower.contains("aria-label") || lower.contains("role=") || lower.contains("@role") || lower.contains("label");
    }

    private static boolean isAbsoluteXpath(String lower) {
        return lower.contains("by.xpath: /html") || lower.contains("xpath: /html") || lower.contains("/body/");
    }

    private static boolean isIndexBased(String lower) {
        return lower.matches(".*(\\[[0-9]+]|nth-child|nth-of-type).* ") ||
                lower.contains("[1]") || lower.contains("[2]") || lower.contains("[3]") ||
                lower.contains("nth-child") || lower.contains("nth-of-type");
    }

    private static boolean usesGeneratedFrameworkTokens(String lower) {
        return lower.matches(".*(_ngcontent|ng-|mat-|css-[a-z0-9]+|jss[0-9]+|mui[a-z0-9_-]*-[0-9]|chakra-[a-z0-9_-]*-[0-9]).*");
    }

    private static boolean usesClassOnly(String lower) {
        return lower.contains("by.classname") || lower.contains("by.class name") ||
                (lower.contains("by.cssselector") && lower.matches(".*by\\.cssselector: \\.[a-z0-9_-]+.*"));
    }

    private static boolean usesTextOnly(String lower) {
        return lower.contains("contains(text()") || lower.contains("normalize-space(.)") || lower.contains("contains(.,");
    }

    private static boolean isTooLong(String raw) {
        return raw != null && raw.length() > 120;
    }

    private static boolean looksDynamicId(String lower) {
        return lower.matches(".*(id[:=][^ ]*(\\d{4,}|[a-f0-9]{8,}|uuid|guid|generated|ember|react|select2|mat-)).*");
    }

    private static boolean hasNoBusinessSignal(String lower) {
        return !(lower.contains("data-") || lower.contains("aria-") || lower.contains("by.id") || lower.contains("name") || lower.contains("label") || lower.contains("role"));
    }

    private static boolean containsArabic(String value) {
        return value != null && value.matches(".*[\\u0600-\\u06FF].*");
    }

    private static boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private static LocatorRiskLevel riskFor(int score, List<LocatorQualityIssue> issues) {
        boolean critical = issues.stream().anyMatch(issue -> issue.severity() == LocatorRiskLevel.CRITICAL);
        if (critical || score < 25) {
            return LocatorRiskLevel.CRITICAL;
        }
        if (score < 50) {
            return LocatorRiskLevel.HIGH;
        }
        if (score < 75) {
            return LocatorRiskLevel.MEDIUM;
        }
        return LocatorRiskLevel.LOW;
    }

    private static String suggestedStableLocator(String name, String expectedAction, String expectedArabicText, String lower) {
        if (usesTestAttribute(lower)) {
            return "Current locator already uses a test attribute. Keep it stable and documented.";
        }
        String base = slug(notBlank(expectedAction) ? expectedAction : notBlank(name) ? name : expectedArabicText);
        if (base.isBlank()) {
            base = "target-element";
        }
        return "By.cssSelector(\"[data-testid='" + base + "']\")";
    }

    private static String slug(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\u0600-\\u06FF]+", "-")
                .replaceAll("^-+|-+$", "");
        if (normalized.matches(".*[\\u0600-\\u06FF].*")) {
            return "arabic-element";
        }
        return normalized;
    }
}
