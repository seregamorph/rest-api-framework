package com.seregamorph.restapi.demo.config;

import static com.seregamorph.restapi.exceptions.RestExceptionHandler.VALIDATION_FAILED;
import static com.seregamorph.restapi.test.base.JsonMatcher.jsonMatching;
import static com.seregamorph.restapi.test.base.JsonMatcher.matching;
import static com.seregamorph.restapi.test.base.ResultType.SINGLE;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.seregamorph.restapi.errors.ErrorResponse;
import com.seregamorph.restapi.errors.FieldError;
import com.seregamorph.restapi.partial.PayloadModel;
import com.seregamorph.restapi.test.config.spi.TestFrameworkConfig;
import java.util.Collections;
import org.springframework.test.web.servlet.ResultMatcher;

public class RestApiTestFrameworkConfig implements TestFrameworkConfig {

    @Override
    public String getDefaultMockUsername() {
        return "system";
    }

    @Override
    public boolean isDefaultAuthenticationRequired() {
        return false;
    }

    @Override
    public boolean isDefaultPaginationSupported() {
        return false;
    }

    @Override
    public ResultMatcher getRequiredMatcher(Class<?> resourceClass, String fieldName) {
        return SINGLE.matcherOf(jsonMatching(ErrorResponse.class)
                .setCode(ErrorResponse.getErrorCode(BAD_REQUEST))
                .setMessage(VALIDATION_FAILED)
                .setFieldErrors(Collections.singletonList(jsonMatching(FieldError.class)
                        .setField(matching(notNullValue()))
                        .setRejectedValue(matching(notNullValue()))
                        .setObjectName(matching(endsWith("Controller")))
                        .setCode(PayloadModel.class.getSimpleName())
                        .setMessage(matching(containsString(String.format(
                                "Fields [%s] are required for resource [%s]", fieldName, resourceClass.getSimpleName())))))));
    }

    @Override
    public ResultMatcher getRedundantMatcher(Class<?> resourceClass, String fieldName) {
        return SINGLE.matcherOf(jsonMatching(ErrorResponse.class)
                .setCode(ErrorResponse.getErrorCode(BAD_REQUEST))
                .setMessage(VALIDATION_FAILED)
                .setFieldErrors(Collections.singletonList(jsonMatching(FieldError.class)
                        .setField(matching(notNullValue()))
                        .setRejectedValue(matching(notNullValue()))
                        .setObjectName(matching(endsWith("Controller")))
                        .setCode(PayloadModel.class.getSimpleName())
                        .setMessage(matching(containsString(String.format(
                                "Fields [%s] are not allowed for resource [%s]", fieldName, resourceClass.getSimpleName())))))));
    }

    @Override
    public ResultMatcher getTypeMismatchMatcher(String errorMessage) {
        return SINGLE.matcherOf(jsonMatching(ErrorResponse.class)
                .setCode(ErrorResponse.getErrorCode(BAD_REQUEST))
                .setMessage(matching(startsWith(errorMessage))));
    }

    @Override
    public ResultMatcher getNotFoundMatcher(Object[] pathVariables) {
        return SINGLE.matcherOf(jsonMatching(ErrorResponse.class)
                .setCode(ErrorResponse.getErrorCode(NOT_FOUND))
                .setMessage(matching(notFoundDefaultMessageMatcher(pathVariables))));
    }

}
