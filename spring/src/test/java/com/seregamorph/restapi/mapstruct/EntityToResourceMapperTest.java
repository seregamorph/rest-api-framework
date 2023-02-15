package com.seregamorph.restapi.mapstruct;

import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertEquals;

import com.seregamorph.restapi.base.BaseProjection;
import com.seregamorph.restapi.base.BaseResource;
import com.seregamorph.restapi.test.common.AbstractUnitTest;
import org.junit.Test;

public class EntityToResourceMapperTest extends AbstractUnitTest {

    @Test
    public void extractResourceClassShouldBeExpected() {
        SampleEntityToResourceMapper mapper = new SampleEntityToResourceMapper();

        assertEquals(SampleResource.class, mapper.extractResourceClass());
    }

    @Test
    public void mapWithResourceAndProjectionShouldInvokeGeneratedMethodAndReturnValue() {
        SampleEntityToResourceMapper mapper = new SampleEntityToResourceMapper();

        SampleResource result = mapper.map(new Sample(), SampleProjection.class);

        collector.checkThat(result, sameInstance(SampleEntityToResourceMapper.SAMPLE_RESOURCE));
    }

    static class SampleEntityToResourceMapper implements EntityToResourceMapper<Sample, SampleResource> {

        static final SampleResource SAMPLE_RESOURCE = new SampleResource();

        @Override
        public SampleResource map(Sample entity, CachedMappingContext cache) {
            return SAMPLE_RESOURCE;
        }
    }

    static class Sample {

    }

    static class SampleResource implements BaseResource {

    }

    interface SampleProjection extends BaseProjection {

    }
}
