package com.seregamorph.restapi.test.base.setup.common.payload;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PayloadProperty {

    private final Class<?> resourceClass;
    private final String fieldName;
    private final FieldType fieldType;
    private final Object value;

    @Override
    public String toString() {
        return "PayloadProperty{" + fieldType.name().toLowerCase() + " " + fieldName
                + "=" + value + "}";
    }
}
