package com.mobilize.apibuilder.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonFlattener {

    private final String jsonString;

    public JsonFlattener(String json) {
        this.jsonString = json;
    }

    public Map<String, String> flattenToMap() throws IOException {
        Map<String, String> valueMap = new HashMap<>();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(this.jsonString);

        addKeys("", root, valueMap);

        return valueMap;
    }

    private void addKeys(String currentPath, JsonNode jsonNode, Map<String, String> map) {
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();

            String pathPrefix = currentPath.isEmpty() ? "" : currentPath + ".";

            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> entry = iter.next();
                addKeys(pathPrefix + entry.getKey(), entry.getValue(), map);
            }
        } else if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (int i = 0; i < arrayNode.size(); i++) {
                addKeys(currentPath + "[" + i + "]", arrayNode.get(i), map);
            }
        } else if (jsonNode.isValueNode()) {
            ValueNode valueNode = (ValueNode) jsonNode;
            map.put(currentPath, valueNode.textValue());
        }
    }
}
