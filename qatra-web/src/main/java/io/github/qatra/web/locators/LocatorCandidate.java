package io.github.qatra.web.locators;

import org.openqa.selenium.By;

/**
 * One locator candidate inside a QATRA self-healing locator chain.
 */
public final class LocatorCandidate {

    private final By locator;
    private final String source;
    private final int order;

    public LocatorCandidate(By locator, String source, int order) {
        if (locator == null) {
            throw new IllegalArgumentException("Locator candidate cannot be null");
        }
        this.locator = locator;
        this.source = source == null ? "custom" : source;
        this.order = order;
    }

    public By locator() {
        return locator;
    }

    public String source() {
        return source;
    }

    public int order() {
        return order;
    }

    public boolean primary() {
        return order == 0;
    }

    @Override
    public String toString() {
        return "LocatorCandidate{" +
                "locator=" + locator +
                ", source='" + source + '\'' +
                ", order=" + order +
                '}';
    }
}
