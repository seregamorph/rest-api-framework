package com.seregamorph.restapi.test.base.request.support;

import com.seregamorph.restapi.base.BasePayload;
import com.seregamorph.restapi.test.base.support.ProjectionSupportDelegate;
import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nonnull;
import org.springframework.test.web.servlet.ResultMatcher;

public interface RequestResultMatcherSupportDelegate<P> {

    RequestResultMatcherSupport<P> getRequestResultMatcherSupport();

    default P setResultMatchers(BasePayload jsonMatchingPayload) {
        return getRequestResultMatcherSupport().setResultMatchers(jsonMatchingPayload);
    }

    default P setResultMatchers(ResultMatcher... matchers) {
        return setResultMatchers(Arrays.asList(matchers));
    }

    default P setResultMatchers(@Nonnull Collection<ResultMatcher> matchers) {
        return getRequestResultMatcherSupport().setResultMatchers(matchers);
    }

    default Collection<ResultMatcher> getResultMatchers(ProjectionSupportDelegate<?> setup) {
        return getRequestResultMatcherSupport().getResultMatchers(setup);
    }
}
