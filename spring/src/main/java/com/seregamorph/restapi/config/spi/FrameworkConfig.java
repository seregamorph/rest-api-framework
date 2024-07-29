package com.seregamorph.restapi.config.spi;

import com.seregamorph.restapi.search.SearchOperator;
import com.seregamorph.restapi.search.SearchValue;
import com.seregamorph.restapi.utils.RequestMappingUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

public interface FrameworkConfig {

    Set<String> DEFAULT_UPPERCASE_DESCRIPTIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "http",
            "id",
            "url",
            "ssh"
    )));

    @Nonnull
    default String getControllerMapping(Class<?> controllerClass) {
        return RequestMappingUtils.getControllerMapping(controllerClass);
    }

    default int getDefaultPageSize() {
        return 20;
    }

    default boolean isProjectionSupported() {
        return true;
    }

    default List<SearchOperator> getSupportedSearchOperators() {
        return Arrays.asList(SearchOperator.values());
    }

    default List<SearchValue> getSupportedSpecialSearchValues() {
        return Arrays.asList(SearchValue.values());
    }

    default boolean isSortSupported() {
        return true;
    }

    default boolean isUppercaseDescription(String word) {
        return DEFAULT_UPPERCASE_DESCRIPTIONS.contains(word);
    }
}
