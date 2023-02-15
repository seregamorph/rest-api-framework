package com.seregamorph.restapi.test.base.setup.common;

import com.seregamorph.restapi.search.Search;
import com.seregamorph.restapi.test.base.AbstractStackTraceHolder;
import com.seregamorph.restapi.test.base.MockMvcTestSetup;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import java.util.Arrays;
import java.util.Collection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.test.web.servlet.ResultMatcher;

@Getter
@RequiredArgsConstructor
@ToString
public class VerifiableSearch extends AbstractStackTraceHolder implements NamedExecution {

    private final String search;
    @ToString.Exclude
    private final Collection<ResultMatcher> resultMatchers;

    public VerifiableSearch(Search search, ResultMatcher... resultMatchers) {
        this(search, Arrays.asList(resultMatchers));
    }

    public VerifiableSearch(Search search, Collection<ResultMatcher> resultMatchers) {
        this.search = search.toString();
        this.resultMatchers = resultMatchers;
    }

    @Override
    public String getName(MockMvcTestSetup rootSetup, BaseSetup<?, ?> setup) {
        return getSearch();
    }

}
