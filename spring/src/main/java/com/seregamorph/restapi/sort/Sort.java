package com.seregamorph.restapi.sort;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.val;

/**
 * Sort should be a {@link List} for two purposes:
 * <ul>
 * <li>Swagger declares parameter as array [string]</li>
 * <li>Feign client serialization as repeated query parameter</li>
 * </ul>
 */
public class Sort extends AbstractList<SortField> {

    private final List<SortField> fields;

    public Sort(List<SortField> fields) {
        this.fields = new ArrayList<>(fields);
    }

    public Sort(SortField... fields) {
        this(Arrays.asList(fields));
    }

    public Sort(String fieldName, SortDirection direction) {
        this(new SortField(fieldName, direction));
    }

    @Override
    public SortField get(int index) {
        return fields.get(index);
    }

    @Override
    public int size() {
        return fields.size();
    }

    public static Sort by(String fieldName, String... fieldNames) {
        val fields = new ArrayList<String>();
        fields.add(fieldName);
        Collections.addAll(fields, fieldNames);

        return new Sort(fields.stream()
                .map(SortField::new)
                .collect(Collectors.toList()));
    }
}
