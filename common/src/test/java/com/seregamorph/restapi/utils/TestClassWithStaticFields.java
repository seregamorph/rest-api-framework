package com.seregamorph.restapi.utils;

import lombok.experimental.FieldNameConstants;
import lombok.experimental.UtilityClass;

@FieldNameConstants
@UtilityClass
@SuppressWarnings("unused")
class TestClassWithStaticFields {

    public static final String STATIC_FIELD = "staticField";

    private static String staticField = "staticFieldValue";
}
