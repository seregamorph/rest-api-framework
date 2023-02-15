package com.seregamorph.restapi.test.base;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.seregamorph.restapi.test.base.BaseJsonPathUtils.mergeExistingFields;
import static com.seregamorph.restapi.test.base.BaseJsonPathUtils.removeIgnoredNodes;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seregamorph.restapi.base.NestedResource;
import com.seregamorph.restapi.partial.PartialPayload;
import com.seregamorph.restapi.test.common.AbstractUnitTest;
import com.seregamorph.restapi.test.utils.JsonExtensions;
import java.util.Collections;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import lombok.val;
import org.junit.Test;

public class BaseJsonPathUtilsTest extends AbstractUnitTest implements JsonExtensions {

    private final ObjectMapper mapper = new ObjectMapper()
            .enable(INDENT_OUTPUT);

    @Override
    public ObjectMapper objectMapper() {
        return mapper;
    }

    @Test
    public void simpleIgnoredNodeShouldBeRemoved() {
        val object = object()
                .set("a", 1)
                .set("b", 2);

        removeIgnoredNodes(object, Collections.singletonList("$.a"));

        assertThat(object, equalTo(object()
                .set("b", 2)));
    }

    @Test
    public void simpleNestedIgnoredNodeShouldBeRemoved() {
        val object = object()
                .set("wrapper", object()
                        .set("nested", 1))
                .set("b", 2);

        removeIgnoredNodes(object, Collections.singletonList("$.wrapper.nested"));

        assertThat(object, equalTo(object()
                .set("wrapper", object())
                .set("b", 2)));
    }

    @Test
    public void collectionIgnoredNodeShouldBeRemoved() {
        val object = array()
                .add(object()
                        .set("a", 1)
                        .set("b", 2))
                .add(object()
                        .set("a", 3)
                        .set("b", 4));

        removeIgnoredNodes(object, Collections.singletonList("$[*].a"));

        assertThat(object, equalTo(array()
                .add(object()
                        .set("b", 2))
                .add(object()
                        .set("b", 4))));
    }

    @Test
    public void nestedCollectionIgnoredNodeShouldBeRemoved() {
        val object = array()
                .add(object()
                        .set("a", 1)
                        .set("b", array(object()
                                .set("c", 2))))
                .add(object()
                        .set("a", 3)
                        .set("b", array(object()
                                .set("c", 4))));

        removeIgnoredNodes(object, Collections.singletonList("$[*].b[*].c"));

        assertThat(object, equalTo(array()
                .add(object()
                        .set("a", 1)
                        .set("b", array(object())))
                .add(object()
                        .set("a", 3)
                        .set("b", array(object())))));
    }

    @Test
    public void nonPayloadIdFieldsShouldBeMerged() {
        val targetNode = object()
                .set(SimplePayload.Fields.NEXT, object()
                        .set(SimplePayload.Fields.VALUE1, "a")
                        .set(SimplePayload.Fields.VALUE2, "b"));
        val ignored = mergeExistingFields(
                targetNode,
                object()
                        .set(SimplePayload.Fields.NEXT, object()
                                .set(SimplePayload.Fields.VALUE2, "c")
                                .set(SimplePayload.Fields.VALUE3, "d")),
                SimplePayload.class
        );

        collector.checkThat(targetNode, equalTo(object()
                .set(SimplePayload.Fields.NEXT, object()
                        .set(SimplePayload.Fields.VALUE1, "a")
                        .set(SimplePayload.Fields.VALUE2, "c"))));
        collector.checkThat(ignored, empty());
    }

    @Test
    public void payloadIdFieldsShouldBeMergedAndHandled() {
        val targetNode = object()
                .set(SimplePayload.Fields.NEXT, object()
                        .set(SimplePayload.Fields.VALUE1, "a")
                        .set(SimplePayload.Fields.VALUE2, "b")
                        .set(SimplePayload.Fields.NESTED, object()
                                .set(NestedResource.FIELD_ID, 1)
                                .set(NestedResource.Fields.VALUE, "old")));
        val ignored = mergeExistingFields(
                targetNode,
                object()
                        .set(SimplePayload.Fields.NEXT, object()
                                .set(SimplePayload.Fields.VALUE2, "c")
                                .set(SimplePayload.Fields.VALUE3, "d")
                                .set(SimplePayload.Fields.NESTED, object()
                                        .set(NestedResource.FIELD_ID, 2))),
                SimplePayload.class
        );

        collector.checkThat(targetNode, equalTo(object()
                .set(SimplePayload.Fields.NEXT, object()
                        .set(SimplePayload.Fields.VALUE1, "a")
                        .set(SimplePayload.Fields.VALUE2, "c")
                        .set(SimplePayload.Fields.NESTED, object()
                                .set(NestedResource.FIELD_ID, 2)
                                // note: field is still here (ignored list updated)
                                .set(NestedResource.Fields.VALUE, "old")))));
        collector.checkThat(ignored, contains("$.next.nested.value", "$.next.nested.wrapper"));
    }

    @Data
    @FieldNameConstants
    private static class SimplePayload extends PartialPayload {

        private String value1;

        private String value2;

        private String value3;

        private SimplePayload next;

        private NestedResource nested;
    }
}
