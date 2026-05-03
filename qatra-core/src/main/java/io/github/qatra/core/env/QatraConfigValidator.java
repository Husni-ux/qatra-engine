package io.github.qatra.core.env;

import io.github.qatra.core.config.QatraConfig;

/**
 * Small validation facade for teams that want explicit config validation in setup hooks.
 */
public final class QatraConfigValidator {

    private QatraConfigValidator() {
    }

    public static void validateRequiredProperties() {
        QatraConfig.getInstance().validateRequiredProperties();
    }

    public static void validateRequiredProperties(String... keys) {
        if (keys == null || keys.length == 0) {
            return;
        }

        StringBuilder missing = new StringBuilder();
        QatraConfig config = QatraConfig.getInstance();

        for (String key : keys) {
            if (key == null || key.isBlank()) continue;
            String value = config.getProperty(key.trim());
            if (value == null || value.isBlank()) {
                if (!missing.isEmpty()) missing.append(", ");
                missing.append(key.trim());
            }
        }

        if (!missing.isEmpty()) {
            throw new IllegalStateException("Missing required QATRA configuration keys: " + missing);
        }
    }
}
