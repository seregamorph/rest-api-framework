package com.seregamorph.restapi.test.base;

import static java.util.Collections.list;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import lombok.val;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;

class TestRestTemplateRest extends Rest {

    private final TestRestTemplate testRestTemplate;
    private final String[] authorities;

    TestRestTemplateRest(String rootEndpoint, ObjectMapper objectMapper, BaseSetup<?, ?> setup,
                         TestRestTemplate testRestTemplate, String[] authorities) {
        super(rootEndpoint, objectMapper, setup);
        this.testRestTemplate = testRestTemplate;
        this.authorities = authorities;
    }

    @Override
    public Rest withAuthorities(String... newAuthorities) {
        return new TestRestTemplateRest(getRootEndpoint(), getObjectMapper(), getSetup(),
                testRestTemplate, newAuthorities);
    }

    @Override
    public Rest withRootEndpoint(String rootEndpoint) {
        return new TestRestTemplateRest(rootEndpoint, getObjectMapper(), getSetup(),
                testRestTemplate, authorities);
    }

    @Override
    public RequestBuilderDelegate perform(MockHttpServletRequestBuilder requestBuilder) {
        val request = requestBuilder.buildRequest(null);
        val url = getUrl(request);
        val delegate = new TestRestTemplateRequestBuilderDelegate(this, getSetup(), authorities,
                testRestTemplate,
                HttpMethod.valueOf(Objects.requireNonNull(request.getMethod())), url);

        list(request.getHeaderNames()).forEach(headerName -> {
            delegate.header(headerName, list(request.getHeaders(headerName)).toArray());
        });

        if (request instanceof MockMultipartHttpServletRequest) {
            val multipartRequest = (MockMultipartHttpServletRequest) request;
            delegate.contentType = MULTIPART_FORM_DATA;
            val body = new LinkedMultiValueMap<String, Object>();
            multipartRequest.getMultiFileMap().forEach((name, files) -> {
                files.forEach(file -> {
                    val fileMap = new LinkedMultiValueMap<String, String>();
                    val contentDisposition = contentDisposition(name);
                    fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
                    HttpEntity<byte[]> fileEntity;
                    try {
                        fileEntity = new HttpEntity<>(file.getBytes(), fileMap);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }

                    body.add(name, fileEntity);
                });
            });
            delegate.content = body;
        }

        return delegate;
    }

    private static String getUrl(MockHttpServletRequest request) {
        val queryString = request.getQueryString();
        return request.getRequestURI() + (queryString == null ? "" : "?" + queryString);
    }

    private static String contentDisposition(String name) {
        return String.format("form-data; name=\"%s\"; filename=\"%s\"", name, name + "file");
    }

}
