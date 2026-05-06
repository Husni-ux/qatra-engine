package io.github.qatra.web.assertions.engine;

import io.github.qatra.web.rtl.RtlEngine;
import org.openqa.selenium.WebElement;

/** Encoding and mojibake-focused assertions. */
public class EncodingAssert {

    private final ElementAssert parent;

    EncodingAssert(ElementAssert parent) {
        this.parent = parent;
    }

    public EncodingAssert hasNoBrokenArabicCharacters() {
        WebElement element = parent.resolvePresent();
        String actual = content(element);
        if (RtlEngine.hasBrokenArabicEncoding(actual)) {
            parent.fail("No broken Arabic characters", "clean Arabic content", actual, element);
        }
        return this;
    }

    public EncodingAssert hasNoMojibake() {
        WebElement element = parent.resolvePresent();
        String actual = content(element);
        if (actual.matches("(?s).*(?:[ÃÂØÙÛÐ][\\s\\S]){2,}.*")) {
            parent.fail("No mojibake", "UTF-8-readable Arabic text", actual, element);
        }
        return this;
    }

    public EncodingAssert hasNoReplacementCharacters() {
        WebElement element = parent.resolvePresent();
        String actual = content(element);
        if (actual.contains("\uFFFD") || actual.contains("�")) {
            parent.fail("No replacement characters", "no Unicode replacement character", actual, element);
        }
        return this;
    }

    public EncodingAssert hasNoQuestionMarkCorruption() {
        WebElement element = parent.resolvePresent();
        String actual = content(element);
        if (actual.matches(".*\\?{2,}.*")) {
            parent.fail("No question-mark corruption", "no repeated question marks", actual, element);
        }
        return this;
    }

    public EncodingAssert isUtf8SafeContent() {
        return hasNoMojibake()
                .hasNoReplacementCharacters()
                .hasNoQuestionMarkCorruption()
                .hasNoBrokenArabicCharacters();
    }

    public ElementAssert and() {
        return parent;
    }

    private static String content(WebElement element) {
        return AssertionEvidence.readableContent(element);
    }
}
