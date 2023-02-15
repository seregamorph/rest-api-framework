package com.seregamorph.restapi.test.base.setup.common;

import com.seregamorph.restapi.test.base.MockMvcTestSetup;
import com.seregamorph.restapi.test.base.StackTraceHolder;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import com.seregamorph.restapi.test.utils.WebTestUtils;
import java.util.Arrays;

public interface NamedExecution extends StackTraceHolder {

    static String buildExecutionEndpointPath(MockMvcTestSetup rootSetup, String pathTemplate, Object[] pathVariables) {
        String uri = pathTemplate;
        if (rootSetup.getEndpoint().contains("{")) {
            // controller mapping has parameters
            uri = rootSetup.getEndpoint() + pathTemplate;
        }
        if (uri.isEmpty()) {
            assert pathVariables.length == 0 : "pathVariables should be empty array: " + Arrays.toString(pathVariables);
            return uri;
        }
        return WebTestUtils.buildEndpoint(uri, pathVariables)
                .toString();
    }

    static String buildExecutionEndpointPath(MockMvcTestSetup rootSetup, BaseSetup<?, ?> setup) {
        return buildExecutionEndpointPath(rootSetup, setup.getPathTemplate(), setup.getPathVariables(false));
    }

    String getName(MockMvcTestSetup rootSetup, BaseSetup<?, ?> setup);
}
