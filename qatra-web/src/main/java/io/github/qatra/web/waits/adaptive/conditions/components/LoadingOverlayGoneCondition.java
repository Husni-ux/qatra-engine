package io.github.qatra.web.waits.adaptive.conditions.components;

import io.github.qatra.web.waits.adaptive.QatraCondition;
import io.github.qatra.web.waits.adaptive.QatraConditionResult;
import io.github.qatra.web.waits.adaptive.QatraWaitSignal;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Map;

public class LoadingOverlayGoneCondition implements QatraCondition {

    private final By overlayLocator;

    public LoadingOverlayGoneCondition() {
        this(By.cssSelector(".loading, .spinner, .loader, .skeleton, [aria-busy='true'], [data-loading='true']"));
    }

    public LoadingOverlayGoneCondition(By overlayLocator) {
        this.overlayLocator = overlayLocator;
    }

    @Override
    public QatraConditionResult evaluate(WebDriver driver, WebElement element) {
        long visible = driver.findElements(overlayLocator).stream().filter(WebElement::isDisplayed).count();
        return visible == 0
                ? QatraConditionResult.passed("No visible loading overlays")
                : QatraConditionResult.failed("Visible loading overlays remain", Map.of("visibleOverlays", String.valueOf(visible)));
    }

    @Override
    public String description() {
        return "No visible loading overlays";
    }

    @Override
    public QatraWaitSignal signal() {
        return QatraWaitSignal.COMPONENT;
    }
}
