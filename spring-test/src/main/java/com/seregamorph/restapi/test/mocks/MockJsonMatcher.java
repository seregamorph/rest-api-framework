package com.seregamorph.restapi.test.mocks;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seregamorph.restapi.test.TestDescription;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@Data
@JsonDeserialize
class MockJsonMatcher {

    static final char SEPARATOR = '#';

    @JsonProperty("className")
    private Class<?> testClass;

    private List<String> methodGroups;

    private List<String> targetMethodGroups;

    /**
     * If this list is null or empty, then it means ALL methods in the specified class.
     */
    private List<String> includes;

    /**
     * If this list is null or empty, then it means NO methods in the specified class.
     */
    private List<String> excludes;

    void validate(List<String> errors) throws IllegalStateException {
        if (testClass == null) {
            errors.add("className is null");
            return;
        }

        if (CollectionUtils.isNotEmpty(includes) && includes.stream().anyMatch(StringUtils::isBlank)) {
            errors.add("includes section has blank string: " + includes);
        }

        if (CollectionUtils.isNotEmpty(excludes) && excludes.stream().anyMatch(StringUtils::isBlank)) {
            errors.add("excludes section has blank string: " + excludes);
        }

        List<String> methodNames = TestClassUtils.getTestMethodNames(testClass, true);
        List<String> cleanIncludes = removeExecutionIds(includes);

        if (CollectionUtils.isNotEmpty(cleanIncludes) && !CollectionUtils.containsAll(methodNames, cleanIncludes)) {
            errors.add("Included test method names " + includes + " not exist in " + getClassName());
        }

        if (CollectionUtils.isNotEmpty(excludes) && !CollectionUtils.containsAll(methodNames, excludes)) {
            errors.add("Excluded test method names " + includes + " not exist in " + getClassName());
        }

        if (isOverlapped(cleanIncludes, excludes)) {
            errors.add("Includes " + cleanIncludes + " and excludes " + excludes + " have intersections");
        }
    }

    /**
     * Test if this matcher matches the specified <code>testDescription</code>.
     */
    boolean matches(TestDescription testDescription) {
        // Check if class name matches
        if (!getTestClass().isAssignableFrom(testDescription.getDescription().getTestClass())) {
            return false;
        }

        // Check if method group matches
        if (CollectionUtils.isNotEmpty(methodGroups)
                && (StringUtils.isBlank(testDescription.getMethodGroup())
                || methodGroups.stream().noneMatch(testDescription.getMethodGroup()::equals))) {
            return false;
        }

        // Check if target method group matches
        if (CollectionUtils.isNotEmpty(targetMethodGroups)
                && (StringUtils.isBlank(testDescription.getTargetMethodGroup())
                || targetMethodGroups.stream().noneMatch(testDescription.getTargetMethodGroup()::equals))) {
            return false;
        }

        String methodName = testDescription.getDescription().getMethodName();

        // check if method matches
        if (CollectionUtils.isNotEmpty(excludes) && excludes.stream().anyMatch(methodName::equals)) {
            return false;
        }

        if (CollectionUtils.isNotEmpty(includes)) {
            for (String include : includes) {
                int index = include.indexOf(SEPARATOR);
                String includeMethod = index >= 0 ? include.substring(0, index) : include;
                String executionId = index >= 0 ? include.substring(index + 1) : null;
                if (methodName.equals(includeMethod)
                        && (StringUtils.isBlank(executionId) || executionId.equals(testDescription.getExecutionId()))) {
                    return true;
                }
            }

            return false;
        }

        return true;
    }

    public String toString() {
        return getClassName()
                + ", methodGroups: " + toString(methodGroups, true)
                + ", targetMethodGroups: " + toString(targetMethodGroups, true)
                + ", include: " + toString(includes, true)
                + ", exclude: " + toString(excludes, false);
    }

    private String getClassName() {
        return testClass == null ? "null" : testClass.getName();
    }

    private String toString(List<String> strings, boolean allIfEmpty) {
        StringBuilder builder = new StringBuilder();

        if (CollectionUtils.isEmpty(strings)) {
            builder.append("(").append(allIfEmpty ? "ALL" : "NONE").append(")");
        } else {
            builder.append("(").append(String.join(", ", strings)).append(")");
        }

        return builder.toString();
    }

    private static List<String> removeExecutionIds(List<String> methodsWithExecutionIds) {
        if (CollectionUtils.isEmpty(methodsWithExecutionIds)) {
            return methodsWithExecutionIds;
        }

        return methodsWithExecutionIds.stream()
                .map(element -> {
                    int index = element.indexOf(SEPARATOR);
                    return index > 0 ? element.substring(0, index) : element;
                })
                .map(element -> {
                    int index = element.indexOf('[');
                    return index > 0 ? element.substring(0, index) : element;
                })
                .collect(Collectors.toList());
    }

    private static <T> boolean isOverlapped(Collection<T> first, Collection<T> second) {
        if (CollectionUtils.isEmpty(first) || CollectionUtils.isEmpty(second)) {
            return false;
        }

        return first.stream().distinct().anyMatch(second::contains)
                || second.stream().distinct().anyMatch(first::contains);
    }
}
