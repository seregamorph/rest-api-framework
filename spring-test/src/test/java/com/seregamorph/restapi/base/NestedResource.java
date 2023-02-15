package com.seregamorph.restapi.base;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class NestedResource extends IdResource<String, NestedResource> {

    private String value;

    private WrapperResource wrapper;
}
