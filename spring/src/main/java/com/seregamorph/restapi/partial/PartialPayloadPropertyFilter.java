package com.seregamorph.restapi.partial;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.std.MapProperty;

public class PartialPayloadPropertyFilter extends SimpleBeanPropertyFilter {

    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer)
            throws Exception {
        if (include(writer)) {
            if (pojo instanceof PartialPayload) {
                if (writer.getName().equals(PartialPayload.Fields.PARTIAL_PROPERTIES)) {
                    return;
                }

                PartialPayload payload = (PartialPayload) pojo;

                // note: writer instanceof MapProperty condition is used for nested partial objects in plain maps
                if (writer instanceof MapProperty || (
                        !payload.isPartialPropertiesInitialized()
                                || payload.getPartialProperties().containsKey(writer.getName()))) {
                    writer.serializeAsField(pojo, jgen, provider);
                }
            } else {
                super.serializeAsField(pojo, jgen, provider, writer);
            }
        } else if (!jgen.canOmitFields()) {
            writer.serializeAsOmittedField(pojo, jgen, provider);
        }
    }
}
