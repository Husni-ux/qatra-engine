package io.github.qatra.web.data;

import io.github.qatra.core.config.QatraConfig;
import io.github.qatra.core.logger.QatraLogger;
import io.github.qatra.web.reports.AllureReport;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main QATRA data-driven testing entry point.
 */
public final class QatraData {

    private static final QatraLogger LOG = QatraLogger.getInstance();

    private QatraData() {
    }

    public static QatraDataSet from(String path) {
        return from(path, DataFormat.AUTO, ',', true, "", 0);
    }

    public static QatraDataSet fromCsv(String path) {
        return from(path, DataFormat.CSV, ',', true, "", 0);
    }

    public static QatraDataSet fromCsv(String path, char delimiter, boolean hasHeader) {
        return from(path, DataFormat.CSV, delimiter, hasHeader, "", 0);
    }

    public static QatraDataSet fromJson(String path) {
        return from(path, DataFormat.JSON, ',', true, "", 0);
    }

    public static QatraDataSet fromExcel(String path) {
        return from(path, DataFormat.EXCEL, ',', true, "", 0);
    }

    public static QatraDataSet fromExcel(String path, String sheetName) {
        return from(path, DataFormat.EXCEL, ',', true, sheetName, 0);
    }

    public static QatraDataSet fromExcel(String path, int sheetIndex) {
        return from(path, DataFormat.EXCEL, ',', true, "", sheetIndex);
    }

    public static QatraDataSet from(QatraDataFile dataFile) {
        if (dataFile == null) {
            throw new IllegalArgumentException("QatraDataFile annotation is required.");
        }
        return from(
                dataFile.path(),
                dataFile.format(),
                dataFile.delimiter(),
                dataFile.hasHeader(),
                dataFile.sheet(),
                dataFile.sheetIndex()
        );
    }

    public static Object[][] testNgRecords(String path) {
        return from(path).toTestNgData();
    }

    public static Object[][] testNgRecords(QatraDataFile dataFile) {
        return from(dataFile).toTestNgData();
    }

    static QatraDataSet from(String path,
                             DataFormat requestedFormat,
                             char delimiter,
                             boolean hasHeader,
                             String sheetName,
                             int sheetIndex) {
        Path resolvedPath = resolvePath(path);
        DataFormat format = requestedFormat == DataFormat.AUTO ? DataFormat.detect(resolvedPath.toString()) : requestedFormat;

        if (format == DataFormat.AUTO) {
            throw new IllegalArgumentException("Could not detect QATRA data file format from path: " + path);
        }

        LOG.action("Loading QATRA data file: {} ({})", resolvedPath, format);
        QatraDataSet dataSet = switch (format) {
            case CSV -> QatraCsvReader.read(resolvedPath, delimiter, hasHeader);
            case JSON -> QatraJsonReader.read(resolvedPath);
            case EXCEL -> QatraExcelReader.read(resolvedPath, sheetName, sheetIndex, hasHeader);
            case AUTO -> throw new IllegalArgumentException("AUTO format must be resolved before loading.");
        };

        attachDataSummary(path, format, dataSet);
        LOG.info("Loaded QATRA data set: {} record(s) from {}", dataSet.size(), path);
        return dataSet;
    }

    public static Path resolvePath(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("QATRA data file path must not be blank.");
        }

        Path directPath = Paths.get(location);
        if (Files.exists(directPath)) {
            return directPath.toAbsolutePath().normalize();
        }

        Path configuredDataDirPath = resolveFromConfiguredDataDir(location);
        if (configuredDataDirPath != null) {
            return configuredDataDirPath;
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(location);
        if (resource != null) {
            try {
                return Paths.get(resource.toURI()).toAbsolutePath().normalize();
            } catch (URISyntaxException e) {
                throw new IllegalStateException("Could not resolve QATRA data resource: " + location, e);
            }
        }

        throw new IllegalArgumentException("QATRA data file was not found as filesystem path or classpath resource: " + location);
    }

    private static Path resolveFromConfiguredDataDir(String location) {
        String dataDir = QatraConfig.getInstance().getProperty("qatra.data.dir", "src/test/resources/test-data");
        Path candidate = Paths.get(dataDir, location);
        if (Files.exists(candidate)) {
            return candidate.toAbsolutePath().normalize();
        }

        Path candidateByFileName = Paths.get(dataDir, Paths.get(location).getFileName().toString());
        if (Files.exists(candidateByFileName)) {
            return candidateByFileName.toAbsolutePath().normalize();
        }

        return null;
    }

    private static void attachDataSummary(String path, DataFormat format, QatraDataSet dataSet) {
        boolean attach = QatraConfig.getInstance().getBooleanProperty("qatra.data.attach", true);
        if (!attach) {
            return;
        }

        StringBuilder summary = new StringBuilder();
        summary.append("QATRA Data File\n");
        summary.append("Path: ").append(path).append('\n');
        summary.append("Format: ").append(format).append('\n');
        summary.append("Records: ").append(dataSet.size()).append('\n');
        if (!dataSet.isEmpty()) {
            summary.append("Columns: ").append(dataSet.first().keys()).append('\n');
            summary.append("First record: ").append(dataSet.first()).append('\n');
        }
        AllureReport.attachText("QATRA Data - " + path, summary.toString());
    }
}
