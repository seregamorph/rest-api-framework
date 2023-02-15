package com.seregamorph.restapi.validators;

import static com.seregamorph.restapi.validators.AcceptValidationUtils.isCollectionValid;

import java.util.Collection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AcceptEnumCollectionValidator implements ConstraintValidator<Accept, Collection<? extends Enum>> {

    private String[] acceptableValues;
    private String defaultMessage;

    @Override
    public void initialize(Accept constraintAnnotation) {
        acceptableValues = constraintAnnotation.value();
        defaultMessage = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Collection<? extends Enum> values, ConstraintValidatorContext context) {
        return isCollectionValid(values, Enum::name, acceptableValues, defaultMessage, context);
    }
}
