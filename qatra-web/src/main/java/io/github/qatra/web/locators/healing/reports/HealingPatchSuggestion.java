package io.github.qatra.web.locators.healing.reports;

import io.github.qatra.web.locators.LocatorHealingReport;
import io.github.qatra.web.locators.QatraLocator;
import org.openqa.selenium.By;

/**
 * Human-reviewable patch suggestion produced after a locator is safely healed.
 *
 * <p>QATRA does not modify source code automatically. It generates evidence and a suggested replacement
 * so the automation engineer can review the change and apply it intentionally.</p>
 */
public final class HealingPatchSuggestion {
    private final String locatorName;
    private final By oldLocator;
    private final By newLocator;
    private final String confidence;
    private final String risk;
    private final String suggestedJavaCode;

    public HealingPatchSuggestion(String locatorName, By oldLocator, By newLocator,
                                  String confidence, String risk, String suggestedJavaCode) {
        this.locatorName = locatorName;
        this.oldLocator = oldLocator;
        this.newLocator = newLocator;
        this.confidence = confidence;
        this.risk = risk;
        this.suggestedJavaCode = suggestedJavaCode;
    }

    public static HealingPatchSuggestion from(QatraLocator locator, LocatorHealingReport report) {
        By resolved = report.resolvedLocator();
        return new HealingPatchSuggestion(
                locator == null ? report.name() : locator.name(),
                report.primaryLocator(),
                resolved,
                report.confidence() == null ? "N/A" : report.confidence() + "%",
                report.riskLevel() == null ? "N/A" : report.riskLevel(),
                toJavaBy(resolved)
        );
    }

    public String locatorName() { return locatorName; }
    public By oldLocator() { return oldLocator; }
    public By newLocator() { return newLocator; }
    public String confidence() { return confidence; }
    public String risk() { return risk; }
    public String suggestedJavaCode() { return suggestedJavaCode; }

    public String toJson() {
        return "{" +
                "\n  \"locatorName\": \"" + json(locatorName) + "\"," +
                "\n  \"oldLocator\": \"" + json(String.valueOf(oldLocator)) + "\"," +
                "\n  \"newLocator\": \"" + json(String.valueOf(newLocator)) + "\"," +
                "\n  \"confidence\": \"" + json(confidence) + "\"," +
                "\n  \"risk\": \"" + json(risk) + "\"," +
                "\n  \"suggestedJavaCode\": \"" + json(suggestedJavaCode) + "\"" +
                "\n}";
    }

    public static String toJavaBy(By locator) {
        if (locator == null) {
            return "N/A";
        }
        String text = locator.toString();
        if (text.startsWith("By.id: ")) {
            return "By.id(\"" + java(text.substring(7)) + "\")";
        }
        if (text.startsWith("By.cssSelector: ")) {
            return "By.cssSelector(\"" + java(text.substring(16)) + "\")";
        }
        if (text.startsWith("By.xpath: ")) {
            return "By.xpath(\"" + java(text.substring(10)) + "\")";
        }
        if (text.startsWith("By.name: ")) {
            return "By.name(\"" + java(text.substring(9)) + "\")";
        }
        if (text.startsWith("By.className: ")) {
            return "By.className(\"" + java(text.substring(14)) + "\")";
        }
        if (text.startsWith("By.tagName: ")) {
            return "By.tagName(\"" + java(text.substring(12)) + "\")";
        }
        if (text.startsWith("By.linkText: ")) {
            return "By.linkText(\"" + java(text.substring(13)) + "\")";
        }
        if (text.startsWith("By.partialLinkText: ")) {
            return "By.partialLinkText(\"" + java(text.substring(20)) + "\")";
        }
        return "/* Review manually */ " + text;
    }

    private static String json(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    private static String java(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
