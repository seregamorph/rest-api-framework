package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.test.base.JsonMatcher.path;
import static org.hamcrest.Matchers.hasSize;

import com.seregamorph.restapi.base.BasePayload;
import com.seregamorph.restapi.utils.AbstractProxyFactory;
import com.seregamorph.restapi.utils.ObjectUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.val;

class JsonMatcherProxyFactory extends AbstractProxyFactory {

    @RequiredArgsConstructor
    private static class CurrentMatcher {

        /**
         * Keep stack trace information to make detailed diagnostics in case of mismatch.
         */
        private final StackTraceElement[] trace = new Throwable().getStackTrace();

        private final Object matcher;
    }

    private static final ThreadLocal<CurrentMatcher> currentMatcher = new ThreadLocal<>();

    <T extends BasePayload> T create(Class<T> superClass, JsonMatcher... initialJsonMatchers) {
        assertEmptyCurrentMatcher();

        val proxy = super.create(superClass);
        val handler = (JsonMatcherProxyMethodHandler) tryGetMethodHandler(proxy);
        assert handler != null;
        for (val matcher : initialJsonMatchers) {
            handler.put(matcherKey(matcher), matcher);
        }
        return proxy;
    }

    static void setCurrentMatcher(Object matcher) {
        assertEmptyCurrentMatcher();
        currentMatcher.set(new CurrentMatcher(matcher));
    }

    static void clearCurrentMatcher() {
        try {
            assertEmptyCurrentMatcher();
        } finally {
            currentMatcher.remove();
        }
    }

    @Nonnull
    static List<JsonMatcher> getJsonMatchers(Object proxy) {
        if (!(isJsonMatcherProxy(proxy))) {
            throw new IllegalStateException("Object should be a proxy, created by JsonMatcher.jsonMatching(Class)");
        }
        return ((JsonMatcherProxyMethodHandler) ((ProxyObject) proxy).getHandler()).getJsonMatchers();
    }

    static boolean isJsonMatcherProxy(Object object) {
        val methodHandler = tryGetMethodHandler(object);
        return methodHandler instanceof JsonMatcherProxyMethodHandler;
    }

    @Override
    protected MethodHandler getMethodHandler(Class<?> superClass) {
        return new JsonMatcherProxyMethodHandler();
    }

    @Override
    protected Object handleSetter(Object self, Method thisMethod, Method proceed, Object[] args)
            throws InvocationTargetException, IllegalAccessException {
        Object result = super.handleSetter(self, thisMethod, proceed, args);

        val methodHandler = (JsonMatcherProxyMethodHandler) ((ProxyObject) self).getHandler();
        val fieldName = extractFieldName(thisMethod);
        val matcherValue = getJsonMatcher(args);
        if (matcherValue instanceof Map) {
            handleSetMap(methodHandler, fieldName, (Map<?, ?>) matcherValue);
        } else if (matcherValue instanceof Collection) {
            handleSetCollection(methodHandler, fieldName, (Collection<?>) matcherValue);
        } else if (matcherValue != null && matcherValue.getClass().isArray() && !(matcherValue instanceof byte[])) {
            // Bytes are encoded as base64. To handle it separately.
            handleSetCollection(methodHandler, fieldName, ObjectUtils.collection(matcherValue));
        } else {
            methodHandler.put(fieldName, path(fieldName, matcherValue));
        }
        return result;
    }

    private void handleSetMap(JsonMatcherProxyMethodHandler methodHandler, String fieldName, Map<?, ?> map) {
        val list = plainMap(map);
        if (list.isEmpty()) {
            methodHandler.put(fieldName, path(fieldName, map));
        } else {
            methodHandler.put(fieldName, path(fieldName, list.stream()
                    .map(entry -> path(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList())));
        }
    }

    private void handleSetCollection(JsonMatcherProxyMethodHandler methodHandler, String fieldName, Collection<?> collection) {
        if (!collection.isEmpty() && collection.stream().allMatch(element -> element instanceof JsonMatcher)) {
            for (Object element : collection) {
                val matcher = (JsonMatcher) element;
                val path = fieldName + (matcher.getPath() == null ? "" : "." + matcher.getPath());
                if (matcher.getIndex() == null) {
                    methodHandler.put(path, path(path, matcher.getMatcher()));
                } else {
                    methodHandler.put(path + "[" + matcher.getIndex() + "]", path(path, matcher.getIndex(), matcher.getMatcher()));
                }
            }
        } else {
            methodHandler.put(fieldName, path(fieldName, hasSize(collection.size())));
            int index = 0;
            for (Object element : collection) {
                methodHandler.put(fieldName + "[" + index + "]", path(fieldName, index++, element));
            }
        }
    }

    @Override
    protected String handleToString(Object self) {
        return "JsonMatcherProxy<" + self.getClass().getSuperclass().getName() + ">("
                + getJsonMatchers(self).stream()
                .map(JsonMatcher::getPath)
                .collect(Collectors.joining(", "))
                + ")";
    }

    static List<Map.Entry<String, ?>> plainMap(Map<?, ?> map) {
        val list = new ArrayList<Map.Entry<String, ?>>();
        plainMap(list, map, null);
        return list;
    }

    private static void plainMap(List<Map.Entry<String, ?>> list, Map<?, ?> map, @Nullable String path) {
        // note: arrays are not supported, implementation on demand
        map.forEach((key, value) -> {
            val fullPath = path == null ? key.toString() : path + "." + key;
            if (value instanceof Map) {
                plainMap(list, (Map<?, ?>) value, fullPath);
            } else {
                list.add(new AbstractMap.SimpleImmutableEntry<>(fullPath, value));
            }
        });
    }

    private static String matcherKey(JsonMatcher matcher) {
        return matcher.getPath() + (matcher.getIndex() == null ? "" : "[" + matcher.getIndex() + "]");
    }

    private static void assertEmptyCurrentMatcher() {
        if (currentMatcher.get() != null) {
            val exception = new IllegalStateException("Wrong JsonMatcher call state: previous matcher "
                    + "was not consumed by setter (check attached suppressed exception)");
            val error = new AssertionError();
            error.setStackTrace(currentMatcher.get().trace);
            exception.addSuppressed(error);
            throw exception;
        }
    }

    private static Object getJsonMatcher(Object[] args) {
        val matcher = currentMatcher.get();
        if (matcher == null) {
            assert args.length == 1;
            return args[0];
        } else {
            currentMatcher.remove();
            // If matcher is not null, mostly arg is null, but can be also a primitive type
            return matcher.matcher;
        }
    }

    private class JsonMatcherProxyMethodHandler implements MethodHandler {

        private final Map<String, JsonMatcher> jsonMatchers = new LinkedHashMap<>();

        List<JsonMatcher> getJsonMatchers() {
            return new ArrayList<>(jsonMatchers.values());
        }

        @Override
        public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args)
                throws InvocationTargetException, IllegalAccessException {
            return handle(self, thisMethod, proceed, args);
        }

        void put(String matcherKey, JsonMatcher matcher) {
            if (jsonMatchers.containsKey(matcherKey)) {
                throw new IllegalStateException(matcherKey + " field matcher is already set");
            }
            jsonMatchers.put(matcherKey, matcher);
        }
    }
}
