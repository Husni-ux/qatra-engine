package io.github.qatra.web.rtl;

import io.github.qatra.core.logger.QatraLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * QATRA Arabic/RTL analysis engine.
 *
 * <p>This class contains the reusable low-level rules behind QATRA's Arabic-first
 * assertions and page scanner. It intentionally starts small and practical:
 * direction, Arabic text presence, Arabic digits, and common encoding issues.</p>
 */
public final class RtlEngine {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    /*
     * Use find()-friendly patterns instead of matches()-with-.* patterns.
     * matches() + ".*" is fragile with multi-line page text because Java regex dot does not
     * match line terminators unless DOTALL is enabled. QATRA collects whole-page text that often
     * contains new lines, so find() gives more reliable RTL detection.
     */
    private static final Pattern ARABIC_LETTERS = Pattern.compile("[\\u0600-\\u06FF\\u0750-\\u077F\\u08A0-\\u08FF\\uFB50-\\uFDFF\\uFE70-\\uFEFF]");
    private static final Pattern ARABIC_INDIC_DIGITS = Pattern.compile("[\\u0660-\\u0669]");
    private static final Pattern EASTERN_ARABIC_INDIC_DIGITS = Pattern.compile("[\\u06F0-\\u06F9]");
    private static final Pattern ENGLISH_DIGITS = Pattern.compile("[0-9]");
    private static final Pattern LATIN_LETTERS = Pattern.compile("[A-Za-z]");
    private static final Pattern REPLACEMENT_CHARACTER = Pattern.compile("\\uFFFD");
    private static final Pattern QUESTION_MARK_MOJIBAKE = Pattern.compile("\\?{2,}");
    private static final Pattern COMMON_ARABIC_MOJIBAKE = Pattern.compile("(?:[ÃÂØÙÛÐ][\\s\\S]){2,}");

    private RtlEngine() {
    }

    public static boolean containsArabicText(String text) {
        return ARABIC_LETTERS.matcher(safe(text)).find();
    }

    public static boolean containsArabicDigits(String text) {
        String safeText = safe(text);
        return ARABIC_INDIC_DIGITS.matcher(safeText).find()
                || EASTERN_ARABIC_INDIC_DIGITS.matcher(safeText).find();
    }

    public static boolean containsEnglishDigits(String text) {
        return ENGLISH_DIGITS.matcher(safe(text)).find();
    }

    public static boolean containsLatinLetters(String text) {
        return LATIN_LETTERS.matcher(safe(text)).find();
    }

    public static boolean containsMixedArabicAndLatin(String text) {
        String safeText = safe(text);
        return containsArabicText(safeText) && containsLatinLetters(safeText);
    }

    public static boolean hasBrokenArabicEncoding(String text) {
        String safeText = safe(text);
        return REPLACEMENT_CHARACTER.matcher(safeText).find()
                || QUESTION_MARK_MOJIBAKE.matcher(safeText).find()
                || COMMON_ARABIC_MOJIBAKE.matcher(safeText).find();
    }

    public static boolean isDirectionRtl(WebElement element) {
        return "rtl".equalsIgnoreCase(effectiveDirection(element));
    }

    public static boolean isDirectionLtr(WebElement element) {
        return "ltr".equalsIgnoreCase(effectiveDirection(element));
    }

    public static String effectiveDirection(WebElement element) {
        if (element == null) {
            return "";
        }

        String dir = safe(element.getAttribute("dir")).trim();
        if (!dir.isBlank()) {
            return normalizeDirection(dir);
        }

        String cssDirection = safe(element.getCssValue("direction")).trim();
        if (!cssDirection.isBlank()) {
            return normalizeDirection(cssDirection);
        }

        return "";
    }

    public static String effectiveDirection(WebDriver driver, WebElement element) {
        if (element == null) {
            return "";
        }

        String dir = safe(element.getAttribute("dir")).trim();
        if (!dir.isBlank()) {
            return normalizeDirection(dir);
        }

        try {
            if (driver instanceof JavascriptExecutor js) {
                Object value = js.executeScript("return window.getComputedStyle(arguments[0]).direction;", element);
                String computed = normalizeDirection(safe(String.valueOf(value)));
                if (!computed.isBlank()) {
                    return computed;
                }
            }
        } catch (Throwable ignored) {
            // Fall through to Selenium CSS value.
        }

        String cssDirection = safe(element.getCssValue("direction")).trim();
        return normalizeDirection(cssDirection);
    }

    public static String normalizeDirection(String direction) {
        String value = safe(direction).trim().toLowerCase(Locale.ROOT);
        if (value.contains("rtl")) return "rtl";
        if (value.contains("ltr")) return "ltr";
        return value;
    }


    /**
     * Collect Arabic-relevant page text in a browser-friendly way.
     *
     * <p>Some pages, especially data URLs or dynamic pages, may return an empty
     * value from Selenium's {@code body.getText()} even though specific elements
     * contain text. This method combines visible text, textContent, and common
     * user-facing attributes so page-level Arabic checks remain reliable.</p>
     */
    public static String collectPageText(WebDriver driver) {
        if (driver == null) {
            return "";
        }

        StringBuilder text = new StringBuilder();

        try {
            WebElement body = driver.findElement(By.tagName("body"));
            text.append(' ').append(safe(body.getText()));
        } catch (Throwable ignored) {
            // Continue with JavaScript and element traversal.
        }

        try {
            if (driver instanceof JavascriptExecutor js) {
                Object value = js.executeScript("""
                        const root = document.documentElement;
                        if (!root) return '';
                        const parts = [];
                        parts.push(root.innerText || '');
                        parts.push(root.textContent || '');
                        document.querySelectorAll('*').forEach(function (el) {
                            parts.push(el.innerText || '');
                            parts.push(el.textContent || '');
                            parts.push(el.getAttribute('placeholder') || '');
                            parts.push(el.getAttribute('value') || '');
                            parts.push(el.getAttribute('aria-label') || '');
                            parts.push(el.getAttribute('title') || '');
                            parts.push(el.getAttribute('alt') || '');
                        });
                        return parts.join(' ');
                        """);
                text.append(' ').append(safe(String.valueOf(value)));
            }
        } catch (Throwable ignored) {
            // Continue with Selenium element traversal.
        }

        try {
            for (WebElement element : driver.findElements(By.cssSelector("html, body, body *"))) {
                try {
                    text.append(' ').append(safe(element.getText()));
                    text.append(' ').append(safe(element.getAttribute("textContent")));
                    text.append(' ').append(safe(element.getAttribute("placeholder")));
                    text.append(' ').append(safe(element.getAttribute("value")));
                    text.append(' ').append(safe(element.getAttribute("aria-label")));
                    text.append(' ').append(safe(element.getAttribute("title")));
                    text.append(' ').append(safe(element.getAttribute("alt")));
                } catch (StaleElementReferenceException ignored) {
                    // Dynamic element changed during collection; skip it.
                }
            }
        } catch (Throwable ignored) {
            // Continue with page source fallback.
        }

        try {
            text.append(' ').append(safe(driver.getPageSource()));
        } catch (Throwable ignored) {
            // Last fallback failed; return whatever we collected.
        }

        return text.toString();
    }

    public static RtlScanResult scanPage(WebDriver driver) {
        return scanPage(driver, RtlScanConfig.fromConfig());
    }

    public static RtlScanResult scanPage(WebDriver driver, RtlScanConfig config) {
        if (driver == null) {
            return RtlScanResult.empty();
        }

        RtlScanConfig effectiveConfig = config == null ? RtlScanConfig.fromConfig() : config;
        if (!effectiveConfig.isEnabled()) {
            LOG.info("RTL page scan is disabled by configuration.");
            return RtlScanResult.empty();
        }

        List<RtlIssue> issues = new ArrayList<>();
        int scanned = 0;
        int arabicElements = 0;

        try {
            List<WebElement> elements = driver.findElements(By.cssSelector(effectiveConfig.selector()));
            scanned = elements.size();

            for (WebElement element : elements) {
                try {
                    String visibleText = safe(element.getText());
                    String textContent = safe(element.getAttribute("textContent"));
                    String placeholder = safe(element.getAttribute("placeholder"));
                    String value = safe(element.getAttribute("value"));
                    String ariaLabel = safe(element.getAttribute("aria-label"));
                    String title = safe(element.getAttribute("title"));
                    String alt = safe(element.getAttribute("alt"));
                    String combined = String.join(" ", visibleText, textContent, placeholder, value, ariaLabel, title, alt)
                            .replaceAll("\\s+", " ")
                            .trim();

                    if (combined.isBlank()) {
                        continue;
                    }

                    boolean hasArabic = containsArabicText(combined);
                    boolean hasArabicDigits = containsArabicDigits(combined);
                    boolean hasEnglishDigits = containsEnglishDigits(combined);
                    String mixedDirectionContent = String.join(" ", visibleText, placeholder, value, ariaLabel, title, alt)
                            .replaceAll("\\s+", " ")
                            .trim();
                    boolean hasMixedDirection = containsMixedArabicAndLatin(mixedDirectionContent);
                    boolean hasBrokenEncoding = hasBrokenArabicEncoding(combined);
                    String elementName = describe(element);
                    String direction = effectiveDirection(driver, element);
                    String textAlign = safe(element.getCssValue("text-align")).trim().toLowerCase(Locale.ROOT);
                    String expectedDigits = safe(element.getAttribute("data-qatra-digits")).trim().toLowerCase(Locale.ROOT);

                    if (hasArabic) {
                        arabicElements++;

                        if (effectiveConfig.shouldScan(RtlIssueType.DIRECTION) && !"rtl".equalsIgnoreCase(direction)) {
                            issues.add(new RtlIssue(
                                    RtlIssueSeverity.WARNING,
                                    RtlIssueType.DIRECTION,
                                    elementName,
                                    "Arabic content detected but element direction is not RTL.",
                                    "direction=" + blankAsUnknown(direction) + ", text=" + summarize(combined),
                                    "Set dir='rtl' or CSS direction: rtl on the element or a parent container."
                            ));
                        }

                        if (effectiveConfig.shouldScan(RtlIssueType.ALIGNMENT) && "left".equalsIgnoreCase(textAlign)) {
                            issues.add(new RtlIssue(
                                    RtlIssueSeverity.INFO,
                                    RtlIssueType.ALIGNMENT,
                                    elementName,
                                    "Arabic content is rendered with left text alignment.",
                                    "text-align=" + textAlign + ", text=" + summarize(combined),
                                    "For Arabic sections, consider text-align: right or start, depending on the design system."
                            ));
                        }

                        if (effectiveConfig.shouldScan(RtlIssueType.MIXED_DIRECTION) && hasMixedDirection) {
                            issues.add(new RtlIssue(
                                    RtlIssueSeverity.INFO,
                                    RtlIssueType.MIXED_DIRECTION,
                                    elementName,
                                    "Mixed Arabic and Latin text detected.",
                                    summarize(mixedDirectionContent),
                                    "Verify visual order. Consider <bdi>, unicode-bidi, or explicit direction for dynamic mixed content."
                            ));
                        }
                    }

                    if (effectiveConfig.shouldScan(RtlIssueType.PLACEHOLDER) && !placeholder.isBlank() && containsArabicText(placeholder) && !"rtl".equalsIgnoreCase(direction)) {
                        issues.add(new RtlIssue(
                                RtlIssueSeverity.WARNING,
                                RtlIssueType.PLACEHOLDER,
                                elementName,
                                "Arabic placeholder detected but element direction is not RTL.",
                                "placeholder=" + summarize(placeholder) + ", direction=" + blankAsUnknown(direction),
                                "Arabic inputs should usually have dir='rtl' or inherit RTL direction."
                        ));
                    }

                    if (effectiveConfig.shouldScan(RtlIssueType.DIGITS) && "arabic".equals(expectedDigits) && hasEnglishDigits) {
                        issues.add(new RtlIssue(
                                RtlIssueSeverity.WARNING,
                                RtlIssueType.DIGITS,
                                elementName,
                                "Arabic digits were expected but English digits were found.",
                                summarize(combined),
                                "Use Arabic-Indic digits when this field is configured with data-qatra-digits='arabic'."
                        ));
                    }

                    if (effectiveConfig.shouldScan(RtlIssueType.DIGITS) && "english".equals(expectedDigits) && hasArabicDigits) {
                        issues.add(new RtlIssue(
                                RtlIssueSeverity.INFO,
                                RtlIssueType.DIGITS,
                                elementName,
                                "English digits were expected but Arabic-Indic digits were found.",
                                summarize(combined),
                                "Use English digits when this field is configured with data-qatra-digits='english'."
                        ));
                    }

                    if (effectiveConfig.shouldScan(RtlIssueType.ENCODING) && hasBrokenEncoding) {
                        issues.add(new RtlIssue(
                                RtlIssueSeverity.ERROR,
                                RtlIssueType.ENCODING,
                                elementName,
                                "Possible broken Arabic encoding or mojibake detected.",
                                summarize(combined),
                                "Verify UTF-8 encoding, response headers, database collation, and frontend rendering."
                        ));
                    }
                } catch (StaleElementReferenceException ignored) {
                    // Dynamic page changed during scan; ignore and continue.
                }
            }
        } catch (Throwable t) {
            LOG.warn("RTL page scan failed: {}", t.getMessage());
            issues.add(new RtlIssue(
                    RtlIssueSeverity.ERROR,
                    RtlIssueType.SCAN,
                    "page",
                    "RTL scan failed before completion.",
                    t.getMessage(),
                    "Check page stability and browser session state."
            ));
        }

        return new RtlScanResult(issues, scanned, arabicElements);
    }

    private static String blankAsUnknown(String value) {
        String safeValue = safe(value).trim();
        return safeValue.isBlank() ? "unknown" : safeValue;
    }

    public static String describe(WebElement element) {
        if (element == null) {
            return "unknown-element";
        }

        try {
            String tag = safe(element.getTagName());
            String id = safe(element.getAttribute("id"));
            String name = safe(element.getAttribute("name"));
            String classes = safe(element.getAttribute("class")).trim().replaceAll("\\s+", ".");

            StringBuilder selector = new StringBuilder(tag.isBlank() ? "element" : tag);
            if (!id.isBlank()) {
                selector.append("#").append(id);
            }
            if (!classes.isBlank()) {
                selector.append(".").append(classes);
            }
            if (!name.isBlank()) {
                selector.append("[name='").append(name).append("']");
            }
            return selector.toString();
        } catch (Throwable t) {
            return "stale-or-detached-element";
        }
    }

    public static String summarize(String text) {
        String value = safe(text).replaceAll("\\s+", " ").trim();
        if (value.length() <= 120) {
            return value;
        }
        return value.substring(0, 117) + "...";
    }

    public static String safe(String value) {
        return value == null ? "" : value;
    }
}
