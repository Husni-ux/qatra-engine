package io.github.qatra.web.waits.adaptive.diagnostics;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Collects lightweight evidence that explains why a wait is not ready yet.
 */
public final class WaitEvidenceCollector {

    private WaitEvidenceCollector() {
    }

    public static Map<String, String> collect(WebDriver driver, WebElement element) {
        Map<String, String> evidence = new LinkedHashMap<>();
        if (driver != null) {
            evidence.put("url", safe(driver.getCurrentUrl()));
            evidence.put("title", safe(driver.getTitle()));
        }
        if (element != null) {
            evidence.put("text", abbreviate(safe(element.getText()), 180));
            evidence.put("textContent", abbreviate(safe(element.getAttribute("textContent")), 180));
            evidence.put("dir", safe(element.getAttribute("dir")));
            evidence.put("class", safe(element.getAttribute("class")));
            evidence.put("aria-busy", safe(element.getAttribute("aria-busy")));
            try {
                Rectangle rect = element.getRect();
                evidence.put("rect", rect.getX() + "," + rect.getY() + " " + rect.getWidth() + "x" + rect.getHeight());
            } catch (RuntimeException ignored) {
                evidence.put("rect", "unavailable");
            }
        }
        if (driver instanceof JavascriptExecutor js && element != null) {
            try {
                Object direction = js.executeScript("return window.getComputedStyle(arguments[0]).direction", element);
                Object align = js.executeScript("return window.getComputedStyle(arguments[0]).textAlign", element);
                evidence.put("css.direction", safe(String.valueOf(direction)));
                evidence.put("css.textAlign", safe(String.valueOf(align)));
            } catch (RuntimeException ignored) {
                evidence.put("css", "unavailable");
            }
        }
        return evidence;
    }

    public static String abbreviate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        String normalized = value.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= maxLength) {
            return normalized;
        }
        return normalized.substring(0, Math.max(0, maxLength - 3)) + "...";
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
