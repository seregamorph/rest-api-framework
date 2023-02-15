package com.seregamorph.restapi.test.base;

import static com.google.common.collect.Maps.immutableEntry;
import static com.seregamorph.restapi.test.base.JsonMatcher.jsonMatchersOf;
import static com.seregamorph.restapi.test.base.JsonMatcher.jsonMatching;
import static com.seregamorph.restapi.test.utils.MoreMatchers.where;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.nullValue;

import com.google.common.collect.ImmutableMap;
import com.seregamorph.restapi.base.BasePayload;
import com.seregamorph.restapi.base.NestedResource;
import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.util.Arrays;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.val;
import org.junit.Test;

public class JsonMatcherProxyFactoryTest extends AbstractUnitTest {

    private static final String NAME = "value";

    @Test
    public void nestedResourceShouldJsonMatchingToString() {
        val resource = jsonMatching(NestedResource.class)
                .setValue("value");

        collector.checkThat(resource, hasToString("JsonMatcherProxy<com.seregamorph.restapi.base.NestedResource>(value)"));
    }

    @Test
    public void regularPojoShouldShouldJsonMatching() {
        val proxy = jsonMatching(RegularPojo.class);
        proxy.setName(NAME);

        val jsonMatchers = jsonMatchersOf(proxy);

        collector.checkThat(jsonMatchers, contains(allOf(
                where(JsonMatcher::getPath, equalTo(RegularPojo.Fields.NAME)),
                where(JsonMatcher::getIndex, nullValue()),
                where(JsonMatcher::getMatcher, instanceOf(String.class))
        )));
        collector.checkThat(proxy, hasToString("JsonMatcherProxy<com.seregamorph.restapi.test.base"
                + ".JsonMatcherProxyFactoryTest$RegularPojo>(name)"));
    }

    @Test
    public void plainMapShouldMapNested() {
        val map = ImmutableMap.of(
                "wrapper1", ImmutableMap.of("nested", 1),
                "wrapper2", ImmutableMap.of("nested", 2)
        );

        val list = JsonMatcherProxyFactory.plainMap(map);

        collector.checkThat(list, equalTo(Arrays.asList(
                immutableEntry("wrapper1.nested", 1),
                immutableEntry("wrapper2.nested", 2)
        )));
    }

    // note: intended default chain=false for void setter non-chained method
    @Accessors(chain = false)
    @Data
    @FieldNameConstants
    public static class RegularPojo implements BasePayload {

        private String name;
    }
}
