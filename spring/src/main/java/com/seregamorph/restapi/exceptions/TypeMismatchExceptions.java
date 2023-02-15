package com.seregamorph.restapi.exceptions;

import lombok.experimental.UtilityClass;
import org.springframework.beans.TypeMismatchException;

@UtilityClass
public class TypeMismatchExceptions {

    private static final String ERROR_MESSAGE_TEMPLATE = "Argument [%s] for parameter [%s] is invalid: [%s]";

    public static TypeMismatchException create(Class<?> requiredType, String parameterName, Object argumentValue,
                                               String errorMessage, Throwable cause) {
        return new TypeMismatchException(argumentValue, requiredType,
                new IllegalArgumentException(String.format(ERROR_MESSAGE_TEMPLATE,
                        argumentValue, parameterName, errorMessage), cause));
    }

    public static TypeMismatchException create(Class<?> requiredType, String parameterName, Object argumentValue,
                                               String errorMessage) {
        return create(requiredType, parameterName, argumentValue, errorMessage, null);
    }
}
