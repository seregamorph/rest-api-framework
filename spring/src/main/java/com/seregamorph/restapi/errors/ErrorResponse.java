package com.seregamorph.restapi.errors;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.seregamorph.restapi.base.BasePayload;
import java.util.List;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorResponse implements BasePayload {

    @JsonInclude(NON_NULL)
    private String code;

    @JsonInclude(NON_NULL)
    private String message;

    public ErrorResponse() {
        // Intentionally left blank, necessary for json matching
    }

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorResponse(HttpStatus httpStatus, String message) {
        this(getErrorCode(httpStatus), message);
    }

    @JsonInclude(NON_EMPTY)
    private List<ObjectError> objectErrors;

    @JsonInclude(NON_EMPTY)
    private List<FieldError> fieldErrors;

    public static String getErrorCode(HttpStatus httpStatus) {
        return "HTTP-" + httpStatus.value();
    }
}
