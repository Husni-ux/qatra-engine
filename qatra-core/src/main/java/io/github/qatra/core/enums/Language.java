package io.github.qatra.core.enums;

/**
 * Supported languages and their text directions.
 * Core to QATRA's RTL-first design.
 */
public enum Language {

    ENGLISH("en", "English", Direction.LTR),
    ARABIC("ar", "العربية", Direction.RTL),
    HEBREW("he", "עברית", Direction.RTL),
    FRENCH("fr", "Français", Direction.LTR),
    GERMAN("de", "Deutsch", Direction.LTR),
    SPANISH("es", "Español", Direction.LTR),
    URDU("ur", "اردو", Direction.RTL),
    PERSIAN("fa", "فارسی", Direction.RTL);

    private final String code;
    private final String displayName;
    private final Direction direction;

    Language(String code, String displayName, Direction direction) {
        this.code = code;
        this.displayName = displayName;
        this.direction = direction;
    }

    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }
    public Direction getDirection() { return direction; }
    public boolean isRTL() { return direction == Direction.RTL; }
    public boolean isLTR() { return direction == Direction.LTR; }

    public enum Direction {
        LTR, RTL
    }

    public static Language fromCode(String code) {
        for (Language lang : values()) {
            if (lang.code.equalsIgnoreCase(code)) return lang;
        }
        return ENGLISH;
    }
}
