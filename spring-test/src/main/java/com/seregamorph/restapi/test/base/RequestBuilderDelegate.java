package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.test.base.ResultMatchers.success;
import static com.seregamorph.restapi.test.utils.StandardValues.string;
import static com.seregamorph.restapi.test.utils.StandardValues.strings;
import static com.seregamorph.restapi.utils.ObjectUtils.singleOrCollection;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.seregamorph.restapi.base.BasePayload;
import com.seregamorph.restapi.base.ProjectionName;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloads;
import com.seregamorph.restapi.test.config.spi.AuthKeyProvider;
import com.seregamorph.restapi.test.config.spi.TestFrameworkConfigHolder;
import com.seregamorph.restapi.test.utils.StandardValues;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SuppressWarnings("WeakerAccess")
@RequiredArgsConstructor
public abstract class RequestBuilderDelegate {

    final MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
    final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

    final Rest rest;
    @Nullable
    private final BaseSetup<?, ?> setup;
    final String[] authorities;

    Enum<?> projection;
    MediaType contentType;
    /**
     * Either byte array or MultiValueMap with multipart
     */
    Object content;

    public RequestBuilderDelegate projection(Enum<? extends ProjectionName> projection) {
        this.projection = projection;
        return this;
    }

    public RequestBuilderDelegate param(String key, Object... values) {
        parameters.put(key, Arrays.asList(strings(values)));
        return this;
    }

    public RequestBuilderDelegate params(Map<String, ?> params) {
        params.forEach((key, value) -> {
            // note: Feign also passes repeated parameters as Collection
            if (value instanceof Collection) {
                parameters.put(key, ((Collection<?>) value).stream()
                        .map(StandardValues::string)
                        .collect(Collectors.toList()));
            } else {
                parameters.set(key, string(value));
            }
        });
        return this;
    }

    public RequestBuilderDelegate header(String key, Object... values) {
        headers.put(key, Arrays.asList(strings(values)));
        return this;
    }

    public RequestBuilderDelegate headers(Map<String, ?> headers) {
        headers.forEach((key, value) -> this.headers.set(key, string(value)));
        return this;
    }

    public RequestBuilderDelegate content(Object content) {
        val extractedContent = extractContent(content);
        if (isJson(extractedContent)) {
            this.contentType = APPLICATION_JSON;
            this.content = serialize(extractedContent);
        } else if ("".equals(extractedContent)) {
            // normally we should not pass strings as argument here, that's an exception
            this.contentType = APPLICATION_JSON;
            this.content = extractedContent;
        } else if (extractedContent != null) {
            throw new IllegalArgumentException("Unexpected content type " + extractedContent.getClass());
        }
        return this;
    }

    AuthKeyProvider getAuthenticationKeyProvider() {
        return setup == null ? TestFrameworkConfigHolder.getTestFrameworkConfig()
                .getAuthenticationKeyProvider() : setup.getAuthKeyProvider();
    }

    private static Object extractContent(Object content) {
        Object standardContent = singleOrCollection(content);

        if (standardContent instanceof Collection) {
            return ((Collection<?>) standardContent)
                    .stream()
                    .map(RequestBuilderDelegate::extractContent)
                    .collect(Collectors.toList());
        }

        if (GenericPayloads.isGenericPayloadProxy(standardContent)) {
            return GenericPayloads.genericPayloadOf(standardContent)
                    .getDefaultPayload();
        }

        return standardContent;
    }

    private byte[] serialize(Object json) {
        try {
            return rest.getObjectMapper().writeValueAsBytes(json);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public abstract ResultActions andExpect(ResultMatcher resultMatcher) throws Exception;

    public ResultActions andDo(ResultHandler resultHandler) throws Exception {
        return andExpect(success()).andDo(resultHandler);
    }

    public MvcResult andReturn() throws Exception {
        return andExpect(success()).andReturn();
    }

    private static boolean isJson(Object content) {
        return content instanceof BasePayload
                || content instanceof JsonNode
                || content instanceof Map
                || content instanceof Collection;
    }

}
