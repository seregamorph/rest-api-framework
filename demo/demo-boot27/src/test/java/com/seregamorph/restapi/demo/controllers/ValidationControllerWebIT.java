package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.demo.controllers.CommonResultMatchers.ofErrorResponse;
import static com.seregamorph.restapi.demo.controllers.ValidationController.HEADER_NUMBER;
import static com.seregamorph.restapi.demo.controllers.ValidationController.METHOD_PARAM_HEADER_NUMBER;
import static com.seregamorph.restapi.demo.controllers.ValidationController.PARAM_TIMESTAMP;
import static com.seregamorph.restapi.demo.controllers.ValidationController.QUERY_PARAM_NUMBER;
import static com.seregamorph.restapi.demo.resources.ValidationResource.MAX;
import static com.seregamorph.restapi.demo.resources.ValidationResource.MIN;
import static com.seregamorph.restapi.demo.utils.DemoConstants.SAMPLE_STRING;
import static com.seregamorph.restapi.exceptions.RestExceptionHandler.VALIDATION_FAILED;
import static com.seregamorph.restapi.test.base.JsonMatcher.jsonMatching;
import static com.seregamorph.restapi.test.base.JsonMatcher.matching;
import static com.seregamorph.restapi.test.base.ResultType.SINGLE;
import static org.hamcrest.Matchers.nullValue;

import com.google.common.collect.ImmutableMap;
import com.seregamorph.restapi.demo.resources.ValidationResource;
import com.seregamorph.restapi.errors.ErrorResponse;
import com.seregamorph.restapi.errors.FieldError;
import com.seregamorph.restapi.test.base.InitTest;
import com.seregamorph.restapi.test.base.setup.GetOneSetup;
import com.seregamorph.restapi.test.base.setup.PostSetup;
import com.seregamorph.restapi.test.base.support.RequestType;
import java.beans.Introspector;
import java.time.Instant;
import java.util.Collections;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultMatcher;

@InitTest(ValidationController.class)
public abstract class ValidationControllerWebIT extends AbstractBaseWebIT {

    private static final Instant TIMESTAMP = Instant.parse("2020-08-04T10:41:09Z");

    @InitTest
    public static GetOneSetup getOneSetup() {
        return new GetOneSetup("")
                .setDefaultResultMatchers(resultMatchers())
                .provideParameter(QUERY_PARAM_NUMBER, MAX, missingParameter(int.class, QUERY_PARAM_NUMBER))
                .provideParameter(PARAM_TIMESTAMP, TIMESTAMP, missingParameter(Instant.class, PARAM_TIMESTAMP))
                .provideHeader(HEADER_NUMBER, MAX, missingHeader(int.class, HEADER_NUMBER))
                .addBadParameterValue(QUERY_PARAM_NUMBER, SAMPLE_STRING, badDataTypeQueryParam(QUERY_PARAM_NUMBER))
                .addBadParameterValue(QUERY_PARAM_NUMBER, MIN - 1,
                        badMethodParameter(ValidationController.class.getSimpleName(),
                                QUERY_PARAM_NUMBER, MIN - 1, Min.class.getSimpleName(),
                                String.format("must be greater than or equal to %d", MIN)))
                .addBadParameterValue(QUERY_PARAM_NUMBER, MAX + 1,
                        badMethodParameter(ValidationController.class.getSimpleName(),
                                QUERY_PARAM_NUMBER, MAX + 1, Max.class.getSimpleName(),
                                String.format("must be less than or equal to %d", MAX)))
                .addBadParameterValue(PARAM_TIMESTAMP, SAMPLE_STRING, badFormatParameter(PARAM_TIMESTAMP))
                .addBadHeaderValue(HEADER_NUMBER, MIN - 1,
                        badHeaderParameter(ValidationController.class.getSimpleName(),
                                METHOD_PARAM_HEADER_NUMBER, MIN - 1, Min.class.getSimpleName(),
                                String.format("must be greater than or equal to %d", MIN)))
                .addBadHeaderValue(HEADER_NUMBER, MAX + 1,
                        badHeaderParameter(ValidationController.class.getSimpleName(),
                                METHOD_PARAM_HEADER_NUMBER, MAX + 1, Max.class.getSimpleName(),
                                String.format("must be less than or equal to %d", MAX)))
                .addBadHeaderValue(HEADER_NUMBER, SAMPLE_STRING, badDataTypeHeader(HEADER_NUMBER));
    }

    @InitTest
    public static PostSetup postSetup() {
        return new PostSetup()
                .setRequestPayload(new ValidationResource().setNumber(MIN).setTimestamp(Instant.now()))
                .setRequestType(RequestType.NO_RETRIEVAL)
                .putInvalidPayload("badDataType", invalidPostWithBadDataTypeValue(),
                        badDataTypePayloadField())
                .putInvalidPayload("tooSmallValue", invalidPostPayloadWithTooSmallValue(),
                        badMethodParameter(Introspector.decapitalize(ValidationResource.class.getSimpleName()),
                                ValidationResource.Fields.NUMBER, MIN - 1, Min.class.getSimpleName(),
                                String.format("must be greater than or equal to %d", MIN)))
                .putInvalidPayload("tooLargeValue", invalidPostPayloadWithTooLargeValue(),
                        badMethodParameter(Introspector.decapitalize(ValidationResource.class.getSimpleName()),
                                ValidationResource.Fields.NUMBER, MAX + 1, Max.class.getSimpleName(),
                                String.format("must be less than or equal to %d", MAX)))
                .putInvalidPayload("badFormat", invalidPostPayloadWithBadFormatValue(),
                        badFormatPayloadField());
    }

    private static Object invalidPostWithBadDataTypeValue() {
        return ImmutableMap.of(ValidationResource.Fields.NUMBER, SAMPLE_STRING);
    }

    private static Object invalidPostPayloadWithTooSmallValue() {
        return ImmutableMap.of(ValidationResource.Fields.NUMBER, MIN - 1);
    }

    private static Object invalidPostPayloadWithTooLargeValue() {
        return ImmutableMap.of(ValidationResource.Fields.NUMBER, MAX + 1);
    }

    private static Object invalidPostPayloadWithBadFormatValue() {
        return ImmutableMap.of(ValidationResource.Fields.TIMESTAMP, SAMPLE_STRING);
    }

    private static ValidationResource resultMatchers() {
        return jsonMatching(ValidationResource.class)
                .setId(matching(nullValue()))
                .setNumber(MAX)
                .setHeaderNumber(MAX)
                .setTimestamp(TIMESTAMP);
    }

    static ResultMatcher missingParameter(Class<?> dataType, String paramName) {
        val errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
                String.format("Required %s parameter '%s' is not present", dataType.getSimpleName(), paramName));
        return ofErrorResponse(errorResponse);
    }

    static ResultMatcher missingHeader(Class<?> dataType, String headerName) {
        val errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
                String.format("Missing request header '%s' for method parameter of type %s",
                        headerName, dataType.getSimpleName()));
        return ofErrorResponse(errorResponse);
    }

    static ResultMatcher badMethodParameter(String objectName,
                                            String paramName,
                                            Object paramValue,
                                            String code,
                                            String message) {
        // Either MethodArgumentNotValidException or ConstraintViolationException
        return SINGLE.matcherOf(jsonMatching(ErrorResponse.class)
                .setCode(ErrorResponse.getErrorCode(HttpStatus.BAD_REQUEST))
                .setMessage(VALIDATION_FAILED)
                .setFieldErrors(Collections.singletonList(jsonMatching(FieldError.class)
                        .setObjectName(objectName)
                        .setCode(code)
                        .setMessage(message)
                        .setField(paramName)
                        .setRejectedValue(paramValue))));
    }

    static ResultMatcher badHeaderParameter(String objectName,
                                            String headerName,
                                            Object headerValue,
                                            String code,
                                            String message) {
        // Either MethodArgumentNotValidException or ConstraintViolationException
        return SINGLE.matcherOf(jsonMatching(ErrorResponse.class)
                .setCode(ErrorResponse.getErrorCode(HttpStatus.BAD_REQUEST))
                .setMessage(VALIDATION_FAILED)
                .setFieldErrors(Collections.singletonList(jsonMatching(FieldError.class)
                        .setObjectName(objectName)
                        .setCode(code)
                        .setMessage(message)
                        .setField(headerName)
                        .setRejectedValue(headerValue))));
    }

    static ResultMatcher badDataTypeQueryParam(String parameterName) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
                String.format("Query parameter '" + parameterName + "': For input string: \"%s\"", SAMPLE_STRING));
        return ofErrorResponse(errorResponse);
    }

    static ResultMatcher badDataTypeHeader(String headerName) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
                String.format("Header '" + headerName + "': For input string: \"%s\"", SAMPLE_STRING));
        return ofErrorResponse(errorResponse);
    }

    static ResultMatcher badDataTypePayloadField() {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
                String.format("Cannot deserialize value of type `int` from String \"%s\": not a valid Integer value",
                        SAMPLE_STRING));
        return ofErrorResponse(errorResponse);
    }

    static ResultMatcher badFormatParameter(String parameterName) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
                String.format("Query parameter '" + parameterName + "': "
                        + "Parse attempt failed for value [%s]", SAMPLE_STRING));
        return ofErrorResponse(errorResponse);
    }

    static ResultMatcher badFormatPayloadField() {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST,
                String.format("Text '%s' could not be parsed at index 0", SAMPLE_STRING));
        return ofErrorResponse(errorResponse);
    }
}
