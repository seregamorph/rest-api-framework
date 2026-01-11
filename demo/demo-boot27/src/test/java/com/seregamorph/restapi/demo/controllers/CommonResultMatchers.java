package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.test.base.JsonMatcher.jsonMatching;
import static com.seregamorph.restapi.test.base.ResultType.SINGLE;

import com.seregamorph.restapi.errors.ErrorResponse;
import lombok.experimental.UtilityClass;
import org.springframework.test.web.servlet.ResultMatcher;

@UtilityClass
class CommonResultMatchers {

    static ResultMatcher ofErrorResponse(ErrorResponse errorResponse) {
        return SINGLE.matcherOf(jsonMatching(ErrorResponse.class)
                .setCode(errorResponse.getCode())
                .setMessage(errorResponse.getMessage()));
    }

}
