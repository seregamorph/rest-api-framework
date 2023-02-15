package com.seregamorph.restapi.test.base.setup.common.payload;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class AnonymousPayloadProperty {

    private final String fieldName;
    private final FieldType fieldType;
    private final Object value;
}
