package com.seregamorph.restapi.partial;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

/**
 * Utility class to configure {@link ObjectMapper} specifically for the case where objects are {@link PartialPayload}.
 */
@UtilityClass
public class PartialPayloadMapperUtils {

    /**
     * Initialize objectMapper to support partial payload serialization and deserialization.
     *
     * @return initialized ObjectMapper instance
     * @see PartialPayload
     * @see PartialPayloadFactory#partial(Class)
     */
    public static ObjectMapper configure(ObjectMapper objectMapper) {
        return objectMapper.registerModule(new PartialPayloadModule());
    }

}
