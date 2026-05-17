package io.github.qatra.web.locators.advisor;

/**
 * Configuration for proactive locator quality analysis.
 */
public final class LocatorAdvisorOptions {

    private final int minimumScore;
    private final boolean failOnCritical;
    private final boolean exportReports;
    private final boolean includeCandidateLocators;

    private LocatorAdvisorOptions(Builder builder) {
        this.minimumScore = builder.minimumScore;
        this.failOnCritical = builder.failOnCritical;
        this.exportReports = builder.exportReports;
        this.includeCandidateLocators = builder.includeCandidateLocators;
    }

    public static LocatorAdvisorOptions defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public int minimumScore() {
        return minimumScore;
    }

    public boolean failOnCritical() {
        return failOnCritical;
    }

    public boolean exportReports() {
        return exportReports;
    }

    public boolean includeCandidateLocators() {
        return includeCandidateLocators;
    }

    public static final class Builder {
        private int minimumScore = 70;
        private boolean failOnCritical = false;
        private boolean exportReports = true;
        private boolean includeCandidateLocators = true;

        public Builder minimumScore(int minimumScore) {
            this.minimumScore = Math.max(0, Math.min(100, minimumScore));
            return this;
        }

        public Builder failOnCritical(boolean failOnCritical) {
            this.failOnCritical = failOnCritical;
            return this;
        }

        public Builder exportReports(boolean exportReports) {
            this.exportReports = exportReports;
            return this;
        }

        public Builder includeCandidateLocators(boolean includeCandidateLocators) {
            this.includeCandidateLocators = includeCandidateLocators;
            return this;
        }

        public LocatorAdvisorOptions build() {
            return new LocatorAdvisorOptions(this);
        }
    }
}
