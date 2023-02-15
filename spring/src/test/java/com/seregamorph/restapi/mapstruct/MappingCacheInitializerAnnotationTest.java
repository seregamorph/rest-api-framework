package com.seregamorph.restapi.mapstruct;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.seregamorph.restapi.base.BaseProjection;
import com.seregamorph.restapi.base.BaseResource;
import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.junit.Test;

/**
 * This class is dedicated to test the behavior of {@link MappingCacheInitializer} with implicit or explicit
 * annotations.
 */
public class MappingCacheInitializerAnnotationTest extends AbstractUnitTest {

    private static final SampleEntity3 SAMPLE_ENTITY_3_1 = new SampleEntity3()
            .setId("SAMPLE_ENTITY_3_1");
    private static final SampleEntity3 SAMPLE_ENTITY_3_2 = new SampleEntity3()
            .setId("SAMPLE_ENTITY_3_2");
    private static final SampleEntity3 SAMPLE_ENTITY_3_3 = new SampleEntity3()
            .setId("SAMPLE_ENTITY_3_3");
    private static final SampleEntity3 SAMPLE_ENTITY_3_4 = new SampleEntity3()
            .setId("SAMPLE_ENTITY_3_4");
    private static final SampleEntity3 SAMPLE_ENTITY_3_0_1 = new SampleEntity3()
            .setId("SAMPLE_ENTITY_3_0_1");
    private static final SampleEntity3 SAMPLE_ENTITY_3_0_2 = new SampleEntity3()
            .setId("SAMPLE_ENTITY_3_0_2");
    private static final SampleEntity3 SAMPLE_ENTITY_3_0_3 = new SampleEntity3()
            .setId("SAMPLE_ENTITY_3_0_3");
    private static final SampleEntity3 SAMPLE_ENTITY_3_0_4 = new SampleEntity3()
            .setId("SAMPLE_ENTITY_3_0_4");
    private static final SampleEntity2 SAMPLE_ENTITY_2_1 = new SampleEntity2()
            .setId("SAMPLE_ENTITY_2_1")
            .setField(SAMPLE_ENTITY_3_1)
            .setProxiedField(SAMPLE_ENTITY_3_0_1);
    private static final SampleEntity2 SAMPLE_ENTITY_2_2 = new SampleEntity2()
            .setId("SAMPLE_ENTITY_2_2")
            .setField(SAMPLE_ENTITY_3_2)
            .setProxiedField(SAMPLE_ENTITY_3_0_2);
    private static final SampleEntity2 SAMPLE_ENTITY_2_3 = new SampleEntity2()
            .setId("SAMPLE_ENTITY_2_3")
            .setField(SAMPLE_ENTITY_3_3)
            .setProxiedField(SAMPLE_ENTITY_3_0_3);
    private static final SampleEntity2 SAMPLE_ENTITY_2_4 = new SampleEntity2()
            .setId("SAMPLE_ENTITY_2_4")
            .setField(SAMPLE_ENTITY_3_4)
            .setProxiedField(SAMPLE_ENTITY_3_0_4);
    private static final SampleEntity1 SAMPLE_ENTITY_1 = new SampleEntity1()
            .setId("SAMPLE_ENTITY_1")
            .setField1(SAMPLE_ENTITY_2_1)
            .setField2(SAMPLE_ENTITY_2_2)
            .setField3(Collections.singletonList(SAMPLE_ENTITY_2_3))
            .setField4(Arrays.asList(SAMPLE_ENTITY_2_4, null));

    @Test
    public void initializeCacheWithAllFieldsProjectionShouldReturnEmptyCache() {
        CachedMappingContext cache = MappingCacheInitializer.initializeCache(
                SAMPLE_ENTITY_1,
                SampleEntity1Resource.class,
                SampleEntity1AllFieldsProjection.class);
        collector.checkThat(cache.size(), is(0));
    }

    @Test
    public void initializeImmutableCacheWithAllFieldsProjectionShouldReturnEmptyCache() {
        CachedMappingContext cache = MappingCacheInitializer.initializeImmutableCache(
                SAMPLE_ENTITY_1,
                SampleEntity1Resource.class,
                SampleEntity1AllFieldsProjection.class);
        collector.checkThat(cache.size(), is(0));
    }

    @Test
    public void initializeCacheWithIdProjectionShouldReturnCacheWithAllDirectlyNestedResources() {
        CachedMappingContext cache = MappingCacheInitializer.initializeCache(
                SAMPLE_ENTITY_1,
                SampleEntity1Resource.class,
                IdProjection.class);
        cacheInitializeWithIdProjectionShouldContainAllDirectlyNestedResources(cache);
    }

    @Test
    public void initializeImmutableCacheWithIdProjectionShouldReturnCacheWithAllDirectlyNestedResources() {
        CachedMappingContext cache = MappingCacheInitializer.initializeImmutableCache(
                SAMPLE_ENTITY_1,
                SampleEntity1Resource.class,
                IdProjection.class);
        cacheInitializeWithIdProjectionShouldContainAllDirectlyNestedResources(cache);
    }

    private void cacheInitializeWithIdProjectionShouldContainAllDirectlyNestedResources(CachedMappingContext cache) {
        collector.checkThat(cache.size(), is(4));
        Object object = new SampleEntity2Resource();
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_2_1, SampleEntity2Resource.class), equalTo(object));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_2_2, SampleEntity2Resource.class), equalTo(object));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_2_3, SampleEntity2Resource.class), equalTo(object));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_2_4, SampleEntity2Resource.class), equalTo(object));
    }

    @Test
    public void initializeCacheWithSomeFieldsProjectionShouldReturnCacheWithDeeperNestedResources() {
        CachedMappingContext cache = MappingCacheInitializer.initializeCache(
                SAMPLE_ENTITY_1,
                SampleEntity1Resource.class,
                SampleEntity1SomeFieldsProjection.class);
        cacheInitializedWithSomeFieldsProjectionShouldContainDeeperNestedResources(cache);
    }

    @Test
    public void initializeImmutableCacheWithSomeFieldsProjectionShouldReturnCacheWithDeeperNestedResources() {
        CachedMappingContext cache = MappingCacheInitializer.initializeImmutableCache(
                SAMPLE_ENTITY_1,
                SampleEntity1Resource.class,
                SampleEntity1SomeFieldsProjection.class);
        cacheInitializedWithSomeFieldsProjectionShouldContainDeeperNestedResources(cache);
    }

    private void cacheInitializedWithSomeFieldsProjectionShouldContainDeeperNestedResources(
            CachedMappingContext cache) {
        collector.checkThat(cache.size(), is(6));
        Object res2 = new SampleEntity2Resource();
        Object res3 = new SampleEntity3Resource();
        // SampleEntity1SomeFieldsProjection needs field1 and field3, both are IdProjection. Therefore, field2 and
        // field4 are in the cache, and SampleEntity2Resource instances for field1 and field3 are also in the cache.
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_2_2, SampleEntity2Resource.class), equalTo(res2));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_2_4, SampleEntity2Resource.class), equalTo(res2));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_3_1, SampleEntity3Resource.class), equalTo(res3));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_3_3, SampleEntity3Resource.class), equalTo(res3));
        // These are proxied fields, but they are deeply nested and their proxy methods are NOT used in the projection
        // (IdProjection or @DefaultMappingProjection); therefore, these proxied fields are put into the cache as usual.
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_3_0_1, SampleEntity3Resource.class), equalTo(res3));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_3_0_3, SampleEntity3Resource.class), equalTo(res3));
    }

    @Test
    public void initializeCacheWithNoProjectionShouldReturnCacheWithRequiredNestedResources() {
        CachedMappingContext cache = MappingCacheInitializer.initializeCache(
                SAMPLE_ENTITY_1,
                SampleEntity1Resource.class);
        cacheInitializedWithNoProjectionShouldContainRequiredNestedResources(cache);
    }

    @Test
    public void initializeImmutableCacheWithNoProjectionShouldReturnCacheWithRequiredNestedResources() {
        CachedMappingContext cache = MappingCacheInitializer.initializeImmutableCache(
                SAMPLE_ENTITY_1,
                SampleEntity1Resource.class);
        cacheInitializedWithNoProjectionShouldContainRequiredNestedResources(cache);
    }

    private void cacheInitializedWithNoProjectionShouldContainRequiredNestedResources(CachedMappingContext cache) {
        collector.checkThat(cache.size(), is(6));
        Object object = new SampleEntity3Resource();
        // field1 is marked Required with a projection containing a nested projection for SampleEntity3Resource
        // Hence SAMPLE_ENTITY_3_1 is required and not in the cache.
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_3_2, SampleEntity3Resource.class), equalTo(object));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_3_3, SampleEntity3Resource.class), equalTo(object));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_3_4, SampleEntity3Resource.class), equalTo(object));
        // The are proxied fields, but they are deeply nested and their proxy methods are NOT used in the projection
        // (IdProjection or @DefaultMappingProjection); therefore, these proxied fields are put into the cache as usual.
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_3_0_2, SampleEntity3Resource.class), equalTo(object));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_3_0_3, SampleEntity3Resource.class), equalTo(object));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_3_0_4, SampleEntity3Resource.class), equalTo(object));
    }

    @Data
    private static class SampleEntity1 {

        private String id;
        private SampleEntity2 field1;
        private SampleEntity2 field2;
        private List<SampleEntity2> field3;
        private List<SampleEntity2> field4;
    }

    @Data
    private static class SampleEntity2 {

        private String id;
        private SampleEntity3 field;
        private SampleEntity3 untouchedField; // We don't touch this field
        private SampleEntity3 proxiedField;
    }

    @Data
    private static class SampleEntity3 {

        private String id;
    }

    @Data
    private static class SampleEntity1Resource implements BaseResource {

        private String id;
        @DefaultMappingProjection(SampleEntity2Projection.class)
        private SampleEntity2Resource field1;
        @Renamed("field2")
        private SampleEntity2Resource field2Renamed;
        private List<SampleEntity2Resource> field3;
        @Renamed("field4")
        private List<SampleEntity2Resource> field4Renamed;
    }

    @Data
    @FieldNameConstants
    private static class SampleEntity2Resource implements BaseResource {

        private static int SAMPLE_STATIC_FIELD = 1;
        private final int sampleFinalField = 2;

        private String id;
        private SampleEntity3Resource field;
        private SampleEntity3Resource untouchedField; // We don't touch this field
        private SampleEntity3Resource proxiedField;
        private SampleEntity3Resource nonExistingField; // Does not exist in entity class

        @Proxy(SampleEntity2Resource.Fields.PROXIED_FIELD)
        @SuppressWarnings("unused")
        public String getSomething() {
            return "whatever";
        }
    }

    @Data
    private static class SampleEntity3Resource implements BaseResource {

        private String id;
    }

    private interface IdProjection extends BaseProjection {

        @SuppressWarnings("unused")
        String getId();
    }

    private interface SampleEntity1AllFieldsProjection extends IdProjection {

        @SuppressWarnings("unused")
        SampleEntity2Projection getField1();

        @SuppressWarnings("unused")
        SampleEntity2Projection getField2Renamed();

        @SuppressWarnings("unused")
        List<SampleEntity2Projection> getField3();

        @SuppressWarnings("unused")
        List<SampleEntity2Projection> getField4Renamed();
    }

    private interface SampleEntity1SomeFieldsProjection extends IdProjection {

        @SuppressWarnings("unused")
        IdProjection getField1();

        @SuppressWarnings("unused")
        List<IdProjection> getField3();
    }

    private interface SampleEntity2Projection extends IdProjection {

        @SuppressWarnings("unused")
        IdProjection getField();

        @SuppressWarnings("unused")
        IdProjection getNonExistingField();

        @SuppressWarnings("unused")
        String getSomething();
    }
}
