package io.github.qatra.web.waits.adaptive;

import org.openqa.selenium.WebElement;

/**
 * Successful wait result. Keeps the element for continued actions and a summary report.
 */
public final class QatraWaitResult {

    private final WebElement element;
    private final QatraWaitReport report;

    public QatraWaitResult(WebElement element, QatraWaitReport report) {
        this.element = element;
        this.report = report;
    }

    public WebElement element() {
        return element;
    }

    public QatraWaitReport report() {
        return report;
    }
}
