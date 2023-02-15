package com.seregamorph.restapi.test.config;

import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;

public class ResponseStatusValidator {

    public boolean shouldValidate(HandlerMethod handlerMethod) {
        return true;
    }

    public void validate(HandlerMethod handlerMethod, HttpStatus httpStatus) {
        val responseStatus = handlerMethod.getMethodAnnotation(ResponseStatus.class);
        if (responseStatus != null) {
            if (responseStatus.value() != httpStatus) {
                throw new AssertionError("Expected: @ResponseStatus(" + httpStatus.name() + ") "
                        + "on " + handlerMethod + " actual code is " + responseStatus.value());
            }
        } else {
            throw new AssertionError("Missing @ResponseStatus annotation on " + handlerMethod);
        }

    }

}
