package com.seregamorph.restapi.partial;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import lombok.val;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

public class PartialPayloadDeserializerTest {

    private static final String ID_PROPERTY = "id";
    private static final Long ID_VALUE = 1986L;

    @Rule
    public final ErrorCollector collector = new ErrorCollector();

    @Test
    public void shouldDelegateResolve() throws Exception {
        // Arrange
        val defaultDeserializer = mock(BeanDeserializer.class);
        val deserializer = new PartialPayloadDeserializer(defaultDeserializer, UserResource.class);
        val ctxt = mock(DeserializationContext.class);

        // Act
        deserializer.resolve(ctxt);

        // Assert
        verify(defaultDeserializer).resolve(ctxt);
    }

    @Test
    public void shouldDeserializeResource() throws Exception {
        // Arrange
        val defaultDeserializer = mock(JsonDeserializer.class);
        val deserializer = new PartialPayloadDeserializer(defaultDeserializer, UserResource.class);
        val parser = mock(JsonParser.class);
        val ctxt = mock(DeserializationContext.class);
        val node = new ObjectNode(null);
        when(parser.readValueAsTree())
                .thenReturn(node);
        when(defaultDeserializer.deserialize(any(), eq(ctxt)))
                .thenReturn(new UserResource());

        // Act
        val deserialized = deserializer.deserialize(parser, ctxt);

        // Assert
        assertThat(deserialized, is(new UserResource()));
    }

    @Test
    public void shouldDeserializeResourceWithNonNullFieldHavingJsonPropertyAnnotation() throws Exception {
        // Arrange
        val defaultDeserializer = mock(JsonDeserializer.class);
        val deserializer = new PartialPayloadDeserializer(defaultDeserializer, UserResource.class);
        val parser = mock(JsonParser.class);
        val ctxt = mock(DeserializationContext.class);
        val node = new ObjectNode(new JsonNodeFactory(false))
                .put(ID_PROPERTY, ID_VALUE);
        when(parser.readValueAsTree())
                .thenReturn(node);
        when(defaultDeserializer.deserialize(any(), eq(ctxt)))
                .thenReturn(new UserResource());

        // Act
        val deserialized = deserializer.deserialize(parser, ctxt);

        // Assert
        collector.checkThat(deserialized, is(new UserResource()));
        collector.checkThat(deserialized.getPartialProperties().containsKey(UserResource.Fields.ENTITY_ID), is(true));
    }

    @Test
    public void shouldDeserializeResourceWithNullFieldHavingJsonPropertyAnnotation() throws Exception {
        // Arrange
        val defaultDeserializer = mock(JsonDeserializer.class);
        val deserializer = new PartialPayloadDeserializer(defaultDeserializer, UserResource.class);
        val parser = mock(JsonParser.class);
        val ctxt = mock(DeserializationContext.class);
        val node = new ObjectNode(new JsonNodeFactory(false))
                .putNull(ID_PROPERTY);
        when(parser.readValueAsTree())
                .thenReturn(node);
        when(defaultDeserializer.deserialize(any(), eq(ctxt)))
                .thenReturn(new UserResource());

        // Act
        val deserialized = deserializer.deserialize(parser, ctxt);

        // Assert
        collector.checkThat(deserialized, is(new UserResource()));
        collector.checkThat(deserialized.getPartialProperties().containsKey(UserResource.Fields.ENTITY_ID), is(true));
    }

    @Data
    @FieldNameConstants
    private static final class UserResource extends PartialResource {

        @JsonProperty(ID_PROPERTY)
        private long entityId;
        private String name;
    }
}
