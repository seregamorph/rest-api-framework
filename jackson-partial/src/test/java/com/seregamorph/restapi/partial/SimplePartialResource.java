package com.seregamorph.restapi.partial;

import static com.seregamorph.restapi.partial.PartialPayloadFactory.partial;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * A sample partial resource to be used in some tests.
 */
@Data
@FieldNameConstants
class SimplePartialResource extends TestIdResource<Long, SimplePartialResource> implements SimplePartial {

    private String name;

    private String title;

    private String description;

    private int version;

    private SimplePartialResource linked;

    public static SimplePartialResource create() {
        return partial(SimplePartialResource.class);
    }
}
