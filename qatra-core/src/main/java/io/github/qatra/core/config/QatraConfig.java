package io.github.qatra.core.config;

import io.github.qatra.core.logger.QatraLogger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Central configuration manager for QATRA.
 *
 * <p>Phase 3.7 adds environment profile support.</p>
 *
 * <p>Priority order (highest to lowest):</p>
 * <ol>
 *     <li>System properties, for example {@code -Dqatra.browser=firefox}</li>
 *     <li>Environment variables, for example {@code QATRA_BROWSER=firefox}</li>
 *     <li>Active profile file, for example {@code qatra-staging.properties}</li>
 *     <li>Base {@code qatra.properties}</li>
 *     <li>Built-in defaults</li>
 * </ol>
 */
public final class QatraConfig {

    private static final QatraLogger LOG = QatraLogger.getInstance();
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}]+)}");
    private static volatile QatraConfig instance;

    private final Properties properties = new Properties();
    private final Properties profileProperties = new Properties();
    private final Properties defaults = new Properties();
    private final List<String> loadedSources = new ArrayList<>();

    private String activeEnvironment = "local";

    private QatraConfig() {
        loadDefaults();
        loadUserProperties();
        resolveActiveEnvironment();
        loadProfileProperties();
        validateOnStartupIfEnabled();
    }

    public static QatraConfig getInstance() {
        if (instance == null) {
            synchronized (QatraConfig.class) {
                if (instance == null) {
                    instance = new QatraConfig();
                }
            }
        }
        return instance;
    }

    /**
     * Reset the singleton and reload configuration. Intended for tests and tooling.
     */
    public static void reload() {
        synchronized (QatraConfig.class) {
            instance = new QatraConfig();
        }
    }

    // ─── Getters ──────────────────────────────────────────────────────────────

    public String getProperty(String key) {
        return resolvePlaceholders(rawProperty(key), new HashSet<>());
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    public String getSecretProperty(String key) {
        return getProperty(key);
    }

    public String getMaskedProperty(String key) {
        String value = getProperty(key);
        if (value == null) return null;
        return shouldMaskKey(key) ? mask(value) : value;
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            LOG.warn("Invalid integer value for key '{}': '{}'. Using default: {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    public String activeEnvironment() {
        return activeEnvironment;
    }

    public List<String> loadedSources() {
        return List.copyOf(loadedSources);
    }

    public Properties snapshot() {
        Properties snapshot = new Properties();
        defaults.forEach((key, value) -> snapshot.setProperty(String.valueOf(key), String.valueOf(value)));
        properties.forEach((key, value) -> snapshot.setProperty(String.valueOf(key), String.valueOf(value)));
        profileProperties.forEach((key, value) -> snapshot.setProperty(String.valueOf(key), String.valueOf(value)));
        System.getProperties().forEach((key, value) -> {
            String stringKey = String.valueOf(key);
            if (stringKey.startsWith("qatra.")) {
                snapshot.setProperty(stringKey, String.valueOf(value));
            }
        });
        return snapshot;
    }

    public void validateRequiredProperties() {
        String requiredKeys = getProperty(QatraProperties.CONFIG_REQUIRED_KEYS, "");
        if (requiredKeys == null || requiredKeys.isBlank()) {
            return;
        }

        List<String> missing = new ArrayList<>();
        for (String key : requiredKeys.split(",")) {
            String trimmed = key.trim();
            if (trimmed.isEmpty()) continue;
            String value = getProperty(trimmed);
            if (value == null || value.isBlank()) {
                missing.add(trimmed);
            }
        }

        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "Missing required QATRA configuration keys for environment '" + activeEnvironment + "': " + missing
            );
        }
    }

    public void printAll() {
        LOG.info("─── QATRA Configuration ─────────────────────");
        LOG.info("  Environment : {}", activeEnvironment());
        LOG.info("  Sources     : {}", loadedSources());
        LOG.info("  Browser     : {}", getMaskedProperty(QatraProperties.BROWSER));
        LOG.info("  Headless    : {}", getMaskedProperty(QatraProperties.HEADLESS));
        LOG.info("  Base URL    : {}", getMaskedProperty(QatraProperties.BASE_URL));
        LOG.info("  Timeout(s)  : {}", getMaskedProperty(QatraProperties.ELEMENT_TIMEOUT));
        LOG.info("  Language    : {}", getMaskedProperty(QatraProperties.LANGUAGE));
        LOG.info("  API Base URL: {}", getMaskedProperty(QatraProperties.API_BASE_URL));
        LOG.info("  Screenshots : {}", getMaskedProperty(QatraProperties.TAKE_SCREENSHOTS));
        LOG.info("  Evidence    : {}", getMaskedProperty(QatraProperties.EVIDENCE_ON_FAILURE));
        LOG.info("─────────────────────────────────────────────");
    }

    // ─── Loaders ──────────────────────────────────────────────────────────────

    private void loadDefaults() {
        // Environment Profiles / Configuration Management
        defaults.setProperty(QatraProperties.ENV, "local");
        defaults.setProperty(QatraProperties.CONFIG_PROFILE_FILE_PATTERN, "qatra-%s.properties");
        defaults.setProperty(QatraProperties.CONFIG_VALIDATE_ON_STARTUP, "false");
        defaults.setProperty(QatraProperties.CONFIG_REQUIRED_KEYS, "");
        defaults.setProperty(QatraProperties.CONFIG_ENV_OVERRIDES, "true");
        defaults.setProperty(QatraProperties.CONFIG_MASK_SECRETS, "true");
        defaults.setProperty(QatraProperties.CONFIG_SECRET_KEYWORDS, "password,secret,token,key,accesskey,authorization,bearer");
        defaults.setProperty(QatraProperties.CONFIG_FAIL_ON_UNKNOWN_ENV, "false");
        defaults.setProperty(QatraProperties.ENV_NAME, "Local");
        defaults.setProperty(QatraProperties.ENV_DESCRIPTION, "Local/default QATRA environment");

        // Web
        defaults.setProperty(QatraProperties.BROWSER, "chrome");
        defaults.setProperty(QatraProperties.HEADLESS, "false");
        defaults.setProperty(QatraProperties.BASE_URL, "");
        defaults.setProperty(QatraProperties.ELEMENT_TIMEOUT, "10");
        defaults.setProperty(QatraProperties.PAGE_LOAD_TIMEOUT, "30");
        defaults.setProperty(QatraProperties.IMPLICIT_WAIT, "0");
        defaults.setProperty(QatraProperties.WAIT_POLLING_MS, "250");
        defaults.setProperty(QatraProperties.WAIT_PAGE_READY, "true");
        defaults.setProperty(QatraProperties.HIGHLIGHT_ELEMENTS, "false");
        defaults.setProperty(QatraProperties.MAXIMIZE_BROWSER, "true");
        defaults.setProperty(QatraProperties.BROWSER_VERSION, "");

        // API
        defaults.setProperty(QatraProperties.API_BASE_URL, "");
        defaults.setProperty(QatraProperties.API_TIMEOUT_SECONDS, "30");
        defaults.setProperty(QatraProperties.API_RELAXED_HTTPS, "true");
        defaults.setProperty(QatraProperties.API_LOG_REQUEST, "true");
        defaults.setProperty(QatraProperties.API_LOG_RESPONSE, "true");
        defaults.setProperty(QatraProperties.API_ATTACH_REQUEST, "true");
        defaults.setProperty(QatraProperties.API_ATTACH_RESPONSE, "true");

        // Language & RTL
        defaults.setProperty(QatraProperties.LANGUAGE, "en");
        defaults.setProperty(QatraProperties.RTL_VALIDATION, "false");
        defaults.setProperty(QatraProperties.RTL_SCAN_ENABLED, "true");
        defaults.setProperty(QatraProperties.RTL_FAIL_ON_ISSUES, "true");
        defaults.setProperty(QatraProperties.RTL_FAIL_ON, "issues");
        defaults.setProperty(QatraProperties.RTL_REPORT_ATTACH, "true");
        defaults.setProperty(QatraProperties.RTL_REPORT_EXPORT, "true");
        defaults.setProperty(QatraProperties.RTL_REPORT_DIR, "target/qatra-reports/rtl");
        defaults.setProperty(QatraProperties.RTL_REPORT_FORMATS, "txt,json,html");
        defaults.setProperty(QatraProperties.RTL_REPORT_FILENAME, "rtl-scan-report");
        defaults.setProperty(QatraProperties.RTL_REPORT_HISTORY_ENABLED, "true");
        defaults.setProperty(QatraProperties.RTL_REPORT_HISTORY_DIR, "target/qatra-reports/rtl/history");
        defaults.setProperty(QatraProperties.RTL_REPORT_HISTORY_INDEX, "true");
        defaults.setProperty(QatraProperties.RTL_SCAN_DIRECTION, "true");
        defaults.setProperty(QatraProperties.RTL_SCAN_ENCODING, "true");
        defaults.setProperty(QatraProperties.RTL_SCAN_PLACEHOLDER, "true");
        defaults.setProperty(QatraProperties.RTL_SCAN_DIGITS, "true");
        defaults.setProperty(QatraProperties.RTL_SCAN_MIXED_DIRECTION, "true");
        defaults.setProperty(QatraProperties.RTL_SCAN_ALIGNMENT, "true");
        defaults.setProperty(QatraProperties.RTL_SCAN_SELECTOR, "html, body, body *");
        defaults.setProperty(QatraProperties.RTL_BASELINE_ENABLED, "true");
        defaults.setProperty(QatraProperties.RTL_BASELINE_PATH, "target/qatra-reports/rtl/baseline/rtl-baseline.json");
        defaults.setProperty(QatraProperties.RTL_BASELINE_UPDATE, "false");
        defaults.setProperty(QatraProperties.RTL_BASELINE_FAIL_ON_NEW, "true");
        defaults.setProperty(QatraProperties.RTL_BASELINE_REPORT_EXPORT, "true");
        defaults.setProperty(QatraProperties.RTL_BASELINE_REPORT_FILENAME, "rtl-baseline-comparison");
        defaults.setProperty(QatraProperties.RTL_QUALITY_GATE_ENABLED, "true");
        defaults.setProperty(QatraProperties.RTL_QUALITY_GATE_MIN_SCORE, "80");
        defaults.setProperty(QatraProperties.RTL_QUALITY_GATE_MAX_ERRORS, "0");
        defaults.setProperty(QatraProperties.RTL_QUALITY_GATE_MAX_WARNINGS, "5");
        defaults.setProperty(QatraProperties.RTL_QUALITY_GATE_FAIL_ON_FAILURE, "true");
        defaults.setProperty(QatraProperties.RTL_QUALITY_GATE_REPORT_EXPORT, "true");
        defaults.setProperty(QatraProperties.RTL_QUALITY_GATE_REPORT_FILENAME, "rtl-quality-gate");

        // Data-driven testing
        defaults.setProperty(QatraProperties.DATA_DIR, "src/test/resources/test-data");
        defaults.setProperty(QatraProperties.DATA_ATTACH, "true");
        defaults.setProperty(QatraProperties.DATA_CSV_DELIMITER, ",");
        defaults.setProperty(QatraProperties.DATA_CSV_HAS_HEADER, "true");
        defaults.setProperty(QatraProperties.DATA_EXCEL_SHEET, "");
        defaults.setProperty(QatraProperties.DATA_EXCEL_SHEET_INDEX, "0");

        // Retry / Parallel / Stability
        defaults.setProperty(QatraProperties.RETRY_ENABLED, "false");
        defaults.setProperty(QatraProperties.RETRY_COUNT, "1");
        defaults.setProperty(QatraProperties.RETRY_ATTACH_EVIDENCE, "true");
        defaults.setProperty(QatraProperties.PARALLEL_ENABLED, "false");
        defaults.setProperty(QatraProperties.PARALLEL_THREAD_COUNT, "2");
        defaults.setProperty(QatraProperties.PARALLEL_MODE, "methods");
        defaults.setProperty(QatraProperties.STABILITY_ATTEMPTS, "3");
        defaults.setProperty(QatraProperties.STABILITY_DELAY_MS, "250");
        defaults.setProperty(QatraProperties.STABILITY_ATTACH, "true");

        // Reporting
        defaults.setProperty(QatraProperties.TAKE_SCREENSHOTS, "true");
        defaults.setProperty(QatraProperties.SCREENSHOT_ON_FAILURE, "true");
        defaults.setProperty(QatraProperties.SCREENSHOTS_DIR, "target/qatra-reports/screenshots");
        defaults.setProperty(QatraProperties.RECORD_VIDEO, "false");
        defaults.setProperty(QatraProperties.EVIDENCE_ON_FAILURE, "true");
        defaults.setProperty(QatraProperties.PAGE_SOURCE_ON_FAILURE, "true");
        defaults.setProperty(QatraProperties.BROWSER_LOGS_ON_FAILURE, "true");
        defaults.setProperty(QatraProperties.PAGE_SOURCE_DIR, "target/qatra-reports/page-source");
        defaults.setProperty(QatraProperties.BROWSER_LOGS_DIR, "target/qatra-reports/browser-logs");

        // Cloud
        defaults.setProperty(QatraProperties.CLOUD_PROVIDER, "");
        defaults.setProperty(QatraProperties.CLOUD_USERNAME, "");
        defaults.setProperty(QatraProperties.CLOUD_ACCESS_KEY, "");

        // Engine
        defaults.setProperty("qatra.version", "1.0.0");

        LOG.debug("Default configuration loaded.");
    }

    private void loadUserProperties() {
        String[] locations = {"qatra.properties", "config/qatra.properties"};
        for (String location : locations) {
            try (InputStream is = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(location)) {
                if (is != null) {
                    properties.load(is);
                    loadedSources.add(location);
                    LOG.info("Loaded base configuration from: {}", location);
                    return;
                }
            } catch (IOException e) {
                LOG.warn("Could not load properties from: {}", location);
            }
        }
        LOG.debug("No qatra.properties found. Using defaults + system/environment properties.");
    }

    private void resolveActiveEnvironment() {
        String env = firstNonBlank(
                System.getProperty(QatraProperties.ENV),
                environmentValue(QatraProperties.ENV),
                properties.getProperty(QatraProperties.ENV),
                defaults.getProperty(QatraProperties.ENV)
        );

        activeEnvironment = normalizeEnvironment(env);
        LOG.info("Active QATRA environment: {}", activeEnvironment);
    }

    private void loadProfileProperties() {
        String pattern = rawPropertyWithoutProfile(QatraProperties.CONFIG_PROFILE_FILE_PATTERN);
        String fileName = String.format(pattern == null || pattern.isBlank() ? "qatra-%s.properties" : pattern, activeEnvironment);

        try (InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(fileName)) {
            if (is != null) {
                profileProperties.load(is);
                loadedSources.add(fileName);
                LOG.info("Loaded environment profile configuration from: {}", fileName);
                return;
            }
        } catch (IOException e) {
            LOG.warn("Could not load environment profile properties from: {}", fileName);
        }

        boolean failOnUnknown = Boolean.parseBoolean(rawPropertyWithoutProfile(QatraProperties.CONFIG_FAIL_ON_UNKNOWN_ENV));
        if (failOnUnknown) {
            throw new IllegalStateException("QATRA environment profile file was not found: " + fileName);
        }

        LOG.debug("No profile file found for environment '{}'. Expected: {}", activeEnvironment, fileName);
    }

    private void validateOnStartupIfEnabled() {
        if (getBooleanProperty(QatraProperties.CONFIG_VALIDATE_ON_STARTUP, false)) {
            validateRequiredProperties();
            LOG.info("QATRA configuration validation passed for environment: {}", activeEnvironment);
        }
    }

    // ─── Resolution Helpers ──────────────────────────────────────────────────

    private String rawProperty(String key) {
        String sysVal = System.getProperty(key);
        if (notBlank(sysVal)) return sysVal;

        String envVal = environmentValue(key);
        if (notBlank(envVal)) return envVal;

        String profileVal = profileProperties.getProperty(key);
        if (notBlank(profileVal)) return profileVal;

        String propVal = properties.getProperty(key);
        if (notBlank(propVal)) return propVal;

        return defaults.getProperty(key);
    }

    private String rawPropertyWithoutProfile(String key) {
        String sysVal = System.getProperty(key);
        if (notBlank(sysVal)) return sysVal;

        String envVal = environmentValue(key);
        if (notBlank(envVal)) return envVal;

        String propVal = properties.getProperty(key);
        if (notBlank(propVal)) return propVal;

        return defaults.getProperty(key);
    }

    private String resolvePlaceholders(String value, Set<String> visitedKeys) {
        if (value == null || value.isBlank()) {
            return value;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(value);
        StringBuffer resolved = new StringBuffer();

        while (matcher.find()) {
            String nestedKey = matcher.group(1).trim();
            String replacement;

            if (visitedKeys.contains(nestedKey)) {
                replacement = "";
                LOG.warn("Circular placeholder reference detected for key '{}'. Replacing with empty value.", nestedKey);
            } else {
                visitedKeys.add(nestedKey);
                replacement = resolvePlaceholders(rawProperty(nestedKey), visitedKeys);
                visitedKeys.remove(nestedKey);
            }

            matcher.appendReplacement(resolved, Matcher.quoteReplacement(replacement == null ? "" : replacement));
        }

        matcher.appendTail(resolved);
        return resolved.toString();
    }

    private String environmentValue(String key) {
        boolean envOverridesEnabled = Boolean.parseBoolean(rawPropertyEnvToggleSafe());
        if (!envOverridesEnabled) {
            return null;
        }

        String normalized = key.toUpperCase(Locale.ROOT)
                .replace('.', '_')
                .replace('-', '_');
        return System.getenv(normalized);
    }

    private String rawPropertyEnvToggleSafe() {
        String sysVal = System.getProperty(QatraProperties.CONFIG_ENV_OVERRIDES);
        if (notBlank(sysVal)) return sysVal;

        String normalized = QatraProperties.CONFIG_ENV_OVERRIDES.toUpperCase(Locale.ROOT)
                .replace('.', '_')
                .replace('-', '_');
        String envVal = System.getenv(normalized);
        if (notBlank(envVal)) return envVal;

        String propVal = properties.getProperty(QatraProperties.CONFIG_ENV_OVERRIDES);
        if (notBlank(propVal)) return propVal;

        return defaults.getProperty(QatraProperties.CONFIG_ENV_OVERRIDES, "true");
    }

    private boolean shouldMaskKey(String key) {
        if (!getBooleanProperty(QatraProperties.CONFIG_MASK_SECRETS, true)) {
            return false;
        }

        String lowerKey = key == null ? "" : key.toLowerCase(Locale.ROOT);
        String keywords = getProperty(QatraProperties.CONFIG_SECRET_KEYWORDS,
                "password,secret,token,key,accesskey,authorization,bearer");

        for (String keyword : keywords.split(",")) {
            String trimmed = keyword.trim().toLowerCase(Locale.ROOT);
            if (!trimmed.isEmpty() && lowerKey.contains(trimmed)) {
                return true;
            }
        }
        return false;
    }

    private String mask(String value) {
        if (value == null) return null;
        if (value.length() <= 4) return "****";
        return value.substring(0, 2) + "****" + value.substring(value.length() - 2);
    }

    private String normalizeEnvironment(String env) {
        if (env == null || env.isBlank()) {
            return "local";
        }
        return env.trim()
                .toLowerCase(Locale.ROOT)
                .replace("production", "prod");
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (notBlank(value)) return value;
        }
        return null;
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
