package com.seregamorph.restapi.mapstruct;

public class MappingCacheInitializationException extends RuntimeException {

    private static final long serialVersionUID = 1586332270565L;

    MappingCacheInitializationException(String message) {
        super(message);
    }

    MappingCacheInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
