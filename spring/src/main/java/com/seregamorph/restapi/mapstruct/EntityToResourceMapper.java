package com.seregamorph.restapi.mapstruct;

import static com.seregamorph.restapi.mapstruct.MappingCacheInitializer.initializeCache;
import static com.seregamorph.restapi.mapstruct.MappingCacheInitializer.initializeImmutableCache;

import com.seregamorph.restapi.base.BaseProjection;
import com.seregamorph.restapi.base.BaseResource;
import com.seregamorph.restapi.base.PruningMapper;
import com.seregamorph.restapi.utils.RecursionPruner;
import org.mapstruct.Context;
import org.mapstruct.Named;
import org.springframework.data.util.ClassTypeInformation;

public interface EntityToResourceMapper<E, R extends BaseResource> extends PruningMapper<E, R> {

    R map(E entity, @Context CachedMappingContext cache);

    /**
     * Maps with a pre-populated cache that can still be manipulated later on.
     * @param entity the entity.
     * @return the resource.
     */
    @Named("DefaultMap")
    default R map(E entity) {
        CachedMappingContext cache = initializeCache(entity, extractResourceClass());
        return map(entity, cache);
    }

    /**
     * Maps with a pre-populated cache that can still be manipulated later on.
     * @param entity the entity.
     * @param projectionClass the projection class to pre-populate the cache.
     * @return the resource.
     */
    default <P extends BaseProjection> R map(E entity, Class<P> projectionClass) {
        CachedMappingContext cache = initializeCache(entity, extractResourceClass(), projectionClass);
        return map(entity, cache);
    }

    /**
     * Maps with a pre-populated cache that cannot be manipulated later on.
     * @param entity the entity.
     * @return the resource.
     */
    @Named("EagerMap")
    default R eagerMap(E entity) {
        CachedMappingContext cache = initializeImmutableCache(entity, extractResourceClass());
        return map(entity, cache);
    }

    /**
     * Maps with a pre-populated cache that cannot be manipulated later on.
     * @param entity the entity.
     * @param projectionClass the projection class to pre-populate the cache.
     * @return the resource.
     */
    default <P extends BaseProjection> R eagerMap(E entity, Class<P> projectionClass) {
        CachedMappingContext cache = initializeImmutableCache(entity, extractResourceClass(), projectionClass);
        return map(entity, cache);
    }

    /**
     * Maps with an empty cache that can still be manipulated later on.
     * @param entity the entity.
     * @return the resource.
     */
    @Named("LazyMap")
    default R lazyMap(E entity) {
        return map(entity, CachedMappingContext.instance());
    }

    // Reasons for separate mapping APIs for recursion pruning instead of supporting it in other APIs:
    // - `map` and `eagerMap` both pre-initialize the cache. The mapping results are NOT supposed to be
    // serialized to json directly - they are either used in business logic (e.g. UPDATE) or projected before
    // being serialized (which means infinite recursions are supposed to be eliminated by projections).
    // In theory, we could update logic for them so that they can also perform recursion pruning transparently,
    // but it's not necessary.
    // - `eagerMap` and `cleanMap` have immutable cache. If we do have infinite recursion, then the error
    // would happen at mapping stage, before the serialization stage.
    // - `lazyMap` is where infinite recursion may happen, not at mapping stage, but at serialization stage.
    // However, doing recursion pruning by default would change existing behaviors, and adding a new parameter
    // to turn on / off the pruning would remove the possibility of using method reference
    // (e.g. entities.stream().map(mapper::lazyMap).collect(toList())).
    // We therefore add new mapping methods instead.

    /**
     * Maps with an empty cache that can still be manipulated later on. Recursion of existing instances deep down
     * the hierarchy will be pruned.
     * @param entity the entity.
     * @return the resource.
     */
    @Named("PruningMap")
    @Override
    default R pruningMap(E entity) {
        CachedMappingContext cache = CachedMappingContext.pruningInstance();
        return map(entity, cache);
    }

    /**
     * Maps with an empty cache that can still be manipulated later on. Recursion of existing instances deep down
     * the hierarchy will be pruned.
     * @param entity the entity.
     * @param recursionPruner the recursion pruner.
     * @return the resource.
     */
    @Override
    default R pruningMap(E entity, RecursionPruner recursionPruner) {
        CachedMappingContext cache = CachedMappingContext.pruningInstance(recursionPruner);
        return map(entity, cache);
    }

    /**
     * Maps with an empty cache that cannot be manipulated later on.
     * @param entity the entity.
     * @return the resource.
     */
    @Named("CleanMap")
    default R cleanMap(E entity) {
        return map(entity, CachedMappingContext.immutableInstance());
    }

    default Class<? extends BaseResource> extractResourceClass() {
        Class<?> type = ClassTypeInformation.from(getClass())
                .getSuperTypeInformation(EntityToResourceMapper.class)
                .getTypeArguments()
                .get(1)
                .getType();
        return type.asSubclass(BaseResource.class);
    }
}
