package io.github.qatra.web.locators;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Result of resolving a QatraLocator against the current page.
 */
public final class LocatorResolution {

    private final WebElement element;
    private final By locator;
    private final LocatorCandidate candidate;
    private final LocatorHealingStatus status;
    private final LocatorHealingReport report;

    public LocatorResolution(WebElement element, By locator, LocatorCandidate candidate,
                             LocatorHealingStatus status, LocatorHealingReport report) {
        this.element = element;
        this.locator = locator;
        this.candidate = candidate;
        this.status = status;
        this.report = report;
    }

    public WebElement element() {
        return element;
    }

    public By locator() {
        return locator;
    }

    public LocatorCandidate candidate() {
        return candidate;
    }

    public LocatorHealingStatus status() {
        return status;
    }

    public LocatorHealingReport report() {
        return report;
    }

    public boolean healed() {
        return status == LocatorHealingStatus.HEALED_WITH_FALLBACK;
    }
}
