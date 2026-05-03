package io.github.qatra.web.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A loaded data file represented as ordered records.
 */
public final class QatraDataSet implements Iterable<QatraDataRecord> {

    private final List<QatraDataRecord> records;

    public QatraDataSet(List<QatraDataRecord> records) {
        this.records = records == null ? List.of() : new ArrayList<>(records);
    }

    public static QatraDataSet empty() {
        return new QatraDataSet(List.of());
    }

    public int size() {
        return records.size();
    }

    public boolean isEmpty() {
        return records.isEmpty();
    }

    public QatraDataRecord first() {
        if (records.isEmpty()) {
            throw new IllegalStateException("QATRA data set is empty.");
        }
        return records.get(0);
    }

    public QatraDataRecord get(int index) {
        return records.get(index);
    }

    public List<QatraDataRecord> records() {
        return Collections.unmodifiableList(records);
    }

    public Object[][] toTestNgData() {
        Object[][] matrix = new Object[records.size()][1];
        for (int i = 0; i < records.size(); i++) {
            matrix[i][0] = records.get(i);
        }
        return matrix;
    }

    @Override
    public Iterator<QatraDataRecord> iterator() {
        return records().iterator();
    }

    @Override
    public String toString() {
        return "QatraDataSet{records=" + records.size() + '}';
    }
}
