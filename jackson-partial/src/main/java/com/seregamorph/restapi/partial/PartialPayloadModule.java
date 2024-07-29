package com.seregamorph.restapi.partial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public class PartialPayloadModule extends SimpleModule {

    private static final String MODULE_NAME = "PartialPayload";

    public PartialPayloadModule() {
        super(MODULE_NAME);

        setDeserializerModifier(new PartialPayloadDeserializerModifier());
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);

        // Allows to use resource classes directly to represent request payloads without accidentally sending
        // redundant, null fields
        // DO NOT use .setSerializationInclusion(JsonInclude.Include.NON_NULL) here - sometimes we do need to send
        // null in the request payload
        ObjectMapper objectMapper = context.getOwner();
        objectMapper.setFilterProvider(partialPayloadPropertyFilterProvider());
    }

    private static SimpleFilterProvider partialPayloadPropertyFilterProvider() {
        return new SimpleFilterProvider()
                .addFilter(PartialPayloadUtils.FILTER_NAME, new PartialPayloadPropertyFilter());
    }

}
