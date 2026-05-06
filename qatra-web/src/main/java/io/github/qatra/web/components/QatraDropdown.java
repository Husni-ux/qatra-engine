package io.github.qatra.web.components;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import io.github.qatra.web.waits.adaptive.QatraAdaptiveWait;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * Reusable dropdown component with Arabic text support.
 * Supports both native {@code <select>} elements and common custom dropdown/listbox widgets.
 */
public final class QatraDropdown extends QatraComponent<QatraDropdown> {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private By optionsLocator;

    private QatraDropdown(WebDriver driver, By rootLocator) {
        super(driver, rootLocator);
    }

    public static QatraDropdown of(WebDriver driver, By rootLocator) {
        return new QatraDropdown(driver, rootLocator);
    }

    public QatraDropdown withOptions(By optionsLocator) {
        this.optionsLocator = optionsLocator;
        return this;
    }

    @Override
    public QatraDropdown waitUntilReady() {
        if (optionsLocator == null) {
            waitForRoot()
                    .require()
                    .visible()
                    .enabled()
                    .stable()
                    .noLoadingOverlay()
                    .untilReady();
        } else {
            QatraAdaptiveWait.forElement(driver, rootLocator)
                    .withTimeout(timeout)
                    .pollingEvery(polling)
                    .withQuietWindow(quietWindow)
                    .untilDropdownReady(optionsLocator);
        }
        return this;
    }

    public QatraDropdown open() {
        LOG.action("Open dropdown: {}", rootLocator);
        AllureReport.step("Open dropdown: " + rootLocator);
        WebElement root = waitForRoot()
                .require()
                .visible()
                .enabled()
                .stable()
                .notCovered()
                .noLoadingOverlay()
                .untilReady()
                .element();
        root.click();
        return this;
    }

    public QatraDropdown selectArabicText(String arabicText) {
        return selectText(arabicText);
    }

    public QatraDropdown selectText(String text) {
        LOG.action("Select dropdown text '{}' from {}", text, rootLocator);
        AllureReport.step("Select dropdown text '" + text + "' from " + rootLocator);
        WebElement root = waitForRoot()
                .require()
                .visible()
                .enabled()
                .untilReady()
                .element();

        if ("select".equalsIgnoreCase(root.getTagName())) {
            new Select(root).selectByVisibleText(text);
            return this;
        }

        root.click();
        By option = optionsLocator == null ? defaultOptionLocator(text) : optionsLocator;
        List<WebElement> candidates = driver.findElements(option);
        WebElement selected = candidates.stream()
                .filter(WebElement::isDisplayed)
                .filter(e -> normalize(e.getText()).contains(normalize(text)))
                .findFirst()
                .orElseGet(() -> driver.findElement(defaultOptionLocator(text)));
        selected.click();
        return this;
    }

    public QatraDropdown assertSelectedTextContains(String expectedText) {
        String actual = selectedText();
        if (!normalize(actual).contains(normalize(expectedText))) {
            throw new AssertionError("Dropdown selected text mismatch. Expected to contain: '"
                    + expectedText + "' but actual was: '" + actual + "'");
        }
        return this;
    }

    public QatraDropdown assertSelectedArabicText(String expectedArabicText) {
        return assertSelectedTextContains(expectedArabicText);
    }

    public String selectedText() {
        WebElement root = root();
        if ("select".equalsIgnoreCase(root.getTagName())) {
            return new Select(root).getFirstSelectedOption().getText();
        }
        return root.getText();
    }

    private static By defaultOptionLocator(String text) {
        return By.xpath("//*[self::li or self::option or @role='option' or contains(@class,'option')]["
                + ArabicXPath.textContains(text) + "]");
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }
}
