package com.seregamorph.restapi.search;

import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public enum SearchValue {

    NULL, EMPTY, BLANK;

    @Nullable
    public static SearchValue of(String value) {
        try {
            return SearchValue.valueOf(StringUtils.trimToEmpty(value).toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
