package com.seregamorph.restapi.mapstruct;

import static com.seregamorph.restapi.mapstruct.MappingCacheInitializationExceptions.errorRetrievingField;
import static com.seregamorph.restapi.mapstruct.MappingCacheInitializationExceptions.invalidClass;
import static com.seregamorph.restapi.mapstruct.MappingCacheInitializationExceptions.invalidProjection;
import static com.seregamorph.restapi.mapstruct.MappingCacheInitializationExceptions.invalidProjectionMethod;
import static com.seregamorph.restapi.mapstruct.MappingCacheInitializationExceptions.invalidProxy;
import static com.seregamorph.restapi.mapstruct.MappingCacheInitializationExceptions.proxyContainsNonResourceFieldInMiddle;
import static com.seregamorph.restapi.mapstruct.MappingCacheInitializationExceptions.proxyPointsToWrongResource;
import static com.seregamorph.restapi.utils.ClassUtils.extractField;
import static com.seregamorph.restapi.utils.ClassUtils.extractPropertyDescriptors;
import static com.seregamorph.restapi.utils.ClassUtils.getFieldValue;
import static com.seregamorph.restapi.utils.TypeUtils.extractElementClass;
import static org.springframework.beans.BeanUtils.instantiateClass;

import com.seregamorph.restapi.base.BaseProjection;
import com.seregamorph.restapi.base.BaseResource;
import com.seregamorph.restapi.base.IdProjection;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * Initializes a cache for the mapping. The general idea is to check the projection class
 * and if some fields are not present in projection - put dummy values for  these fields to the cache.
 * This allows to achieve a significant (up to 60x) performance boost, for example in case of lazy
 * Hibernate associations.
 */
@UtilityClass
@Slf4j
@SuppressWarnings("WeakerAccess")
public class MappingCacheInitializer {

    public static CachedMappingContext initializeImmutableCache(
            Object entityInstance,
            Class<? extends BaseResource> resourceClass
    ) {
        MappingCacheInitializationContext initContext = initialize(entityInstance, resourceClass);
        return initContext.buildImmutableCache();
    }

    public static CachedMappingContext initializeImmutableCache(
            Object entityInstance,
            Class<? extends BaseResource> resourceClass,
            Class<? extends BaseProjection> projectionClass
    ) {
        MappingCacheInitializationContext initContext = initialize(entityInstance, resourceClass, projectionClass);
        return initContext.buildImmutableCache();
    }

    public static CachedMappingContext initializeCache(
            Object entityInstance,
            Class<? extends BaseResource> resourceClass
    ) {
        MappingCacheInitializationContext initContext = initialize(entityInstance, resourceClass);
        return initContext.buildCache();
    }

    public static CachedMappingContext initializeCache(
            Object entityInstance,
            Class<? extends BaseResource> resourceClass,
            Class<? extends BaseProjection> projectionClass
    ) {
        MappingCacheInitializationContext initContext = initialize(entityInstance, resourceClass, projectionClass);
        return initContext.buildCache();
    }

    private static MappingCacheInitializationContext initialize(
            @NotNull
            Object entityInstance,
            @NotNull
            Class<? extends BaseResource> resourceClass
    ) {
        MappingCacheInitializationContext context = new MappingCacheInitializationContext();
        processClass(context, entityInstance, resourceClass);
        return context;
    }

    private static MappingCacheInitializationContext initialize(
            @NotNull
            Object entityInstance,
            @NotNull
            Class<? extends BaseResource> resourceClass,
            @NotNull
            Class<? extends BaseProjection> projectionClass
    ) {
        MappingCacheInitializationContext context = new MappingCacheInitializationContext();
        processClass(context, entityInstance, resourceClass, projectionClass);
        return context;
    }

    private static void processClass(
            @NotNull
            MappingCacheInitializationContext context,
            @NotNull
            Object entityInstance,
            @NotNull
            Class<? extends BaseResource> resourceClass
    ) {
        log.trace("Processing fields for [{}]...", resourceClass.getName());

        // The entity instance is obviously required.
        // Marking it required in advance helps in case of circular reference.
        context.markRequired(entityInstance);

        // Notice that fields from ancestor classes are included also
        List<Field> resourceFields = extractAllNestedResourceFieldList(resourceClass);

        for (Field resourceField : resourceFields) {
            Object entityFieldValue = getEntityFieldValue(entityInstance, resourceField);

            if (entityFieldValue == null) {
                continue;
            }

            Class<? extends BaseResource> nestedResourceClass = extractElementClass(resourceField)
                    .asSubclass(BaseResource.class);

            DefaultMappingProjection defaultProjection = resourceField.getAnnotation(DefaultMappingProjection.class);
            Class<?> nestedProjectionClass = defaultProjection == null ? IdProjection.class : defaultProjection.value();

            for (Object element : collectFieldValues(entityFieldValue)) {
                try {
                    processClass(context, element, nestedResourceClass, nestedProjectionClass);
                } catch (MappingCacheInitializationException e) {
                    throw invalidClass(resourceClass, e);
                }
            }
        }
    }

    private static void processClass(
            @NotNull
            MappingCacheInitializationContext context,
            @NotNull
            Object entityInstance,
            @NotNull
            Class<? extends BaseResource> resourceClass,
            @NotNull
            Class<?> projectionClass
    ) {
        log.trace("Processing fields for [{}] using projection [{}]...",
                resourceClass.getName(), projectionClass.getName());

        // The entity instance is obviously required.
        // Marking it required in advance helps in case of circular reference.
        context.markRequired(entityInstance);

        // First, mark all nested resource fields as redundant.
        // Notice: They may later be marked required if needed for the projection.
        List<Field> resourceFields = extractAllNestedResourceFieldList(resourceClass);

        for (Field resourceField : resourceFields) {
            Object cachedValue = instantiateClass(extractElementClass(resourceField));
            Object entityFieldValue = getEntityFieldValue(entityInstance, resourceField);

            if (entityFieldValue != null) {
                collectFieldValues(entityFieldValue)
                        .forEach(element -> context.cacheIfNotRequired(element, cachedValue));
            }
        }

        // Now check which fields are really required in the projection
        Collection<PropertyDescriptor> projectionProperties = extractPropertyDescriptors(projectionClass);

        for (PropertyDescriptor projectionProperty : projectionProperties) {
            Method resourceMethod = null;

            try {
                resourceMethod = resourceClass.getMethod(projectionProperty.getReadMethod().getName());
            } catch (NoSuchMethodException e) {
                if (projectionProperty.getReadMethod().getAnnotation(Value.class) == null) {
                    throw invalidProjectionMethod(resourceClass, projectionProperty.getReadMethod());
                }
            }

            Field resourceField = extractField(resourceClass, projectionProperty.getName());

            if (resourceField == null && resourceMethod != null) {
                processProxy(context, entityInstance, resourceMethod, projectionProperty.getReadMethod());
            } else if (resourceField != null) {
                Class<?> nestedClass = extractElementClass(resourceField);

                if (!BaseResource.class.isAssignableFrom(nestedClass)) {
                    continue;
                }

                // Field exists and is a resource. Process further.
                Class<? extends BaseResource> nestedResourceClass = nestedClass.asSubclass(BaseResource.class);
                Object entityFieldValue = getEntityFieldValue(entityInstance, resourceField);

                if (entityFieldValue == null) {
                    continue;
                }

                Class<?> nestedProjectionClass = extractElementClass(projectionProperty.getReadMethod());

                for (Object element : collectFieldValues(entityFieldValue)) {
                    try {
                        processClass(context, element, nestedResourceClass, nestedProjectionClass);
                    } catch (MappingCacheInitializationException e) {
                        throw invalidProjection(resourceClass, projectionClass, e);
                    }
                }
            }
        }
    }

    private static void processProxy(
            @NotNull
            MappingCacheInitializationContext context,
            @NotNull
            Object entityInstance,
            @NotNull
            Method resourceMethod,
            @NotNull
            Method projectionMethod
    ) {
        Proxy proxy = resourceMethod.getAnnotation(Proxy.class);

        if (proxy == null) {
            return;
        }

        Class<? extends BaseResource> nestedResourceClass = resourceMethod.getDeclaringClass()
                .asSubclass(BaseResource.class);
        Object nestedEntityFieldValue = entityInstance;

        for (int i = 0; i < proxy.value().length; ++i) {
            String resourceFieldName = proxy.value()[i];
            List<Field> nestedResourceFields = extractAllNestedResourceFieldList(nestedResourceClass);

            // First, mark all nested resource fields as redundant.
            for (Field nestedResourceField : nestedResourceFields) {
                Object entityFieldValue = getEntityFieldValue(nestedEntityFieldValue, nestedResourceField);
                if (entityFieldValue != null) {
                    Object cachedValue = instantiateClass(extractElementClass(nestedResourceField));
                    collectFieldValues(entityFieldValue)
                            .forEach(element -> context.cacheIfNotRequired(element, cachedValue));
                }
            }

            Field nestedResourceField = extractField(nestedResourceClass, resourceFieldName);

            if (nestedResourceField == null) {
                throw errorRetrievingField(nestedResourceClass, resourceFieldName);
            }

            Class<?> nestedResourceFieldType = extractElementClass(nestedResourceField);

            if (!BaseResource.class.isAssignableFrom(nestedResourceFieldType)) {
                if (i < proxy.value().length - 1) {
                    throw proxyContainsNonResourceFieldInMiddle(resourceMethod, i);
                }
                break;
            }

            nestedResourceClass = nestedResourceFieldType.asSubclass(BaseResource.class);
            nestedEntityFieldValue = getEntityFieldValue(nestedEntityFieldValue, nestedResourceField);

            if (nestedEntityFieldValue != null) {
                // Now, mark the target field as required
                collectFieldValues(nestedEntityFieldValue).forEach(context::markRequired);
            }
        }

        // The leaf most resource recognized by the proxy should be exactly the same as the method return type
        Class<?> returnType = extractElementClass(resourceMethod);

        if (BaseResource.class.isAssignableFrom(returnType) && returnType != nestedResourceClass) {
            throw proxyPointsToWrongResource(resourceMethod, returnType, nestedResourceClass);
        }

        // Continue processing further using the leaf-most nested resource class
        returnType = extractElementClass(projectionMethod);

        if (BaseProjection.class.isAssignableFrom(returnType)) {
            Class<? extends BaseProjection> nestedProjectionClass = returnType.asSubclass(BaseProjection.class);

            try {
                processClass(context, nestedEntityFieldValue, nestedResourceClass, nestedProjectionClass);
            } catch (MappingCacheInitializationException e) {
                throw invalidProxy(resourceMethod, e);
            }
        }
    }

    private static Collection<Object> collectFieldValues(@NotNull Object fieldValue) {
        if (fieldValue instanceof Collection) {
            Collection<?> collection = (Collection<?>) fieldValue;
            return collection.stream().filter(Objects::nonNull).collect(Collectors.toList());
        } else {
            return Collections.singletonList(fieldValue);
        }
    }

    private static List<Field> extractAllNestedResourceFieldList(@NotNull Class<? extends BaseResource> resourceClass) {
        // Notice that fields from ancestor classes are included also
        List<Field> allFields = FieldUtils.getAllFieldsList(resourceClass);
        List<Field> allNestedResourceFields = new ArrayList<>();

        for (Field resourceField : allFields) {
            if (Modifier.isStatic(resourceField.getModifiers()) || Modifier.isFinal(resourceField.getModifiers())) {
                continue;
            }

            Class<?> resourceFieldType = extractElementClass(resourceField);

            if (BaseResource.class.isAssignableFrom(resourceFieldType)) {
                allNestedResourceFields.add(resourceField);
            }
        }

        return allNestedResourceFields;
    }

    private static Object getEntityFieldValue(@NotNull Object entityInstance, @NotNull Field resourceField) {
        String entityFieldName = resourceField.getName();
        Renamed originalName = resourceField.getAnnotation(Renamed.class);

        if (originalName != null) {
            entityFieldName = originalName.value();
        }

        // Notice: A return value null can either mean the field doesn't exist in the entity class, or exist
        // with value null. Either way, we only process non null values.
        return extractField(entityInstance.getClass(), entityFieldName) == null
                ? null
                : getFieldValue(entityInstance, entityFieldName);
    }
}
