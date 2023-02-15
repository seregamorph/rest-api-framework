package com.seregamorph.restapi.test.base.support;

import com.seregamorph.restapi.test.base.setup.BaseSetup;

public interface FieldMappingSupportDelegate<S extends BaseSetup<S, ?>> {

    FieldMappingSupport<S> getFieldMappingSupport();

    default S mapField(String field, String... jsonPathElements) {
        return getFieldMappingSupport().mapField(field, jsonPathElements);
    }

    default String getJsonPath(String field) {
        return getFieldMappingSupport().getJsonPath(field);
    }

}
