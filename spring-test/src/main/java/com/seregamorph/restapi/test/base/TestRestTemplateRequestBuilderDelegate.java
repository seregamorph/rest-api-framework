package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.common.Constants.HEADER_ACCEPT_PROJECTION;
import static com.seregamorph.restapi.common.Constants.PARAM_PROJECTION;
import static com.seregamorph.restapi.test.filters.TestContextFilter.HEADER_TEST_AUTHORITIES;
import static org.springframework.http.HttpMethod.GET;

import com.seregamorph.restapi.test.base.setup.BaseSetup;
import java.net.URI;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

class TestRestTemplateRequestBuilderDelegate extends RequestBuilderDelegate {

    private final TestRestTemplate restTemplate;
    private final HttpMethod httpMethod;
    private final String url;

    TestRestTemplateRequestBuilderDelegate(TestRestTemplateRest rest, @Nullable BaseSetup<?, ?> setup, String[] authorities,
                                           TestRestTemplate restTemplate,
                                           HttpMethod httpMethod, String url) {
        super(rest, setup, authorities);
        this.restTemplate = restTemplate;
        this.httpMethod = httpMethod;
        this.url = url;
    }

    @Override
    public ResultActions andExpect(ResultMatcher resultMatcher) throws Exception {
        val uri = formatUri();

        val requestBuilder = RequestEntity.method(httpMethod, uri);
        headers.forEach((headerName, headerValues) -> {
            requestBuilder.header(headerName, headerValues.toArray(new String[0]));
        });
        if (projection != null && httpMethod != GET) {
            requestBuilder.header(HEADER_ACCEPT_PROJECTION, projection.name());
        }

        if (authorities != null) {
            val authenticationKeyProvider = getAuthenticationKeyProvider();
            if (authenticationKeyProvider == null) {
                requestBuilder.header(HEADER_TEST_AUTHORITIES, String.join(",", authorities));
            } else {
                val authenticationKey = authenticationKeyProvider.getAuthKey(
                        rest.withAuthorities((String[]) null), authorities);
                requestBuilder.header(authenticationKeyProvider.getAuthHeader(), authenticationKey);
            }
        }

        if (contentType != null) {
            requestBuilder.contentType(contentType);
        }
        RequestEntity<?> requestEntity;
        if (content == null) {
            requestEntity = requestBuilder.build();
        } else {
            requestEntity = requestBuilder.body(content);
        }

        val responseEntity = restTemplate.exchange(requestEntity, byte[].class);
        val mvcResult = new RestMvcResultAdapter(responseEntity);
        resultMatcher.match(mvcResult);
        return new ResultActions() {
            @Override
            public ResultActions andExpect(ResultMatcher matcher) throws Exception {
                matcher.match(mvcResult);
                return this;
            }

            @Override
            public ResultActions andDo(ResultHandler handler) throws Exception {
                handler.handle(mvcResult);
                return this;
            }

            @Override
            public MvcResult andReturn() {
                return mvcResult;
            }
        };
    }

    URI formatUri() {
        // we should separately format path variables and query parameters for correct escaping
        val uriBuilder = UriComponentsBuilder.fromUriString(url)
                .queryParams(parameters);
        if (projection != null && httpMethod == GET) {
            uriBuilder.queryParam(PARAM_PROJECTION, projection.name());
        }
        val uriPlus = uriBuilder.build(false).encode().toUri();
        // https://stackoverflow.com/questions/54294843/plus-sign-not-encoded-with-resttemplate-using-string-url-but-interpreted
        val strictlyEscapedQuery = StringUtils.replace(uriPlus.getRawQuery(), "+", "%2B");
        return UriComponentsBuilder.fromUri(uriPlus)
                .replaceQuery(strictlyEscapedQuery)
                .build(true).toUri();
    }

    @RequiredArgsConstructor
    private static class RestMvcResultAdapter implements MvcResult {

        private final ResponseEntity<byte[]> responseEntity;

        @Override
        public MockHttpServletRequest getRequest() {
            throw new UnsupportedOperationException();
        }

        @SneakyThrows
        @Override
        public MockHttpServletResponse getResponse() {
            val response = new MockHttpServletResponse();
            response.setStatus(responseEntity.getStatusCodeValue());
            responseEntity.getHeaders().forEach((name, values) -> {
                values.forEach(value -> response.addHeader(name, value));
            });
            val body = responseEntity.getBody();
            if (body != null) {
                response.getOutputStream().write(body);
            }
            return response;
        }

        @Override
        public Object getHandler() {
            throw new UnsupportedOperationException();
        }

        @Override
        public HandlerInterceptor[] getInterceptors() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ModelAndView getModelAndView() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Exception getResolvedException() {
            throw new UnsupportedOperationException();
        }

        @Override
        public FlashMap getFlashMap() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getAsyncResult() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getAsyncResult(long timeToWait) {
            throw new UnsupportedOperationException();
        }
    }

}
