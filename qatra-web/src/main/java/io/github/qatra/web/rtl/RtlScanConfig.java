package io.github.qatra.web.rtl;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;

import java.util.Locale;

/**
 * Runtime configuration for the QATRA RTL page scanner.
 *
 * <p>Configuration can be controlled from {@code qatra.properties} or Maven/system
 * properties. System properties always win, for example:</p>
 *
 * <pre>
 * mvn test -Dqatra.rtl.scan.digits=false -Dqatra.rtl.fail-on=errors
 * </pre>
 */
public final class RtlScanConfig {

    public static final String FAIL_ON_NONE = "none";
    public static final String FAIL_ON_ERRORS = "errors";
    public static final String FAIL_ON_WARNINGS = "warnings";
    public static final String FAIL_ON_ISSUES = "issues";

    private final boolean enabled;
    private final boolean scanDirection;
    private final boolean scanEncoding;
    private final boolean scanPlaceholder;
    private final boolean scanDigits;
    private final boolean scanMixedDirection;
    private final boolean scanAlignment;
    private final boolean attachReport;
    private final boolean exportReport;
    private final String reportDir;
    private final String reportFormats;
    private final String reportFileName;
    private final boolean historyEnabled;
    private final String historyDir;
    private final boolean historyIndexEnabled;
    private final String failOn;
    private final String selector;
    private final boolean baselineEnabled;
    private final String baselinePath;
    private final boolean baselineUpdate;
    private final boolean baselineFailOnNewIssues;
    private final boolean baselineReportExport;
    private final String baselineReportFileName;
    private final boolean qualityGateEnabled;
    private final int qualityGateMinScore;
    private final int qualityGateMaxErrors;
    private final int qualityGateMaxWarnings;
    private final boolean qualityGateFailOnFailure;
    private final boolean qualityGateReportExport;
    private final String qualityGateReportFileName;

    public RtlScanConfig(
            boolean enabled,
            boolean scanDirection,
            boolean scanEncoding,
            boolean scanPlaceholder,
            boolean scanDigits,
            boolean scanMixedDirection,
            boolean scanAlignment,
            boolean attachReport,
            boolean exportReport,
            String reportDir,
            String reportFormats,
            String reportFileName,
            boolean historyEnabled,
            String historyDir,
            boolean historyIndexEnabled,
            String failOn,
            String selector,
            boolean baselineEnabled,
            String baselinePath,
            boolean baselineUpdate,
            boolean baselineFailOnNewIssues,
            boolean baselineReportExport,
            String baselineReportFileName,
            boolean qualityGateEnabled,
            int qualityGateMinScore,
            int qualityGateMaxErrors,
            int qualityGateMaxWarnings,
            boolean qualityGateFailOnFailure,
            boolean qualityGateReportExport,
            String qualityGateReportFileName
    ) {
        this.enabled = enabled;
        this.scanDirection = scanDirection;
        this.scanEncoding = scanEncoding;
        this.scanPlaceholder = scanPlaceholder;
        this.scanDigits = scanDigits;
        this.scanMixedDirection = scanMixedDirection;
        this.scanAlignment = scanAlignment;
        this.attachReport = attachReport;
        this.exportReport = exportReport;
        this.reportDir = normalizeReportDir(reportDir);
        this.reportFormats = normalizeReportFormats(reportFormats);
        this.reportFileName = normalizeReportFileName(reportFileName);
        this.historyEnabled = historyEnabled;
        this.historyDir = normalizeHistoryDir(historyDir, this.reportDir);
        this.historyIndexEnabled = historyIndexEnabled;
        this.failOn = normalizeFailOn(failOn);
        this.selector = normalizeSelector(selector);
        this.baselineEnabled = baselineEnabled;
        this.baselinePath = normalizeBaselinePath(baselinePath, this.reportDir);
        this.baselineUpdate = baselineUpdate;
        this.baselineFailOnNewIssues = baselineFailOnNewIssues;
        this.baselineReportExport = baselineReportExport;
        this.baselineReportFileName = normalizeReportFileName(baselineReportFileName == null || baselineReportFileName.isBlank()
                ? "rtl-baseline-comparison" : baselineReportFileName);
        this.qualityGateEnabled = qualityGateEnabled;
        this.qualityGateMinScore = clamp(qualityGateMinScore, 0, 100);
        this.qualityGateMaxErrors = Math.max(0, qualityGateMaxErrors);
        this.qualityGateMaxWarnings = Math.max(0, qualityGateMaxWarnings);
        this.qualityGateFailOnFailure = qualityGateFailOnFailure;
        this.qualityGateReportExport = qualityGateReportExport;
        this.qualityGateReportFileName = normalizeReportFileName(qualityGateReportFileName == null || qualityGateReportFileName.isBlank()
                ? "rtl-quality-gate" : qualityGateReportFileName);
    }

    public static RtlScanConfig fromConfig() {
        QatraConfig config = QatraConfig.getInstance();

        boolean legacyFailOnIssues = config.getBooleanProperty(QatraProperties.RTL_FAIL_ON_ISSUES, true);
        String defaultFailOn = legacyFailOnIssues ? FAIL_ON_ISSUES : FAIL_ON_NONE;

        return new RtlScanConfig(
                config.getBooleanProperty(QatraProperties.RTL_SCAN_ENABLED, true),
                config.getBooleanProperty(QatraProperties.RTL_SCAN_DIRECTION, true),
                config.getBooleanProperty(QatraProperties.RTL_SCAN_ENCODING, true),
                config.getBooleanProperty(QatraProperties.RTL_SCAN_PLACEHOLDER, true),
                config.getBooleanProperty(QatraProperties.RTL_SCAN_DIGITS, true),
                config.getBooleanProperty(QatraProperties.RTL_SCAN_MIXED_DIRECTION, true),
                config.getBooleanProperty(QatraProperties.RTL_SCAN_ALIGNMENT, true),
                config.getBooleanProperty(QatraProperties.RTL_REPORT_ATTACH, true),
                config.getBooleanProperty(QatraProperties.RTL_REPORT_EXPORT, true),
                config.getProperty(QatraProperties.RTL_REPORT_DIR, "target/qatra-reports/rtl"),
                config.getProperty(QatraProperties.RTL_REPORT_FORMATS, "txt,json,html"),
                config.getProperty(QatraProperties.RTL_REPORT_FILENAME, "rtl-scan-report"),
                config.getBooleanProperty(QatraProperties.RTL_REPORT_HISTORY_ENABLED, true),
                config.getProperty(QatraProperties.RTL_REPORT_HISTORY_DIR, "target/qatra-reports/rtl/history"),
                config.getBooleanProperty(QatraProperties.RTL_REPORT_HISTORY_INDEX, true),
                config.getProperty(QatraProperties.RTL_FAIL_ON, defaultFailOn),
                config.getProperty(QatraProperties.RTL_SCAN_SELECTOR, "html, body, body *"),
                config.getBooleanProperty(QatraProperties.RTL_BASELINE_ENABLED, true),
                config.getProperty(QatraProperties.RTL_BASELINE_PATH, "target/qatra-reports/rtl/baseline/rtl-baseline.json"),
                config.getBooleanProperty(QatraProperties.RTL_BASELINE_UPDATE, false),
                config.getBooleanProperty(QatraProperties.RTL_BASELINE_FAIL_ON_NEW, true),
                config.getBooleanProperty(QatraProperties.RTL_BASELINE_REPORT_EXPORT, true),
                config.getProperty(QatraProperties.RTL_BASELINE_REPORT_FILENAME, "rtl-baseline-comparison"),
                config.getBooleanProperty(QatraProperties.RTL_QUALITY_GATE_ENABLED, true),
                config.getIntProperty(QatraProperties.RTL_QUALITY_GATE_MIN_SCORE, 80),
                config.getIntProperty(QatraProperties.RTL_QUALITY_GATE_MAX_ERRORS, 0),
                config.getIntProperty(QatraProperties.RTL_QUALITY_GATE_MAX_WARNINGS, 5),
                config.getBooleanProperty(QatraProperties.RTL_QUALITY_GATE_FAIL_ON_FAILURE, true),
                config.getBooleanProperty(QatraProperties.RTL_QUALITY_GATE_REPORT_EXPORT, true),
                config.getProperty(QatraProperties.RTL_QUALITY_GATE_REPORT_FILENAME, "rtl-quality-gate")
        );
    }

    public boolean isEnabled() { return enabled; }

    public boolean isReportAttachEnabled() { return attachReport; }

    public boolean isReportExportEnabled() { return exportReport; }

    public String reportDir() { return reportDir; }

    public String reportFormats() { return reportFormats; }

    public String reportFileName() { return reportFileName; }

    public boolean isHistoryEnabled() { return historyEnabled; }

    public String historyDir() { return historyDir; }

    public boolean isHistoryIndexEnabled() { return historyIndexEnabled; }

    public String failOn() { return failOn; }

    public String selector() { return selector; }

    public boolean isBaselineEnabled() { return baselineEnabled; }

    public String baselinePath() { return baselinePath; }

    public boolean isBaselineUpdateEnabled() { return baselineUpdate; }

    public boolean shouldFailOnNewBaselineIssues() { return baselineFailOnNewIssues; }

    public boolean isBaselineReportExportEnabled() { return baselineReportExport; }

    public String baselineReportFileName() { return baselineReportFileName; }

    public boolean isQualityGateEnabled() { return qualityGateEnabled; }

    public int qualityGateMinScore() { return qualityGateMinScore; }

    public int qualityGateMaxErrors() { return qualityGateMaxErrors; }

    public int qualityGateMaxWarnings() { return qualityGateMaxWarnings; }

    public boolean shouldFailOnQualityGateFailure() { return qualityGateFailOnFailure; }

    public boolean isQualityGateReportExportEnabled() { return qualityGateReportExport; }

    public String qualityGateReportFileName() { return qualityGateReportFileName; }

    public boolean shouldScan(RtlIssueType type) {
        if (type == null) {
            return true;
        }

        return switch (type) {
            case DIRECTION -> scanDirection;
            case ENCODING -> scanEncoding;
            case PLACEHOLDER -> scanPlaceholder;
            case DIGITS -> scanDigits;
            case MIXED_DIRECTION -> scanMixedDirection;
            case ALIGNMENT -> scanAlignment;
            case SCAN -> true;
        };
    }

    public boolean shouldFail(RtlScanResult result) {
        if (result == null) {
            return false;
        }

        return switch (failOn) {
            case FAIL_ON_NONE -> false;
            case FAIL_ON_ERRORS -> result.hasErrors();
            case FAIL_ON_WARNINGS -> result.errorCount() > 0 || result.warningCount() > 0;
            case FAIL_ON_ISSUES -> result.hasIssues();
            default -> result.hasIssues();
        };
    }



    /**
     * Create a mutable builder initialized with the current configuration values.
     * This is useful for tests and advanced users who need a local scanner configuration
     * without changing JVM-wide system properties. It also keeps parallel tests stable.
     */
    public Builder toBuilder() {
        return new Builder(this);
    }

    /**
     * Create a mutable builder initialized from qatra.properties and current system properties.
     */
    public static Builder builder() {
        return fromConfig().toBuilder();
    }

    public static final class Builder {
        private boolean enabled;
        private boolean scanDirection;
        private boolean scanEncoding;
        private boolean scanPlaceholder;
        private boolean scanDigits;
        private boolean scanMixedDirection;
        private boolean scanAlignment;
        private boolean attachReport;
        private boolean exportReport;
        private String reportDir;
        private String reportFormats;
        private String reportFileName;
        private boolean historyEnabled;
        private String historyDir;
        private boolean historyIndexEnabled;
        private String failOn;
        private String selector;
        private boolean baselineEnabled;
        private String baselinePath;
        private boolean baselineUpdate;
        private boolean baselineFailOnNewIssues;
        private boolean baselineReportExport;
        private String baselineReportFileName;
        private boolean qualityGateEnabled;
        private int qualityGateMinScore;
        private int qualityGateMaxErrors;
        private int qualityGateMaxWarnings;
        private boolean qualityGateFailOnFailure;
        private boolean qualityGateReportExport;
        private String qualityGateReportFileName;

        private Builder(RtlScanConfig config) {
            this.enabled = config.enabled;
            this.scanDirection = config.scanDirection;
            this.scanEncoding = config.scanEncoding;
            this.scanPlaceholder = config.scanPlaceholder;
            this.scanDigits = config.scanDigits;
            this.scanMixedDirection = config.scanMixedDirection;
            this.scanAlignment = config.scanAlignment;
            this.attachReport = config.attachReport;
            this.exportReport = config.exportReport;
            this.reportDir = config.reportDir;
            this.reportFormats = config.reportFormats;
            this.reportFileName = config.reportFileName;
            this.historyEnabled = config.historyEnabled;
            this.historyDir = config.historyDir;
            this.historyIndexEnabled = config.historyIndexEnabled;
            this.failOn = config.failOn;
            this.selector = config.selector;
            this.baselineEnabled = config.baselineEnabled;
            this.baselinePath = config.baselinePath;
            this.baselineUpdate = config.baselineUpdate;
            this.baselineFailOnNewIssues = config.baselineFailOnNewIssues;
            this.baselineReportExport = config.baselineReportExport;
            this.baselineReportFileName = config.baselineReportFileName;
            this.qualityGateEnabled = config.qualityGateEnabled;
            this.qualityGateMinScore = config.qualityGateMinScore;
            this.qualityGateMaxErrors = config.qualityGateMaxErrors;
            this.qualityGateMaxWarnings = config.qualityGateMaxWarnings;
            this.qualityGateFailOnFailure = config.qualityGateFailOnFailure;
            this.qualityGateReportExport = config.qualityGateReportExport;
            this.qualityGateReportFileName = config.qualityGateReportFileName;
        }

        public Builder enabled(boolean value) { this.enabled = value; return this; }
        public Builder scanDirection(boolean value) { this.scanDirection = value; return this; }
        public Builder scanEncoding(boolean value) { this.scanEncoding = value; return this; }
        public Builder scanPlaceholder(boolean value) { this.scanPlaceholder = value; return this; }
        public Builder scanDigits(boolean value) { this.scanDigits = value; return this; }
        public Builder scanMixedDirection(boolean value) { this.scanMixedDirection = value; return this; }
        public Builder scanAlignment(boolean value) { this.scanAlignment = value; return this; }
        public Builder attachReport(boolean value) { this.attachReport = value; return this; }
        public Builder exportReport(boolean value) { this.exportReport = value; return this; }
        public Builder reportDir(String value) { this.reportDir = value; return this; }
        public Builder reportFormats(String value) { this.reportFormats = value; return this; }
        public Builder reportFileName(String value) { this.reportFileName = value; return this; }
        public Builder historyEnabled(boolean value) { this.historyEnabled = value; return this; }
        public Builder historyDir(String value) { this.historyDir = value; return this; }
        public Builder historyIndexEnabled(boolean value) { this.historyIndexEnabled = value; return this; }
        public Builder failOn(String value) { this.failOn = value; return this; }
        public Builder selector(String value) { this.selector = value; return this; }
        public Builder baselineEnabled(boolean value) { this.baselineEnabled = value; return this; }
        public Builder baselinePath(String value) { this.baselinePath = value; return this; }
        public Builder baselineUpdate(boolean value) { this.baselineUpdate = value; return this; }
        public Builder baselineFailOnNewIssues(boolean value) { this.baselineFailOnNewIssues = value; return this; }
        public Builder baselineReportExport(boolean value) { this.baselineReportExport = value; return this; }
        public Builder baselineReportFileName(String value) { this.baselineReportFileName = value; return this; }
        public Builder qualityGateEnabled(boolean value) { this.qualityGateEnabled = value; return this; }
        public Builder qualityGateMinScore(int value) { this.qualityGateMinScore = value; return this; }
        public Builder qualityGateMaxErrors(int value) { this.qualityGateMaxErrors = value; return this; }
        public Builder qualityGateMaxWarnings(int value) { this.qualityGateMaxWarnings = value; return this; }
        public Builder qualityGateFailOnFailure(boolean value) { this.qualityGateFailOnFailure = value; return this; }
        public Builder qualityGateReportExport(boolean value) { this.qualityGateReportExport = value; return this; }
        public Builder qualityGateReportFileName(String value) { this.qualityGateReportFileName = value; return this; }

        public RtlScanConfig build() {
            return new RtlScanConfig(
                    enabled,
                    scanDirection,
                    scanEncoding,
                    scanPlaceholder,
                    scanDigits,
                    scanMixedDirection,
                    scanAlignment,
                    attachReport,
                    exportReport,
                    reportDir,
                    reportFormats,
                    reportFileName,
                    historyEnabled,
                    historyDir,
                    historyIndexEnabled,
                    failOn,
                    selector,
                    baselineEnabled,
                    baselinePath,
                    baselineUpdate,
                    baselineFailOnNewIssues,
                    baselineReportExport,
                    baselineReportFileName,
                    qualityGateEnabled,
                    qualityGateMinScore,
                    qualityGateMaxErrors,
                    qualityGateMaxWarnings,
                    qualityGateFailOnFailure,
                    qualityGateReportExport,
                    qualityGateReportFileName
            );
        }
    }

    public String describe() {
        return "RtlScanConfig{" +
                "enabled=" + enabled +
                ", direction=" + scanDirection +
                ", encoding=" + scanEncoding +
                ", placeholder=" + scanPlaceholder +
                ", digits=" + scanDigits +
                ", mixedDirection=" + scanMixedDirection +
                ", alignment=" + scanAlignment +
                ", attachReport=" + attachReport +
                ", exportReport=" + exportReport +
                ", reportDir='" + reportDir + '\'' +
                ", reportFormats='" + reportFormats + '\'' +
                ", reportFileName='" + reportFileName + '\'' +
                ", historyEnabled=" + historyEnabled +
                ", historyDir='" + historyDir + '\'' +
                ", historyIndexEnabled=" + historyIndexEnabled +
                ", baselineEnabled=" + baselineEnabled +
                ", baselinePath='" + baselinePath + '\'' +
                ", baselineUpdate=" + baselineUpdate +
                ", baselineFailOnNewIssues=" + baselineFailOnNewIssues +
                ", baselineReportExport=" + baselineReportExport +
                ", baselineReportFileName='" + baselineReportFileName + '\'' +
                ", qualityGateEnabled=" + qualityGateEnabled +
                ", qualityGateMinScore=" + qualityGateMinScore +
                ", qualityGateMaxErrors=" + qualityGateMaxErrors +
                ", qualityGateMaxWarnings=" + qualityGateMaxWarnings +
                ", qualityGateFailOnFailure=" + qualityGateFailOnFailure +
                ", qualityGateReportExport=" + qualityGateReportExport +
                ", qualityGateReportFileName='" + qualityGateReportFileName + '\'' +
                ", failOn='" + failOn + '\'' +
                ", selector='" + selector + '\'' +
                '}';
    }

    private static String normalizeFailOn(String value) {
        String normalized = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        if (normalized.isBlank()) {
            return FAIL_ON_ISSUES;
        }

        return switch (normalized) {
            case "off", "false", "disabled", "never", "none" -> FAIL_ON_NONE;
            case "error", "errors" -> FAIL_ON_ERRORS;
            case "warning", "warnings", "warnings-and-errors" -> FAIL_ON_WARNINGS;
            case "issue", "issues", "all" -> FAIL_ON_ISSUES;
            default -> FAIL_ON_ISSUES;
        };
    }

    private static String normalizeSelector(String value) {
        String selectorValue = value == null ? "" : value.trim();
        return selectorValue.isBlank() ? "html, body, body *" : selectorValue;
    }

    private static String normalizeReportDir(String value) {
        String dir = value == null ? "" : value.trim();
        return dir.isBlank() ? "target/qatra-reports/rtl" : dir;
    }

    private static String normalizeReportFormats(String value) {
        String formats = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        return formats.isBlank() ? "txt,json,html" : formats;
    }

    private static String normalizeHistoryDir(String value, String reportDir) {
        String dir = value == null ? "" : value.trim();
        if (!dir.isBlank()) {
            return dir;
        }
        return normalizeReportDir(reportDir) + "/history";
    }

    private static String normalizeBaselinePath(String value, String reportDir) {
        String path = value == null ? "" : value.trim();
        if (!path.isBlank()) {
            return path;
        }
        return normalizeReportDir(reportDir) + "/baseline/rtl-baseline.json";
    }

    private static String normalizeReportFileName(String value) {
        String fileName = value == null ? "" : value.trim();
        if (fileName.isBlank()) {
            return "rtl-scan-report";
        }
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
