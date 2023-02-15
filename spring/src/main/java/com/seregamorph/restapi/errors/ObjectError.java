package com.seregamorph.restapi.errors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.seregamorph.restapi.base.BasePayload;
import lombok.Data;

@Data
public class ObjectError implements BasePayload {

    @JsonInclude(NON_NULL)
    private String objectName;

    @JsonInclude(NON_NULL)
    private Object objectId;

    @JsonInclude(NON_NULL)
    private String code;

    @JsonInclude(NON_NULL)
    private String message;
}
