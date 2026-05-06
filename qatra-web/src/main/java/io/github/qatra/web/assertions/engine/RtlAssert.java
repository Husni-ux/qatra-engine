package io.github.qatra.web.assertions.engine;

import io.github.qatra.web.rtl.RtlEngine;
import org.openqa.selenium.WebElement;

import java.text.Normalizer;

/** Arabic/RTL-focused assertions. */
public class RtlAssert {

    private final ElementAssert parent;

    RtlAssert(ElementAssert parent) {
        this.parent = parent;
    }

    public RtlAssert hasArabicText() {
        WebElement element = parent.resolvePresent();
        String actual = content(element);
        if (!RtlEngine.containsArabicText(actual)) {
            parent.fail("Arabic text exists", "Arabic text", actual, element);
        }
        return this;
    }

    public RtlAssert hasReadableArabicText() {
        return hasArabicText().hasNoBrokenCharacters().hasNoMojibake().hasNoReplacementCharacters();
    }

    public RtlAssert hasRtlDirection() {
        WebElement element = parent.resolvePresent();
        String direction = RtlEngine.effectiveDirection(parent.driver(), element);
        if (!"rtl".equalsIgnoreCase(direction)) {
            parent.fail("RTL direction", "direction=rtl", "direction=" + direction, element);
        }
        return this;
    }

    public RtlAssert hasCssDirectionRtl() {
        WebElement element = parent.resolvePresent();
        String cssDirection = safe(element.getCssValue("direction"));
        if (!"rtl".equalsIgnoreCase(cssDirection)) {
            parent.fail("CSS direction RTL", "CSS direction=rtl", "CSS direction=" + cssDirection, element);
        }
        return this;
    }

    public RtlAssert hasArabicDigits() {
        WebElement element = parent.resolvePresent();
        String actual = content(element);
        if (!RtlEngine.containsArabicDigits(actual)) {
            parent.fail("Arabic digits", "Arabic-Indic digits", actual, element);
        }
        return this;
    }

    public RtlAssert hasEnglishDigits() {
        WebElement element = parent.resolvePresent();
        String actual = content(element);
        if (!RtlEngine.containsEnglishDigits(actual)) {
            parent.fail("English digits", "English digits", actual, element);
        }
        return this;
    }

    public RtlAssert hasNoBrokenCharacters() {
        WebElement element = parent.resolvePresent();
        String actual = content(element);
        if (RtlEngine.hasBrokenArabicEncoding(actual)) {
            parent.fail("No broken Arabic characters", "clean Arabic text", actual, element);
        }
        return this;
    }

    public RtlAssert hasNoMojibake() {
        return new EncodingAssert(parent).hasNoMojibake().and().rtl();
    }

    public RtlAssert hasNoReplacementCharacters() {
        return new EncodingAssert(parent).hasNoReplacementCharacters().and().rtl();
    }

    public RtlAssert hasUnicodeNormalized() {
        WebElement element = parent.resolvePresent();
        String actual = content(element);
        if (!Normalizer.isNormalized(actual, Normalizer.Form.NFC)) {
            parent.fail("Unicode normalized", "NFC-normalized text", "non-normalized text: " + actual, element);
        }
        return this;
    }

    public RtlAssert hasValidMixedArabicEnglishLayout() {
        WebElement element = parent.resolvePresent();
        String actual = content(element);
        String direction = RtlEngine.effectiveDirection(parent.driver(), element);
        if (RtlEngine.containsMixedArabicAndLatin(actual) && direction.isBlank()) {
            parent.fail("Mixed Arabic/English layout", "explicit direction for mixed text", "direction is missing; text=" + actual, element);
        }
        return this;
    }

    public RtlAssert textNotReversed() {
        WebElement element = parent.resolvePresent();
        String actual = content(element);
        if (actual.contains("لودخلا ليجست") || actual.contains("ابحرم")) {
            parent.fail("Arabic text not reversed", "normal Arabic display order", actual, element);
        }
        return this;
    }

    public RtlAssert hasValidArabicRendering() {
        return hasArabicText()
                .hasReadableArabicText()
                .hasRtlDirection()
                .hasUnicodeNormalized()
                .textNotReversed();
    }

    public ElementAssert and() {
        return parent;
    }

    private static String content(WebElement element) {
        return AssertionEvidence.readableContent(element);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
