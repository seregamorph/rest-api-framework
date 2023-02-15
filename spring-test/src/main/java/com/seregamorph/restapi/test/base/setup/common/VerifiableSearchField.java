package com.seregamorph.restapi.test.base.setup.common;

import com.seregamorph.restapi.search.SingleSearchCondition;
import com.seregamorph.restapi.test.base.AbstractStackTraceHolder;
import com.seregamorph.restapi.test.base.MockMvcTestSetup;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import java.util.Arrays;
import java.util.Collection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matcher;
import org.springframework.test.web.servlet.ResultMatcher;

@Getter
// This constructor is intentionally declared private as two matchers can't both be non null
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VerifiableSearchField extends AbstractStackTraceHolder implements NamedExecution {

    private final SingleSearchCondition searchCondition;
    private final Matcher<?> valueMatcher;
    private final Collection<ResultMatcher> resultMatchers;

    public VerifiableSearchField(SingleSearchCondition searchCondition) {
        this(searchCondition, null, null);
    }

    public VerifiableSearchField(SingleSearchCondition searchCondition, Matcher<?> valueMatcher) {
        this(searchCondition, valueMatcher, null);
    }

    public VerifiableSearchField(SingleSearchCondition searchCondition, ResultMatcher... resultMatchers) {
        this(searchCondition, Arrays.asList(resultMatchers));
    }

    public VerifiableSearchField(SingleSearchCondition searchCondition, Collection<ResultMatcher> resultMatchers) {
        this(searchCondition, null, resultMatchers);
    }

    @Override
    public String getName(MockMvcTestSetup rootSetup, BaseSetup<?, ?> setup) {
        return getSearchCondition().toString();
    }

}
