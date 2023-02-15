package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.test.utils.StandardValues.jsonObjects;
import static lombok.AccessLevel.PACKAGE;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seregamorph.restapi.test.TestContext;
import com.seregamorph.restapi.test.base.request.DeleteRequest;
import com.seregamorph.restapi.test.base.request.GetAllRequest;
import com.seregamorph.restapi.test.base.request.GetOneRequest;
import com.seregamorph.restapi.test.base.request.PatchRequest;
import com.seregamorph.restapi.test.base.request.PostRequest;
import com.seregamorph.restapi.test.base.request.PutRequest;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import com.seregamorph.restapi.test.base.setup.DeleteSetup;
import com.seregamorph.restapi.test.base.setup.GetAllSetup;
import com.seregamorph.restapi.test.base.setup.GetOneSetup;
import com.seregamorph.restapi.test.base.setup.PatchSetup;
import com.seregamorph.restapi.test.base.setup.PostSetup;
import com.seregamorph.restapi.test.base.setup.PutSetup;
import com.seregamorph.restapi.test.base.setup.common.VerifiablePathVariables;
import com.seregamorph.restapi.test.utils.WebTestUtils;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RequiredArgsConstructor
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class Rest {

    @Getter(PACKAGE)
    private final String rootEndpoint;

    @Getter
    private final ObjectMapper objectMapper;

    @Nullable
    @Getter(PACKAGE)
    private final BaseSetup<?, ?> setup;

    public abstract Rest withRootEndpoint(String rootEndpoint);

    public abstract Rest withAuthorities(String... authorities);

    public abstract RequestBuilderDelegate perform(MockHttpServletRequestBuilder requestBuilder);

    public RequestBuilderDelegate get(String pathTemplate, Object... pathVariables) {
        return perform(request(GET, pathTemplate, pathVariables));
    }

    public RequestBuilderDelegate get() {
        return get("");
    }

    RequestBuilderDelegate get(GetAllSetup setup) {
        return get(setup.getPathTemplate(), setup.getPathVariables());
    }

    RequestBuilderDelegate get(GetAllSetup setup, VerifiablePathVariables verifiablePathVariables) {
        return get(setup.getPathTemplate(), verifiablePathVariables.getPathVariables());
    }

    RequestBuilderDelegate get(GetAllSetup setup, GetAllRequest request) {
        return get(setup.getPathTemplate(), request.getPathVariables(setup));
    }

    RequestBuilderDelegate get(GetOneSetup setup) {
        return get(setup.getPathTemplate(), setup.getPathVariables());
    }

    RequestBuilderDelegate get(GetOneSetup setup, VerifiablePathVariables verifiablePathVariables) {
        return get(setup.getPathTemplate(), verifiablePathVariables.getPathVariables());
    }

    RequestBuilderDelegate get(GetOneSetup setup, GetOneRequest request) {
        return get(setup.getPathTemplate(), request.getPathVariables(setup));
    }

    public RequestBuilderDelegate post(String pathTemplate, Object... pathVariables) {
        return perform(request(POST, pathTemplate, pathVariables));
    }

    public RequestBuilderDelegate post() {
        return post("");
    }

    RequestBuilderDelegate post(PostSetup setup) {
        return post(setup.getPathTemplate(), setup.getPathVariables());
    }

    RequestBuilderDelegate post(PostSetup setup, VerifiablePathVariables verifiablePathVariables) {
        return post(setup.getPathTemplate(), verifiablePathVariables.getPathVariables());
    }

    RequestBuilderDelegate post(PostSetup setup, PostRequest request) {
        return post(setup.getPathTemplate(), request.getPathVariables(setup));
    }

    public RequestBuilderDelegate patch(String pathTemplate, Object... pathVariables) {
        return perform(request(PATCH, pathTemplate, pathVariables));
    }

    public RequestBuilderDelegate patch() {
        return patch("");
    }

    RequestBuilderDelegate patch(PatchSetup setup) {
        return patch(setup.getPathTemplate(), setup.getPathVariables());
    }

    RequestBuilderDelegate patch(PatchSetup setup, VerifiablePathVariables verifiablePathVariables) {
        return patch(setup.getPathTemplate(), verifiablePathVariables.getPathVariables());
    }

    RequestBuilderDelegate patch(PatchSetup setup, PatchRequest request) {
        return patch(setup.getPathTemplate(), request.getPathVariables(setup));
    }

    public RequestBuilderDelegate put(String pathTemplate, Object... pathVariables) {
        return perform(request(PUT, pathTemplate, pathVariables));
    }

    public RequestBuilderDelegate put() {
        return put("");
    }

    RequestBuilderDelegate put(PutSetup setup) {
        return put(setup.getPathTemplate(), setup.getPathVariables());
    }

    RequestBuilderDelegate put(PutSetup setup, VerifiablePathVariables verifiablePathVariables) {
        return put(setup.getPathTemplate(), verifiablePathVariables.getPathVariables());
    }

    RequestBuilderDelegate put(PutSetup setup, PutRequest request) {
        return put(setup.getPathTemplate(), request.getPathVariables(setup));
    }

    public RequestBuilderDelegate delete(String pathTemplate, Object... pathVariables) {
        return perform(request(DELETE, pathTemplate, pathVariables));
    }

    public RequestBuilderDelegate delete() {
        return delete("");
    }

    RequestBuilderDelegate delete(DeleteSetup setup) {
        return delete(setup.getPathTemplate(), setup.getPathVariables());
    }

    RequestBuilderDelegate delete(DeleteSetup setup, VerifiablePathVariables verifiablePathVariables) {
        return delete(setup.getPathTemplate(), verifiablePathVariables.getPathVariables());
    }

    RequestBuilderDelegate delete(DeleteSetup setup, DeleteRequest request) {
        return delete(setup.getPathTemplate(), request.getPathVariables(setup));
    }

    private MockHttpServletRequestBuilder request(HttpMethod httpMethod, String pathTemplate, Object[] pathVariables) {
        updateTestContext(httpMethod, pathTemplate);
        // almost the same as MockMvcRequestBuilders.request(HttpMethod, String, Object[]), but with more strict
        // validation of redundant path variable values
        val url = WebTestUtils.buildEndpoint(rootEndpoint + pathTemplate, jsonObjects(pathVariables));
        return MockMvcRequestBuilders.request(httpMethod, url);
    }

    private static void updateTestContext(HttpMethod httpMethod, String pathTemplate) {
        TestContext.getCurrentTest().setMethodGroup(httpMethod.name().toLowerCase());
        TestContext.getCurrentTest().setTargetMethodGroup(pathTemplate);
    }

}
