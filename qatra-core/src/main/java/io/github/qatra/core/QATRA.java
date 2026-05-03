package io.github.qatra.core;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.logger.QatraLogger;

/**
 * ======================================================
 *  QATRA — Quality Automation Testing & RTL Architecture
 * ======================================================
 *
 *  The single entry point for the entire QATRA framework.
 *  Everything starts here.
 *
 *  Usage:
 *  <pre>
 *    // The engine itself provides the drivers
 *    var driver = new QATRA.GUI.WebDriver();
 *    driver.browser().navigateTo("https://example.com")
 *          .element().type(By.id("search"), "QATRA")
 *          .assertThat().title().contains("Results");
 *  </pre>
 *
 * @author QATRA Community
 * @version 1.0.0
 */
public final class QATRA {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    // Prevent instantiation — this is a namespace class
    private QATRA() {
        throw new UnsupportedOperationException("QATRA is a utility namespace. Do not instantiate.");
    }

    /**
     * Returns the current QATRA framework version.
     */
    public static String version() {
        return QatraConfig.getInstance().getProperty("qatra.version", "1.0.0");
    }

    /**
     * Prints a summary of the current QATRA configuration to the log.
     */
    public static void printConfig() {
        LOG.info("╔══════════════════════════════════════════╗");
        LOG.info("║       QATRA Engine v" + version() + "              ║");
        LOG.info("║  Quality Automation & RTL Architecture   ║");
        LOG.info("╚══════════════════════════════════════════╝");
        QatraConfig.getInstance().printAll();
    }

    /**
     * GUI namespace — contains WebDriver and MobileDriver.
     * Nested as an inner class to allow: new QATRA.GUI.WebDriver()
     */
    public static final class GUI {
        private GUI() {}

        // WebDriver class is in qatra-web module.
        // It will extend this marker to be accessible as QATRA.GUI.WebDriver.
        // This is just the namespace declaration.
    }

    /**
     * API namespace — for REST/GraphQL testing.
     * Will be populated in qatra-api module.
     */
    public static final class API {
        private API() {}
    }

    /**
     * DB namespace — for database testing.
     * Will be populated in qatra-db module.
     */
    public static final class DB {
        private DB() {}
    }
}
