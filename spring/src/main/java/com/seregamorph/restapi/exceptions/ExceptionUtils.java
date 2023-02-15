package com.seregamorph.restapi.exceptions;

import static java.util.stream.Collectors.joining;

import com.seregamorph.restapi.annotations.ResourceName;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
class ExceptionUtils {

    private static final List<String> SUFFIXES = Collections.unmodifiableList(
            Arrays.asList("Resource", "Partial", "DTO", "Dto", "VO", "Vo"));

    static String getMessage(String errorTemplate, Class<?> resourceClass, Object... arguments) {
        ResourceName annotation = resourceClass.getAnnotation(ResourceName.class);
        String resourceName = annotation == null ? getDefaultResourceName(resourceClass) : annotation.value();
        return String.format(errorTemplate,
                resourceName,
                Arrays.stream(arguments).map(String::valueOf).collect(joining(", ")));
    }

    static String getMessage(String errorTemplate,
                             Class<?> resourceClass,
                             String idField1Name,
                             Object idField1Value,
                             String idField2Name,
                             Object idField2Value) {
        return getMessage(errorTemplate, resourceClass,
                getIdFieldArgument(idField1Name, idField1Value),
                getIdFieldArgument(idField2Name, idField2Value));
    }

    static String getMessage(String errorTemplate, Class<?> resourceClass, Map<String, Object> idFields) {
        Object[] arguments = idFields.entrySet()
                .stream()
                .map(entry -> getIdFieldArgument(entry.getKey(), entry.getValue()))
                .toArray(String[]::new);
        return getMessage(errorTemplate, resourceClass, arguments);
    }

    private static String getDefaultResourceName(Class<?> resourceClass) {
        String resourceName = resourceClass.getSimpleName();

        for (String suffix : SUFFIXES) {
            if (StringUtils.endsWith(resourceName, suffix) && resourceName.length() > suffix.length()) {
                return resourceName.substring(0, resourceName.length() - suffix.length());
            }
        }

        return resourceName;
    }

    private static String getIdFieldArgument(String name, Object value) {
        return name + " (" + value + ")";
    }
}
