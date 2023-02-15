package com.seregamorph.restapi.services.impl;

import com.seregamorph.restapi.base.BaseProjection;
import com.seregamorph.restapi.base.BaseResource;
import com.seregamorph.restapi.mapstruct.EntityToResourceMapper;
import com.seregamorph.restapi.services.ProjectionSupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ProjectionSupportServiceImpl implements ProjectionSupportService {

    private final ProjectionFactory projectionFactory;

    @Override
    public <E, R extends BaseResource, P extends BaseProjection> P toProjection(
            E entity,
            EntityToResourceMapper<E, R> mapper,
            Class<P> projectionClass
    ) {
        R resource = mapper.map(entity, projectionClass);
        return projectionFactory.createProjection(projectionClass, resource);
    }

    @Override
    public <R extends BaseResource, P extends BaseProjection> P toProjection(
            R resource,
            Class<P> projectionClass
    ) {
        return projectionFactory.createProjection(projectionClass, resource);
    }
}
