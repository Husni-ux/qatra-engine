package io.github.qatra.web.locators.healing.accessibility;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Locale;

/**
 * Resolves practical accessibility identity signals for locator healing.
 *
 * <p>This is not a full W3C accessible-name implementation. It is a pragmatic
 * automation-focused resolver that checks the same signals teams should use for
 * stable locators: role, aria-label, aria-labelledby, associated label, placeholder,
 * title, alt, value, and visible text.</p>
 */
public final class AccessibleNameResolver {

    private AccessibleNameResolver() {}

    public static AccessibilitySignal resolve(WebDriver driver, WebElement element) {
        String tagName = safe(() -> element.getTagName());
        String type = safe(() -> element.getAttribute("type"));
        String role = firstNonBlank(safe(() -> element.getAttribute("role")), inferRole(tagName, type));
        String ariaLabel = safe(() -> element.getAttribute("aria-label"));
        String ariaLabelledBy = safe(() -> element.getAttribute("aria-labelledby"));
        String labelText = associatedLabelText(driver, element, ariaLabelledBy);
        String placeholder = safe(() -> element.getAttribute("placeholder"));
        String title = safe(() -> element.getAttribute("title"));
        String alt = safe(() -> element.getAttribute("alt"));
        String value = safe(() -> element.getAttribute("value"));
        String visibleText = safe(() -> element.getText());

        String accessibleName = firstNonBlank(
                ariaLabel,
                firstNonBlank(labelText,
                        firstNonBlank(alt,
                                firstNonBlank(title,
                                        firstNonBlank(placeholder,
                                                firstNonBlank(value, visibleText))))));

        return new AccessibilitySignal(role, accessibleName, ariaLabel, labelText, placeholder, title);
    }

    public static String normalize(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    public static boolean matches(String actual, String expected) {
        String a = normalize(actual);
        String e = normalize(expected);
        return !a.isBlank() && !e.isBlank() && (a.equals(e) || a.contains(e));
    }

    private static String associatedLabelText(WebDriver driver, WebElement element, String ariaLabelledBy) {
        if (!(driver instanceof JavascriptExecutor js)) {
            return "";
        }
        try {
            Object value = js.executeScript("""
                    const el = arguments[0];
                    const labelledBy = arguments[1];
                    const parts = [];
                    if (labelledBy) {
                      labelledBy.split(/\s+/).forEach(id => {
                        const ref = document.getElementById(id);
                        if (ref && ref.innerText) parts.push(ref.innerText.trim());
                      });
                    }
                    if (el.id) {
                      document.querySelectorAll('label[for="' + CSS.escape(el.id) + '"]').forEach(label => {
                        if (label.innerText) parts.push(label.innerText.trim());
                      });
                    }
                    const wrappingLabel = el.closest('label');
                    if (wrappingLabel && wrappingLabel.innerText) parts.push(wrappingLabel.innerText.trim());
                    const previousLabel = el.previousElementSibling;
                    if (previousLabel && previousLabel.tagName && previousLabel.tagName.toLowerCase() === 'label') {
                      parts.push(previousLabel.innerText.trim());
                    }
                    return parts.filter(Boolean).join(' ').trim();
                    """, element, ariaLabelledBy);
            return value == null ? "" : String.valueOf(value).trim();
        } catch (Exception ignored) {
            return "";
        }
    }

    private static String inferRole(String tagName, String type) {
        String tag = normalize(tagName);
        String inputType = normalize(type);
        if ("button".equals(tag)) return "button";
        if ("a".equals(tag)) return "link";
        if ("select".equals(tag)) return "combobox";
        if ("textarea".equals(tag)) return "textbox";
        if ("input".equals(tag)) {
            if ("checkbox".equals(inputType)) return "checkbox";
            if ("radio".equals(inputType)) return "radio";
            if ("button".equals(inputType) || "submit".equals(inputType) || "reset".equals(inputType)) return "button";
            return "textbox";
        }
        return "";
    }

    private static String firstNonBlank(String first, String second) {
        return first != null && !first.isBlank() ? first : (second == null ? "" : second);
    }

    private interface SupplierWithException<T> { T get() throws Exception; }
    private static String safe(SupplierWithException<String> supplier) {
        try { return supplier.get(); } catch (Exception ignored) { return ""; }
    }
}
