package com.seregamorph.restapi.exceptions;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(FORBIDDEN)
public class ForbiddenException extends RuntimeException {

    private static final long serialVersionUID = 1553222263844L;

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
