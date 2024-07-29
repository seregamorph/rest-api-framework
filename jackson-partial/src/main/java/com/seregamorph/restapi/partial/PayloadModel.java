package com.seregamorph.restapi.partial;

import com.seregamorph.restapi.base.BasePartial;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = {
        PayloadModelValueValidator.class,
        PayloadModelCollectionValidator.class
})
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface PayloadModel {

    /**
     * The model interface class.
     */
    Class<?> value() default BasePartial.class;

    /**
     * Message returned on failed validation.
     */
    String message() default "";

    /**
     * @return the groups
     */
    Class<?>[] groups() default { };

    /**
     * @return the payload
     */
    Class<? extends Payload>[] payload() default { };
}
