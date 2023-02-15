package com.seregamorph.restapi.exceptions;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.Map;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(NOT_FOUND)
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1572238336993L;

    public static final String ERROR_TEMPLATE = "%s [%s] can't be found.";

    public NotFoundException(Class<?> resourceClass, Object... ids) {
        super(getMessage(resourceClass, ids));
    }

    public NotFoundException(Class<?> resourceClass,
                             String idField1Name,
                             Object idField1Value,
                             String idField2Name,
                             Object idField2Value) {
        super(getMessage(resourceClass, idField1Name, idField1Value, idField2Name, idField2Value));
    }

    public NotFoundException(Class<?> resourceClass, Map<String, Object> idFields) {
        super(getMessage(resourceClass, idFields));
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable throwable) {
        super(message, throwable);
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
