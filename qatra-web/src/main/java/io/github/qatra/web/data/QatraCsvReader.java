package io.github.qatra.web.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class QatraCsvReader {

    private QatraCsvReader() {
    }

    static QatraDataSet read(Path path, char delimiter, boolean hasHeader) {
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            return parse(lines, delimiter, hasHeader);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read CSV data file: " + path, e);
        }
    }

    static QatraDataSet parse(List<String> lines, char delimiter, boolean hasHeader) {
        if (lines == null || lines.isEmpty()) {
            return QatraDataSet.empty();
        }

        List<List<String>> rows = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }
            rows.add(parseLine(line, delimiter));
        }

        if (rows.isEmpty()) {
            return QatraDataSet.empty();
        }

        List<String> headers;
        int startRow;
        if (hasHeader) {
            headers = normalizeHeaders(rows.get(0));
            startRow = 1;
        } else {
            headers = generatedHeaders(rows.get(0).size());
            startRow = 0;
        }

        List<QatraDataRecord> records = new ArrayList<>();
        for (int rowIndex = startRow; rowIndex < rows.size(); rowIndex++) {
            List<String> row = rows.get(rowIndex);
            Map<String, Object> values = new LinkedHashMap<>();
            for (int column = 0; column < headers.size(); column++) {
                String value = column < row.size() ? row.get(column) : "";
                values.put(headers.get(column), value);
            }
            records.add(new QatraDataRecord(values));
        }
        return new QatraDataSet(records);
    }

    private static List<String> parseLine(String line, char delimiter) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                boolean escapedQuote = insideQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"';
                if (escapedQuote) {
                    current.append('"');
                    i++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (ch == delimiter && !insideQuotes) {
                values.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        values.add(current.toString().trim());
        return values;
    }

    private static List<String> normalizeHeaders(List<String> rawHeaders) {
        List<String> headers = new ArrayList<>();
        for (int i = 0; i < rawHeaders.size(); i++) {
            String header = rawHeaders.get(i);
            if (header == null || header.isBlank()) {
                headers.add("column_" + i);
            } else {
                headers.add(header.trim());
            }
        }
        return headers;
    }

    private static List<String> generatedHeaders(int size) {
        List<String> headers = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            headers.add("column_" + i);
        }
        return headers;
    }
}
