package com.seregamorph.restapi.partial;

import static com.seregamorph.restapi.partial.PartialPayloadFactory.partial;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.seregamorph.restapi.base.IdResource;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

/**
 * A sample partial resource to be used in some tests.
 */
@Getter
@Setter
@FieldNameConstants
class SamplePartialResource extends IdResource<Long, SamplePartialResource> implements SamplePartial {

    public static final String JSON_PROPERTY_VALUE = "Foo";
    public static final String JSON_PROPERTY_VALUE_2 = "Bar";

    private String normalField1;
    private int normalField2;
    private SimplePartialResource partialResourceField1;
    private SimplePartialResource partialResourceField2;
    private List<SimplePartialResource> partialResourceCollection1;
    private Set<SimplePartialResource> partialResourceCollection2;
    private SimplePartialResource[] partialResourceArray1;
    private SimplePartialResource[] partialResourceArray2;
    private Object untouchedField;
    @JsonProperty(JSON_PROPERTY_VALUE)
    private Object field1WithJsonProperty;
    @JsonProperty(JSON_PROPERTY_VALUE_2)
    private Object field2WithJsonProperty;
    @JsonProperty
    private Object fieldWithJsonPropertyWithoutCustomValue;

    public static SamplePartialResource create() {
        return partial(SamplePartialResource.class);
    }
}
