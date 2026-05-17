package io.github.qatra.web.locators.advisor;

/**
 * Simple quality gate that can be used in tests or CI to prevent fragile locators.
 */
public final class LocatorQualityGate {

    private final LocatorAdvisorOptions options;

    private LocatorQualityGate(LocatorAdvisorOptions options) {
        this.options = options == null ? LocatorAdvisorOptions.defaults() : options;
    }

    public static LocatorQualityGate withDefaults() {
        return new LocatorQualityGate(LocatorAdvisorOptions.defaults());
    }

    public static LocatorQualityGate withOptions(LocatorAdvisorOptions options) {
        return new LocatorQualityGate(options);
    }

    public LocatorAdvisorReport require(LocatorAdvisorReport report) {
        if (report == null) {
            throw new IllegalArgumentException("LocatorAdvisorReport cannot be null");
        }
        if (!report.passed(options)) {
            throw new AssertionError("QATRA Locator Quality Gate failed.\n" + report);
        }
        return report;
    }
}
