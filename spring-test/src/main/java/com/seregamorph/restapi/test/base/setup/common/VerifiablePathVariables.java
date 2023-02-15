package com.seregamorph.restapi.test.base.setup.common;

import com.seregamorph.restapi.test.base.AbstractStackTraceHolder;
import com.seregamorph.restapi.test.base.MockMvcTestSetup;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import java.util.Collection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.test.web.servlet.ResultMatcher;

@Getter
@RequiredArgsConstructor
public class VerifiablePathVariables extends AbstractStackTraceHolder implements NamedExecution {

    private final Object[] pathVariables;
    private final Collection<ResultMatcher> resultMatchers;

    @Override
    public String getName(MockMvcTestSetup rootSetup, BaseSetup<?, ?> setup) {
        return NamedExecution.buildExecutionEndpointPath(rootSetup, setup.getPathTemplate(), pathVariables);
    }
}
