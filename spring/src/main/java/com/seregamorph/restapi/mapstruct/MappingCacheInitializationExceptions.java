package com.seregamorph.restapi.mapstruct;

import java.lang.reflect.Method;
import lombok.experimental.UtilityClass;

@UtilityClass
class MappingCacheInitializationExceptions {

    private static final String ERROR_RETRIEVING_FIELD = "Error retrieving field [%s] from [%s].";
    private static final String ILLEGAL_METHOD = "Method [%s] is not a SpEL method "
            + "and doesn't exist in the resource class.";
    private static final String INVALID_CLASS = "Class [%s] is invalid: [%s]";
    private static final String INVALID_PROJECTION = "Projection [%s] for class [%s] is invalid: [%s]";
    private static final String INVALID_PROXY = "Invalid proxy [%s] annotated on method [%s] "
            + "defined in resource class [%s]: %s";
    private static final String INVALID_LEAF_MOST_RESOURCE_FIELD = "Expected leaf-most resource class [%s]; hit [%s].";
    private static final String HIT_NON_RESOURCE_FIELD = "Hit non-resource field at index [%d].";

    static MappingCacheInitializationException invalidClass(Class<?> clazz, Throwable cause) {
        String message = String.format(INVALID_CLASS, clazz.getName(), cause.getMessage());
        return new MappingCacheInitializationException(message, cause);
    }

    static MappingCacheInitializationException invalidProjection(Class<?> resourceClass,
                                                                 Class<?> projectionClass,
                                                                 Throwable cause) {
        String message = String.format(
                INVALID_PROJECTION,
                projectionClass.getName(),
                resourceClass.getName(),
                cause.getMessage());
        return new MappingCacheInitializationException(message, cause);
    }

    static MappingCacheInitializationException invalidProjectionMethod(Class<?> resourceClass, Method illegalMethod) {
        String error = String.format(ILLEGAL_METHOD, illegalMethod.getName());
        String message = String.format(
                INVALID_PROJECTION,
                illegalMethod.getDeclaringClass().getName(),
                resourceClass.getName(),
                error);
        return new MappingCacheInitializationException(message);
    }

    static MappingCacheInitializationException invalidProxy(Method proxyMethod, Throwable cause) {
        String message = String.format(
                INVALID_PROXY,
                extractProxyPath(proxyMethod),
                proxyMethod.getName(),
                proxyMethod.getDeclaringClass().getName(),
                cause.getMessage());
        return new MappingCacheInitializationException(message, cause);
    }

    static MappingCacheInitializationException proxyContainsNonResourceFieldInMiddle(Method proxyMethod, int index) {
        String error = String.format(HIT_NON_RESOURCE_FIELD, index);
        String message = String.format(
                INVALID_PROXY,
                extractProxyPath(proxyMethod),
                proxyMethod.getName(),
                proxyMethod.getDeclaringClass().getName(),
                error);
        return new MappingCacheInitializationException(message);
    }

    static MappingCacheInitializationException proxyPointsToWrongResource(Method proxyMethod,
                                                                          Class<?> expectedResourceClass,
                                                                          Class<?> actualResourceClass) {
        String error = String.format(INVALID_LEAF_MOST_RESOURCE_FIELD,
                expectedResourceClass.getName(),
                actualResourceClass.getName());
        String message = String.format(
                INVALID_PROXY,
                extractProxyPath(proxyMethod),
                proxyMethod.getName(),
                proxyMethod.getDeclaringClass().getName(),
                error);
        return new MappingCacheInitializationException(message);
    }

    static MappingCacheInitializationException errorRetrievingField(Class<?> clazz, String field) {
        String message = String.format(ERROR_RETRIEVING_FIELD, field, clazz.getName());
        return new MappingCacheInitializationException(message);
    }

    private static String extractProxyPath(Method proxyMethod) {
        Proxy proxy = proxyMethod.getAnnotation(Proxy.class);
        if (proxy == null) {
            throw new IllegalArgumentException(String.format("[%s] is not a proxy method.", proxyMethod.getName()));
        }
        return String.join(", ", proxy.value());
    }
}
