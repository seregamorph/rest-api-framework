package com.seregamorph.restapi.utils;

import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class MoreReflectionUtils {

    public static <T> Class<? extends T> classForName(String name, Class<T> requiredSuperType) throws ClassNotFoundException {
        val clazz = Class.forName(name);
        return clazz.asSubclass(requiredSuperType);
    }

    @Nullable
    public static <T> Class<? extends T> tryClassForName(String name, Class<T> requiredSuperType) {
        try {
            return classForName(name, requiredSuperType);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
