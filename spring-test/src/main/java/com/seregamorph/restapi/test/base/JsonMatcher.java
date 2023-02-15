package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.test.utils.MoreMatchers.hasRelaxedValue;
import static com.seregamorph.restapi.test.utils.StandardValues.jsonObject;
import static lombok.AccessLevel.PRIVATE;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.google.common.collect.Lists;
import com.seregamorph.restapi.base.BasePayload;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matcher;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class JsonMatcher extends AbstractStackTraceHolder {

    private static final JsonMatcherProxyFactory factory = new JsonMatcherProxyFactory();

    @Nullable
    private final String path;

    @Nullable
    private final Integer index;

    // This can either be a value, a Matcher, a collection of JsonMatcher(s) or null.
    // Where null is treated as 'no matcher', not 'matcher being null value'. It means we don't intend to match the
    // value of the path. We simply check that the path exists.
    @Nullable
    private final Object matcher;

    /**
     * Verifies that the specified <code>path</code> exists.
     */
    public static JsonMatcher path(String path) {
        return new JsonMatcher(path, null, null);
    }

    public static JsonMatcher path(String path, @Nullable Object value) {
        return new JsonMatcher(path, null, matcher(value));
    }

    public static <T> JsonMatcher path(String path,
                                       @Nullable T value,
                                       String nestedPath,
                                       Function<T, Object> nestedFunction) {
        return path(path, value == null ? null : path(nestedPath, nestedFunction.apply(value)));
    }

    public static <T> JsonMatcher path(String path,
                                       @Nullable T value,
                                       String nestedPath,
                                       ResourceType nestedResourceType,
                                       BiFunction<T, ResourceType, Object> nestedFunction) {
        return path(path, value == null ? value : path(nestedPath, nestedFunction.apply(value, nestedResourceType)));
    }

    public static <T> JsonMatcher path(String path, @Nullable T value, Function<T, Object> matcherFunction) {
        return path(path, value == null ? null : matcherFunction.apply(value));
    }

    public static <T> JsonMatcher path(String path,
                                       @Nullable T value,
                                       ResourceType resourceType,
                                       BiFunction<T, ResourceType, Object> matcherFunction) {
        return path(path, value == null ? null : matcherFunction.apply(value, resourceType));
    }

    public static JsonMatcher path(String path, JsonMatcher... values) {
        return new JsonMatcher(path, null, matcher(values));
    }

    public static JsonMatcher path(String path, int index, Object value) {
        return new JsonMatcher(path, index, matcher(value));
    }

    public static <T> JsonMatcher path(String path, int index, @Nullable T value, Function<T, Object> matcherFunction) {
        return path(path, index, value == null ? null : matcherFunction.apply(value));
    }

    public static <T> JsonMatcher path(String path,
                                       int index,
                                       @Nullable T value,
                                       ResourceType resourceType,
                                       BiFunction<T, ResourceType, Object> matcherFunction) {
        return path(path, index, value == null ? value : matcherFunction.apply(value, resourceType));
    }

    public static JsonMatcher path(String path, int index, JsonMatcher... values) {
        return new JsonMatcher(path, index, matcher(values));
    }

    public static <T> JsonMatcher pathEach(String path,
                                           @Nullable Collection<T> collection,
                                           Function<T, Object> matcherFunction) {
        if (collection == null) {
            return path(path, (Object) null);
        }

        List<JsonMatcher> jsonMatchers = Lists.newArrayList(
                path(null, hasSize(collection.size()))
        );

        int index = 0;

        for (T element : collection) {
            jsonMatchers.add(path(null, index++, element, matcherFunction));
        }

        return path(path, jsonMatchers);
    }

    public static <T> JsonMatcher pathEach(String path,
                                           @Nullable Collection<T> collection,
                                           ResourceType resourceType,
                                           BiFunction<T, ResourceType, Object> matcherFunction) {
        if (collection == null) {
            return path(path, (Object) null);
        }

        List<JsonMatcher> jsonMatchers = Lists.newArrayList(
                path(null, hasSize(collection.size())));

        int index = 0;

        for (T element : collection) {
            jsonMatchers.add(path(null, index++, element, resourceType, matcherFunction));
        }

        return path(path, jsonMatchers);
    }

    public static <V> V matching(@Nullable V value, Function<V, Object> matcherFunction) {
        if (value != null) {
            JsonMatcherProxyFactory.setCurrentMatcher(matcherFunction.apply(value));
        }
        return null;
    }

    public static <V> V matching(@Nullable V value, ResourceType resourceType,
                                 BiFunction<V, ResourceType, Object> matcherFunction) {
        if (value != null) {
            JsonMatcherProxyFactory.setCurrentMatcher(matcherFunction.apply(value, resourceType));
        }
        return null;
    }

    /**
     * Helper to match only first element(s) of the collection.
     */
    public static <V, C extends Collection<V>> C firstMatching(Matcher<Integer> totalSizeMatcher, int firstOffset, C collection) {
        List<JsonMatcher> jsonMatchers = Lists.newArrayList(
                path(null, hasSize(totalSizeMatcher)));

        int index = 0;

        for (V element : collection) {
            jsonMatchers.add(path(null, firstOffset + index++, element));
        }

        JsonMatcherProxyFactory.setCurrentMatcher(jsonMatchers);
        return null;
    }

    /**
     * Helper to match only first element(s) of the collection.
     */
    public static <V, C extends Collection<V>> C firstMatching(Matcher<Integer> totalSizeMatcher, C collection) {
        return firstMatching(totalSizeMatcher, 0, collection);
    }

    /**
     * Helper to match only first element(s) of the collection.
     */
    public static <V, C extends Collection<V>> C firstMatching(int totalSize, C collection) {
        return firstMatching(equalTo(totalSize), collection);
    }

    /**
     * Note: this method can be used only with setter-based Json Matchers.
     */
    @SuppressWarnings("unused")
    public static <V> V immutableValue(ResourceType resourceType, V defaultValue) {
        if (resourceType != ResourceType.CREATED && resourceType != ResourceType.SAVED) {
            return defaultValue;
        }

        JsonMatcherProxyFactory.setCurrentMatcher(notNullValue());
        return null;
    }

    /**
     * Note: this method can be used only with setter-based Json Matchers.
     */
    @SuppressWarnings("unused")
    public static <V> V auditableValue(ResourceType resourceType, V defaultValue) {
        if (resourceType == ResourceType.EXISTING) {
            return defaultValue;
        }

        JsonMatcherProxyFactory.setCurrentMatcher(notNullValue());
        return null;
    }

    @Nonnull
    public static List<JsonMatcher> jsonMatchersOf(BasePayload proxy) {
        return JsonMatcherProxyFactory.getJsonMatchers(proxy);
    }

    /**
     * Returns an instance of payload class, that intercepts each setter call and adds the given value to the list
     * of matchers. To extract matchers use helper methods {@link #jsonMatchersOf(BasePayload)} or
     * {@link ResultType#matcherOf(BasePayload)}.
     */
    public static <P extends BasePayload> P jsonMatching(Class<P> payloadType, JsonMatcher... initialJsonMatchers) {
        return factory.create(payloadType, initialJsonMatchers);
    }

    public static <V> V matching(Matcher<? super V> matcher) {
        return matching(matcher, (V) null);
    }

    /**
     * null safe implementation of {@link JsonMatcher#matching(Matcher)}. Can be used for matching primitive type.
     *
     * @param matcher
     * @param returnValue value to return from method
     * @param <V>
     * @return
     */
    public static <V> V matching(Matcher<? super V> matcher, V returnValue) {
        JsonMatcherProxyFactory.setCurrentMatcher(matcher);
        return returnValue;
    }

    @Override
    public String toString() {
        return "JsonMatcher{"
                + path
                + (index == null ? "" : "[" + index + "]")
                + " is " + (matcher instanceof Object[] ? Arrays.toString((Object[]) matcher) : Objects.toString(matcher))
                + "}";
    }

    private static Object matcher(@Nullable Object object) {
        if (object == null) {
            return nullValue();
        }

        if (object instanceof Number) {
            // Long value not equal to Integer value - hence relax the type check
            // BigDecimal value not equal to Double value - hence relax the type check
            // double value not equal to float value - hence relax the type check
            return hasRelaxedValue(object);
        }

        if (object instanceof OffsetDateTime) {
            // If we send an OffsetDateTime in a POST/PATCH/PUT payload, server may convert to UTC and we end up with
            // a different value. Hence relax the constraint.
            return hasRelaxedValue(object);
        }

        if (JsonMatcherProxyFactory.isJsonMatcherProxy(object)) {
            return matcher(JsonMatcherProxyFactory.getJsonMatchers(object));
        }

        if (object instanceof Collection) {
            Collection<?> collection = (Collection<?>) object;

            if (!collection.isEmpty() && collection.iterator().next() instanceof JsonMatcher) {
                return collection.stream().map(element -> (JsonMatcher) element).toArray(JsonMatcher[]::new);
            }

            List<JsonMatcher> jsonMatchers = Lists.newArrayList(
                    path(null, hasSize(collection.size()))
            );

            int index = 0;

            for (Object element : collection) {
                jsonMatchers.add(path(null, index++, element));
            }

            return jsonMatchers.toArray(new JsonMatcher[0]);
        }

        return jsonObject(object);
    }
}
