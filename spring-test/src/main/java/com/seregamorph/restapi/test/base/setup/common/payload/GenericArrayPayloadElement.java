package com.seregamorph.restapi.test.base.setup.common.payload;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.val;

@SuppressWarnings("WeakerAccess")
public class GenericArrayPayloadElement {

    @Getter
    private final List<AnonymousPayloadProperty> properties = new ArrayList<>();

    GenericArrayPayloadElement field(String fieldName, FieldType fieldType, Object value) {
        val propertyValue = PayloadUtils.payload(value);
        properties.add(new AnonymousPayloadProperty(fieldName, fieldType, propertyValue));
        return this;
    }

    public GenericArrayPayloadElement requiredField(String fieldName, Object value) {
        return field(fieldName, FieldType.REQUIRED, value);
    }

    public GenericArrayPayloadElement optionalField(String fieldName, Object value) {
        return field(fieldName, FieldType.OPTIONAL, value);
    }

    public GenericArrayPayloadElement redundantField(String fieldName, Object value) {
        return field(fieldName, FieldType.REDUNDANT, value);
    }
}
