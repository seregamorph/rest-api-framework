package com.seregamorph.restapi.validators;

import static com.seregamorph.restapi.validators.AcceptValidationUtils.isValueValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AcceptStringValueValidator implements ConstraintValidator<Accept, String> {

    private String[] acceptableValues;
    private String defaultMessage;

    @Override
    public void initialize(Accept constraintAnnotation) {
        acceptableValues = constraintAnnotation.value();
        defaultMessage = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return isValueValid(value, String::valueOf, acceptableValues, defaultMessage, context);
    }
}
