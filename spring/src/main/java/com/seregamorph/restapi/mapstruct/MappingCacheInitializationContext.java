package com.seregamorph.restapi.mapstruct;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class MappingCacheInitializationContext {

    private final Map<Object, Object> skippedObjects = new HashMap<>();
    private final Set<Object> requiredObjects = new HashSet<>();

    void markRequired(Object object) {
        skippedObjects.remove(object);
        requiredObjects.add(object);
    }

    void cacheIfNotRequired(Object object, Object premappedValue) {
        if (!requiredObjects.contains(object)) {
            skippedObjects.put(object, premappedValue);
        }
    }

    CachedMappingContext buildImmutableCache() {
        return CachedMappingContext.immutableInstance(skippedObjects);
    }

    CachedMappingContext buildCache() {
        return CachedMappingContext.instance(skippedObjects);
    }
}
