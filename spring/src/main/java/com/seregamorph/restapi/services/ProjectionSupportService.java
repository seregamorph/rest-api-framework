package com.seregamorph.restapi.services;

import com.seregamorph.restapi.base.BaseProjection;
import com.seregamorph.restapi.base.BaseResource;
import com.seregamorph.restapi.mapstruct.EntityToResourceMapper;

public interface ProjectionSupportService {

    <E, R extends BaseResource, P extends BaseProjection> P toProjection(
            E entity,
            EntityToResourceMapper<E, R> mapper,
            Class<P> projectionClass
    );

    <R extends BaseResource, P extends BaseProjection> P toProjection(
            R resource,
            Class<P> projectionClass
    );
}
