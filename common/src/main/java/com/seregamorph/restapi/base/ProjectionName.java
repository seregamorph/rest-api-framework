package com.seregamorph.restapi.base;

import com.seregamorph.restapi.common.Constants;

public interface ProjectionName {

    String name();

    Class<? extends BaseProjection> getProjectionClass();

    static <T extends Enum<T>> T getDefaultProjection(Class<T> enumType) {
        try {
            return Enum.valueOf(enumType, Constants.DEFAULT_PROJECTION);
        } catch (IllegalArgumentException e) {
            return enumType.getEnumConstants()[0];
        }
    }
}
