package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.common.Constants.HEADER_ACCEPT_PROJECTION;
import static com.seregamorph.restapi.common.Constants.PARAM_PROJECTION;
import static com.seregamorph.restapi.test.security.TestAuthorityUtils.doWithPermissions;

import com.seregamorph.restapi.annotations.Compatibility;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import com.seregamorph.restapi.test.config.MockMvcUtils;
import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import lombok.val;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.MultiValueMap;

class MockMvcRequestBuilderDelegate extends RequestBuilderDelegate {

    private final MockMvc mockMvc;
    private final ServletContext servletContext;
    private final MockHttpServletRequestBuilder requestBuilder;

    MockMvcRequestBuilderDelegate(MockMvcRest rest, @Nullable BaseSetup<?, ?> setup, String[] authorities,
                                  MockMvc mockMvc, ServletContext servletContext,
                                  MockHttpServletRequestBuilder requestBuilder) {
        super(rest, setup, authorities);
        this.mockMvc = mockMvc;
        this.servletContext = servletContext;
        this.requestBuilder = requestBuilder;
    }

    @Override
    public ResultActions andExpect(ResultMatcher resultMatcher) throws Exception {
        val authenticationKeyProvider = getAuthenticationKeyProvider();
        if (authenticationKeyProvider != null && authorities != null) {
            // Ask the provider what is the authentication key. Whether the authorities are empty
            // (= authorized, but no permissions), valid or invalid is to be handled by the provider.
            val authenticationKey = authenticationKeyProvider.getAuthKey(
                    rest.withAuthorities((String[]) null), authorities);
            requestBuilder.header(authenticationKeyProvider.getAuthHeader(), authenticationKey);
        }

        if (projection != null) {
            // HttpMethod.resolve(String) is not available in older versions of Spring
            if (HttpMethod.GET.name().equals(requestBuilder.buildRequest(servletContext).getMethod())) {
                requestBuilder.param(PARAM_PROJECTION, projection.name());
            } else {
                requestBuilder.header(HEADER_ACCEPT_PROJECTION, projection.name());
            }
        }

        putParams(requestBuilder, parameters);
        putHeaders(requestBuilder, headers);

        if (contentType != null) {
            requestBuilder.contentType(contentType);
        }
        if (content instanceof byte[]) {
            requestBuilder.content((byte[]) content);
        } else if (content instanceof String) {
            requestBuilder.content((String) content);
        } else if (content != null) {
            throw new IllegalStateException("Unexpected " + content);
        }

        try {
            // If anonymous (authorities == null) or custom authentication mechanism, do not setup Spring Security
            return authorities == null || authenticationKeyProvider != null
                    ? mockMvc.perform(requestBuilder).andExpect(resultMatcher)
                    : doWithPermissions(() -> mockMvc.perform(requestBuilder), authorities).andExpect(resultMatcher);
        } catch (Exception e) {
            // If MockMvc throws an unhandled exception, the request payload is not logged - hence print it manually
            MockMvcUtils.printRequest(requestBuilder.buildRequest(servletContext));
            throw e;
        }
    }

    @Compatibility("MockHttpServletRequestBuilder.params added only in 4.2.4")
    private static void putParams(MockHttpServletRequestBuilder requestBuilder, MultiValueMap<String, String> parameters) {
        parameters.forEach((name, values) -> requestBuilder.param(name, values.toArray(new String[0])));
    }

    private static void putHeaders(MockHttpServletRequestBuilder requestBuilder,
                                   MultiValueMap<String, String> headers) {
        headers.forEach((name, values) -> requestBuilder.header(name, values.toArray(new Object[0])));
    }

}
