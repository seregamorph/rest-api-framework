package com.seregamorph.restapi.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ObjectUtils {

    // An array of primitive values (e.g. int[]) is NOT an array of objects. This gives us trouble to check cases
    // as there are many primitive data types. Furthermore, even in the case of non primitive types, we still need
    // to handle arrays and collections differently. This class is to handle those problems by converting them all
    // to collections of objects.

    public static Object singleOrCollection(Object value) {
        if (value instanceof boolean[]) {
            boolean[] values = (boolean[]) value;
            Object[] objects = new Object[values.length];

            for (int i = 0; i < values.length; ++i) {
                objects[i] = values[i];
            }

            return Arrays.asList(objects);
        }

        if (value instanceof byte[]) {
            byte[] values = (byte[]) value;
            Object[] objects = new Object[values.length];

            for (int i = 0; i < values.length; ++i) {
                objects[i] = values[i];
            }

            return Arrays.asList(objects);
        }

        if (value instanceof char[]) {
            char[] values = (char[]) value;
            Object[] objects = new Object[values.length];

            for (int i = 0; i < values.length; ++i) {
                objects[i] = values[i];
            }

            return Arrays.asList(objects);
        }

        if (value instanceof short[]) {
            short[] values = (short[]) value;
            Object[] objects = new Object[values.length];

            for (int i = 0; i < values.length; ++i) {
                objects[i] = values[i];
            }

            return Arrays.asList(objects);
        }

        if (value instanceof int[]) {
            int[] values = (int[]) value;
            Object[] objects = new Object[values.length];

            for (int i = 0; i < values.length; ++i) {
                objects[i] = values[i];
            }

            return Arrays.asList(objects);
        }

        if (value instanceof long[]) {
            long[] values = (long[]) value;
            Object[] objects = new Object[values.length];

            for (int i = 0; i < values.length; ++i) {
                objects[i] = values[i];
            }

            return Arrays.asList(objects);
        }

        if (value instanceof float[]) {
            float[] values = (float[]) value;
            Object[] objects = new Object[values.length];

            for (int i = 0; i < values.length; ++i) {
                objects[i] = values[i];
            }

            return Arrays.asList(objects);
        }

        if (value instanceof double[]) {
            double[] values = (double[]) value;
            Object[] objects = new Object[values.length];

            for (int i = 0; i < values.length; ++i) {
                objects[i] = values[i];
            }

            return Arrays.asList(objects);
        }

        if (value instanceof Object[]) {
            Object[] values = (Object[]) value;
            return Arrays.asList(values);
        }

        return value;
    }

    public static Collection<?> collection(Object value) {
        Object standardValue = singleOrCollection(value);

        if (standardValue instanceof Collection) {
            return (Collection<?>) standardValue;
        }

        return Collections.singletonList(standardValue);
    }
}
