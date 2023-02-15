package com.seregamorph.restapi.mapstruct;

import com.seregamorph.restapi.base.BaseMapper;
import com.seregamorph.restapi.base.BaseResource;
import org.mapstruct.Context;
import org.mapstruct.Named;

public interface ResourceToEntityMapper<R extends BaseResource, E> extends BaseMapper {

    E map(R resource, @Context CachedMappingContext cache);

    /**
     * Default = lazy map: no pre-population, but later manipulation is allowed
     * @param resource the resource.
     * @return the entity.
     */
    @Named("DefaultMap")
    default E map(R resource) {
        return map(resource, CachedMappingContext.instance());
    }

    /**
     * Clean map: No pre-population, later manipulation is not allowed.
     * @param resource the resource.
     * @return the entity.
     */
    @Named("CleanMap")
    @SuppressWarnings("unused")
    default E cleanMap(R resource) {
        return map(resource, CachedMappingContext.immutableInstance());
    }
}
