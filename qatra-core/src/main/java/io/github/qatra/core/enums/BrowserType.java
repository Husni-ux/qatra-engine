package io.github.qatra.core.enums;

/**
 * Supported browser types.
 */
public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE,
    SAFARI;

    public static BrowserType fromString(String name) {
        return switch (name.toLowerCase().trim()) {
            case "chrome"  -> CHROME;
            case "firefox" -> FIREFOX;
            case "edge"    -> EDGE;
            case "safari"  -> SAFARI;
            default -> {
                System.out.println("⚠️  Unknown browser: '" + name + "'. Defaulting to CHROME.");
                yield CHROME;
            }
        };
    }
}
