package io.github.qatra.web.assertions.engine;

import io.github.qatra.web.rtl.RtlEngine;
import org.openqa.selenium.WebElement;

import java.util.regex.Pattern;

/** Text-focused assertions for WebElement content. */
public class TextAssert {

    private final ElementAssert parent;

    TextAssert(ElementAssert parent) {
        this.parent = parent;
    }

    public TextAssert contains(String expectedText) {
        WebElement element = parent.resolvePresent();
        String actual = text(element);
        if (!actual.contains(safe(expectedText))) {
            parent.fail("Text contains", safe(expectedText), actual, element);
        }
        return this;
    }

    public TextAssert equalsTo(String expectedText) {
        WebElement element = parent.resolvePresent();
        String actual = text(element).trim();
        String expected = safe(expectedText).trim();
        if (!actual.equals(expected)) {
            parent.fail("Text equals", expected, actual, element);
        }
        return this;
    }

    public TextAssert notBlank() {
        WebElement element = parent.resolvePresent();
        String actual = text(element);
        if (actual.isBlank()) {
            parent.fail("Text not blank", "non-blank text", "blank text", element);
        }
        return this;
    }

    public TextAssert matches(String regex) {
        WebElement element = parent.resolvePresent();
        String actual = text(element);
        if (!Pattern.compile(regex, Pattern.DOTALL | Pattern.UNICODE_CASE).matcher(actual).find()) {
            parent.fail("Text matches", regex, actual, element);
        }
        return this;
    }

    public TextAssert containsArabic() {
        WebElement element = parent.resolvePresent();
        String actual = text(element);
        if (!RtlEngine.containsArabicText(actual)) {
            parent.fail("Text contains Arabic", "Arabic characters", actual, element);
        }
        return this;
    }

    public TextAssert containsEnglishDigits() {
        WebElement element = parent.resolvePresent();
        String actual = text(element);
        if (!RtlEngine.containsEnglishDigits(actual)) {
            parent.fail("Text contains English digits", "digits 0-9", actual, element);
        }
        return this;
    }

    public TextAssert containsArabicDigits() {
        WebElement element = parent.resolvePresent();
        String actual = text(element);
        if (!RtlEngine.containsArabicDigits(actual)) {
            parent.fail("Text contains Arabic digits", "Arabic-Indic digits", actual, element);
        }
        return this;
    }

    public ElementAssert and() {
        return parent;
    }

    private static String text(WebElement element) {
        return AssertionEvidence.readableContent(element);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
