package com.seregamorph.restapi.exceptions;

import static org.springframework.http.HttpStatus.CONFLICT;

import java.util.Map;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(CONFLICT)
public class ConflictException extends RuntimeException {

    private static final long serialVersionUID = 1572238329549L;

    public static final String ERROR_TEMPLATE = "%s [%s] already exists.";

    public ConflictException(Class<?> resourceClass, Object... ids) {
        super(getMessage(resourceClass, ids));
    }

    public ConflictException(Class<?> resourceClass,
                             String idField1Name,
                             Object idField1Value,
                             String idField2Name,
                             Object idField2Value) {
        super(getMessage(resourceClass, idField1Name, idField1Value, idField2Name, idField2Value));
    }

    public ConflictException(Class<?> resourceClass, Map<String, Object> idFields) {
        super(getMessage(resourceClass, idFields));
    }

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }

    public static String getMessage(Class<?> resourceClass, Object... ids) {
        return ExceptionUtils.getMessage(ERROR_TEMPLATE, resourceClass, ids);
    }

    public static String getMessage(Class<?> resourceClass,
                                    String idField1Name,
                                    Object idField1Value,
                                    String idField2Name,
                                    Object idField2Value) {
        return ExceptionUtils.getMessage(ERROR_TEMPLATE, resourceClass,
                idField1Name, idField1Value,
                idField2Name, idField2Value);
    }

    public static String getMessage(Class<?> resourceClass, Map<String, Object> idFields) {
        return ExceptionUtils.getMessage(ERROR_TEMPLATE, resourceClass, idFields);
    }
}
