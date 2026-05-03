package io.github.qatra.core.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * QATRA's central logger.
 * Wraps SLF4J with clean, emoji-enhanced output for test logs.
 */
public final class QatraLogger {

    private static volatile QatraLogger instance;
    private final Logger logger;

    private QatraLogger() {
        this.logger = LoggerFactory.getLogger("QATRA");
    }

    public static QatraLogger getInstance() {
        if (instance == null) {
            synchronized (QatraLogger.class) {
                if (instance == null) {
                    instance = new QatraLogger();
                }
            }
        }
        return instance;
    }

    // ─── Action Logs ──────────────────────────────────────────────────────────

    public void action(String message, Object... args) {
        logger.info("⚡ " + message, args);
    }

    public void assertion(String message, Object... args) {
        logger.info("✅ " + message, args);
    }

    public void assertionFailed(String message, Object... args) {
        logger.error("❌ " + message, args);
    }

    public void step(String message, Object... args) {
        logger.info("▶  " + message, args);
    }

    public void screenshot(String path) {
        logger.info("📸 Screenshot saved: {}", path);
    }

    public void rtl(String message, Object... args) {
        logger.info("🔤 [RTL] " + message, args);
    }

    // ─── Standard Levels ──────────────────────────────────────────────────────

    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    public void warn(String message, Object... args) {
        logger.warn("⚠️  " + message, args);
    }

    public void error(String message, Object... args) {
        logger.error("💥 " + message, args);
    }

    public void error(String message, Throwable t) {
        logger.error("💥 " + message, t);
    }

    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }
}
