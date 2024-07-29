package com.seregamorph.restapi.validators;

import static com.seregamorph.restapi.validators.AcceptValidationUtils.isArrayValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AcceptStringArrayValidator implements ConstraintValidator<Accept, String[]> {

    private String[] acceptableValues;
    private String defaultMessage;

    @Override
    public void initialize(Accept constraintAnnotation) {
        acceptableValues = constraintAnnotation.value();
        defaultMessage = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String[] values, ConstraintValidatorContext context) {
        return isArrayValid(values, String::valueOf, acceptableValues, defaultMessage, context);
    }
}
