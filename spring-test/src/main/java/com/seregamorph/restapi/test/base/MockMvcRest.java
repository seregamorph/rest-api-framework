package com.seregamorph.restapi.test.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seregamorph.restapi.test.TestContext;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import javax.servlet.ServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

class MockMvcRest extends Rest {

    private final MockMvc mockMvc;
    private final ServletContext servletContext;
    private final String[] authorities;

    MockMvcRest(String rootEndpoint, ObjectMapper objectMapper, BaseSetup<?, ?> setup,
                MockMvc mockMvc, ServletContext servletContext, String[] authorities) {
        super(rootEndpoint, objectMapper, setup);
        this.mockMvc = mockMvc;
        this.servletContext = servletContext;
        this.authorities = authorities;
    }

    @Override
    public Rest withRootEndpoint(String rootEndpoint) {
        return new MockMvcRest(rootEndpoint, getObjectMapper(), getSetup(),
                mockMvc, servletContext, authorities);
    }

    @Override
    public Rest withAuthorities(String... newAuthorities) {
        return new MockMvcRest(getRootEndpoint(), getObjectMapper(), getSetup(),
                mockMvc, servletContext, newAuthorities);
    }

    /**
     * Performs a mock HTTP request using the pre-built <code>requestBuilder</code>. Warning: This method
     * does <b>not</b> update {@link TestContext#getCurrentTest()} with the current HTTP method or path template.
     * @param requestBuilder the pre-built instance of {@link MockHttpServletRequestBuilder}.
     * @return an instance of {@link RequestBuilderDelegate}.
     */
    @Override
    public RequestBuilderDelegate perform(MockHttpServletRequestBuilder requestBuilder) {
        return new MockMvcRequestBuilderDelegate(this, getSetup(), authorities,
                mockMvc, servletContext, requestBuilder);
    }

}
