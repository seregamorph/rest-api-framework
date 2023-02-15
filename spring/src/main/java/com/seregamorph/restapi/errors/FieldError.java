package com.seregamorph.restapi.errors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class FieldError extends ObjectError {

    @JsonInclude(NON_NULL)
    private String field;

    @JsonInclude(NON_NULL)
    private Object rejectedValue;

    @Override
    public FieldError setObjectName(String objectName) {
        super.setObjectName(objectName);
        return this;
    }

    @Override
    public FieldError setObjectId(Object objectId) {
        super.setObjectId(objectId);
        return this;
    }

    @Override
    public FieldError setCode(String code) {
        super.setCode(code);
        return this;
    }

    @Override
    public FieldError setMessage(String message) {
        super.setMessage(message);
        return this;
    }
}
