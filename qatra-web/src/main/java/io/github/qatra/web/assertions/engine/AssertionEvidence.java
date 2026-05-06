package io.github.qatra.web.assertions.engine;

import io.github.qatra.web.rtl.RtlEngine;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Collects readable evidence for assertion failures.
 *
 * <p>This class keeps QATRA assertion failures business-readable by including
 * the locator context, visible text, user-facing attributes, direction, and
 * element geometry when available.</p>
 */
public final class AssertionEvidence {

    private AssertionEvidence() {
    }

    public static String readableContent(WebElement element) {
        if (element == null) {
            return "";
        }
        return String.join(" ",
                safeGet(() -> element.getText()),
                safeGet(() -> element.getAttribute("textContent")),
                safeGet(() -> element.getAttribute("placeholder")),
                safeGet(() -> element.getAttribute("value")),
                safeGet(() -> element.getAttribute("aria-label")),
                safeGet(() -> element.getAttribute("title")),
                safeGet(() -> element.getAttribute("alt"))
        ).replaceAll("\\s+", " ").trim();
    }

    public static String summarize(String value) {
        return RtlEngine.summarize(value);
    }

    public static String direction(WebDriver driver, WebElement element) {
        return RtlEngine.effectiveDirection(driver, element);
    }

    public static String elementSnapshot(WebDriver driver, WebElement element) {
        if (element == null) {
            return "Element evidence: element was null.";
        }

        StringBuilder evidence = new StringBuilder();
        evidence.append("Element evidence:").append(System.lineSeparator());
        evidence.append("- tag: ").append(safeGet(() -> element.getTagName())).append(System.lineSeparator());
        evidence.append("- id: ").append(safeGet(() -> element.getAttribute("id"))).append(System.lineSeparator());
        evidence.append("- class: ").append(safeGet(() -> element.getAttribute("class"))).append(System.lineSeparator());
        evidence.append("- text: ").append(summarize(readableContent(element))).append(System.lineSeparator());
        evidence.append("- direction: ").append(direction(driver, element)).append(System.lineSeparator());
        evidence.append("- displayed: ").append(safeGetBoolean(element::isDisplayed)).append(System.lineSeparator());
        evidence.append("- enabled: ").append(safeGetBoolean(element::isEnabled)).append(System.lineSeparator());
        try {
            Rectangle rect = element.getRect();
            evidence.append("- rectangle: x=").append(rect.getX())
                    .append(", y=").append(rect.getY())
                    .append(", width=").append(rect.getWidth())
                    .append(", height=").append(rect.getHeight())
                    .append(System.lineSeparator());
        } catch (Throwable ignored) {
            evidence.append("- rectangle: unavailable").append(System.lineSeparator());
        }
        evidence.append("- page title: ").append(safeGet(() -> driver == null ? "" : driver.getTitle())).append(System.lineSeparator());
        evidence.append("- current url: ").append(safeGet(() -> driver == null ? "" : driver.getCurrentUrl())).append(System.lineSeparator());
        return evidence.toString();
    }

    public static boolean isInsideViewport(WebDriver driver, WebElement element) {
        if (!(driver instanceof JavascriptExecutor js) || element == null) {
            return false;
        }
        Object result = js.executeScript("""
                const el = arguments[0];
                const r = el.getBoundingClientRect();
                return r.top >= 0 && r.left >= 0 &&
                       r.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
                       r.right <= (window.innerWidth || document.documentElement.clientWidth);
                """, element);
        return Boolean.TRUE.equals(result);
    }

    public static boolean isNotCovered(WebDriver driver, WebElement element) {
        if (!(driver instanceof JavascriptExecutor js) || element == null) {
            return false;
        }
        Object result = js.executeScript("""
                const el = arguments[0];
                const r = el.getBoundingClientRect();
                if (!r || r.width === 0 || r.height === 0) return false;
                const x = Math.floor(r.left + r.width / 2);
                const y = Math.floor(r.top + r.height / 2);
                const top = document.elementFromPoint(x, y);
                return top === el || el.contains(top);
                """, element);
        return Boolean.TRUE.equals(result);
    }

    private static String safeGet(ValueSupplier supplier) {
        try {
            String value = supplier.get();
            return value == null ? "" : value;
        } catch (Throwable ignored) {
            return "";
        }
    }

    private static String safeGetBoolean(BooleanSupplier supplier) {
        try {
            return String.valueOf(supplier.get());
        } catch (Throwable ignored) {
            return "unavailable";
        }
    }

    @FunctionalInterface
    private interface ValueSupplier {
        String get();
    }

    @FunctionalInterface
    private interface BooleanSupplier {
        boolean get();
    }
}
