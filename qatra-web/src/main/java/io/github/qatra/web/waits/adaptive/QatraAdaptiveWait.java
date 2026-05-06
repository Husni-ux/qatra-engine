package io.github.qatra.web.waits.adaptive;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import io.github.qatra.web.waits.adaptive.conditions.components.DropdownReadyCondition;
import io.github.qatra.web.waits.adaptive.conditions.components.LoadingOverlayGoneCondition;
import io.github.qatra.web.waits.adaptive.conditions.components.ModalStableCondition;
import io.github.qatra.web.waits.adaptive.conditions.components.TableRowsLoadedCondition;
import io.github.qatra.web.waits.adaptive.conditions.components.ToastVisibleCondition;
import io.github.qatra.web.waits.adaptive.conditions.dom.ElementClickableCondition;
import io.github.qatra.web.waits.adaptive.conditions.dom.ElementEnabledCondition;
import io.github.qatra.web.waits.adaptive.conditions.dom.ElementExistsCondition;
import io.github.qatra.web.waits.adaptive.conditions.dom.ElementNotCoveredCondition;
import io.github.qatra.web.waits.adaptive.conditions.dom.ElementStableCondition;
import io.github.qatra.web.waits.adaptive.conditions.dom.ElementVisibleCondition;
import io.github.qatra.web.waits.adaptive.conditions.javascript.AngularReadyCondition;
import io.github.qatra.web.waits.adaptive.conditions.javascript.DocumentReadyCondition;
import io.github.qatra.web.waits.adaptive.conditions.javascript.JQueryIdleCondition;
import io.github.qatra.web.waits.adaptive.conditions.javascript.MutationStableCondition;
import io.github.qatra.web.waits.adaptive.conditions.javascript.NetworkIdleCondition;
import io.github.qatra.web.waits.adaptive.conditions.rtl.ArabicTextReadableCondition;
import io.github.qatra.web.waits.adaptive.conditions.rtl.ArabicTextVisibleCondition;
import io.github.qatra.web.waits.adaptive.conditions.rtl.MixedTextDirectionCondition;
import io.github.qatra.web.waits.adaptive.conditions.rtl.NoBrokenArabicCondition;
import io.github.qatra.web.waits.adaptive.conditions.rtl.NoMojibakeCondition;
import io.github.qatra.web.waits.adaptive.conditions.rtl.RtlDirectionCondition;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitAttempt;
import io.github.qatra.web.waits.adaptive.diagnostics.WaitScreenshotCapture;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * QATRA Adaptive Wait Engine.
 *
 * <p>This is the next-level wait API for QATRA. It is not a replacement for Selenium
 * FluentWait; it uses FluentWait internally and adds business-oriented readiness signals:
 * DOM readiness, JavaScript/network readiness, visual stability, Arabic/RTL quality,
 * custom component readiness, and diagnostic-rich timeout reporting.</p>
 */
public final class QatraAdaptiveWait {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final By locator;
    private final QatraWaitOptions options;
    private final QatraConditionGroup requiredConditions;
    private final String context;

    private QatraAdaptiveWait(WebDriver driver, By locator, QatraWaitOptions options, QatraConditionGroup requiredConditions, String context) {
        this.driver = Objects.requireNonNull(driver, "driver must not be null");
        this.locator = locator;
        this.options = options == null ? QatraWaitOptions.fromConfig() : options;
        this.requiredConditions = requiredConditions == null ? new QatraConditionGroup() : requiredConditions;
        this.context = context == null ? defaultContext(locator) : context;
    }

    public static QatraAdaptiveWait forPage(WebDriver driver) {
        return new QatraAdaptiveWait(driver, null, QatraWaitOptions.fromConfig(), new QatraConditionGroup(), "page");
    }

    public static QatraAdaptiveWait forElement(WebDriver driver, By locator) {
        Objects.requireNonNull(locator, "locator must not be null");
        return new QatraAdaptiveWait(driver, locator, QatraWaitOptions.fromConfig(), new QatraConditionGroup(), defaultContext(locator));
    }

    public QatraAdaptiveWait withTimeout(Duration timeout) {
        return withOptions(options.toBuilder().timeout(timeout).build());
    }

    public QatraAdaptiveWait pollingEvery(Duration pollingInterval) {
        return withOptions(options.toBuilder().pollingEvery(pollingInterval).build());
    }

    public QatraAdaptiveWait withQuietWindow(Duration quietWindow) {
        return withOptions(options.toBuilder().quietWindow(quietWindow).build());
    }

    public QatraAdaptiveWait withOptions(QatraWaitOptions newOptions) {
        return new QatraAdaptiveWait(driver, locator, newOptions, requiredConditions, context);
    }

    public Requirement require() {
        return new Requirement(this);
    }

    public QatraWaitResult untilReady() {
        if (requiredConditions.isEmpty()) {
            return require().visible().enabled().stable().notCovered().untilReady();
        }
        return run(requiredConditions.conditions());
    }

    public QatraWaitResult untilReadyForClick() {
        return require()
                .visible()
                .enabled()
                .stable()
                .notCovered()
                .noLoadingOverlay()
                .untilReady();
    }

    public QatraWaitResult untilArabicTextReady(String expectedArabicText) {
        return require()
                .visible()
                .arabicTextVisible(expectedArabicText)
                .arabicTextReadable()
                .noMojibake()
                .noBrokenArabic()
                .rtlDirection()
                .stable()
                .untilReady();
    }

    public QatraWaitResult untilPageFullyReady() {
        return require()
                .documentReady()
                .jqueryIdle()
                .angularReady()
                .networkIdle()
                .mutationStable()
                .noLoadingOverlay()
                .untilReady();
    }

    public QatraWaitResult untilDropdownReady(By optionsLocator) {
        ensureElementContext("untilDropdownReady");
        return require()
                .visible()
                .enabled()
                .stable()
                .noLoadingOverlay()
                .dropdownReady(optionsLocator)
                .untilReady();
    }

    public QatraWaitResult untilTableRowsLoaded(By rowLocator, int minimumRows) {
        ensureElementContext("untilTableRowsLoaded");
        return require()
                .visible()
                .tableRowsLoaded(rowLocator, minimumRows)
                .stable()
                .untilReady();
    }

    private QatraWaitResult run(List<QatraCondition> conditions) {
        String description = "QATRA Adaptive Wait: " + context;
        LOG.action(description);
        AllureReport.step(description);

        Instant started = Instant.now();
        List<WaitAttempt> attempts = new ArrayList<>();
        int[] attemptCounter = {0};

        try {
            WebElement element = fluentWait().until((Function<WebDriver, WebElement>) currentDriver -> {
                attemptCounter[0]++;
                WebElement currentElement;
                try {
                    currentElement = findElementIfNeeded(currentDriver);
                } catch (Throwable t) {
                    attempts.add(new WaitAttempt(attemptCounter[0], "Locate " + context,
                            QatraConditionResult.failed("Unable to locate element during adaptive wait", Map.of(
                                    "context", context,
                                    "locator", String.valueOf(locator)
                            ), t)));
                    return null;
                }
                for (QatraCondition condition : conditions) {
                    QatraConditionResult result;
                    try {
                        result = condition.evaluate(currentDriver, currentElement);
                    } catch (Throwable t) {
                        result = QatraConditionResult.failed("Condition threw exception: " + condition.description(), t);
                    }
                    if (!result.passed()) {
                        attempts.add(new WaitAttempt(attemptCounter[0], condition.description(), result));
                        return null;
                    }
                }
                return currentElement == null ? currentDriver.findElement(By.tagName("body")) : currentElement;
            });

            QatraWaitReport report = QatraWaitReport.builder(context)
                    .elapsed(Duration.between(started, Instant.now()))
                    .build();
            return new QatraWaitResult(element, report);
        } catch (TimeoutException e) {
            String screenshot = options.screenshotOnFailure()
                    ? WaitScreenshotCapture.capture(driver, "adaptive_wait_timeout_" + sanitize(context))
                    : "";
            QatraWaitReport.Builder reportBuilder = QatraWaitReport.builder(context)
                    .elapsed(Duration.between(started, Instant.now()))
                    .screenshotPath(screenshot);
            attempts.forEach(reportBuilder::addAttempt);
            QatraWaitReport report = reportBuilder.build();
            throw new QatraTimeoutException(buildTimeoutMessage(report), report, e);
        }
    }

    private FluentWait<WebDriver> fluentWait() {
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(options.timeout())
                .pollingEvery(options.pollingInterval());
        for (Class<? extends Throwable> ignoredException : options.ignoredExceptions()) {
            wait.ignoring(ignoredException);
        }
        return wait;
    }

    private WebElement findElementIfNeeded(WebDriver currentDriver) {
        if (locator == null) {
            return null;
        }
        return currentDriver.findElement(locator);
    }

    private void ensureElementContext(String method) {
        if (locator == null) {
            throw new IllegalStateException(method + " requires QatraAdaptiveWait.forElement(driver, locator)");
        }
    }

    private static String buildTimeoutMessage(QatraWaitReport report) {
        return "QatraWait Timeout: readiness requirements were not satisfied for " + report.context()
                + System.lineSeparator()
                + report.toDebugText();
    }

    private static String defaultContext(By locator) {
        return locator == null ? "page" : "element " + locator;
    }

    private static String sanitize(String value) {
        return value == null ? "wait" : value.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    public static final class Requirement {
        private final QatraAdaptiveWait owner;
        private final QatraConditionGroup group;

        private Requirement(QatraAdaptiveWait owner) {
            this.owner = owner;
            this.group = new QatraConditionGroup();
        }

        public Requirement exists() {
            group.add(new ElementExistsCondition());
            return this;
        }

        public Requirement visible() {
            group.add(new ElementVisibleCondition());
            return this;
        }

        public Requirement enabled() {
            group.add(new ElementEnabledCondition());
            return this;
        }

        public Requirement clickable() {
            group.add(new ElementClickableCondition());
            return this;
        }

        public Requirement stable() {
            group.add(new ElementStableCondition(owner.options.quietWindow()));
            return this;
        }

        public Requirement notCovered() {
            group.add(new ElementNotCoveredCondition());
            return this;
        }

        public Requirement documentReady() {
            group.add(new DocumentReadyCondition());
            return this;
        }

        public Requirement jqueryIdle() {
            group.add(new JQueryIdleCondition());
            return this;
        }

        public Requirement angularReady() {
            group.add(new AngularReadyCondition());
            return this;
        }

        public Requirement networkIdle() {
            group.add(new NetworkIdleCondition(owner.options.quietWindow()));
            return this;
        }

        public Requirement mutationStable() {
            group.add(new MutationStableCondition(owner.options.quietWindow()));
            return this;
        }

        public Requirement noLoadingOverlay() {
            group.add(new LoadingOverlayGoneCondition());
            return this;
        }

        public Requirement arabicTextVisible(String expectedText) {
            group.add(new ArabicTextVisibleCondition(expectedText));
            return this;
        }

        public Requirement arabicTextReadable() {
            group.add(new ArabicTextReadableCondition());
            return this;
        }

        public Requirement noBrokenArabic() {
            group.add(new NoBrokenArabicCondition());
            return this;
        }

        public Requirement noMojibake() {
            group.add(new NoMojibakeCondition());
            return this;
        }

        public Requirement rtlDirection() {
            group.add(new RtlDirectionCondition());
            return this;
        }

        public Requirement mixedTextDirectionSafe() {
            group.add(new MixedTextDirectionCondition());
            return this;
        }

        public Requirement dropdownReady(By optionsLocator) {
            group.add(new DropdownReadyCondition(optionsLocator));
            return this;
        }

        public Requirement tableRowsLoaded(By rowLocator, int minimumRows) {
            group.add(new TableRowsLoadedCondition(rowLocator, minimumRows));
            return this;
        }

        public Requirement modalStable() {
            group.add(new ModalStableCondition(owner.options.quietWindow()));
            return this;
        }

        public Requirement toastVisible(String expectedText) {
            group.add(new ToastVisibleCondition(expectedText));
            return this;
        }

        public QatraWaitResult untilReady() {
            return new QatraAdaptiveWait(owner.driver, owner.locator, owner.options, group, owner.context).run(group.conditions());
        }
    }
}
