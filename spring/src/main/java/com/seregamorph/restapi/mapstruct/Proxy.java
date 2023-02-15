package com.seregamorph.restapi.mapstruct;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method that it is the proxy of a field. E.g. If you have a method like this:
 * <pre>
 *     public String getUsername() {
 *         return this.user.getUsername();
 *     }
 * </pre>
 * then you need to annotate your method with <code>@Proxy("user")</code>.
 * @see MappingCacheInitializer
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("WeakerAccess")
public @interface Proxy {

    String[] value();
}
