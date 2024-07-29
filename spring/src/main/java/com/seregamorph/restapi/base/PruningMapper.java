package com.seregamorph.restapi.base;

import com.seregamorph.restapi.utils.RecursionPruner;

/**
 * Base interface for mappers that prune recursion of instances down the hierarchy.
 * @param <E> source (typically an entity)
 * @param <R> target (typically a resource)
 */
public interface PruningMapper<E, R extends BaseResource> extends BaseMapper {

    R pruningMap(E entity);

    R pruningMap(E entity, RecursionPruner recursionPruner);
}
