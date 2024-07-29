package com.seregamorph.restapi.search;

import static com.seregamorph.restapi.search.SearchOperator.EQUAL;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface SearchParam {

    Field[] value() default {};

    SearchParam.DefaultField[] defaultSearch() default {};

    @Documented
    @Retention(RUNTIME)
    @Target(PARAMETER)
    @interface Field {

        String name();

        Class<?> type() default String.class;

        /**
         * Swagger description for field
         */
        String desc() default "";
    }

    @Documented
    @Retention(RUNTIME)
    @Target(PARAMETER)
    @interface DefaultField {

        String name();

        SearchOperator operator() default EQUAL;

        String[] value();
    }
}
