package io.github.qatra.web.data;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * One row/object from a data-driven test file.
 */
public final class QatraDataRecord {

    private final Map<String, Object> values;

    public QatraDataRecord(Map<String, ?> values) {
        this.values = new LinkedHashMap<>();
        if (values != null) {
            values.forEach((key, value) -> this.values.put(String.valueOf(key), value));
        }
    }

    public String get(String key) {
        Object value = values.get(key);
        return value == null ? null : String.valueOf(value);
    }

    public String required(String key) {
        String value = get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Required data field is missing or blank: " + key);
        }
        return value;
    }

    public int getInt(String key) {
        return Integer.parseInt(required(key).trim());
    }

    public long getLong(String key) {
        return Long.parseLong(required(key).trim());
    }

    public double getDouble(String key) {
        return Double.parseDouble(required(key).trim());
    }

    public boolean getBoolean(String key) {
        String value = required(key).trim();
        return "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "1".equals(value);
    }

    public boolean contains(String key) {
        return values.containsKey(key);
    }

    public Set<String> keys() {
        return Collections.unmodifiableSet(values.keySet());
    }

    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap(values);
    }

    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public String toString() {
        return "QatraDataRecord" + values;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof QatraDataRecord that)) {
            return false;
        }
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
