package com.seregamorph.restapi.test.base.setup.common;

import com.seregamorph.restapi.test.base.AbstractStackTraceHolder;
import com.seregamorph.restapi.test.base.MockMvcTestSetup;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.test.web.servlet.ResultMatcher;

@RequiredArgsConstructor
@Getter
@ToString
public class VerifiablePayload extends AbstractStackTraceHolder implements NamedExecution {

    private final String name;
    private final Object payload;
    private final ResultMatcher statusMatcher;
    @ToString.Exclude
    private final ResultMatcher resultMatcher;

    @Override
    public String getName(MockMvcTestSetup rootSetup, BaseSetup<?, ?> setup) {
        return name;
    }

}
