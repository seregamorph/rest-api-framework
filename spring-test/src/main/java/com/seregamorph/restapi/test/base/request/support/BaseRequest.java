package com.seregamorph.restapi.test.base.request.support;

import com.seregamorph.restapi.test.base.AbstractStackTraceHolder;
import com.seregamorph.restapi.test.base.MockMvcTestSetup;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import com.seregamorph.restapi.test.base.setup.common.NamedExecution;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public abstract class BaseRequest<R extends BaseRequest<R>> extends AbstractStackTraceHolder implements NamedExecution {

    // A name for the extra request is required. This is not only to explain the purpose of the request, but
    // also to be used later for logging / debugging purpose, and to be used in mock service matchers.
    // The name is like a test method name, and performing an extra request is like executing an extra test method.

    @Getter
    private final String name;

    private Object[] pathVariables;

    private String[] authorities;

    public R setPathVariables(Object... pathVariables) {
        this.pathVariables = pathVariables;
        return self();
    }

    public Object[] getPathVariables(BaseSetup<?, ?> setup) {
        return pathVariables == null ? setup.getPathVariables() : pathVariables.clone();
    }

    public R setAuthorities(String... authorities) {
        this.authorities = authorities;
        return self();
    }

    public String[] getAuthorities(BaseSetup<?, ?> setup) {
        return authorities == null ? setup.getAuthorities() : authorities.clone();
    }

    @Override
    public String getName(MockMvcTestSetup rootSetup, BaseSetup<?, ?> setup) {
        return name;
    }

    @SuppressWarnings("unchecked")
    private R self() {
        return (R) this;
    }

    @Override
    public String toString() {
        return "Name: [" + name + "], "
                + "Path Variables: [" + StringUtils.join(pathVariables, ", ") + "], "
                + "Authorities: [" + StringUtils.join(authorities, ", ") + "]";
    }

}
