package com.seregamorph.restapi.test.base.setup.common.payload;

import static com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloadEnhancer.enhance;
import static com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloads.array;
import static com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloads.object;

public class GenericSinglePayloadEnhancerTest extends GenericPayloadTest {

    private static final GenericSinglePayload GENERIC_PAYLOAD = object(TestPayload.class)
            .requiredField("field 1.1", "value 1.1")
            .optionalField("field 1.2", object(TestPayload.class)
                    .requiredField("field 1.2.1", array(TestPayload.class)
                            .add(object()
                                    .requiredField("field 1.2.1[0].1", "value 1.2.1[0].1")
                                    .optionalField("field 1.2.1[0].2", "value 1.2.1[0].2")
                                    .redundantField("field 1.2.1[0].3", "value 1.2.1[0].3"))
                            .add(object()
                                    .requiredField("field 1.2.1[1].1", "value 1.2.1[1].1")
                                    .optionalField("field 1.2.1[1].2", "value 1.2.1[1].2")))
                    .optionalField("field 1.2.2", "value 1.2.2")
                    .redundantField("field 1.2.3", "value 1.2.3"));

    public GenericSinglePayloadEnhancerTest() {
        // We have 4 objects with 2 containing 2 redundant fields. The other 2 will be enhanced with 1 field
        // not existing in the class, and 2 class fields not existing in the payload.
        // In total, we have 2 + 2 * (1 + 2) = 8
        super(enhance(GENERIC_PAYLOAD), 4, 4, 8);
    }
}
