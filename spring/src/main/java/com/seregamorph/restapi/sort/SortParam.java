package com.seregamorph.restapi.sort;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface SortParam {

    String[] value();

    DefaultField[] defaultSort() default {};

    @Documented
    @Retention(RUNTIME)
    @Target(PARAMETER)
    @interface DefaultField {

        String value();

        SortDirection direction() default SortDirection.ASC;
    }
}
