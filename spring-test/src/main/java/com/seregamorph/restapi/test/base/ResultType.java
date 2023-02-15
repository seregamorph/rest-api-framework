package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.test.base.JsonMatcher.jsonMatchersOf;

import com.seregamorph.restapi.base.BasePayload;
import org.springframework.test.web.servlet.ResultMatcher;

public enum ResultType {

    SINGLE, LIST, PAGE;

    public ResultMatcher matcherOf(JsonPayloadType direction, BasePayload proxy) {
        return ResultMatchers.of(direction, this, jsonMatchersOf(proxy));
    }

    public ResultMatcher matcherOf(BasePayload proxy) {
        return ResultMatchers.of(this, jsonMatchersOf(proxy));
    }
}
