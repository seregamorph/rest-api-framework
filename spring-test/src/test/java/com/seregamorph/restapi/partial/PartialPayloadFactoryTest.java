package com.seregamorph.restapi.partial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seregamorph.restapi.base.IdPartial;
import com.seregamorph.restapi.base.IdResource;
import com.seregamorph.restapi.test.common.AbstractUnitTest;
import com.seregamorph.restapi.test.utils.JsonExtensions;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import lombok.val;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import static com.seregamorph.restapi.partial.PartialPayloadFactory.partial;
import static org.hamcrest.Matchers.*;

public class PartialPayloadFactoryTest extends AbstractUnitTest implements JsonExtensions {

    private static final String FIELD_NESTED = "nested";

    private static final String VALUE_NAME = "value";
    private static final String VALUE_TITLE = "title";

    private ObjectMapper objectMapper;

    @Before
    public void initObjectMapper() {
        objectMapper = PartialPayloadMapperUtils.configure(new ObjectMapper());
    }

    @Override
    public ObjectMapper objectMapper() {
        return objectMapper;
    }

    @Test
    public void listOrPartialShouldBeSerialized() throws Exception {
        val nested = SimplePartialResource.create()
                .setName(VALUE_NAME);

        val list = Arrays.asList(nested);

        val node = readJson(writeJson(list));

        collector.checkThat(node, equalTo(array(object()
                .set(SimplePartialResource.Fields.NAME, VALUE_NAME))));
    }

    @Test
    public void nestedInPlainMapShouldBePartialSerialized() throws Exception {
        val nested = SimplePartialResource.create()
                .setName(VALUE_NAME);

        val map = new LinkedHashMap<>();
        map.put(FIELD_NESTED, nested);

        val node = readJson(writeJson(map));

        collector.checkThat(node, equalTo(object()
                .set(FIELD_NESTED, object()
                        .set(SimplePartialResource.Fields.NAME, VALUE_NAME))));
    }

    @Test
    public void nestedNestedInPlainMapShouldBePartialSerialized() throws Exception {
        val nested1 = SimplePartialResource.create()
                .setName(VALUE_NAME);

        val nested2 = SimplePartialResource.create()
                .setLinked(nested1);

        val map = new LinkedHashMap<>();
        map.put(FIELD_NESTED, nested2);

        val node = readJson(writeJson(map));

        collector.checkThat(node, equalTo(object()
                .set(FIELD_NESTED, object()
                        .set(SimplePartialResource.Fields.LINKED, object()
                                .set(SimplePartialResource.Fields.NAME, VALUE_NAME)))));
    }

    @Test
    public void nestedNestedShouldBePartialSerialized() throws Exception {
        val nested1 = SimplePartialResource.create()
                .setName(VALUE_NAME);

        val nested2 = SimplePartialResource.create()
                .setLinked(nested1);

        val node = readJson(writeJson(nested2));

        collector.checkThat(node, equalTo(object()
                .set(SimplePartialResource.Fields.LINKED, object()
                        .set(SimplePartialResource.Fields.NAME, VALUE_NAME))));
    }

    @Test
    public void partialResourceShouldEqualWithSameFields() {
        val resource1 = SimplePartialResource.create()
                .setName(VALUE_NAME)
                .setTitle(VALUE_TITLE);
        val resource2 = SimplePartialResource.create()
                .setName(VALUE_NAME)
                .setTitle(VALUE_TITLE);

        collector.checkThat(resource1, equalTo(resource2));
    }

    @Test
    public void partialResourceShouldNotEqualWithOtherFields() {
        val resource1 = SimplePartialResource.create()
                .setName(VALUE_NAME)
                .setTitle(VALUE_TITLE);
        val resource2 = SimplePartialResource.create()
                .setName(VALUE_TITLE)
                .setTitle(VALUE_NAME);

        collector.checkThat(resource1, not(equalTo(resource2)));
    }

    @Test
    public void partialResourceShouldEqualRegularResourceWithSameFields() {
        val resource1 = SimplePartialResource.create()
                .setName(VALUE_NAME)
                .setTitle(VALUE_TITLE);
        val resource2 = new SimplePartialResource()
                .setName(VALUE_NAME)
                .setTitle(VALUE_TITLE);

        collector.checkThat(resource1, equalTo(resource2));
    }

    @Test
    public void partialResourceShouldNotEqualRegularResourceWithOtherFields() {
        val resource1 = SimplePartialResource.create()
                .setName(VALUE_NAME)
                .setTitle(VALUE_TITLE);
        val resource2 = new SimplePartialResource()
                .setName(VALUE_TITLE)
                .setTitle(VALUE_NAME);

        collector.checkThat(resource1, not(equalTo(resource2)));
    }

    @Test
    public void partialResourceShouldIncludeOnlySetValues() throws Exception {
        val resource = SimplePartialResource.create()
                .setName(VALUE_NAME);

        val node = readJson(writeJson(resource));

        collector.checkThat(node, equalTo(object()
                .set(SimplePartialResource.Fields.NAME, VALUE_NAME)));
    }

    @Test
    public void emptyPartialPayloadShouldBeSerializedToEmptyJsonString() throws Exception {
        val resource = SimplePartialResource.create();

        String jsonString = objectMapper.writeValueAsString(resource);

        collector.checkThat(resource.hasPartialProperty(SimplePartialResource.Fields.NAME), is(false));
        collector.checkThat(readJson(jsonString), equalTo(object()));
    }

    @Test
    public void nonEmptyPartialPayloadShouldBeSerializedIncludingOnlyPartialFields() throws Exception {
        SimplePartialResource payload = SimplePartialResource.create()
                .setName(VALUE_NAME)
                .setTitle(VALUE_TITLE);

        String jsonString = objectMapper.writeValueAsString(payload);

        collector.checkThat(readJson(jsonString), equalTo(object()
                .set(SimplePartialResource.Fields.NAME, VALUE_NAME)
                .set(SimplePartialResource.Fields.TITLE, VALUE_TITLE)));
    }

    @Test
    public void nonPartialPayloadShouldBeSerializedIncludingAllFields() throws Exception {
        SimplePartialResource payload = new SimplePartialResource()
                .setName(VALUE_NAME)
                .setTitle(VALUE_TITLE)
                .setVersion(1);

        String jsonString = objectMapper.writeValueAsString(payload);

        collector.checkThat(readJson(jsonString), equalTo(object()
                .set(SimplePartialResource.FIELD_ID, null)
                .set(SimplePartialResource.Fields.NAME, VALUE_NAME)
                .set(SimplePartialResource.Fields.TITLE, VALUE_TITLE)
                .set(SimplePartialResource.Fields.DESCRIPTION, null)
                .set(SimplePartialResource.Fields.VERSION, 1)
                .setNull(SimplePartialResource.Fields.LINKED)));
    }

    @Test
    public void partialResourceShouldHavePartialToString() {
        val resource = SimplePartialResource.create()
                .setName(VALUE_NAME)
                .setTitle(VALUE_TITLE);

        collector.checkThat(resource, hasToString("Partial<SimplePartialResource>{name=value, title=title}"));
    }

    @Test
    public void nestedPartialResourceShouldHavePartialToString() throws IOException {
        val resource = SimplePartialResource.create()
                .setName(VALUE_NAME)
                .setTitle(null)
                .setLinked(SimplePartialResource.create()
                        .setId(10L));
        collector.checkThat(resource, hasToString("Partial<SimplePartialResource>{name=value, " +
                "title=null, linked=Partial<SimplePartialResource>{id=10}}"));

        String str = objectMapper.writeValueAsString(resource);
        val parsed = objectMapper.readValue(str, SimplePartialResource.class);
        collector.checkThat(parsed.hasPartialProperty(SimplePartialResource.Fields.NAME), is(true));
        collector.checkThat(parsed.hasPartialProperty(SimplePartialResource.Fields.TITLE), is(true));
        collector.checkThat(parsed.hasPartialProperty(SimplePartialResource.Fields.DESCRIPTION), is(false));
        collector.checkThat(parsed.hasPartialProperty(SimplePartialResource.Fields.LINKED), is(true));
        collector.checkThat(parsed.getLinked().hasPartialProperty(SimplePartialResource.FIELD_ID), is(true));
        collector.checkThat(parsed.getLinked().hasPartialProperty(SimplePartialResource.Fields.NAME), is(false));
    }

    @Test
    public void emptyPartialResourceShouldHaveEmptyPartialToString() {
        val resource = SimplePartialResource.create();

        collector.checkThat(resource, hasToString("Partial<SimplePartialResource>{}"));
    }

    @Test
    public void partialInterfaceShouldNotSerializeRedundantFields() throws Exception {
        val resource = SimplePartialResource.create()
                .setName(VALUE_NAME)
                .setTitle(VALUE_TITLE)
                .setDescription("description");

        // note: "description" field does not exist in SimplePartial interface
        // makes sense for feign clients
        val jsonString = objectMapper().writerFor(SimplePartial.class)
                .writeValueAsString(resource);

        collector.checkThat(readJson(jsonString), equalTo(object()
                .set(SimplePartialResource.Fields.NAME, VALUE_NAME)
                .set(SimplePartialResource.Fields.TITLE, VALUE_TITLE)));
    }

    public interface SimplePartial extends IdPartial<Long> {

        @Required
        String getName();

        @Required
        String getTitle();
    }

    @Data
    @FieldNameConstants
    static class SimplePartialResource extends IdResource<Long, SimplePartialResource> implements SimplePartial {

        private String name;

        private String title;

        private String description;

        private int version;

        private SimplePartialResource linked;

        public static SimplePartialResource create() {
            return partial(SimplePartialResource.class);
        }
    }
}
