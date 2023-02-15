package com.seregamorph.restapi.test.utils;

import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.lang.Nullable;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@UtilityClass
public class WebTestUtils {

    public static URI localhostUri(String pathTemplate, Object... pathVariables) {
        assert pathTemplate.startsWith("/") : "pathTemplate should start with '/'";
        return buildEndpoint("http://localhost" + pathTemplate, pathVariables);
    }

    public static URI buildEndpoint(String pathTemplate, Object... pathVariables) {
        val variablesIterator = new VarArgsTemplateVariables(pathVariables);
        URI url;
        try {
            url = UriComponentsBuilder.fromUriString(pathTemplate)
                    .build().expand(variablesIterator)
                    .encode().toUri();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Missing required path variables to build template "
                    + "[" + pathTemplate + "] variables=" + Arrays.toString(pathVariables), e);
        }
        if (variablesIterator.isEmpty()) {
            return url;
        } else {
            throw new IllegalArgumentException("Redundant path variable to build template "
                    + "[" + pathTemplate + "] variables=" + Arrays.asList(pathVariables));
        }
    }

    private static class VarArgsTemplateVariables implements UriComponents.UriTemplateVariables {

        private final Iterator<Object> valueIterator;

        private VarArgsTemplateVariables(Object... uriVariableValues) {
            this.valueIterator = Arrays.asList(uriVariableValues).iterator();
        }

        boolean isEmpty() {
            return !valueIterator.hasNext();
        }

        @Override
        @Nullable
        public Object getValue(@Nullable String name) {
            if (!this.valueIterator.hasNext()) {
                throw new IllegalArgumentException("Not enough variable values available to expand '" + name + "'");
            }
            return this.valueIterator.next();
        }
    }

}
