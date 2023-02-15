package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.demo.resources.AcceptConstants.FIFTH;
import static com.seregamorph.restapi.demo.resources.AcceptConstants.FIRST;
import static com.seregamorph.restapi.demo.resources.AcceptConstants.FOURTH;
import static com.seregamorph.restapi.demo.resources.AcceptConstants.SECOND;
import static com.seregamorph.restapi.demo.resources.AcceptConstants.THIRD;
import static com.seregamorph.restapi.demo.utils.ApiConstants.PARAM_ENUM_ARRAY;
import static com.seregamorph.restapi.demo.utils.ApiConstants.PARAM_ENUM_LIST;
import static com.seregamorph.restapi.demo.utils.ApiConstants.PARAM_SINGLE_ENUM;
import static com.seregamorph.restapi.demo.utils.ApiConstants.PARAM_SINGLE_STRING;
import static com.seregamorph.restapi.demo.utils.ApiConstants.PARAM_STRING_ARRAY;
import static com.seregamorph.restapi.demo.utils.ApiConstants.PARAM_STRING_LIST;
import static com.seregamorph.restapi.demo.utils.DemoConstants.SAMPLE_STRING;
import static com.seregamorph.restapi.exceptions.RestExceptionHandler.VALIDATION_FAILED;
import static com.seregamorph.restapi.test.base.JsonMatcher.jsonMatching;
import static com.seregamorph.restapi.test.base.JsonMatcher.matching;
import static com.seregamorph.restapi.test.base.ResultType.SINGLE;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.common.collect.ImmutableMap;
import com.seregamorph.restapi.demo.resources.AcceptEnum;
import com.seregamorph.restapi.demo.resources.AcceptResource;
import com.seregamorph.restapi.errors.ErrorResponse;
import com.seregamorph.restapi.errors.FieldError;
import com.seregamorph.restapi.test.base.InitTest;
import com.seregamorph.restapi.validators.Accept;
import java.beans.Introspector;
import java.util.Collections;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultMatcher;

@InitTest(AcceptController.class)
public abstract class AcceptControllerWebIT extends AbstractBaseWebIT {

    @Test
    public void getShouldAcceptValidSingleString() throws Exception {
        getShouldAcceptValidParam(PARAM_SINGLE_STRING, FIRST);
        getShouldAcceptValidParam(PARAM_SINGLE_STRING, SECOND);
        getShouldAcceptValidParam(PARAM_SINGLE_STRING, FOURTH);
    }

    @Test
    public void getShouldRejectInvalidSingleString() throws Exception {
        getShouldRejectInvalidParam(PARAM_SINGLE_STRING, THIRD, THIRD);
        getShouldRejectInvalidParam(PARAM_SINGLE_STRING, FIFTH, FIFTH);
        getShouldRejectInvalidParam(PARAM_SINGLE_STRING, SAMPLE_STRING, SAMPLE_STRING);
    }

    @Test
    public void getShouldAcceptValidStringArray() throws Exception {
        getShouldAcceptValidParam(PARAM_STRING_ARRAY, FIRST);
        getShouldAcceptValidParam(PARAM_STRING_ARRAY, SECOND);
        getShouldAcceptValidParam(PARAM_STRING_ARRAY, FOURTH);
    }

    @Test
    public void getShouldRejectInvalidStringArray() throws Exception {
        getShouldRejectInvalidParam(PARAM_STRING_ARRAY, THIRD, new String[] {THIRD});
        getShouldRejectInvalidParam(PARAM_STRING_ARRAY, FIFTH, new String[] {FIFTH});
        getShouldRejectInvalidParam(PARAM_STRING_ARRAY, SAMPLE_STRING, new String[] {SAMPLE_STRING});
    }

    @Test
    public void getShouldAcceptValidStringList() throws Exception {
        getShouldAcceptValidParam(PARAM_STRING_LIST, FIRST);
        getShouldAcceptValidParam(PARAM_STRING_LIST, SECOND);
        getShouldAcceptValidParam(PARAM_STRING_LIST, FOURTH);
    }

    @Test
    public void getShouldRejectInvalidStringList() throws Exception {
        getShouldRejectInvalidParam(PARAM_STRING_LIST, THIRD, new String[] {THIRD});
        getShouldRejectInvalidParam(PARAM_STRING_LIST, FIFTH, new String[] {FIFTH});
        getShouldRejectInvalidParam(PARAM_STRING_LIST, SAMPLE_STRING, new String[] {SAMPLE_STRING});
    }

    @Test
    public void getShouldAcceptValidSingleEnum() throws Exception {
        getShouldAcceptValidParam(PARAM_SINGLE_ENUM, FIRST);
        getShouldAcceptValidParam(PARAM_SINGLE_ENUM, SECOND);
        getShouldAcceptValidParam(PARAM_SINGLE_ENUM, FOURTH);
    }

    @Test
    public void getShouldRejectInvalidSingleEnum() throws Exception {
        getShouldRejectInvalidParam(PARAM_SINGLE_ENUM, THIRD, THIRD);
        getShouldRejectInvalidParam(PARAM_SINGLE_ENUM, FIFTH, FIFTH);
        getShouldRejectInvalidEnumParam(PARAM_SINGLE_ENUM);
    }

    @Test
    public void getShouldAcceptValidEnumArray() throws Exception {
        getShouldAcceptValidParam(PARAM_ENUM_ARRAY, FIRST);
        getShouldAcceptValidParam(PARAM_ENUM_ARRAY, SECOND);
        getShouldAcceptValidParam(PARAM_ENUM_ARRAY, FOURTH);
    }

    @Test
    public void getShouldRejectInvalidEnumArray() throws Exception {
        getShouldRejectInvalidParam(PARAM_ENUM_ARRAY, THIRD, new String[] {THIRD});
        getShouldRejectInvalidParam(PARAM_ENUM_ARRAY, FIFTH, new String[] {FIFTH});
        getShouldRejectInvalidEnumParam(PARAM_ENUM_ARRAY);
    }

    @Test
    public void getShouldAcceptValidEnumList() throws Exception {
        getShouldAcceptValidParam(PARAM_ENUM_LIST, FIRST);
        getShouldAcceptValidParam(PARAM_ENUM_LIST, SECOND);
        getShouldAcceptValidParam(PARAM_ENUM_LIST, FOURTH);
    }

    @Test
    public void getShouldRejectInvalidEnumList() throws Exception {
        getShouldRejectInvalidParam(PARAM_ENUM_LIST, THIRD, new String[] {THIRD});
        getShouldRejectInvalidParam(PARAM_ENUM_LIST, FIFTH, new String[] {FIFTH});
        getShouldRejectInvalidEnumParam(PARAM_ENUM_LIST);
    }

    @Test
    public void postShouldAcceptValidSingleString() throws Exception {
        postShouldAcceptValidPayload(AcceptResource.Fields.SINGLE_STRING, FIRST);
        postShouldAcceptValidPayload(AcceptResource.Fields.SINGLE_STRING, SECOND);
        postShouldAcceptValidPayload(AcceptResource.Fields.SINGLE_STRING, FOURTH);
    }

    @Test
    public void postShouldRejectInvalidSingleString() throws Exception {
        postShouldRejectInvalidPayload(AcceptResource.Fields.SINGLE_STRING, THIRD, THIRD);
        postShouldRejectInvalidPayload(AcceptResource.Fields.SINGLE_STRING, FIFTH, FIFTH);
        postShouldRejectInvalidPayload(AcceptResource.Fields.SINGLE_STRING, SAMPLE_STRING, SAMPLE_STRING);
    }

    @Test
    public void postShouldAcceptValidStringArray() throws Exception {
        postShouldAcceptValidPayload(AcceptResource.Fields.STRING_ARRAY, new String[] {FIRST});
        postShouldAcceptValidPayload(AcceptResource.Fields.STRING_ARRAY, new String[] {SECOND});
        postShouldAcceptValidPayload(AcceptResource.Fields.STRING_ARRAY, new String[] {FOURTH});
    }

    @Test
    public void postShouldRejectInvalidStringArray() throws Exception {
        postShouldRejectInvalidPayload(AcceptResource.Fields.STRING_ARRAY, THIRD, new String[] {THIRD});
        postShouldRejectInvalidPayload(AcceptResource.Fields.STRING_ARRAY, FIFTH, new String[] {FIFTH});
        postShouldRejectInvalidPayload(AcceptResource.Fields.STRING_ARRAY, SAMPLE_STRING, new String[] {SAMPLE_STRING});
    }

    @Test
    public void postShouldAcceptValidStringList() throws Exception {
        postShouldAcceptValidPayload(AcceptResource.Fields.STRING_LIST, new String[] {FIRST});
        postShouldAcceptValidPayload(AcceptResource.Fields.STRING_LIST, new String[] {SECOND});
        postShouldAcceptValidPayload(AcceptResource.Fields.STRING_LIST, new String[] {FOURTH});
    }

    @Test
    public void postShouldRejectInvalidStringList() throws Exception {
        postShouldRejectInvalidPayload(AcceptResource.Fields.STRING_LIST, THIRD, new String[] {THIRD});
        postShouldRejectInvalidPayload(AcceptResource.Fields.STRING_LIST, FIFTH, new String[] {FIFTH});
        postShouldRejectInvalidPayload(AcceptResource.Fields.STRING_LIST, SAMPLE_STRING, new String[] {SAMPLE_STRING});
    }

    @Test
    public void postShouldAcceptValidSingleEnum() throws Exception {
        postShouldAcceptValidPayload(AcceptResource.Fields.SINGLE_ENUM, FIRST);
        postShouldAcceptValidPayload(AcceptResource.Fields.SINGLE_ENUM, SECOND);
        postShouldAcceptValidPayload(AcceptResource.Fields.SINGLE_ENUM, FOURTH);
    }

    @Test
    public void postShouldRejectInvalidSingleEnum() throws Exception {
        postShouldRejectInvalidPayload(AcceptResource.Fields.SINGLE_ENUM, THIRD, THIRD);
        postShouldRejectInvalidPayload(AcceptResource.Fields.SINGLE_ENUM, FIFTH, FIFTH);
        postShouldRejectInvalidEnumPayload(AcceptResource.Fields.SINGLE_ENUM, SAMPLE_STRING);
    }

    @Test
    public void postShouldAcceptValidEnumArray() throws Exception {
        postShouldAcceptValidPayload(AcceptResource.Fields.ENUM_ARRAY, new String[] {FIRST});
        postShouldAcceptValidPayload(AcceptResource.Fields.ENUM_ARRAY, new String[] {SECOND});
        postShouldAcceptValidPayload(AcceptResource.Fields.ENUM_ARRAY, new String[] {FOURTH});
    }

    @Test
    public void postShouldRejectInvalidEnumArray() throws Exception {
        postShouldRejectInvalidPayload(AcceptResource.Fields.ENUM_ARRAY, THIRD, new String[] {THIRD});
        postShouldRejectInvalidPayload(AcceptResource.Fields.ENUM_ARRAY, FIFTH, new String[] {FIFTH});
        postShouldRejectInvalidEnumPayload(AcceptResource.Fields.ENUM_ARRAY, new String[] {SAMPLE_STRING});
    }

    @Test
    public void postShouldAcceptValidEnumList() throws Exception {
        postShouldAcceptValidPayload(AcceptResource.Fields.ENUM_LIST, new String[] {FIRST});
        postShouldAcceptValidPayload(AcceptResource.Fields.ENUM_LIST, new String[] {SECOND});
        postShouldAcceptValidPayload(AcceptResource.Fields.ENUM_LIST, new String[] {FOURTH});
    }

    @Test
    public void postShouldRejectInvalidEnumList() throws Exception {
        postShouldRejectInvalidPayload(AcceptResource.Fields.ENUM_LIST, THIRD, new String[] {THIRD});
        postShouldRejectInvalidPayload(AcceptResource.Fields.ENUM_LIST, FIFTH, new String[] {FIFTH});
        postShouldRejectInvalidEnumPayload(AcceptResource.Fields.ENUM_LIST, new String[] {SAMPLE_STRING});
    }

    private void getShouldAcceptValidParam(String paramName, String paramValue) throws Exception {
        rest
                .get()
                .param(paramName, paramValue)
                .andExpect(status().isOk());
    }

    private void getShouldRejectInvalidParam(String paramName, String paramValue, Object rejectedValue) throws Exception {
        rest
                .get()
                .param(paramName, paramValue)
                .andExpect(status().isBadRequest())
                .andExpect(paramResultMatchers(paramName, paramValue, rejectedValue));
    }

    private void postShouldAcceptValidPayload(String fieldName, Object fieldValue) throws Exception {
        rest
                .post()
                .content(ImmutableMap.of(fieldName, fieldValue))
                .andExpect(status().isOk());
    }

    private void postShouldRejectInvalidPayload(String fieldName, String fieldValue, Object rejectedValue) throws Exception {
        rest
                .post()
                .content(ImmutableMap.of(fieldName, rejectedValue))
                .andExpect(status().isBadRequest())
                .andExpect(resourceResultMatchers(fieldName, fieldValue, rejectedValue));
    }

    private void getShouldRejectInvalidEnumParam(String paramName) throws Exception {
        rest
                .get()
                .param(paramName, SAMPLE_STRING)
                .andExpect(status().isBadRequest())
                .andExpect(enumResultMatchers());
    }

    private void postShouldRejectInvalidEnumPayload(String fieldName, Object rejectedValue) throws Exception {
        rest
                .post()
                .content(ImmutableMap.of(fieldName, rejectedValue))
                .andExpect(status().isBadRequest())
                .andExpect(enumResourceResultMatchers());
    }

    private static ResultMatcher resultMatchers(String objectName,
                                                String fieldName,
                                                String value,
                                                Object rejectedValue) {
        return SINGLE.matcherOf(jsonMatching(ErrorResponse.class)
                .setCode(ErrorResponse.getErrorCode(HttpStatus.BAD_REQUEST))
                .setMessage(VALIDATION_FAILED)
                .setFieldErrors(Collections.singletonList(jsonMatching(FieldError.class)
                        .setField(fieldName)
                        .setRejectedValue(rejectedValue)
                        .setObjectName(objectName)
                        .setCode(Accept.class.getSimpleName())
                        .setMessage(String.format(
                                "Invalid value %s. Acceptable values: %s, %s, %s", value, FIRST, SECOND, FOURTH)))));
    }

    private static ResultMatcher paramResultMatchers(String fieldName, String value, Object rejectedValue) {
        return resultMatchers(AcceptController.class.getSimpleName(), fieldName, value, rejectedValue);
    }

    private static ResultMatcher enumResultMatchers() {
        return SINGLE.matcherOf(jsonMatching(ErrorResponse.class)
                .setCode(ErrorResponse.getErrorCode(HttpStatus.BAD_REQUEST))
                .setMessage(matching(endsWith(
                        String.format("No enum constant %s.%s", AcceptEnum.class.getName(), SAMPLE_STRING)))));
    }

    private static ResultMatcher resourceResultMatchers(String fieldName, String value, Object rejectedValue) {
        return resultMatchers(Introspector.decapitalize(AcceptResource.class.getSimpleName()),
                fieldName, value, rejectedValue);
    }

    private static ResultMatcher enumResourceResultMatchers() {
        return SINGLE.matcherOf(jsonMatching(ErrorResponse.class)
                .setCode(ErrorResponse.getErrorCode(HttpStatus.BAD_REQUEST))
                .setMessage(
                        String.format("Cannot deserialize value of type `%s` from String \"%s\": "
                                        + "not one of the values accepted for Enum class: [%s, %s, %s, %s, %s]",
                                AcceptEnum.class.getName(),
                                SAMPLE_STRING, AcceptEnum.FIRST, AcceptEnum.FOURTH, AcceptEnum.SECOND,
                                AcceptEnum.THIRD, AcceptEnum.FIFTH)));
    }
}
