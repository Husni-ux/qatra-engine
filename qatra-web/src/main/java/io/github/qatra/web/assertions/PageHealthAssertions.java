package io.github.qatra.web.assertions;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page-level health assertions such as broken links and broken images.
 */
public class PageHealthAssertions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final WebDriver driver;
    private final WebAssertions parent;
    private final HttpClient httpClient;

    public PageHealthAssertions(WebDriver driver, WebAssertions parent) {
        this.driver = driver;
        this.parent = parent;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * Assert that all HTTP/HTTPS links on the current page return a non-4xx/5xx status.
     * Non-network links such as mailto:, tel:, javascript:, anchors, and data: are ignored.
     */
    public PageHealthAssertions hasNoBrokenLinks() {
        LOG.assertion("Assert page has no broken links");
        AllureReport.step("Assert page has no broken links");
        List<String> brokenLinks = new ArrayList<>();
        for (WebElement link : driver.findElements(By.cssSelector("a[href]"))) {
            String href = link.getAttribute("href");
            if (!isCheckableUrl(href)) {
                continue;
            }
            int status = statusCode(href);
            if (status >= 400 || status == -1) {
                brokenLinks.add(href + " -> " + (status == -1 ? "request failed" : status));
            }
        }
        Assert.assertTrue(brokenLinks.isEmpty(), "Broken links found: " + brokenLinks);
        return this;
    }

    /**
     * Assert all images have loaded in the browser using naturalWidth/naturalHeight.
     */
    public PageHealthAssertions hasNoBrokenImages() {
        LOG.assertion("Assert page has no broken images");
        AllureReport.step("Assert page has no broken images");
        List<String> brokenImages = new ArrayList<>();
        List<WebElement> images = driver.findElements(By.cssSelector("img"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (WebElement image : images) {
            Object loaded = js.executeScript(
                    "return arguments[0].complete === true && arguments[0].naturalWidth > 0 && arguments[0].naturalHeight > 0;",
                    image
            );
            if (!Boolean.TRUE.equals(loaded)) {
                brokenImages.add(String.valueOf(image.getAttribute("src")));
            }
        }
        Assert.assertTrue(brokenImages.isEmpty(), "Broken images found: " + brokenImages);
        return this;
    }

    /**
     * Assert each link href is not blank after Selenium resolves it.
     */
    public PageHealthAssertions allLinksHaveHref() {
        LOG.assertion("Assert all links have href values");
        AllureReport.step("Assert all links have href values");
        List<String> emptyLinks = new ArrayList<>();
        for (WebElement link : driver.findElements(By.cssSelector("a"))) {
            String href = link.getAttribute("href");
            if (href == null || href.isBlank()) {
                emptyLinks.add(link.getText());
            }
        }
        Assert.assertTrue(emptyLinks.isEmpty(), "Links without href found: " + emptyLinks);
        return this;
    }

    public WebAssertions and() {
        return parent;
    }

    private int statusCode(String url) {
        try {
            HttpRequest getRequest = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(8))
                    .GET()
                    .build();
            return httpClient.send(getRequest, HttpResponse.BodyHandlers.discarding()).statusCode();
        } catch (Exception ex) {
            return -1;
        }
    }

    private boolean isCheckableUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        String lower = url.toLowerCase();
        return lower.startsWith("http://") || lower.startsWith("https://");
    }
}
