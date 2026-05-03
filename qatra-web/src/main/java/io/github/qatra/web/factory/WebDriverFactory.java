package io.github.qatra.web.factory;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.config.QatraProperties;
import io.github.qatra.core.enums.BrowserType;
import io.github.qatra.core.logger.QatraLogger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import java.time.Duration;
import java.util.logging.Level;

/**
 * Creates and configures Selenium WebDriver instances based on QATRA configuration.
 *
 * Driver binaries are handled by Selenium Manager, which is built into Selenium 4.
 */
public final class WebDriverFactory {

    private static final QatraLogger LOG = QatraLogger.getInstance();
    private static final QatraConfig CONFIG = QatraConfig.getInstance();

    private WebDriverFactory() {
    }

    /** Creates a WebDriver instance for the configured browser. */
    public static WebDriver create() {
        String browserName = CONFIG.getProperty(QatraProperties.BROWSER, "chrome");
        BrowserType browserType = BrowserType.fromString(browserName);
        LOG.action("Launching browser: {} | Headless: {}",
                browserType, CONFIG.getProperty(QatraProperties.HEADLESS));
        return createDriver(browserType);
    }

    /** Creates a WebDriver instance for a specific browser type. */
    public static WebDriver create(BrowserType browserType) {
        LOG.action("Launching browser: {} | Headless: {}",
                browserType, CONFIG.getProperty(QatraProperties.HEADLESS));
        return createDriver(browserType);
    }

    private static WebDriver createDriver(BrowserType browserType) {
        boolean headless = CONFIG.getBooleanProperty(QatraProperties.HEADLESS, false);
        int pageLoadTimeout = CONFIG.getIntProperty(QatraProperties.PAGE_LOAD_TIMEOUT, 30);

        WebDriver driver = switch (browserType) {
            case CHROME -> createChrome(headless);
            case FIREFOX -> createFirefox(headless);
            case EDGE -> createEdge(headless);
            case SAFARI -> createSafari();
        };

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

        boolean maximize = CONFIG.getBooleanProperty(QatraProperties.MAXIMIZE_BROWSER, true);
        if (maximize && !headless) {
            driver.manage().window().maximize();
        }

        LOG.action("Browser ready ✓");
        return driver;
    }

    private static WebDriver createChrome(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        if (headless) {
            options.addArguments("--headless=new");
        }

        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--disable-extensions",
                "--remote-allow-origins=*"
        );

        enableBrowserConsoleLogs(options);

        return new ChromeDriver(options);
    }

    private static WebDriver createFirefox(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();

        if (headless) {
            options.addArguments("-headless");
        }

        return new FirefoxDriver(options);
    }

    private static WebDriver createEdge(boolean headless) {
        EdgeOptions options = new EdgeOptions();

        if (headless) {
            options.addArguments("--headless=new");
        }

        enableBrowserConsoleLogs(options);

        return new EdgeDriver(options);
    }

    private static void enableBrowserConsoleLogs(ChromeOptions options) {
        LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.BROWSER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
    }

    private static void enableBrowserConsoleLogs(EdgeOptions options) {
        LoggingPreferences logs = new LoggingPreferences();
        logs.enable(LogType.BROWSER, Level.ALL);
        options.setCapability("ms:loggingPrefs", logs);
    }

    private static WebDriver createSafari() {
        return new SafariDriver();
    }
}
