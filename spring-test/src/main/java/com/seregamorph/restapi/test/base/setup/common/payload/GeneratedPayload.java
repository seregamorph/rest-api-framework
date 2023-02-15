package com.seregamorph.restapi.test.base.setup.common.payload;

import com.seregamorph.restapi.test.base.AbstractStackTraceHolder;
import com.seregamorph.restapi.test.base.MockMvcTestSetup;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import com.seregamorph.restapi.test.base.setup.common.NamedExecution;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GeneratedPayload extends AbstractStackTraceHolder implements NamedExecution {

    private final Class<?> resourceClass;
    private final String fieldName;
    private final Object payload;

    @Override
    public String getName(MockMvcTestSetup rootSetup, BaseSetup<?, ?> setup) {
        return getResourceClass().getSimpleName() + "." + getFieldName();
    }

}
