package com.seregamorph.restapi.test.utils;

import static com.seregamorph.restapi.test.utils.StandardValues.string;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.SneakyThrows;

@SuppressWarnings("unused")
public interface JsonExtensions {

    ObjectMapper objectMapper();

    default FluentObjectNode object() {
        return new FluentObjectNode(objectMapper());
    }

    default ArrayNode array() {
        return objectMapper().createArrayNode();
    }

    default ArrayNode array(Object... values) {
        ArrayNode arrayNode = objectMapper().createArrayNode();
        for (Object value : values) {
            arrayNode.add(string(value));
        }
        return arrayNode;
    }

    default ArrayNode array(JsonNode... nodes) {
        ArrayNode arrayNode = objectMapper().createArrayNode();
        for (JsonNode node : nodes) {
            arrayNode.add(node);
        }
        return arrayNode;
    }

    @SneakyThrows
    default String writeJson(Object value) {
        return objectMapper().writeValueAsString(value);
    }

    @SneakyThrows
    default JsonNode readJson(String content) {
        return objectMapper().readTree(content);
    }

    @SneakyThrows
    default <T> T readJson(String content, Class<T> type) {
        return objectMapper().readValue(content, type);
    }

    @SneakyThrows
    default <T> T readJson(JsonNode root, Class<T> type) {
        return objectMapper().treeToValue(root, type);
    }

    @SneakyThrows
    default <T> T readJson(JsonNode root, String jsonPointer, Class<T> type) {
        JsonNode node = root.at(jsonPointer);
        if (node.isMissingNode()) {
            throw new IllegalStateException("Missing node `" + jsonPointer + "`");
        }
        return objectMapper().treeToValue(node, type);
    }
}
