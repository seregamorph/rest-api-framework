package com.seregamorph.restapi.test.base;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({METHOD, TYPE})
@Retention(RUNTIME)
@Inherited
@Documented
public @interface InitTest {

    /**
     * Controller class, if set, the @InitTest annotation definition is a shortcut for call of
     * forController(ControllerClass.class)
     */
    Class<?> value() default Object.class;

}
