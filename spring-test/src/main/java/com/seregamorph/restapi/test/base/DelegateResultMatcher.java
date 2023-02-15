package com.seregamorph.restapi.test.base;

import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

@RequiredArgsConstructor
class DelegateResultMatcher implements ResultMatcher {

    @Nullable
    private final String message;
    private final ResultMatcher matcher;
    private final StackTraceElement[] trace;

    DelegateResultMatcher(ResultMatcher matcher, StackTraceHolder stackTraceHolder) {
        this(null, matcher, stackTraceHolder.getTrace());
    }

    @Override
    public void match(MvcResult result) throws Exception {
        try {
            matcher.match(result);
        } catch (AssertionError e) {
            // add diagnostic information to locate test configuration
            val error = new AssertionError(message);
            error.setStackTrace(trace);
            e.addSuppressed(error);
            throw e;
        }
    }
}
