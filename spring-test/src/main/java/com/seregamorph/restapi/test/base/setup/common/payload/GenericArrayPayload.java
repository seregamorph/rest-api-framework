package com.seregamorph.restapi.test.base.setup.common.payload;

import static com.seregamorph.restapi.test.base.setup.common.payload.FieldType.OPTIONAL;
import static com.seregamorph.restapi.test.base.setup.common.payload.FieldType.REDUNDANT;
import static com.seregamorph.restapi.test.base.setup.common.payload.FieldType.REQUIRED;
import static com.seregamorph.restapi.test.base.setup.common.payload.PayloadExtractor.extractAllAcceptableFields;
import static com.seregamorph.restapi.test.base.setup.common.payload.PayloadExtractor.extractAllRequiredFields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;

public class GenericArrayPayload extends GenericPayload {

    @Getter
    private List<List<PayloadProperty>> allProperties = new ArrayList<>();

    GenericArrayPayload(Class<?> resourceClass) {
        super(resourceClass);
    }

    GenericArrayPayload(Class<?> resourceClass, List<List<PayloadProperty>> allProperties) {
        super(resourceClass);
        this.allProperties = allProperties;
    }

    public GenericArrayPayload add(GenericArrayPayloadElement element) {
        List<PayloadProperty> properties = new ArrayList<>();

        for (AnonymousPayloadProperty property : element.getProperties()) {
            properties.add(new PayloadProperty(
                    super.getResourceClass(), property.getFieldName(), property.getFieldType(), property.getValue()));
        }

        allProperties.add(properties);
        return this;
    }

    @Override
    protected boolean hasFields(FieldType fieldType) {
        for (List<PayloadProperty> properties : allProperties) {
            for (PayloadProperty property : properties) {
                if (property.getFieldType() == fieldType) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<Map<String, Object>> getDefaultPayload() {
        return extractAllAcceptableFields(allProperties);
    }

    @Override
    public List<Map<String, Object>> getMinimalPayload() {
        return extractAllRequiredFields(allProperties);
    }

    @Override
    public Iterable<GeneratedPayload> iterateRequiredFields() {
        return iterable(() -> new GeneratedArrayPayloadIterator(this, allProperties, REQUIRED));
    }

    @Override
    public Iterable<GeneratedPayload> iterateOptionalFields() {
        return iterable(() -> new GeneratedArrayPayloadIterator(this, allProperties, OPTIONAL));
    }

    @Override
    public Iterable<GeneratedPayload> iterateRedundantFields() {
        return iterable(() -> new GeneratedArrayPayloadIterator(this, allProperties, REDUNDANT));
    }
}
