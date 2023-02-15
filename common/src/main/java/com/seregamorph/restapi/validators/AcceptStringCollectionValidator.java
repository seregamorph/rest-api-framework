package com.seregamorph.restapi.validators;

import static com.seregamorph.restapi.validators.AcceptValidationUtils.isCollectionValid;

import java.util.Collection;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AcceptStringCollectionValidator implements ConstraintValidator<Accept, Collection<String>> {

    private String[] acceptableValues;
    private String defaultMessage;

    @Override
    public void initialize(Accept constraintAnnotation) {
        acceptableValues = constraintAnnotation.value();
        defaultMessage = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Collection<String> values, ConstraintValidatorContext context) {
        return isCollectionValid(values, String::valueOf, acceptableValues, defaultMessage, context);
    }
}
