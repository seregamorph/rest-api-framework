package com.seregamorph.restapi.test.base.setup.common.payload;

import static com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloadEnhancer.enhance;
import static com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloads.array;
import static com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloads.object;

public class GenericArrayPayloadEnhancerTest extends GenericPayloadTest {

    private static final GenericArrayPayload GENERIC_PAYLOAD = array(TestPayload.class)
            .add(object()
                    .requiredField("field 1[0].1", array(TestPayload.class)
                            .add(object()
                                    .requiredField("field 1[0].1[0].1", "value 1[0].1[0].1")
                                    .optionalField("field 1[0].1[0].2", "value 1[0].1[0].2"))
                            .add(object()
                                    .requiredField("field 1[0].1[1].1", "value 1[0].1[1].1")
                                    .optionalField("field 1[0].1[1].2", "value 1[0].1[1].2")))
                    .optionalField("field 1[0].2", object(TestPayload.class)
                            .requiredField("field 1[0].2.1", "value 1[0].2.1")
                            .optionalField("field 1[0].2.2", "value 1[0].2.2")
                            .redundantField("field 1[0].2.3", "value 1[0].2.3"))
                    .redundantField("field 1[0].3", "value 1[0].3"))
            .add(object()
                    .requiredField("field 1[1].1", array(TestPayload.class)
                            .add(object()
                                    .requiredField("field 1[1].1[0].1", "value 1[1].1[0].1")
                                    .optionalField("field 1[1].1[0].2", "value 1[1].1[0].2"))
                            .add(object()
                                    .requiredField("field 1[1].1[1].1", "value 1[1].1[1].1")
                                    .optionalField("field 1[1].1[1].2", "value 1[1].1[1].2")))
                    .optionalField("field 1[1].2", object(TestPayload.class)
                            .requiredField("field 1[1].2.1", "value 1[1].2.1")
                            .optionalField("field 1[1].2.2", "value 1[1].2.2")
                            .redundantField("field 1[1].2.3", "value 1[1].2.3"))
                    .redundantField("field 1[1].3", "value 1[1].3"));

    public GenericArrayPayloadEnhancerTest() {
        // We have 8 objects with 4 containing 4 redundant fields. The other 4 will be enhanced with 1 field
        // not existing in the class, and 2 class fields not existing in the payload.
        // In total, we have 4 + 4 * (1 + 2) = 16
        super(enhance(GENERIC_PAYLOAD), 8, 8, 16);
    }
}
