package io.github.qatra.web.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class QatraJsonReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private QatraJsonReader() {
    }

    static QatraDataSet read(Path path) {
        try {
            JsonNode root = MAPPER.readTree(path.toFile());
            List<QatraDataRecord> records = new ArrayList<>();

            if (root.isArray()) {
                for (JsonNode item : root) {
                    records.add(new QatraDataRecord(flatten(item)));
                }
            } else if (root.isObject()) {
                JsonNode dataNode = firstArrayChild(root);
                if (dataNode != null) {
                    for (JsonNode item : dataNode) {
                        records.add(new QatraDataRecord(flatten(item)));
                    }
                } else {
                    records.add(new QatraDataRecord(MAPPER.convertValue(root, new TypeReference<Map<String, Object>>() {})));
                }
            } else {
                throw new IllegalArgumentException("JSON data file must contain an object or an array: " + path);
            }

            return new QatraDataSet(records);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read JSON data file: " + path, e);
        }
    }

    private static JsonNode firstArrayChild(JsonNode root) {
        Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            if (field.getValue() != null && field.getValue().isArray()) {
                return field.getValue();
            }
        }
        return null;
    }

    private static Map<String, Object> flatten(JsonNode node) {
        Map<String, Object> values = new LinkedHashMap<>();
        if (!node.isObject()) {
            values.put("value", node.asText());
            return values;
        }

        node.fields().forEachRemaining(entry -> putNode(values, entry.getKey(), entry.getValue()));
        return values;
    }

    private static void putNode(Map<String, Object> values, String key, JsonNode value) {
        if (value == null || value.isNull()) {
            values.put(key, null);
        } else if (value.isNumber()) {
            values.put(key, value.numberValue());
        } else if (value.isBoolean()) {
            values.put(key, value.booleanValue());
        } else if (value.isTextual()) {
            values.put(key, value.asText());
        } else {
            values.put(key, value.toString());
        }
    }
}
