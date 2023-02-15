package com.seregamorph.restapi.test.base.setup.common.payload;

import static com.seregamorph.restapi.test.base.setup.common.payload.PayloadPropertyUtils.containsDescendant;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
class PayloadExtractor {

    static List<Map<String, Object>> extractAllRequiredFields(List<List<PayloadProperty>> allProperties) {
        return extractAll(allProperties, FieldType.REQUIRED);
    }

    static List<Map<String, Object>> extractAllAcceptableFields(List<List<PayloadProperty>> allProperties) {
        return extractAll(allProperties, FieldType.REQUIRED, FieldType.OPTIONAL);
    }

    private static List<Map<String, Object>> extractAll(List<List<PayloadProperty>> allProperties, FieldType... fieldTypes) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (List<PayloadProperty> properties : allProperties) {
            result.add(extract(properties, fieldTypes));
        }
        return result;
    }

    static List<Map<String, Object>> extractAll(List<List<PayloadProperty>> allProperties, FieldType fieldType, PayloadProperty targetProperty) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (List<PayloadProperty> properties : allProperties) {
            result.add(extract(properties, fieldType, targetProperty));
        }
        return result;
    }

    static Map<String, Object> extractRequiredFields(List<PayloadProperty> properties) {
        return extract(properties, FieldType.REQUIRED);
    }

    static Map<String, Object> extractAcceptableFields(List<PayloadProperty> properties) {
        return extract(properties, FieldType.REQUIRED, FieldType.OPTIONAL);
    }

    private static Map<String, Object> extract(List<PayloadProperty> properties, FieldType... fieldTypes) {
        Map<String, Object> result = new LinkedHashMap<>();

        for (PayloadProperty property : properties) {
            for (FieldType fieldType : fieldTypes) {
                if (property.getFieldType() == fieldType) {
                    result.put(property.getFieldName(), extractValue(property.getValue(), fieldTypes));
                }
            }
        }

        return result;
    }

    static Map<String, Object> extract(List<PayloadProperty> properties, FieldType fieldType, PayloadProperty targetProperty) {
        // If fieldType = Required, target property is the property we want to exclude (we include all required properties except that one)
        // If fieldType != Required, target property is the property we want to include (we include all required properties and that one)
        Map<String, Object> result = new LinkedHashMap<>();

        for (PayloadProperty property : properties) {
            if ((property.getFieldType() == FieldType.REQUIRED && property != targetProperty)
                    || (property.getFieldType() != FieldType.REQUIRED && property == targetProperty)
                    || containsDescendant(property, targetProperty)) {
                result.put(property.getFieldName(), extractValue(property.getValue(), fieldType, targetProperty));
            }
        }

        return result;
    }

    private Object extractValue(Object value, FieldType... fieldTypes) {
        if (value instanceof GenericSinglePayload) {
            GenericSinglePayload nestedPayload = (GenericSinglePayload) value;
            return extract(nestedPayload.getProperties(), fieldTypes);
        }

        if (value instanceof GenericArrayPayload) {
            GenericArrayPayload nestedPayloads = (GenericArrayPayload) value;

            List<Map<String, Object>> nestedValues = new ArrayList<>();

            for (List<PayloadProperty> nestedPayload : nestedPayloads.getAllProperties()) {
                nestedValues.add(extract(nestedPayload, fieldTypes));
            }

            return nestedValues;
        }

        return value;
    }

    private Object extractValue(Object value, FieldType fieldType, PayloadProperty targetProperty) {
        if (value instanceof GenericSinglePayload) {
            GenericSinglePayload nestedPayload = (GenericSinglePayload) value;
            return extract(nestedPayload.getProperties(), fieldType, targetProperty);
        }

        if (value instanceof GenericArrayPayload) {
            GenericArrayPayload nestedPayloads = (GenericArrayPayload) value;

            List<Map<String, Object>> nestedValues = new ArrayList<>();

            for (List<PayloadProperty> nestedPayload : nestedPayloads.getAllProperties()) {
                nestedValues.add(extract(nestedPayload, fieldType, targetProperty));
            }

            return nestedValues;
        }

        return value;
    }
}
