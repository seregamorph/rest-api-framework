package com.seregamorph.restapi.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
class TestChildClass extends TestParentClass {

    private String description;
}
