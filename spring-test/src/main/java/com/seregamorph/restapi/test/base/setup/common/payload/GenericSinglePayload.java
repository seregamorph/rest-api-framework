package com.seregamorph.restapi.test.base.setup.common.payload;

import static com.seregamorph.restapi.test.base.setup.common.payload.FieldType.OPTIONAL;
import static com.seregamorph.restapi.test.base.setup.common.payload.FieldType.REDUNDANT;
import static com.seregamorph.restapi.test.base.setup.common.payload.FieldType.REQUIRED;
import static com.seregamorph.restapi.test.base.setup.common.payload.PayloadExtractor.extractAcceptableFields;
import static com.seregamorph.restapi.test.base.setup.common.payload.PayloadExtractor.extractRequiredFields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.val;

@SuppressWarnings("WeakerAccess")
public class GenericSinglePayload extends GenericPayload {

    @Getter
    private List<PayloadProperty> properties = new ArrayList<>();

    GenericSinglePayload(Class<?> resourceClass) {
        super(resourceClass);
    }

    GenericSinglePayload(Class<?> resourceClass, List<PayloadProperty> properties) {
        super(resourceClass);
        this.properties = properties;
    }

    GenericSinglePayload field(String fieldName, FieldType fieldType, Object value) {
        val propertyValue = PayloadUtils.payload(value);
        properties.add(new PayloadProperty(super.getResourceClass(), fieldName, fieldType, propertyValue));
        return this;
    }

    public GenericSinglePayload requiredField(String fieldName, Object value) {
        return field(fieldName, FieldType.REQUIRED, value);
    }

    public GenericSinglePayload optionalField(String fieldName, Object value) {
        return field(fieldName, FieldType.OPTIONAL, value);
    }

    public GenericSinglePayload redundantField(String fieldName, Object value) {
        return field(fieldName, FieldType.REDUNDANT, value);
    }

    @Override
    protected boolean hasFields(FieldType fieldType) {
        for (PayloadProperty property : properties) {
            if (property.getFieldType() == fieldType) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Map<String, Object> getDefaultPayload() {
        return extractAcceptableFields(properties);
    }

    @Override
    public Map<String, Object> getMinimalPayload() {
        return extractRequiredFields(properties);
    }

    @Override
    public Iterable<GeneratedPayload> iterateRequiredFields() {
        return iterable(() -> new GeneratedSinglePayloadIterator(this, properties, REQUIRED));
    }

    @Override
    public Iterable<GeneratedPayload> iterateOptionalFields() {
        return iterable(() -> new GeneratedSinglePayloadIterator(this, properties, OPTIONAL));
    }

    @Override
    public Iterable<GeneratedPayload> iterateRedundantFields() {
        return iterable(() -> new GeneratedSinglePayloadIterator(this, properties, REDUNDANT));
    }
}
