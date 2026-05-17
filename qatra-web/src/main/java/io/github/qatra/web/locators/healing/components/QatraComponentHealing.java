package io.github.qatra.web.locators.healing.components;

import io.github.qatra.web.components.QatraDropdown;
import io.github.qatra.web.components.QatraModal;
import io.github.qatra.web.components.QatraTable;
import io.github.qatra.web.components.QatraToast;
import io.github.qatra.web.locators.LocatorResolution;
import io.github.qatra.web.locators.QatraLocator;
import io.github.qatra.web.locators.QatraLocatorEngine;
import io.github.qatra.web.locators.healing.QatraHealingOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Objects;

/**
 * Arabic component self-healing entry point.
 *
 * <p>This layer resolves components by business intent first, then creates the
 * corresponding QATRA component helper around the resolved locator. It is designed
 * for modern Arabic/RTL enterprise pages where controls are often custom widgets
 * rather than simple native HTML elements.</p>
 */
public final class QatraComponentHealing {

    private final WebDriver driver;
    private Duration timeout = Duration.ofSeconds(10);
    private QatraHealingOptions healingOptions = QatraHealingOptions.defaults();

    private QatraComponentHealing(WebDriver driver) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
    }

    public static QatraComponentHealing of(WebDriver driver) {
        return new QatraComponentHealing(driver);
    }

    public QatraComponentHealing withTimeout(Duration timeout) {
        this.timeout = timeout == null ? this.timeout : timeout;
        return this;
    }

    public QatraComponentHealing withHealingOptions(QatraHealingOptions options) {
        this.healingOptions = options == null ? this.healingOptions : options;
        return this;
    }

    public LocatorResolution resolve(QatraLocator locator) {
        return QatraLocatorEngine.resolve(driver, locator, timeout, healingOptions);
    }

    public QatraDropdown dropdown(String arabicLabel) {
        return dropdown(ComponentLocatorStrategy.dropdownByArabicLabel(arabicLabel));
    }

    public QatraDropdown dropdown(QatraLocator locator) {
        LocatorResolution resolution = resolve(locator);
        return QatraDropdown.of(driver, resolution.locator()).withTimeout(timeout);
    }

    public QatraTable tableContaining(String arabicText) {
        return table(ComponentLocatorStrategy.tableContainingArabicText(arabicText));
    }

    public QatraTable table(QatraLocator locator) {
        LocatorResolution resolution = resolve(locator);
        return QatraTable.of(driver, resolution.locator()).withTimeout(timeout);
    }

    public QatraModal modalContaining(String arabicText) {
        return modal(ComponentLocatorStrategy.modalContainingArabicText(arabicText));
    }

    public QatraModal modal(QatraLocator locator) {
        LocatorResolution resolution = resolve(locator);
        return QatraModal.of(driver, resolution.locator()).withTimeout(timeout);
    }

    public QatraToast toastContaining(String arabicText) {
        return toast(ComponentLocatorStrategy.toastContainingArabicText(arabicText));
    }

    public QatraToast toast(QatraLocator locator) {
        LocatorResolution resolution = resolve(locator);
        return QatraToast.of(driver, resolution.locator()).withTimeout(timeout);
    }

    public WebElement inputByArabicLabel(String arabicLabel) {
        return resolve(ComponentLocatorStrategy.inputByArabicLabel(arabicLabel)).element();
    }

    public WebElement datePickerByArabicLabel(String arabicLabel) {
        return resolve(ComponentLocatorStrategy.datePickerByArabicLabel(arabicLabel)).element();
    }

    public WebElement modalButton(String modalText, String actionText) {
        return resolve(ComponentLocatorStrategy.modalButtonByArabicAction(modalText, actionText)).element();
    }
}
