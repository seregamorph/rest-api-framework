package com.seregamorph.restapi.test.base.setup.common.payload;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;

@Slf4j
@RequiredArgsConstructor
public abstract class GenericPayloadTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(SerializationFeature.INDENT_OUTPUT, true);

    private final GenericPayload genericPayload;
    private final int requiredFieldQty;
    private final int optionalFieldQty;
    private final int redundantFieldQty;

    @Test
    public void requiredIteratorShouldIterateAllRequiredFields() throws Exception {
        int index = 0;

        for (val generatedPayload : genericPayload.iterateRequiredFields()) {
            log.info("Payload with 1 missing required fields #{}: {}, {}",
                    ++index, generatedPayload.getResourceClass(), generatedPayload.getFieldName());
            log.info(OBJECT_MAPPER.writeValueAsString(generatedPayload.getPayload()));
        }

        assertThat(index, is(requiredFieldQty));
    }

    @Test
    public void optionalIteratorShouldIterateAllOptionalFields() throws Exception {
        int index = 0;

        for (val generatedPayload : genericPayload.iterateOptionalFields()) {
            log.info("Payload with all required fields and 1 optional field #{}: {}, {}",
                    ++index, generatedPayload.getResourceClass(), generatedPayload.getFieldName());
            log.info(OBJECT_MAPPER.writeValueAsString(generatedPayload.getPayload()));
        }

        assertThat(index, is(optionalFieldQty));
    }

    @Test
    public void redundantIteratorShouldIterateAllRedundantFields() throws Exception {
        int index = 0;

        for (val generatedPayload : genericPayload.iterateRedundantFields()) {
            log.info("Payload with all required fields and 1 redundant field #{}: {}, {}",
                    ++index, generatedPayload.getResourceClass(), generatedPayload.getFieldName());
            log.info(OBJECT_MAPPER.writeValueAsString(generatedPayload.getPayload()));
        }

        assertThat(index, is(redundantFieldQty));
    }
}
