package com.seregamorph.restapi.test.utils;

import static com.seregamorph.restapi.test.utils.StandardValues.jsonObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.UncheckedIOException;

/**
 * Utility class with methods to fluently construct request payloads with only necessary fields.
 * This is a good alternative to {@link ObjectMapper} as only the fields which are explicitly specified
 * can be serialized to a string.
 */
@SuppressWarnings("unused")
public class FluentObjectNode extends ObjectNode {

    private final ObjectMapper objectMapper;

    public FluentObjectNode(ObjectMapper objectMapper) {
        super(objectMapper.getDeserializationConfig().getNodeFactory());
        this.objectMapper = objectMapper;
    }

    public FluentObjectNode set(String fieldName, Object value) {
        Object object = jsonObject(value);
        // we need exact JsonNode type here (for equality)
        if (object instanceof Boolean) {
            return (FluentObjectNode) put(fieldName, (Boolean) value);
        } else if (object instanceof Integer) {
            return (FluentObjectNode) put(fieldName, (Integer) value);
        } else if (object instanceof Long) {
            return (FluentObjectNode) put(fieldName, (Long) value);
        } else if (object instanceof String) {
            return (FluentObjectNode) put(fieldName, (String) object);
        } else {
            return (FluentObjectNode) putPOJO(fieldName, object);
        }
    }

    @Override
    public FluentObjectNode set(String fieldName, JsonNode value) {
        return (FluentObjectNode) super.set(fieldName, value);
    }

    public FluentObjectNode setNull(String fieldName) {
        return (FluentObjectNode) putNull(fieldName);
    }

    @Override
    public String toString() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
