package com.seregamorph.restapi.sort;

import javax.annotation.Nonnull;

public enum SortDirection {
    ASC,
    DESC;

    @Nonnull
    public static SortDirection fromString(String value) {
        return valueOf(value.trim().toUpperCase());
    }

    /**
     * Used in client code
     */
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
