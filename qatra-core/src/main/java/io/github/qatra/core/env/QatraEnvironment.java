package io.github.qatra.core.env;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;

import java.util.List;

/**
 * Convenience facade for the active QATRA environment profile.
 */
public final class QatraEnvironment {

    private final QatraConfig config;

    private QatraEnvironment(QatraConfig config) {
        this.config = config;
    }

    public static QatraEnvironment current() {
        return new QatraEnvironment(QatraConfig.getInstance());
    }

    public String name() {
        return config.activeEnvironment();
    }

    public String displayName() {
        return config.getProperty(QatraProperties.ENV_NAME, name());
    }

    public String description() {
        return config.getProperty(QatraProperties.ENV_DESCRIPTION, "");
    }

    public String baseUrl() {
        return config.getProperty(QatraProperties.BASE_URL, "");
    }

    public String apiBaseUrl() {
        return config.getProperty(QatraProperties.API_BASE_URL, "");
    }

    public boolean isLocal() {
        return "local".equals(name());
    }

    public boolean isDev() {
        return "dev".equals(name()) || "development".equals(name());
    }

    public boolean isStaging() {
        return "staging".equals(name()) || "stage".equals(name());
    }

    public boolean isProd() {
        return "prod".equals(name()) || "production".equals(name());
    }

    public List<String> loadedSources() {
        return config.loadedSources();
    }

    public String summary() {
        return "QATRA Environment{" +
                "name='" + name() + '\'' +
                ", displayName='" + displayName() + '\'' +
                ", baseUrl='" + baseUrl() + '\'' +
                ", apiBaseUrl='" + apiBaseUrl() + '\'' +
                ", loadedSources=" + loadedSources() +
                '}';
    }
}
