package com.seregamorph.restapi.test.base;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AliasFor;

@Target(TYPE)
@Retention(RUNTIME)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = "web.environment = embedded")
@Inherited
public @interface EmbeddedWebIT {

    @AliasFor(annotation = SpringBootTest.class, attribute = "classes")
    Class<?>[] classes() default {};

}
