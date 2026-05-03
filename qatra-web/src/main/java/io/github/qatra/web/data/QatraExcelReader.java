package io.github.qatra.web.data;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class QatraExcelReader {

    private QatraExcelReader() {
    }

    static QatraDataSet read(Path path, String sheetName, int sheetIndex, boolean hasHeader) {
        try (InputStream inputStream = Files.newInputStream(path);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = resolveSheet(workbook, sheetName, sheetIndex);
            return readSheet(sheet, hasHeader);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read Excel data file: " + path, e);
        }
    }

    private static Sheet resolveSheet(Workbook workbook, String sheetName, int sheetIndex) {
        Sheet sheet;
        if (sheetName != null && !sheetName.isBlank()) {
            sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Excel sheet was not found: " + sheetName);
            }
        } else {
            if (sheetIndex < 0 || sheetIndex >= workbook.getNumberOfSheets()) {
                throw new IllegalArgumentException("Excel sheet index is out of range: " + sheetIndex);
            }
            sheet = workbook.getSheetAt(sheetIndex);
        }
        return sheet;
    }

    private static QatraDataSet readSheet(Sheet sheet, boolean hasHeader) {
        DataFormatter formatter = new DataFormatter();
        List<Row> rows = new ArrayList<>();
        sheet.forEach(row -> {
            if (!isBlank(row, formatter)) {
                rows.add(row);
            }
        });

        if (rows.isEmpty()) {
            return QatraDataSet.empty();
        }

        int maxColumns = maxColumns(rows);
        List<String> headers;
        int startIndex;
        if (hasHeader) {
            headers = readValues(rows.get(0), maxColumns, formatter);
            headers = normalizeHeaders(headers);
            startIndex = 1;
        } else {
            headers = generatedHeaders(maxColumns);
            startIndex = 0;
        }

        List<QatraDataRecord> records = new ArrayList<>();
        for (int rowIndex = startIndex; rowIndex < rows.size(); rowIndex++) {
            List<String> rowValues = readValues(rows.get(rowIndex), maxColumns, formatter);
            Map<String, Object> values = new LinkedHashMap<>();
            for (int column = 0; column < headers.size(); column++) {
                values.put(headers.get(column), column < rowValues.size() ? rowValues.get(column) : "");
            }
            records.add(new QatraDataRecord(values));
        }

        return new QatraDataSet(records);
    }

    private static boolean isBlank(Row row, DataFormatter formatter) {
        if (row == null) {
            return true;
        }
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            if (i >= 0) {
                Cell cell = row.getCell(i);
                if (cell != null && !formatter.formatCellValue(cell).isBlank()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int maxColumns(List<Row> rows) {
        int max = 0;
        for (Row row : rows) {
            max = Math.max(max, row.getLastCellNum());
        }
        return max;
    }

    private static List<String> readValues(Row row, int maxColumns, DataFormatter formatter) {
        List<String> values = new ArrayList<>();
        for (int i = 0; i < maxColumns; i++) {
            Cell cell = row.getCell(i);
            values.add(cell == null ? "" : formatter.formatCellValue(cell).trim());
        }
        return values;
    }

    private static List<String> normalizeHeaders(List<String> rawHeaders) {
        List<String> headers = new ArrayList<>();
        for (int i = 0; i < rawHeaders.size(); i++) {
            String header = rawHeaders.get(i);
            headers.add(header == null || header.isBlank() ? "column_" + i : header.trim());
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
