package com.seregamorph.restapi.partial;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.util.List;
import org.junit.Test;

public class PartialPayloadModuleTest {

    @Test
    public void shouldCreateModule() {
        PartialPayloadModule module = new PartialPayloadModule();
        String string = module.toString();
        assertThat(string, notNullValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void modifySerializerShouldReturnExisting() {
        PartialPayloadDeserializerModifier modifier = new PartialPayloadDeserializerModifier();
        BeanDescription beanDesc = mock(BeanDescription.class);
        JsonDeserializer deserializer = mock(JsonDeserializer.class);
        when(beanDesc.getBeanClass()).thenReturn((Class) List.class);

        JsonDeserializer<?> newDeserializer = modifier.modifyDeserializer(null, beanDesc, deserializer);

        assertThat(newDeserializer, is(deserializer));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void modifySerializerShouldReturnPartialResourceDeserializer() {
        PartialPayloadDeserializerModifier modifier = new PartialPayloadDeserializerModifier();
        BeanDescription beanDesc = mock(BeanDescription.class);
        JsonDeserializer deserializer = mock(JsonDeserializer.class);
        when(beanDesc.getBeanClass()).thenReturn((Class) PartialResource.class);

        JsonDeserializer<?> newDeserializer = modifier.modifyDeserializer(null, beanDesc, deserializer);

        assertThat(newDeserializer, not(is(deserializer)));
        assertThat(newDeserializer, instanceOf(PartialPayloadDeserializer.class));
    }
}
