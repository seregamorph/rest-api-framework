package com.seregamorph.restapi.utils;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
abstract class TestAbstractClass implements TestChildInterface {

    String summary;
}
