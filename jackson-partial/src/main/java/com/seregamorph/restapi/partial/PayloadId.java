package com.seregamorph.restapi.partial;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the id fields for a payload class.
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PayloadId {

    /**
     * The field names.
     * @return the field names.
     */
    String[] value() default {};
}
