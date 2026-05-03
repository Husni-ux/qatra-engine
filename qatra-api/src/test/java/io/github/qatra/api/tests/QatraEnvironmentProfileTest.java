package io.github.qatra.api.tests;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.env.QatraConfigValidator;
import io.github.qatra.core.env.QatraEnvironment;
import io.github.qatra.core.env.QatraSecrets;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * Tests for Phase 3.7 environment profile and configuration management support.
 */
public class QatraEnvironmentProfileTest {

    @AfterMethod(alwaysRun = true)
    public void cleanupSystemProperties() {
        System.clearProperty(QatraProperties.ENV);
        System.clearProperty(QatraProperties.API_BASE_URL);
        System.clearProperty("qatra.demo.token");
        QatraConfig.reload();
    }

    @Test
    public void stagingProfileOverridesBaseConfigurationTest() {
        System.setProperty(QatraProperties.ENV, "staging");
        QatraConfig.reload();

        QatraEnvironment environment = QatraEnvironment.current();

        Assert.assertEquals(environment.name(), "staging");
        Assert.assertEquals(environment.displayName(), "Staging");
        Assert.assertEquals(environment.apiBaseUrl(), "https://staging.example.com/api");
        Assert.assertTrue(environment.loadedSources().contains("qatra-staging.properties"));
    }

    @Test
    public void systemPropertiesOverrideEnvironmentProfileTest() {
        System.setProperty(QatraProperties.ENV, "staging");
        System.setProperty(QatraProperties.API_BASE_URL, "https://override.example.com/api");
        QatraConfig.reload();

        Assert.assertEquals(
                QatraConfig.getInstance().getProperty(QatraProperties.API_BASE_URL),
                "https://override.example.com/api"
        );
    }

    @Test
    public void requiredConfigValidationPassesWhenKeysExistTest() {
        System.setProperty(QatraProperties.ENV, "dev");
        QatraConfig.reload();

        QatraConfigValidator.validateRequiredProperties(
                QatraProperties.BASE_URL,
                QatraProperties.API_BASE_URL
        );
    }

    @Test
    public void secretValuesAreMaskedForSafeLoggingTest() {
        System.setProperty("qatra.demo.token", "super-secret-token-12345");
        QatraConfig.reload();

        Assert.assertEquals(QatraSecrets.get("qatra.demo.token"), "super-secret-token-12345");
        Assert.assertTrue(QatraSecrets.masked("qatra.demo.token").contains("****"));
        Assert.assertFalse(QatraSecrets.masked("qatra.demo.token").contains("secret-token"));
    }
}
