package com.seregamorph.restapi.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks code snippet related to issues with backward/forward compatibility like spring framework version.
 */
@Retention(SOURCE)
@Target({METHOD, TYPE, FIELD})
@Documented
public @interface Compatibility {

    String value();
}
