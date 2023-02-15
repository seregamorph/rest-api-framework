package com.seregamorph.restapi.mapstruct;

import static com.seregamorph.restapi.mapstruct.MappingCacheInitializationExceptions.errorRetrievingField;
import static com.seregamorph.restapi.mapstruct.MappingCacheInitializationExceptions.invalidClass;
import static com.seregamorph.restapi.mapstruct.MappingCacheInitializationExceptions.invalidProjectionMethod;
import static com.seregamorph.restapi.mapstruct.MappingCacheInitializationExceptions.invalidProxy;
import static com.seregamorph.restapi.mapstruct.MappingCacheInitializationExceptions.proxyContainsNonResourceFieldInMiddle;
import static com.seregamorph.restapi.mapstruct.MappingCacheInitializationExceptions.proxyPointsToWrongResource;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.seregamorph.restapi.base.BaseProjection;
import com.seregamorph.restapi.base.BaseResource;
import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * This class is dedicated to test the behavior of {@link MappingCacheInitializer} with proxy methods.
 */
public class MappingCacheInitializerProxyTest extends AbstractUnitTest {

    private static final SampleEntity5 SAMPLE_ENTITY_5_1 = new SampleEntity5()
            .setName("SAMPLE_ENTITY_5_1");
    private static final SampleEntity5 SAMPLE_ENTITY_5_2 = new SampleEntity5()
            .setName("SAMPLE_ENTITY_5_2");
    private static final SampleEntity5 SAMPLE_ENTITY_5_3 = new SampleEntity5()
            .setName("SAMPLE_ENTITY_5_3");
    private static final SampleEntity5 SAMPLE_ENTITY_5_4 = new SampleEntity5()
            .setName("SAMPLE_ENTITY_5_4");
    private static final SampleEntity4 SAMPLE_ENTITY_4_1 = new SampleEntity4()
            .setId("SAMPLE_ENTITY_4_1")
            .setSampleEntity5(SAMPLE_ENTITY_5_1);
    private static final SampleEntity4 SAMPLE_ENTITY_4_2 = new SampleEntity4()
            .setId("SAMPLE_ENTITY_4_2")
            .setSampleEntity5(SAMPLE_ENTITY_5_2);
    private static final SampleEntity4 SAMPLE_ENTITY_4_3 = new SampleEntity4()
            .setId("SAMPLE_ENTITY_4_3")
            .setSampleEntity5(SAMPLE_ENTITY_5_3);
    private static final SampleEntity4 SAMPLE_ENTITY_4_4 = new SampleEntity4()
            .setId("SAMPLE_ENTITY_4_4")
            .setSampleEntity5(SAMPLE_ENTITY_5_4);
    private static final SampleEntity3 SAMPLE_ENTITY_3 = new SampleEntity3()
            .setId("SAMPLE_ENTITY_3")
            .setSampleEntity41(SAMPLE_ENTITY_4_1)
            .setSampleEntity42(SAMPLE_ENTITY_4_2);
    private static final SampleEntity2 SAMPLE_ENTITY_2 = new SampleEntity2()
            .setId("SAMPLE_ENTITY_2")
            .setSampleEntity3(Collections.singletonList(SAMPLE_ENTITY_3))
            .setSampleEntity4(SAMPLE_ENTITY_4_3);
    private static final SampleEntity1 SAMPLE_ENTITY_1 = new SampleEntity1()
            .setId("SAMPLE_ENTITY_1")
            .setSampleEntity2(SAMPLE_ENTITY_2)
            .setSampleEntity4(SAMPLE_ENTITY_4_4);

    @Test
    public void shouldInitializeCacheForValidProxyContainingAllResourcesReturningNonResourceData() {
        CachedMappingContext cache = MappingCacheInitializer.initializeCache(
                SAMPLE_ENTITY_1,
                SampleEntity1Resource.class,
                SampleEntity1Resource.ProjectionForValidProxyContainingAllResourcesReturningNonResourceData.class);

        collector.checkThat(cache.size(), is(3));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_3, SampleEntity3Resource.class),
                equalTo(new SampleEntity3Resource()));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_4_3, SampleEntity4Resource.class),
                equalTo(new SampleEntity4Resource()));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_4_4, SampleEntity4Resource.class),
                equalTo(new SampleEntity4Resource()));
    }

    @Test
    public void shouldInitializeCacheForValidProxyPointingToRenamedResource() {
        CachedMappingContext cache = MappingCacheInitializer.initializeCache(
                SAMPLE_ENTITY_1,
                SampleEntity1Resource.class,
                SampleEntity1Resource.ProjectionForValidProxyPointingToRenamedResource.class);

        collector.checkThat(cache.size(), is(2));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_4_3, SampleEntity4Resource.class),
                equalTo(new SampleEntity4Resource()));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_4_4, SampleEntity4Resource.class),
                equalTo(new SampleEntity4Resource()));
    }

    @Test
    public void shouldInitializeCacheForValidProxyContainingSomeResourcesReturningNonResourceData() {
        CachedMappingContext cache = MappingCacheInitializer.initializeCache(
                SAMPLE_ENTITY_1,
                SampleEntity1Resource.class,
                SampleEntity1Resource.ProjectionForValidProxyContainingSomeResourcesReturningNonResourceData.class);

        collector.checkThat(cache.size(), is(1));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_4_4, SampleEntity4Resource.class),
                equalTo(new SampleEntity4Resource()));
    }

    @Test
    public void shouldNotInitializeCacheForInvalidProxyPointingToNonExistentField() {
        expect(errorRetrievingField(SampleEntity1Resource.class, "whatever"));

        MappingCacheInitializer.initializeCache(
                SAMPLE_ENTITY_1,
                SampleEntity1Resource.class,
                SampleEntity1Resource.ProjectionForInvalidProxyPointingToNonExistentField.class);
    }

    @Test
    public void shouldNotInitializeCacheForInvalidProxyPointingToWrongResource() {
        expect(proxyPointsToWrongResource(getMethod("getInvalidProxyPointingToWrongResource"),
                SampleEntity4Resource.class,
                SampleEntity3Resource.class));

        MappingCacheInitializer.initializeCache(
                SAMPLE_ENTITY_1,
                SampleEntity1Resource.class,
                SampleEntity1Resource.ProjectionForInvalidProxyPointingToWrongResource.class);
    }

    @Test
    public void shouldNotInitializeCacheForInvalidProxyWithIncompletePath() {
        expect(proxyPointsToWrongResource(getMethod("getInvalidProxyWithIncompletePath"),
                SampleEntity3Resource.class,
                SampleEntity2Resource.class));

        MappingCacheInitializer.initializeCache(
                SAMPLE_ENTITY_1,
                SampleEntity1Resource.class,
                SampleEntity1Resource.ProjectionForInvalidProxyWithIncompletePath.class);
    }

    @Test
    public void shouldNotInitializeCacheForInvalidProjection() {
        expect(invalidProxy(getMethod("getValidProxyPointingToRenamedResource"),
                invalidProjectionMethod(SampleEntity3Resource.class, getMethod(
                        SampleEntity4Resource.ProjectionContainingMethodNotExistingInAllOtherProjections.class,
                        "getName"))));

        MappingCacheInitializer.initializeCache(
                SAMPLE_ENTITY_1,
                SampleEntity1Resource.class,
                SampleEntity1Resource.InvalidProjectionForValidProxyPointingToRenamedResource.class);
    }

    @Test
    public void shouldNotInitializeCacheForInvalidProxyContainingNonResourceFieldInMiddle() {
        expect(proxyContainsNonResourceFieldInMiddle(
                getMethod("getInvalidProxyContainingNonResourceFieldInMiddle"), 1));

        MappingCacheInitializer.initializeCache(
                SAMPLE_ENTITY_1,
                SampleEntity1Resource.class,
                SampleEntity1Resource.ProjectionForInvalidProxyContainingNonResourceFieldInMiddle.class);
    }

    @Test
    public void shouldNotInitializeCacheWhenDefaultProjectionContainsNonExistentMethod() {
        expect(invalidClass(SampleEntity4Resource.class,
                invalidProjectionMethod(SampleEntity5Resource.class,
                        getMethod(com.seregamorph.restapi.base.IdProjection.class, "getId"))));

        MappingCacheInitializer.initializeCache(SAMPLE_ENTITY_4_1, SampleEntity4Resource.class);
    }

    @Test
    public void shouldInitializeCacheForProjectionContainingSpelForValidProxyPointingToRenamedResource() {
        CachedMappingContext cache = MappingCacheInitializer.initializeCache(
                SAMPLE_ENTITY_1,
                SampleEntity1Resource.class,
                SampleEntity1Resource.ProjectionContainingSpelForValidProxyPointingToRenamedResource.class);

        collector.checkThat(cache.size(), is(2));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_4_3, SampleEntity4Resource.class),
                equalTo(new SampleEntity4Resource()));
        collector.checkThat(cache.getMappedInstance(SAMPLE_ENTITY_4_4, SampleEntity4Resource.class),
                equalTo(new SampleEntity4Resource()));
    }

    private static Method getMethod(Class<?> clazz, String methodName) {
        Method method = BeanUtils.findMethod(clazz, methodName);
        if (method == null) {
            throw new RuntimeException(String.format("Method [%s] can't be found.", methodName));
        }
        return method;
    }

    private static Method getMethod(String methodName) {
        return getMethod(SampleEntity1Resource.class, methodName);
    }

    @Data
    private static class SampleEntity1 {

        private String id;

        private SampleEntity2 sampleEntity2;

        private SampleEntity4 sampleEntity4;
    }

    @Data
    @FieldNameConstants
    private static class SampleEntity2 {

        private String id;

        private List<SampleEntity3> sampleEntity3;

        private SampleEntity4 sampleEntity4;
    }

    @Data
    private static class SampleEntity3 {

        private String id;

        private SampleEntity4 sampleEntity41;

        private SampleEntity4 sampleEntity42;
    }

    @Data
    private static class SampleEntity4 {

        private String id;

        private SampleEntity5 sampleEntity5;
    }

    @Data
    private static class SampleEntity5 {

        private String name;
    }

    @Data
    @FieldNameConstants
    private static class SampleEntity1Resource implements BaseResource {

        private String id;

        private SampleEntity2Resource sampleEntity2;

        private SampleEntity4Resource sampleEntity4;

        @Proxy({SampleEntity1Resource.Fields.SAMPLE_ENTITY2, SampleEntity2Resource.Fields.ID})
        @SuppressWarnings("unused")
        public String getValidProxyContainingAllResourcesReturningNonResourceData() {
            return sampleEntity2.getId();
        }

        @Proxy({SampleEntity1Resource.Fields.SAMPLE_ENTITY2, SampleEntity2Resource.Fields.SAMPLE_ENTITY3S})
        @SuppressWarnings("unused")
        public List<SampleEntity3Resource> getValidProxyPointingToRenamedResource() {
            return sampleEntity2.getSampleEntity3s();
        }

        // This proxy declaration contains a resource field that has been renamed
        // Furthermore, it should have been declared
        // @Proxy({SampleEntity1Resource.Fields.SAMPLE_ENTITY2, SampleEntity2Resource.Fields.SAMPLE_ENTITY3S})
        // But we exclude the last field intentionally so that we stop processing the last projection
        // While the declaration is incomplete, it's still valid, as the method's return type is not a resource
        // and the system is not able to verify the constraint between the leaf-most projection
        // and the leaf-most resource in the proxy.
        @Proxy(SampleEntity1Resource.Fields.SAMPLE_ENTITY2)
        @SuppressWarnings("unused")
        public String getValidProxyContainingSomeResourcesReturningNonResourceData() {
            return sampleEntity2.getSampleEntity3s().get(0).getId();
        }

        @Proxy("whatever")
        @SuppressWarnings("unused")
        public String getInvalidProxyPointingToNonExistentField() {
            return "something";
        }

        @Proxy({SampleEntity1Resource.Fields.SAMPLE_ENTITY2, SampleEntity2Resource.Fields.SAMPLE_ENTITY3S})
        @SuppressWarnings("unused")
        public SampleEntity4Resource getInvalidProxyPointingToWrongResource() {
            return new SampleEntity4Resource();
        }

        // This proxy declaration is wrong. It doesn't contain all nested resources in the path.
        // It should have been
        // @Proxy({SampleEntity1Resource.Fields.SAMPLE_ENTITY2, SampleEntity2Resource.Fields.SAMPLE_ENTITY3S})
        @Proxy({SampleEntity1Resource.Fields.SAMPLE_ENTITY2})
        @SuppressWarnings("unused")
        public List<SampleEntity3Resource> getInvalidProxyWithIncompletePath() {
            return sampleEntity2.getSampleEntity3s();
        }

        @Proxy({SampleEntity1Resource.Fields.SAMPLE_ENTITY2, SampleEntity2Resource.Fields.ID, "foobar"})
        @SuppressWarnings("unused")
        public String getInvalidProxyContainingNonResourceFieldInMiddle() {
            return sampleEntity2.getId();
        }

        interface ProjectionForValidProxyContainingAllResourcesReturningNonResourceData extends IdProjection {

            @SuppressWarnings("unused")
            String getValidProxyContainingAllResourcesReturningNonResourceData();
        }

        private interface ProjectionForValidProxyPointingToRenamedResource extends IdProjection {

            @SuppressWarnings("unused")
            List<SampleEntity3Resource.ProjectionWithNestedProjectionWithoutSpel> getValidProxyPointingToRenamedResource();
        }

        private interface ProjectionForValidProxyContainingSomeResourcesReturningNonResourceData extends IdProjection {

            @SuppressWarnings("unused")
            String getValidProxyContainingSomeResourcesReturningNonResourceData();
        }

        private interface ProjectionForInvalidProxyPointingToNonExistentField extends IdProjection {

            @SuppressWarnings("unused")
            String getInvalidProxyPointingToNonExistentField();
        }

        private interface ProjectionForInvalidProxyPointingToWrongResource extends IdProjection {

            @SuppressWarnings("unused")
            SampleEntity4Resource.Projection getInvalidProxyPointingToWrongResource();
        }

        private interface ProjectionForInvalidProxyWithIncompletePath extends IdProjection {

            @SuppressWarnings("unused")
            List<SampleEntity3Resource.ProjectionWithNestedProjectionWithoutSpel> getInvalidProxyWithIncompletePath();
        }

        private interface InvalidProjectionForValidProxyPointingToRenamedResource extends IdProjection {

            @SuppressWarnings("unused")
            List<SampleEntity4Resource.ProjectionContainingMethodNotExistingInAllOtherProjections> getValidProxyPointingToRenamedResource();
        }

        private interface ProjectionForInvalidProxyContainingNonResourceFieldInMiddle extends IdProjection {

            @SuppressWarnings("unused")
            String getInvalidProxyContainingNonResourceFieldInMiddle();
        }

        private interface ProjectionContainingSpelForValidProxyPointingToRenamedResource extends IdProjection {

            @SuppressWarnings("unused")
            List<SampleEntity3Resource.ProjectionWithSpel> getValidProxyPointingToRenamedResource();
        }
    }

    @Data
    @FieldNameConstants
    private static class SampleEntity2Resource implements BaseResource {

        private String id;

        @Renamed(SampleEntity2.Fields.SAMPLE_ENTITY3)
        private List<SampleEntity3Resource> sampleEntity3s;

        private SampleEntity4Resource sampleEntity4;
    }

    @Data
    private static class SampleEntity3Resource implements BaseResource {

        private String id;

        private SampleEntity4Resource sampleEntity41;

        private SampleEntity4Resource sampleEntity42;

        private interface ProjectionWithNestedProjectionWithoutSpel extends IdProjection {

            String getId();

            @SuppressWarnings("unused")
            SampleEntity4Resource.Projection getSampleEntity41();
        }

        private interface ProjectionWithSpel extends IdProjection {

            @Value("#{ systemProperties['user.region'] }")
            @SuppressWarnings("unused")
            String getDefaultLocale();
        }
    }

    @Data
    private static class SampleEntity4Resource implements BaseResource {

        private String id;

        private String name;

        private SampleEntity5Resource sampleEntity5;

        private interface Projection extends IdProjection {

        }

        // This projection contains a method that doesn't exist in any other projection. This means, when we apply
        // this projection to any other resource, there will be an error.
        private interface ProjectionContainingMethodNotExistingInAllOtherProjections extends IdProjection {

            String getName();
        }
    }

    @Data
    private static class SampleEntity5Resource implements BaseResource {

        private String name;
    }

    private interface IdProjection extends BaseProjection {

        @SuppressWarnings("unused")
        String getId();
    }
}
