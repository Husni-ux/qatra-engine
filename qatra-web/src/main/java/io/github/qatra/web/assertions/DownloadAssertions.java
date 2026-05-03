package io.github.qatra.web.assertions;

import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;
import org.testng.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * File-system based assertions for browser download folders.
 */
public class DownloadAssertions {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private final Path downloadDirectory;
    private final WebAssertions parent;

    public DownloadAssertions(Path downloadDirectory, WebAssertions parent) {
        this.downloadDirectory = downloadDirectory;
        this.parent = parent;
    }

    public DownloadAssertions directoryExists() {
        LOG.assertion("Assert download directory exists: {}", downloadDirectory);
        AllureReport.step("Assert download directory exists: " + downloadDirectory);
        Assert.assertTrue(Files.isDirectory(downloadDirectory), "Download directory does not exist: " + downloadDirectory);
        return this;
    }

    public DownloadAssertions fileExists(String fileName) {
        LOG.assertion("Assert downloaded file exists: {}", fileName);
        AllureReport.step("Assert downloaded file exists: " + fileName);
        Assert.assertTrue(Files.isRegularFile(downloadDirectory.resolve(fileName)), "Downloaded file was not found: " + downloadDirectory.resolve(fileName));
        return this;
    }

    public DownloadAssertions fileContains(String fileName, String expectedText) {
        LOG.assertion("Assert downloaded file '{}' contains text", fileName);
        AllureReport.step("Assert downloaded file contains text: " + fileName);
        Path file = downloadDirectory.resolve(fileName);
        Assert.assertTrue(Files.isRegularFile(file), "Downloaded file was not found: " + file);
        try {
            String content = Files.readString(file);
            Assert.assertTrue(content.contains(expectedText), "Expected file to contain: " + expectedText + " but content was: " + content);
        } catch (IOException ex) {
            throw new AssertionError("Failed to read downloaded file: " + file, ex);
        }
        return this;
    }

    public DownloadAssertions fileWithExtensionExists(String extension) {
        LOG.assertion("Assert downloaded file with extension exists: {}", extension);
        AllureReport.step("Assert downloaded file with extension exists: " + extension);
        String normalized = extension.startsWith(".") ? extension : "." + extension;
        Assert.assertTrue(files().stream().anyMatch(path -> path.getFileName().toString().endsWith(normalized)),
                "No downloaded file found with extension: " + normalized + " inside " + downloadDirectory);
        return this;
    }

    public DownloadAssertions fileCountAtLeast(int minimumCount) {
        LOG.assertion("Assert download file count at least {}", minimumCount);
        AllureReport.step("Assert download file count at least: " + minimumCount);
        int actual = files().size();
        Assert.assertTrue(actual >= minimumCount,
                "Expected at least " + minimumCount + " downloaded files but found " + actual + " inside " + downloadDirectory);
        return this;
    }

    public Path latestFile() {
        return files().stream()
                .max(Comparator.comparingLong(this::lastModifiedMillis))
                .orElseThrow(() -> new AssertionError("No files found inside download directory: " + downloadDirectory));
    }

    public WebAssertions and() {
        return parent;
    }

    private List<Path> files() {
        try {
            if (!Files.isDirectory(downloadDirectory)) {
                return List.of();
            }
            try (Stream<Path> stream = Files.list(downloadDirectory)) {
                return stream.filter(Files::isRegularFile).toList();
            }
        } catch (IOException ex) {
            throw new AssertionError("Failed to inspect download directory: " + downloadDirectory, ex);
        }
    }

    private long lastModifiedMillis(Path file) {
        try {
            return Files.getLastModifiedTime(file).toMillis();
        } catch (IOException ex) {
            return 0L;
        }
    }
}
