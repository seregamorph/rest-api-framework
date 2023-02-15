package com.seregamorph.restapi.test.base.setup.common.payload;

import java.util.ArrayList;
import java.util.List;

class PayloadPropertyAnalyzer {

    private final List<PayloadProperty> analyzedProperties = new ArrayList<>();

    private void analyzeInternally(List<PayloadProperty> properties, FieldType fieldType) {
        for (PayloadProperty property : properties) {
            if (property.getFieldType() == fieldType) {
                this.analyzedProperties.add(property);
            }

            if (property.getFieldType() == FieldType.REDUNDANT) {
                // Do not check descendants of a redundant property
                continue;
            }

            if (property.getValue() instanceof GenericSinglePayload) {
                GenericSinglePayload nestedPayload = (GenericSinglePayload) property.getValue();
                analyzeInternally(nestedPayload.getProperties(), fieldType);
            } else if (property.getValue() instanceof GenericArrayPayload) {
                GenericArrayPayload nestedPayloads = (GenericArrayPayload) property.getValue();
                for (List<PayloadProperty> nestedPayload : nestedPayloads.getAllProperties()) {
                    analyzeInternally(nestedPayload, fieldType);
                }
            }
        }
    }

    private void analyzeAllInternally(List<List<PayloadProperty>> allProperties, FieldType fieldType) {
        for (List<PayloadProperty> properties : allProperties) {
            analyzeInternally(properties, fieldType);
        }
    }

    static List<PayloadProperty> analyze(List<PayloadProperty> properties, FieldType fieldType) {
        PayloadPropertyAnalyzer analyzer = new PayloadPropertyAnalyzer();
        analyzer.analyzeInternally(properties, fieldType);
        return analyzer.analyzedProperties;
    }

    static List<PayloadProperty> analyzeAll(List<List<PayloadProperty>> allProperties, FieldType fieldType) {
        PayloadPropertyAnalyzer analyzer = new PayloadPropertyAnalyzer();
        analyzer.analyzeAllInternally(allProperties, fieldType);
        return analyzer.analyzedProperties;
    }
}
