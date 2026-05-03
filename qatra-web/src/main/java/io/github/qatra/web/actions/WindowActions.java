package io.github.qatra.web.actions;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.assertions.WebAssertions;
import io.github.qatra.web.fluent.FluentWeb;
import io.github.qatra.web.reports.AllureReport;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Fluent window and tab actions.
 */
public class WindowActions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final FluentWeb parent;

    public WindowActions(WebDriver driver, FluentWeb parent) {
        this.driver = driver;
        this.parent = parent;
    }

    public WindowActions openNewTab() {
        LOG.action("Open new tab");
        AllureReport.step("Open new tab");
        driver.switchTo().newWindow(WindowType.TAB);
        return this;
    }

    public WindowActions openNewWindow() {
        LOG.action("Open new browser window");
        AllureReport.step("Open new browser window");
        driver.switchTo().newWindow(WindowType.WINDOW);
        return this;
    }

    public WindowActions switchToIndex(int index) {
        LOG.action("Switch to window/tab index: {}", index);
        AllureReport.step("Switch to window/tab index: " + index);
        List<String> handles = handles();
        if (index < 0 || index >= handles.size()) {
            throw new IllegalArgumentException("Invalid window index: " + index + ". Available windows: " + handles.size());
        }
        driver.switchTo().window(handles.get(index));
        return this;
    }

    public WindowActions switchToHandle(String handle) {
        LOG.action("Switch to window handle: {}", handle);
        AllureReport.step("Switch to window handle");
        driver.switchTo().window(handle);
        return this;
    }

    public WindowActions switchToTitleContains(String expectedTitlePart) {
        LOG.action("Switch to window with title containing: {}", expectedTitlePart);
        AllureReport.step("Switch to window with title containing: " + expectedTitlePart);
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
            String title = driver.getTitle();
            if (title != null && title.contains(expectedTitlePart)) {
                return this;
            }
        }
        throw new AssertionError("No window found with title containing: " + expectedTitlePart);
    }

    public WindowActions switchToUrlContains(String expectedUrlPart) {
        LOG.action("Switch to window with URL containing: {}", expectedUrlPart);
        AllureReport.step("Switch to window with URL containing: " + expectedUrlPart);
        for (String handle : driver.getWindowHandles()) {
            driver.switchTo().window(handle);
            String url = driver.getCurrentUrl();
            if (url != null && url.contains(expectedUrlPart)) {
                return this;
            }
        }
        throw new AssertionError("No window found with URL containing: " + expectedUrlPart);
    }

    public WindowActions closeCurrent() {
        LOG.action("Close current window/tab");
        AllureReport.step("Close current window/tab");
        driver.close();
        List<String> remaining = handles();
        if (!remaining.isEmpty()) {
            driver.switchTo().window(remaining.get(0));
        }
        return this;
    }

    public WindowActions closeOthers() {
        LOG.action("Close all other windows/tabs");
        AllureReport.step("Close all other windows/tabs");
        String current = driver.getWindowHandle();
        Set<String> allHandles = driver.getWindowHandles();
        for (String handle : allHandles) {
            if (!handle.equals(current)) {
                driver.switchTo().window(handle);
                driver.close();
            }
        }
        driver.switchTo().window(current);
        return this;
    }

    public int count() {
        LOG.action("Get window/tab count");
        AllureReport.step("Get window/tab count");
        return driver.getWindowHandles().size();
    }

    public String currentHandle() {
        return driver.getWindowHandle();
    }

    public List<String> handles() {
        return new ArrayList<>(driver.getWindowHandles());
    }

    public FluentWeb and() {
        return parent;
    }

    public BrowserActions browser() {
        return parent.browser();
    }

    public ElementActions element() {
        return parent.element();
    }

    public WebAssertions assertThat() {
        return parent.assertThat();
    }
}
