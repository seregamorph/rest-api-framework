package com.seregamorph.restapi.partial;

import static com.seregamorph.restapi.partial.PartialPayloadFactory.partial;
import static com.seregamorph.restapi.partial.SamplePartialResource.JSON_PROPERTY_VALUE;
import static com.seregamorph.restapi.partial.SamplePartialResource.JSON_PROPERTY_VALUE_2;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

/**
 * A sample partial payload to be used in some tests.
 */
@Getter
@Setter
@FieldNameConstants
class SamplePartialPayload extends PartialPayload {

    @Required
    private String normalField1;
    private int normalField2;
    @Required
    private SimplePartialPayload partialPayloadField1;
    private SimplePartialPayload partialPayloadField2;
    @Required
    private List<SimplePartialPayload> partialPayloadCollection1;
    private Set<SimplePartialPayload> partialPayloadCollection2;
    @Required
    private SimplePartialPayload[] partialPayloadArray1;
    private SimplePartialPayload[] partialPayloadArray2;
    private Object untouchedField;
    @JsonProperty(JSON_PROPERTY_VALUE)
    private Object field1WithJsonProperty;
    @JsonProperty(JSON_PROPERTY_VALUE_2)
    private Object field2WithJsonProperty;
    @JsonProperty
    private Object fieldWithJsonPropertyWithoutCustomValue;

    public static SamplePartialPayload create() {
        return partial(SamplePartialPayload.class);
    }
}
