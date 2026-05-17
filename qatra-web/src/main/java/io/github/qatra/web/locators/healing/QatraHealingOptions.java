package io.github.qatra.web.locators.healing;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;

/**
 * Runtime options for QATRA locator healing decisions.
 *
 * <p>Defaults are intentionally conservative: SAFE_AUTO_HEAL requires high confidence,
 * low risk, and guardrails that prevent QATRA from hiding real UI bugs.</p>
 */
public final class QatraHealingOptions {

    private final HealingMode mode;
    private final int minimumConfidence;
    private final int safeAutoHealMinimumConfidence;
    private final HealingRiskLevel maximumAutoHealRisk;
    private final boolean requireSemanticMatchForAutoHeal;
    private final boolean blockHiddenCandidates;
    private final boolean blockDisabledCandidates;
    private final boolean failOnAmbiguousCandidates;
    private final int maximumMatchesForAutoHeal;
    private final boolean exportReport;
    private final boolean exportModeAudit;
    private final Path strictApprovalFile;
    private final Duration timeout;

    private QatraHealingOptions(Builder builder) {
        this.mode = builder.mode;
        this.minimumConfidence = builder.minimumConfidence;
        this.safeAutoHealMinimumConfidence = builder.safeAutoHealMinimumConfidence;
        this.maximumAutoHealRisk = builder.maximumAutoHealRisk;
        this.requireSemanticMatchForAutoHeal = builder.requireSemanticMatchForAutoHeal;
        this.blockHiddenCandidates = builder.blockHiddenCandidates;
        this.blockDisabledCandidates = builder.blockDisabledCandidates;
        this.failOnAmbiguousCandidates = builder.failOnAmbiguousCandidates;
        this.maximumMatchesForAutoHeal = builder.maximumMatchesForAutoHeal;
        this.exportReport = builder.exportReport;
        this.exportModeAudit = builder.exportModeAudit;
        this.strictApprovalFile = builder.strictApprovalFile;
        this.timeout = builder.timeout;
    }

    public static QatraHealingOptions defaults() {
        return builder()
                .mode(readMode())
                .minimumConfidence(readInt("qatra.healing.min-confidence", 75))
                .safeAutoHealMinimumConfidence(readInt("qatra.healing.safe-min-confidence", 85))
                .maximumAutoHealRisk(readRisk())
                .requireSemanticMatchForAutoHeal(Boolean.parseBoolean(System.getProperty("qatra.healing.require-semantic-match", "false")))
                .blockHiddenCandidates(Boolean.parseBoolean(System.getProperty("qatra.healing.block-hidden", "true")))
                .blockDisabledCandidates(Boolean.parseBoolean(System.getProperty("qatra.healing.block-disabled", "true")))
                .failOnAmbiguousCandidates(Boolean.parseBoolean(System.getProperty("qatra.healing.fail-on-ambiguous", "true")))
                .maximumMatchesForAutoHeal(readInt("qatra.healing.max-matches", 1))
                .exportReport(Boolean.parseBoolean(System.getProperty("qatra.healing.export-report", "true")))
                .exportModeAudit(Boolean.parseBoolean(System.getProperty("qatra.healing.export-mode-audit", "true")))
                .strictApprovalFile(Path.of(System.getProperty("qatra.healing.approval-file", "qatra-healing-approved.json")))
                .timeout(Duration.ofSeconds(readInt("qatra.healing.timeout.seconds", 10)))
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public HealingMode mode() { return mode; }
    public int minimumConfidence() { return minimumConfidence; }
    public int safeAutoHealMinimumConfidence() { return safeAutoHealMinimumConfidence; }
    public HealingRiskLevel maximumAutoHealRisk() { return maximumAutoHealRisk; }
    public boolean requireSemanticMatchForAutoHeal() { return requireSemanticMatchForAutoHeal; }
    public boolean blockHiddenCandidates() { return blockHiddenCandidates; }
    public boolean blockDisabledCandidates() { return blockDisabledCandidates; }
    public boolean failOnAmbiguousCandidates() { return failOnAmbiguousCandidates; }
    public int maximumMatchesForAutoHeal() { return maximumMatchesForAutoHeal; }
    public boolean exportReport() { return exportReport; }
    public boolean exportModeAudit() { return exportModeAudit; }
    public Path strictApprovalFile() { return strictApprovalFile; }
    public Duration timeout() { return timeout; }

    public boolean autoHealingAllowed() {
        return mode == HealingMode.SAFE_AUTO_HEAL || mode == HealingMode.AUTO_HEAL || mode == HealingMode.STRICT_APPROVAL;
    }

    private static HealingMode readMode() {
        String raw = System.getProperty("qatra.healing.mode", "SAFE_AUTO_HEAL");
        try {
            return HealingMode.valueOf(raw.trim().toUpperCase(Locale.ROOT).replace('-', '_'));
        } catch (Exception ignored) {
            return HealingMode.SAFE_AUTO_HEAL;
        }
    }

    private static HealingRiskLevel readRisk() {
        String raw = System.getProperty("qatra.healing.max-auto-risk", "LOW");
        try {
            return HealingRiskLevel.valueOf(raw.trim().toUpperCase(Locale.ROOT).replace('-', '_'));
        } catch (Exception ignored) {
            return HealingRiskLevel.LOW;
        }
    }

    private static int readInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(System.getProperty(key, String.valueOf(defaultValue)));
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    public static final class Builder {
        private HealingMode mode = HealingMode.SAFE_AUTO_HEAL;
        private int minimumConfidence = 75;
        private int safeAutoHealMinimumConfidence = 85;
        private HealingRiskLevel maximumAutoHealRisk = HealingRiskLevel.LOW;
        private boolean requireSemanticMatchForAutoHeal;
        private boolean blockHiddenCandidates = true;
        private boolean blockDisabledCandidates = true;
        private boolean failOnAmbiguousCandidates = true;
        private int maximumMatchesForAutoHeal = 1;
        private boolean exportReport = true;
        private boolean exportModeAudit = true;
        private Path strictApprovalFile = Path.of("qatra-healing-approved.json");
        private Duration timeout = Duration.ofSeconds(10);

        public Builder mode(HealingMode mode) {
            this.mode = mode == null ? HealingMode.SAFE_AUTO_HEAL : mode;
            return this;
        }

        public Builder minimumConfidence(int minimumConfidence) {
            this.minimumConfidence = clamp(minimumConfidence);
            return this;
        }

        public Builder safeAutoHealMinimumConfidence(int confidence) {
            this.safeAutoHealMinimumConfidence = clamp(confidence);
            return this;
        }

        public Builder maximumAutoHealRisk(HealingRiskLevel risk) {
            this.maximumAutoHealRisk = risk == null ? HealingRiskLevel.LOW : risk;
            return this;
        }

        public Builder requireSemanticMatchForAutoHeal(boolean value) {
            this.requireSemanticMatchForAutoHeal = value;
            return this;
        }

        public Builder blockHiddenCandidates(boolean value) {
            this.blockHiddenCandidates = value;
            return this;
        }

        public Builder blockDisabledCandidates(boolean value) {
            this.blockDisabledCandidates = value;
            return this;
        }

        public Builder failOnAmbiguousCandidates(boolean value) {
            this.failOnAmbiguousCandidates = value;
            return this;
        }

        public Builder maximumMatchesForAutoHeal(int value) {
            this.maximumMatchesForAutoHeal = Math.max(1, value);
            return this;
        }

        public Builder exportReport(boolean value) {
            this.exportReport = value;
            return this;
        }

        public Builder exportModeAudit(boolean value) {
            this.exportModeAudit = value;
            return this;
        }

        public Builder strictApprovalFile(Path value) {
            this.strictApprovalFile = value == null ? Path.of("qatra-healing-approved.json") : value;
            return this;
        }

        public Builder timeout(Duration timeout) {
            this.timeout = timeout == null ? Duration.ofSeconds(10) : timeout;
            return this;
        }

        public QatraHealingOptions build() {
            return new QatraHealingOptions(this);
        }

        private static int clamp(int value) {
            return Math.max(0, Math.min(100, value));
        }
    }
}
