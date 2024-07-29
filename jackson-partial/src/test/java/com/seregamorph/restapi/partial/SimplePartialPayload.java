package com.seregamorph.restapi.partial;

import static com.seregamorph.restapi.partial.PartialPayloadFactory.partial;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * A sample partial payload to be used in some tests.
 */
@Data
@FieldNameConstants
class SimplePartialPayload extends PartialPayload {

    @Required
    private String name;

    @Required
    private String title;

    private String description;

    private int version;

    public static SimplePartialPayload create() {
        return partial(SimplePartialPayload.class);
    }
}
