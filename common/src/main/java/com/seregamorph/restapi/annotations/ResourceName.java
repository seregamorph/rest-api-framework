package com.seregamorph.restapi.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Provides a custom name for a type. If the system needs to refer to a specific class, instead of using the class's
 * simple name or fully qualified name, it may decide to use the class's custom name instead. This may be helpful
 * in several cases, e.g. an exception is thrown out for a specific resource, and we want the exception message to be
 * user-friendly. For example, the message could be "User [1] can't be found" instead of "Uvcs_user [1] can't be found".
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface ResourceName {

    String value();
}
