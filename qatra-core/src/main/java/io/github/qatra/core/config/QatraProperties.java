package io.github.qatra.core.config;

/**
 * All QATRA property keys in one place.
 * Use these constants instead of raw strings.
 */
public final class QatraProperties {

    private QatraProperties() {}


    // ─── Environment Profiles / Configuration Management ────────────────────
    public static final String ENV                         = "qatra.env";
    public static final String CONFIG_PROFILE_FILE_PATTERN = "qatra.config.profile-file-pattern";
    public static final String CONFIG_LOADED_SOURCES       = "qatra.config.loaded.sources";
    public static final String CONFIG_VALIDATE_ON_STARTUP  = "qatra.config.validate-on-startup";
    public static final String CONFIG_REQUIRED_KEYS        = "qatra.config.required";
    public static final String CONFIG_ENV_OVERRIDES        = "qatra.config.env-overrides";
    public static final String CONFIG_MASK_SECRETS         = "qatra.config.mask-secrets";
    public static final String CONFIG_SECRET_KEYWORDS      = "qatra.config.secret-keywords";
    public static final String CONFIG_FAIL_ON_UNKNOWN_ENV  = "qatra.config.fail-on-unknown-env";
    public static final String ENV_NAME                    = "qatra.env.name";
    public static final String ENV_DESCRIPTION             = "qatra.env.description";

    // ─── Web ──────────────────────────────────────────────────────────────────
    public static final String BROWSER             = "qatra.browser";
    public static final String HEADLESS            = "qatra.headless";
    public static final String BASE_URL            = "qatra.base.url";
    public static final String ELEMENT_TIMEOUT     = "qatra.timeout.element";
    public static final String PAGE_LOAD_TIMEOUT   = "qatra.timeout.pageload";
    public static final String IMPLICIT_WAIT       = "qatra.timeout.implicit";
    public static final String WAIT_POLLING_MS    = "qatra.wait.polling.ms";
    public static final String WAIT_PAGE_READY    = "qatra.wait.page.ready";
    public static final String HIGHLIGHT_ELEMENTS = "qatra.element.highlight";
    public static final String MAXIMIZE_BROWSER    = "qatra.browser.maximize";
    public static final String BROWSER_VERSION     = "qatra.browser.version";


    // ─── API ──────────────────────────────────────────────────────────────────
    public static final String API_BASE_URL          = "qatra.api.base.url";
    public static final String API_TIMEOUT_SECONDS   = "qatra.api.timeout.seconds";
    public static final String API_RELAXED_HTTPS     = "qatra.api.relaxed.https";
    public static final String API_LOG_REQUEST       = "qatra.api.log.request";
    public static final String API_LOG_RESPONSE      = "qatra.api.log.response";
    public static final String API_ATTACH_REQUEST    = "qatra.api.attach.request";
    public static final String API_ATTACH_RESPONSE   = "qatra.api.attach.response";

    // ─── Mobile ───────────────────────────────────────────────────────────────
    public static final String PLATFORM_NAME      = "qatra.mobile.platform";
    public static final String DEVICE_NAME        = "qatra.mobile.device";
    public static final String APP_PATH           = "qatra.mobile.app";
    public static final String APPIUM_URL         = "qatra.mobile.appium.url";

    // ─── Language & RTL ───────────────────────────────────────────────────────
    public static final String LANGUAGE           = "qatra.language";
    public static final String RTL_VALIDATION     = "qatra.rtl.validation";
    public static final String RTL_SCAN_ENABLED         = "qatra.rtl.scan.enabled";
    public static final String RTL_FAIL_ON_ISSUES       = "qatra.rtl.fail.on.issues"; // legacy
    public static final String RTL_FAIL_ON              = "qatra.rtl.fail-on";
    public static final String RTL_REPORT_ATTACH        = "qatra.rtl.report.attach";
    public static final String RTL_REPORT_EXPORT        = "qatra.rtl.report.export";
    public static final String RTL_REPORT_DIR           = "qatra.rtl.report.dir";
    public static final String RTL_REPORT_FORMATS       = "qatra.rtl.report.formats";
    public static final String RTL_REPORT_FILENAME      = "qatra.rtl.report.filename";
    public static final String RTL_REPORT_HISTORY_ENABLED = "qatra.rtl.report.history.enabled";
    public static final String RTL_REPORT_HISTORY_DIR   = "qatra.rtl.report.history.dir";
    public static final String RTL_REPORT_HISTORY_INDEX = "qatra.rtl.report.history.index";
    public static final String RTL_SCAN_DIRECTION       = "qatra.rtl.scan.direction";
    public static final String RTL_SCAN_ENCODING        = "qatra.rtl.scan.encoding";
    public static final String RTL_SCAN_PLACEHOLDER     = "qatra.rtl.scan.placeholder";
    public static final String RTL_SCAN_DIGITS          = "qatra.rtl.scan.digits";
    public static final String RTL_SCAN_MIXED_DIRECTION = "qatra.rtl.scan.mixed-direction";
    public static final String RTL_SCAN_ALIGNMENT       = "qatra.rtl.scan.alignment";
    public static final String RTL_SCAN_SELECTOR        = "qatra.rtl.scan.selector";
    public static final String RTL_BASELINE_ENABLED        = "qatra.rtl.baseline.enabled";
    public static final String RTL_BASELINE_PATH           = "qatra.rtl.baseline.path";
    public static final String RTL_BASELINE_UPDATE         = "qatra.rtl.baseline.update";
    public static final String RTL_BASELINE_FAIL_ON_NEW    = "qatra.rtl.baseline.fail-on-new-issues";
    public static final String RTL_BASELINE_REPORT_EXPORT  = "qatra.rtl.baseline.report.export";
    public static final String RTL_BASELINE_REPORT_FILENAME = "qatra.rtl.baseline.report.filename";
    public static final String RTL_QUALITY_GATE_ENABLED      = "qatra.rtl.quality-gate.enabled";
    public static final String RTL_QUALITY_GATE_MIN_SCORE    = "qatra.rtl.quality-gate.min-score";
    public static final String RTL_QUALITY_GATE_MAX_ERRORS   = "qatra.rtl.quality-gate.max-errors";
    public static final String RTL_QUALITY_GATE_MAX_WARNINGS = "qatra.rtl.quality-gate.max-warnings";
    public static final String RTL_QUALITY_GATE_FAIL_ON_FAILURE = "qatra.rtl.quality-gate.fail-on-failure";
    public static final String RTL_QUALITY_GATE_REPORT_EXPORT = "qatra.rtl.quality-gate.report.export";
    public static final String RTL_QUALITY_GATE_REPORT_FILENAME = "qatra.rtl.quality-gate.report.filename";



    // ─── Data-Driven Testing ─────────────────────────────────────────────────
    public static final String DATA_DIR              = "qatra.data.dir";
    public static final String DATA_ATTACH           = "qatra.data.attach";
    public static final String DATA_CSV_DELIMITER    = "qatra.data.csv.delimiter";
    public static final String DATA_CSV_HAS_HEADER   = "qatra.data.csv.has-header";
    public static final String DATA_EXCEL_SHEET      = "qatra.data.excel.sheet";
    public static final String DATA_EXCEL_SHEET_INDEX = "qatra.data.excel.sheet-index";

    // ─── Cloud ────────────────────────────────────────────────────────────────
    public static final String CLOUD_PROVIDER     = "qatra.cloud.provider"; // browserstack, lambdatest
    public static final String CLOUD_USERNAME     = "qatra.cloud.username";
    public static final String CLOUD_ACCESS_KEY   = "qatra.cloud.accesskey";

    // ─── Retry / Parallel / Stability ────────────────────────────────────────
    public static final String RETRY_ENABLED          = "qatra.retry.enabled";
    public static final String RETRY_COUNT            = "qatra.retry.count";
    public static final String RETRY_ATTACH_EVIDENCE  = "qatra.retry.attach.evidence";
    public static final String PARALLEL_ENABLED       = "qatra.parallel.enabled";
    public static final String PARALLEL_THREAD_COUNT  = "qatra.parallel.thread-count";
    public static final String PARALLEL_MODE          = "qatra.parallel.mode";
    public static final String STABILITY_ATTEMPTS     = "qatra.stability.attempts";
    public static final String STABILITY_DELAY_MS     = "qatra.stability.delay.ms";
    public static final String STABILITY_ATTACH       = "qatra.stability.attach";

    // ─── Reporting ────────────────────────────────────────────────────────────
    public static final String TAKE_SCREENSHOTS      = "qatra.screenshots";
    public static final String SCREENSHOT_ON_FAILURE = "qatra.screenshots.on.failure";
    public static final String SCREENSHOTS_DIR       = "qatra.screenshots.dir";
    public static final String RECORD_VIDEO          = "qatra.video.record";
    public static final String ALLURE_RESULTS_DIR    = "qatra.allure.results.dir";

    // ─── Failure Evidence / Diagnostics ──────────────────────────────────────
    public static final String EVIDENCE_ON_FAILURE        = "qatra.evidence.on.failure";
    public static final String PAGE_SOURCE_ON_FAILURE     = "qatra.evidence.page-source.on.failure";
    public static final String BROWSER_LOGS_ON_FAILURE    = "qatra.evidence.browser-logs.on.failure";
    public static final String PAGE_SOURCE_DIR            = "qatra.evidence.page-source.dir";
    public static final String BROWSER_LOGS_DIR           = "qatra.evidence.browser-logs.dir";
}

