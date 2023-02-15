package com.seregamorph.restapi.errors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;

@Data
@SuppressWarnings("WeakerAccess")
public class BulkOperationObjectError extends ObjectError {

    @JsonInclude(NON_NULL)
    private List<FieldError> fieldErrors;
}
