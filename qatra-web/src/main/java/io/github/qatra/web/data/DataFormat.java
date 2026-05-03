package io.github.qatra.web.data;

/**
 * Supported QATRA data-driven testing file formats.
 */
public enum DataFormat {
    AUTO,
    CSV,
    JSON,
    EXCEL;

    public static DataFormat detect(String path) {
        if (path == null || path.isBlank()) {
            return AUTO;
        }

        String normalized = path.toLowerCase();
        if (normalized.endsWith(".csv")) {
            return CSV;
        }
        if (normalized.endsWith(".json")) {
            return JSON;
        }
        if (normalized.endsWith(".xlsx") || normalized.endsWith(".xls")) {
            return EXCEL;
        }
        return AUTO;
    }
}
