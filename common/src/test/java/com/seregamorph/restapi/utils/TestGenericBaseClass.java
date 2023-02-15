package com.seregamorph.restapi.utils;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
abstract class TestGenericBaseClass<T extends Serializable> {

    static final String GET_ID = "getId";
    static final String SET_ID = "setId";

    private T id;
}
