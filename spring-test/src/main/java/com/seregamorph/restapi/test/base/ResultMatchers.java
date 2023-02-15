package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.test.base.JsonPayloadType.REQUEST;
import static com.seregamorph.restapi.test.base.JsonPayloadType.RESPONSE;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.seregamorph.restapi.test.JsonConstants;
import com.seregamorph.restapi.test.config.MockMvcUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;
import org.junit.runners.model.MultipleFailureException;
import org.springframework.test.util.JsonPathExpectationsHelper;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Slf4j
@UtilityClass
public class ResultMatchers {

    private static final int ERRORS_THRESHOLD = 1;

    public static ResultMatcher collect(@Nullable Collection<? extends ResultMatcher> resultMatchers) {
        if (resultMatchers == null) {
            return success();
        }

        if (resultMatchers.size() == 1) {
            return resultMatchers.iterator().next();
        }

        return mvcResult -> {
            List<Throwable> errors = new ArrayList<>();
            for (val resultMatcher : resultMatchers) {
                try {
                    resultMatcher.match(mvcResult);
                } catch (AssertionError e) {
                    errors.add(e);
                }
            }
            // to avoid log pollution, all exception except first ERRORS_THRESHOLD
            // are accumulated to a single error
            List<Throwable> multiErrors = errors;
            if (errors.size() > ERRORS_THRESHOLD) {
                multiErrors = new ArrayList<>(errors.subList(0, ERRORS_THRESHOLD));

                val messages = errors.subList(ERRORS_THRESHOLD, errors.size()).stream()
                        .map(Throwable::getMessage)
                        .collect(Collectors.joining(System.lineSeparator()));
                multiErrors.add(new AssertionError(messages));
            }
            MultipleFailureException.assertEmpty(multiErrors);
        };
    }

    public static ResultMatcher collect(@Nullable ResultMatcher... resultMatchers) {
        return collect(resultMatchers == null ? null : Arrays.asList(resultMatchers));
    }

    static ResultMatcher success() {
        return mvcResult -> {
        };
    }

    public static ResultMatcher of(JsonMatcher... jsonMatchers) {
        List<ResultMatcher> resultMatchers = new ArrayList<>();

        log.trace("Creating matchers...");
        processAll(RESPONSE, resultMatchers, null, null, new Throwable().getStackTrace(), jsonMatchers);

        return collect(resultMatchers);
    }

    public static ResultMatcher of(ResultType resultType, JsonMatcher... jsonMatchers) {
        return of(RESPONSE, resultType, jsonMatchers);
    }

    public static ResultMatcher of(JsonPayloadType payloadType, ResultType resultType, JsonMatcher... jsonMatchers) {
        String path;
        Integer index;

        switch (resultType) {
            case LIST:
                path = JsonConstants.ROOT;
                index = 0;
                break;
            case PAGE:
                path = JsonConstants.ROOT_CONTENT;
                index = 0;
                break;
            default:
                path = JsonConstants.ROOT;
                index = null;
        }

        List<ResultMatcher> resultMatchers = new ArrayList<>();

        log.trace("Creating matchers...");
        processAll(payloadType, resultMatchers, path, index, new Throwable().getStackTrace(), jsonMatchers);

        return collect(resultMatchers);
    }

    public static ResultMatcher of(ResultType resultType, Collection<JsonMatcher> jsonMatchers) {
        return of(RESPONSE, resultType, jsonMatchers);
    }

    public static ResultMatcher of(JsonPayloadType payloadType, ResultType resultType,
                                   Collection<JsonMatcher> jsonMatchers) {
        return of(payloadType, resultType, jsonMatchers.toArray(new JsonMatcher[0]));
    }

    private static void processAll(JsonPayloadType payloadType,
                                   Collection<ResultMatcher> resultMatchers,
                                   String currentPath,
                                   Integer currentIndex,
                                   StackTraceElement[] currentTrace,
                                   JsonMatcher... nextJsonMatchers) {
        for (JsonMatcher nextJsonMatcher : nextJsonMatchers) {
            process(payloadType, resultMatchers, currentPath, currentIndex, nextJsonMatcher);
        }

        if (StringUtils.isBlank(currentPath)) {
            return;
        }

        // Strong assertion - no redundancy, nothing missing

        // Element matchers may contain index or matchers to verify the number of their children
        // In that case, we count only once
        Set<String> uniquePaths = new LinkedHashSet<>();

        for (JsonMatcher nextJsonMatcher : nextJsonMatchers) {
            // If child path is not null, we don't care about index of child path. E.g. Matcher for child path /
            // child index may be a hasSize(size) (index = null), or a matcher for each element of a collection
            // (index != null, loop). Either way, we count only the child path.
            // But if child path is null, we need to count the index. E.g. In the case current path points to
            // a collection, and all child paths has path = null and index != null -> need to count the indices.
            if (nextJsonMatcher.getPath() != null || nextJsonMatcher.getIndex() != null) {
                uniquePaths.add(defaultIfNull(nextJsonMatcher.getPath(), String.valueOf(nextJsonMatcher.getIndex())));
            }
        }

        String jsonPath = currentIndex == null
                ? currentPath + ".*"
                : currentPath + "[" + currentIndex + "].*";
        log.trace("[{}] {} has length {}", ResultMatchers.class.getSimpleName(), jsonPath, uniquePaths.size());

        resultMatchers.add(new DelegateResultMatcher("Not all paths are covered, covered only " + uniquePaths,
                jsonPath(payloadType, jsonPath, hasSize(uniquePaths.size())), currentTrace));
    }

    private static void process(JsonPayloadType payloadType,
                                Collection<ResultMatcher> resultMatchers,
                                String currentPath,
                                Integer currentIndex,
                                JsonMatcher nextJsonMatcher) {
        if (nextJsonMatcher.getMatcher() == null) {
            String jsonPath = nextPath(currentPath, currentIndex, nextJsonMatcher.getPath(), nextJsonMatcher.getIndex());
            log.trace("[{}] {} exists", ResultMatchers.class.getSimpleName(), jsonPath);
            // Verify that json path exists.
            // org.springframework.test.web.servlet.result.JsonPathResultMatchers.hasJsonPath doesn't exist
            // in the minimum version of Spring that we support,
            // and org.springframework.test.web.servlet.result.JsonPathResultMatchers.exists doesn't accept null value.
            resultMatchers.add(new DelegateResultMatcher(
                    jsonPath(payloadType, jsonPath, anyOf(nullValue(), notNullValue())), nextJsonMatcher));
        } else if (nextJsonMatcher.getMatcher() instanceof Matcher) {
            Matcher<?> matcher = (Matcher<?>) nextJsonMatcher.getMatcher();
            String jsonPath = nextPath(currentPath, currentIndex, nextJsonMatcher.getPath(), nextJsonMatcher.getIndex());
            log.trace("[{}] {} matches {}", ResultMatchers.class.getSimpleName(), jsonPath, matcher.getClass());
            resultMatchers.add(new DelegateResultMatcher(jsonPath(payloadType, jsonPath, matcher), nextJsonMatcher));
        } else if (nextJsonMatcher.getMatcher() instanceof JsonMatcher[]) {
            String nextPath = nextPath(currentPath, currentIndex, nextJsonMatcher.getPath());
            JsonMatcher[] elementMatchers = (JsonMatcher[]) nextJsonMatcher.getMatcher();
            processAll(payloadType, resultMatchers, nextPath, nextJsonMatcher.getIndex(),
                    nextJsonMatcher.getTrace(), elementMatchers);
        } else if (nextJsonMatcher.getMatcher() instanceof JsonMatcher) {
            String nextPath = nextPath(currentPath, currentIndex, nextJsonMatcher.getPath());
            JsonMatcher elementMatcher = (JsonMatcher) nextJsonMatcher.getMatcher();
            processAll(payloadType, resultMatchers, nextPath, nextJsonMatcher.getIndex(),
                    nextJsonMatcher.getTrace(), elementMatcher);
        } else {
            String jsonPath = nextPath(currentPath, currentIndex, nextJsonMatcher.getPath(), nextJsonMatcher.getIndex());
            log.trace("[{}] {} has value [{}]", ResultMatchers.class.getSimpleName(), jsonPath, nextJsonMatcher.getMatcher());
            resultMatchers.add(new DelegateResultMatcher(
                    jsonPathValue(payloadType, jsonPath, nextJsonMatcher.getMatcher()), nextJsonMatcher));
        }
    }

    private static <T> ResultMatcher jsonPath(JsonPayloadType payloadType, String expression, Matcher<T> matcher) {
        if (payloadType == REQUEST) {
            val jsonPathHelper = new JsonPathExpectationsHelper(expression);
            return result -> {
                val content = getRequestContent(result);
                jsonPathHelper.assertValue(content, matcher);
            };
        } else {
            return MockMvcResultMatchers.jsonPath(expression, matcher);
        }
    }

    private static ResultMatcher jsonPathValue(JsonPayloadType payloadType, String expression, Object expectedValue) {
        if (payloadType == REQUEST) {
            val jsonPathHelper = new JsonPathExpectationsHelper(expression);
            return result -> {
                val content = getRequestContent(result);
                jsonPathHelper.assertValue(content, expectedValue);
            };
        } else {
            return MockMvcResultMatchers.jsonPath(expression).value(expectedValue);
        }
    }

    @Nonnull
    private static String getRequestContent(MvcResult result) throws IOException {
        val content = MockMvcUtils.getContentAsString(result.getRequest());
        if (content == null) {
            throw new AssertionError("Missing request content");
        }
        return content;
    }

    private static String nextPath(String currentPath, Integer currentIndex, String childPath) {
        return new NextPathBuilder()
                .append(currentPath)
                .append(currentIndex)
                .append(childPath)
                .build();
    }

    private static String nextPath(String currentPath, Integer currentIndex, String childPath, Integer childIndex) {
        return new NextPathBuilder()
                .append(currentPath)
                .append(currentIndex)
                .append(childPath)
                .append(childIndex)
                .build();
    }

    private static class NextPathBuilder {

        private final StringBuilder stringBuilder = new StringBuilder();

        private NextPathBuilder append(String path) {
            if (path == null) {
                return this;
            }

            if (stringBuilder.length() > 0) {
                stringBuilder.append('.');
            }

            stringBuilder.append(path);

            return this;
        }

        private NextPathBuilder append(Integer index) {
            if (index == null) {
                return this;
            }

            stringBuilder.append('[').append(index).append(']');

            return this;
        }

        private String build() {
            return stringBuilder.toString();
        }
    }
}
