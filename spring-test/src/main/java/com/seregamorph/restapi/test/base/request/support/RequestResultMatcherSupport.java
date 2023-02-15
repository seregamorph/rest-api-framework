package com.seregamorph.restapi.test.base.request.support;

import static java.util.Collections.singleton;

import com.seregamorph.restapi.base.BasePayload;
import com.seregamorph.restapi.test.base.support.ProjectionSupportDelegate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.test.web.servlet.ResultMatcher;

@RequiredArgsConstructor
public class RequestResultMatcherSupport<P> {

    private final P parent;

    private Function<ProjectionSupportDelegate<?>, Collection<ResultMatcher>> resultMatchersFunction;

    public Collection<ResultMatcher> getResultMatchers(ProjectionSupportDelegate<?> setup) {
        return resultMatchersFunction == null ? setup.getDefaultResultMatchers() : resultMatchersFunction.apply(setup);
    }

    public P setResultMatchers(BasePayload jsonMatchingPayload) {
        // at the moment of the call we do not have the BaseSetup reference
        resultMatchersFunction = setup -> singleton(setup.getResultType()
                .matcherOf(jsonMatchingPayload));
        return parent;
    }

    @SuppressWarnings("WeakerAccess")
    public P setResultMatchers(@Nonnull Collection<ResultMatcher> matchers) {
        this.resultMatchersFunction = setup -> new HashSet<>(matchers);
        return this.parent;
    }

    @SuppressWarnings("unused")
    public P setResultMatchers(@Nonnull ResultMatcher... matchers) {
        return setResultMatchers(Arrays.asList(matchers));
    }

}
