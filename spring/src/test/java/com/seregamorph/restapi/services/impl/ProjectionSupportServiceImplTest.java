package com.seregamorph.restapi.services.impl;

import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.seregamorph.restapi.mapstruct.EntityToResourceMapper;
import com.seregamorph.restapi.test.common.AbstractUnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.projection.ProjectionFactory;

@RunWith(MockitoJUnitRunner.class)
public class ProjectionSupportServiceImplTest extends AbstractUnitTest {

    @Mock
    private ProjectionFactory projectionFactory;

    @InjectMocks
    private ProjectionSupportServiceImpl service;

    @Test
    @SuppressWarnings("unchecked")
    public void toProjectionShouldMapAndCreateProjection() {
        SampleEntity entity = new SampleEntity();
        SampleResource resource = new SampleResource();
        SampleProjection projection = mock(SampleProjection.class);
        EntityToResourceMapper<SampleEntity, SampleResource> mapper = mock(EntityToResourceMapper.class);
        when(mapper.map(entity, SampleProjection.class)).thenReturn(resource);
        when(projectionFactory.createProjection(SampleProjection.class, resource)).thenReturn(projection);

        SampleProjection result = service.toProjection(entity, mapper, SampleProjection.class);

        collector.checkThat(result, sameInstance(projection));
    }

    @Test
    public void toProjectionShouldCreateProjection() {
        SampleResource resource = new SampleResource();
        SampleProjection projection = mock(SampleProjection.class);
        when(projectionFactory.createProjection(SampleProjection.class, resource)).thenReturn(projection);

        SampleProjection result = service.toProjection(resource, SampleProjection.class);

        collector.checkThat(result, sameInstance(projection));
    }
}
