package com.seregamorph.restapi.partial;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;

class PartialPayloadDeserializerModifier extends BeanDeserializerModifier {

    @Override
    public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc,
                                                  JsonDeserializer<?> deserializer) {
        if (PartialPayload.class.isAssignableFrom(beanDesc.getBeanClass())) {
            return new PartialPayloadDeserializer(deserializer, beanDesc.getBeanClass());
        }
        return deserializer;
    }
}
