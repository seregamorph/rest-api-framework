package com.seregamorph.restapi.sort;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class SortField {

    private final String fieldName;
    private final SortDirection direction;

    public SortField(String fieldName) {
        this(fieldName, SortDirection.ASC);
    }

    /**
     * Used in client code (including Feign clients)
     *
     * @return formatted value for query parameter
     */
    @Override
    public String toString() {
        return fieldName + ":" + direction;
    }
}
