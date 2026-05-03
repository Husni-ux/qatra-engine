package io.github.qatra.core.env;

import io.github.qatra.core.config.QatraConfig;

/**
 * Helper for reading secret-like configuration values without printing them accidentally.
 */
public final class QatraSecrets {

    private QatraSecrets() {
    }

    public static String get(String key) {
        return QatraConfig.getInstance().getSecretProperty(key);
    }

    public static String masked(String key) {
        return QatraConfig.getInstance().getMaskedProperty(key);
    }
}
