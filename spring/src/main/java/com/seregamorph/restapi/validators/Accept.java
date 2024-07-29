package com.seregamorph.restapi.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = {
        AcceptEnumValueValidator.class,
        AcceptEnumArrayValidator.class,
        AcceptEnumCollectionValidator.class,
        AcceptStringValueValidator.class,
        AcceptStringArrayValidator.class,
        AcceptStringCollectionValidator.class
})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Accept {

    /**
     * Acceptable values.
     */
    String[] value();

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
