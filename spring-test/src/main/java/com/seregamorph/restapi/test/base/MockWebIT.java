package com.seregamorph.restapi.test.base;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AliasFor;

@Target(TYPE)
@Retention(RUNTIME)
@AutoConfigureMockMvc
@SpringBootTest(properties = "web.environment = mock")
@Inherited
public @interface MockWebIT {

    @AliasFor(annotation = SpringBootTest.class, attribute = "classes")
    Class<?>[] classes() default {};

}
