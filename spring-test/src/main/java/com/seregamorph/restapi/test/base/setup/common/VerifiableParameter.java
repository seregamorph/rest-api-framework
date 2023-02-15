package com.seregamorph.restapi.test.base.setup.common;

import com.seregamorph.restapi.test.base.AbstractStackTraceHolder;
import com.seregamorph.restapi.test.base.MockMvcTestSetup;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import java.util.Collection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.test.web.servlet.ResultMatcher;

@RequiredArgsConstructor
@Getter
@ToString
public class VerifiableParameter extends AbstractStackTraceHolder implements NamedExecution {

    private final String name;
    private final Object parameterValue;
    @ToString.Exclude
    private final Collection<ResultMatcher> resultMatchers;

    @Override
    public String getName(MockMvcTestSetup rootSetup, BaseSetup<?, ?> setup) {
        return name + "=" + parameterValue;
    }

}
