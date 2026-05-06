package io.github.qatra.web.assertions.engine;

import org.openqa.selenium.WebElement;

/** Visual-state assertions that make UI failures easier to understand. */
public class VisualAssert {

    private final ElementAssert parent;

    VisualAssert(ElementAssert parent) {
        this.parent = parent;
    }

    public VisualAssert isDisplayed() {
        WebElement element = parent.resolvePresent();
        if (!element.isDisplayed()) {
            parent.fail("Element displayed", "displayed=true", "displayed=false", element);
        }
        return this;
    }

    public VisualAssert isInsideViewport() {
        WebElement element = parent.resolvePresent();
        if (!AssertionEvidence.isInsideViewport(parent.driver(), element)) {
            parent.fail("Element inside viewport", "inside viewport", "outside viewport", element);
        }
        return this;
    }

    public VisualAssert notCovered() {
        WebElement element = parent.resolvePresent();
        if (!AssertionEvidence.isNotCovered(parent.driver(), element)) {
            parent.fail("Element not covered", "element center is reachable", "element appears covered or not hittable", element);
        }
        return this;
    }

    public VisualAssert hasCssDirection(String expectedDirection) {
        WebElement element = parent.resolvePresent();
        String actual = safe(element.getCssValue("direction"));
        if (!actual.equalsIgnoreCase(safe(expectedDirection))) {
            parent.fail("CSS direction", safe(expectedDirection), actual, element);
        }
        return this;
    }

    public ElementAssert and() {
        return parent;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
