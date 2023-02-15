package com.seregamorph.restapi.mapstruct;

import com.seregamorph.restapi.base.BaseResource;
import com.seregamorph.restapi.base.IdResource;
import com.seregamorph.restapi.partial.PartialPayload;
import com.seregamorph.restapi.partial.PartialPayloadFactory;
import com.seregamorph.restapi.utils.ClassUtils;
import com.seregamorph.restapi.utils.RecursionPruner;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Stack;
import org.apache.commons.lang3.Validate;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;

/**
 * An internal cache which is supposed to be used as a Mapstruct's mapping context.
 */
@SuppressWarnings("WeakerAccess")
public class CachedMappingContext {

    // A stack of classes. If mapper M1 for resource A uses mapper M2 for resource B which uses mapper M3
    // for resource C, then these mappers can add A, B and C into the stack to record the mapping hierarchy.
    private final Stack<Class<?>> stack = new Stack<>();
    // A cache of mapping results. This cache can be pre-initialized to avoid mapping redundant objects,
    // or can be manipulated during the mapping process to avoid circular reference.
    private final Map<Object, Object> cache = new IdentityHashMap<>();
    // If this is null, then recursion pruning is not enabled.
    private final RecursionPruner recursionPruner;
    // True - Cache is populated upon construction and never grows or shrinks (immutable).
    // False - Cache is populated upon construction and can be manipulated later on (mutable).
    private final boolean immutable;

    private CachedMappingContext(Map<Object, Object> mappedObjects,
                                 RecursionPruner recursionPruner,
                                 boolean immutable) {
        Validate.isTrue(mappedObjects != null);
        mappedObjects.forEach(cache::put);
        this.immutable = immutable;
        this.recursionPruner = recursionPruner;
    }

    @BeforeMapping
    public <T> T getMappedInstance(Object source, @TargetType Class<T> targetType) {
        // We do not stack up the targetType here
        if (recursionPruner != null
                && BaseResource.class.isAssignableFrom(targetType)
                && stack.contains(targetType)
                && source != null) {
            return recursionPruner.map(source, targetType);
        }

        return targetType.cast(cache.get(source));
    }

    @BeforeMapping
    public void storeMappedInstance(Object source, @MappingTarget Object target, @TargetType Class<?> targetType) {
        // We stack up the targetType here
        // This is where the 'actual' mapping operation starts (see generated mapping logic for details)
        stack.add(targetType);
        if (!immutable) {
            cache.put(source, target);
        }
    }

    @AfterMapping
    public void afterMapping(@TargetType Class<?> targetType) {
        if (stack.isEmpty()) {
            return;
        }
        if (stack.peek() == targetType) {
            stack.pop();
        }
    }

    int size() {
        return cache.size();
    }

    /**
     * Constructs an empty immutable cache with recursion pruning disabled.
     * @return an immutable instance.
     */
    public static CachedMappingContext immutableInstance() {
        return new CachedMappingContext(Collections.emptyMap(), null, true);
    }

    /**
     * Constructs an immutable cache with recursion pruning disabled using the specified objects.
     * @param mappedObjects the objects to initialize the cache.
     * @return an immutable instance.
     */
    public static CachedMappingContext immutableInstance(Map<Object, Object> mappedObjects) {
        return new CachedMappingContext(mappedObjects, null, true);
    }

    /**
     * Constructs an empty mutable cache with recursion pruning enabled.
     * @return a mutable instance.
     */
    public static CachedMappingContext pruningInstance() {
        return new CachedMappingContext(Collections.emptyMap(), CachedMappingContext::defaultRecursionPruner, false);
    }

    /**
     * Constructs an empty mutable cache with recursion pruning enabled.
     * @return a mutable instance.
     */
    public static CachedMappingContext pruningInstance(RecursionPruner recursionPruner) {
        return new CachedMappingContext(Collections.emptyMap(), recursionPruner, false);
    }

    /**
     * Constructs an empty mutable cache with recursion pruning disabled.
     * @return a mutable instance.
     */
    public static CachedMappingContext instance() {
        return new CachedMappingContext(Collections.emptyMap(), null, false);
    }

    /**
     * Constructs a mutable cache with recursion pruning disabled using the specified objects.
     * @param mappedObjects the objects to initialize the cache.
     * @return a mutable instance.
     */
    public static CachedMappingContext instance(Map<Object, Object> mappedObjects) {
        return new CachedMappingContext(mappedObjects, null, false);
    }

    public static <T> T defaultRecursionPruner(Object source, Class<T> targetType) {
        if (!IdResource.class.isAssignableFrom(targetType)) {
            throw new IllegalStateException(
                    String.format("%s is not an instance of %s", targetType.getName(), IdResource.class.getName()));
        }

        try {
            Object fieldValue = ClassUtils.getFieldValue(source, IdResource.FIELD_ID);
            T result = targetType.cast(PartialPayloadFactory.partial(targetType.asSubclass(PartialPayload.class)));
            ClassUtils.setFieldValue(result, IdResource.FIELD_ID, fieldValue);
            return result;
        } catch (Exception e) {
            throw new IllegalStateException(
                    String.format("Unable to initialize an instance of %s with id", targetType.getName()), e);
        }
    }
}
