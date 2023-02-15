package com.seregamorph.restapi.partial;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

class PartialPayloadDeserializer extends StdDeserializer<PartialPayload> implements ResolvableDeserializer {

    private static final long serialVersionUID = 1L;

    private static final String UNABLE_TO_READ_FIELD = "Unable to read field %s from object %s";

    private final JsonDeserializer<?> defaultDeserializer;

    PartialPayloadDeserializer(JsonDeserializer<?> defaultDeserializer, Class<?> valueType) {
        super(valueType);
        this.defaultDeserializer = defaultDeserializer;
    }

    @Override
    public PartialPayload deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.readValueAsTree();
        try (JsonParser newParser = new TreeTraversingParser(node, parser.getCodec())) {
            // required by Jackson to iterate to the first token
            newParser.nextToken();

            List<String> fieldNames = new ArrayList<>();
            node.fieldNames().forEachRemaining(fieldNames::add);

            val payload = (PartialPayload) defaultDeserializer.deserialize(newParser, context);
            Map<String, Object> properties = new HashMap<>();

            for (String field : fieldNames) {
                try {
                    properties.put(field, FieldUtils.readField(payload, field, true));
                } catch (IllegalAccessException e) {
                    throw new IOException(String.format(UNABLE_TO_READ_FIELD, field, payload.getClass().getName()));
                } catch (IllegalArgumentException e) {
                    // This happens when the field does NOT exist in the payload class declaration
                    properties.put(field, node.findValue(field).asText());
                }
            }

            // Handle the case when property name doesn't match field name.
            List<Field> fieldsWithAlias = FieldUtils
                    .getFieldsListWithAnnotation(payload.getClass(), JsonProperty.class);

            fieldsWithAlias.forEach(field -> {
                String jsonPropertyName = field.getAnnotation(JsonProperty.class).value();

                if (StringUtils.isNotEmpty(jsonPropertyName) && properties.containsKey(jsonPropertyName)) {
                    Object value = properties.get(jsonPropertyName);
                    properties.remove(jsonPropertyName);
                    properties.put(field.getName(), value);
                }
            });

            payload.setPartialProperties(properties);
            return payload;
        }
    }

    @Override
    public void resolve(DeserializationContext context) throws JsonMappingException {
        ((ResolvableDeserializer) defaultDeserializer).resolve(context);
    }
}
