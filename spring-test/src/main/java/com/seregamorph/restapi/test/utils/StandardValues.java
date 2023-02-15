package com.seregamorph.restapi.test.utils;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_ENUMS_USING_TO_STRING;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seregamorph.restapi.search.SearchValue;
import com.seregamorph.restapi.test.TestApplicationContextHolder;
import java.net.URI;
import java.net.URL;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.util.Base64Utils;

@UtilityClass
public class StandardValues {

    // Vararg is NOT supported here because (1) we want to avoid mis-use or confusion or unnecessary cast
    // or unnecessary warning suppression (e.g. a caller passing Object... which can confuse IDE/compiler
    // as both vararg and non-vararg match) (2) this is supposed to be used internally, and vararg is not
    // necessary.

    /**
     * Converts from a java-represented object to a json-ready object. The result of the conversion may be used
     * in a request payload or a response body. Example: An enum is a java-represented object, but when it's serialized
     * (before sending a request or response), we have a {@link String} instead.
     *
     * @param value the java-represented object to standardize.
     * @return the json-ready object.
     */
    public static Object jsonObject(Object value) {
        if (value instanceof SearchValue) {
            // SearchValue instances are to be handled separately
            return value;
        }

        if (value instanceof Enum) {
            val objectMapper = TestApplicationContextHolder.getApplicationContext()
                    .map(applicationContext -> applicationContext.getBean(ObjectMapper.class))
                    .orElse(null);
            if (objectMapper == null || !objectMapper.isEnabled(WRITE_ENUMS_USING_TO_STRING)) {
                // default
                return ((Enum<?>) value).name();
            } else {
                // special case when configured
                return value.toString();
            }
        }

        if (value instanceof byte[]) {
            // By default byte array values are encoded as base64 in jackson
            return Base64Utils.encodeToString((byte[]) value);
        }

        if (value instanceof Temporal
                || value instanceof URL
                || value instanceof URI) {
            return value.toString();
        }

        return value;
    }

    public static Object[] jsonObjects(Object[] values) {
        Object[] results = new Object[values.length];
        for (int i = 0; i < values.length; ++i) {
            results[i] = jsonObject(values[i]);
        }
        return results;
    }

    public static List<Object> jsonObjects(Collection<?> values) {
        return values.stream()
                .map(StandardValues::jsonObject)
                .collect(Collectors.toList());
    }

    public static String string(Object value) {
        if (value instanceof SearchValue) {
            return ((SearchValue) value).name().toLowerCase();
        }

        if (value instanceof Enum) {
            return ((Enum<?>) value).name();
        }

        return String.valueOf(value);
    }

    public static String[] strings(Object[] values) {
        String[] results = new String[values.length];
        for (int i = 0; i < values.length; ++i) {
            results[i] = string(values[i]);
        }
        return results;
    }

    public static List<String> strings(Collection<?> values) {
        return values.stream().map(StandardValues::string).collect(Collectors.toList());
    }
}
