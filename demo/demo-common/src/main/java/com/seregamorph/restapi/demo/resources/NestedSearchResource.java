package com.seregamorph.restapi.demo.resources;

import com.seregamorph.restapi.partial.PartialResource;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
public class NestedSearchResource extends PartialResource {

    private String nestedStringField;
}
