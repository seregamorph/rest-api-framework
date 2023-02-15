package com.seregamorph.restapi.mapstruct;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.time.Instant;
import java.util.Collections;
import org.junit.Test;

public class CachedMappingContextTest extends AbstractUnitTest {

    private static final String SAMPLE_SOURCE = "source";

    @Test
    public void instanceShouldReturnNullObject() {
        CachedMappingContext context = CachedMappingContext.instance();

        Object result = context.getMappedInstance(SAMPLE_SOURCE, Instant.class);

        collector.checkThat(result, nullValue());
    }

    @Test
    public void instanceShouldReturnInitializedObject() {
        Object object = Instant.now();
        CachedMappingContext context = CachedMappingContext.instance(
                Collections.singletonMap(SAMPLE_SOURCE, object));

        Object result = context.getMappedInstance(SAMPLE_SOURCE, Instant.class);

        collector.checkThat(result, sameInstance(object));
    }

    @Test
    public void instanceShouldReturnSameObject() {
        CachedMappingContext context = CachedMappingContext.instance();

        Object object = Instant.now();
        context.storeMappedInstance(SAMPLE_SOURCE, object, Instant.class);

        Object result = context.getMappedInstance(SAMPLE_SOURCE, Instant.class);

        collector.checkThat(result, sameInstance(object));
    }

    @Test
    public void instanceShouldReturnLastStoredObject() {
        CachedMappingContext context = CachedMappingContext.instance(
                Collections.singletonMap(SAMPLE_SOURCE, Instant.now()));

        Object object = Instant.now();
        context.storeMappedInstance(SAMPLE_SOURCE, object, Instant.class);

        Object result = context.getMappedInstance(SAMPLE_SOURCE, Instant.class);

        collector.checkThat(result, sameInstance(object));
    }

    @Test
    public void immutableInstanceShouldReturnNullObject() {
        CachedMappingContext context = CachedMappingContext.immutableInstance();

        Object result = context.getMappedInstance(SAMPLE_SOURCE, Instant.class);

        collector.checkThat(result, nullValue());
    }

    @Test
    public void immutableInstanceShouldReturnOriginallyNullObject() {
        CachedMappingContext context = CachedMappingContext.immutableInstance();
        context.storeMappedInstance(SAMPLE_SOURCE, Instant.now(), Instant.class);

        Object result = context.getMappedInstance(SAMPLE_SOURCE, Instant.class);

        collector.checkThat(result, nullValue());
    }

    @Test
    public void immutableInstanceShouldReturnInitializedObject() {
        Object object = Instant.now();
        CachedMappingContext context = CachedMappingContext.immutableInstance(
                Collections.singletonMap(SAMPLE_SOURCE, object));

        Object result = context.getMappedInstance(SAMPLE_SOURCE, Instant.class);

        collector.checkThat(result, sameInstance(object));
    }

    @Test
    public void immutableInstanceShouldReturnOriginallyInitializedObject() {
        Object object = Instant.now();
        CachedMappingContext context = CachedMappingContext.immutableInstance(
                Collections.singletonMap(SAMPLE_SOURCE, object));
        context.storeMappedInstance(SAMPLE_SOURCE, Instant.now(), Instant.class);

        Object result = context.getMappedInstance(SAMPLE_SOURCE, Instant.class);

        collector.checkThat(result, sameInstance(object));
    }
}
