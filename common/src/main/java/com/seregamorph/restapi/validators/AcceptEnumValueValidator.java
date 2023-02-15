package com.seregamorph.restapi.validators;

import static com.seregamorph.restapi.validators.AcceptValidationUtils.isValueValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AcceptEnumValueValidator implements ConstraintValidator<Accept, Enum> {

    private String[] acceptableValues;
    private String defaultMessage;

    @Override
    public void initialize(Accept constraintAnnotation) {
        acceptableValues = constraintAnnotation.value();
        defaultMessage = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(Enum value, ConstraintValidatorContext context) {
        return isValueValid(value, Enum::name, acceptableValues, defaultMessage, context);
    }
}
