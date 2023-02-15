package com.seregamorph.restapi.mapstruct;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the default projection interface for the data type class of a field that will be used
 * when mapping without any explicit projection.
 * WARNING: This projection is to be used in mapping APIs. Only nested resources / nested collections of resources
 * are considered. Whether the projection contains atomic fields or not doesn't matter.
 *
 * @see MappingCacheInitializer
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultMappingProjection {

    /**
     * The default projection class that will be used when mapping without any explicit projection.
     *
     * @return default projection class
     */
    Class<?> value();
}
