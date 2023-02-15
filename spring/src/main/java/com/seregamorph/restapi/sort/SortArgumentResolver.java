package com.seregamorph.restapi.sort;

import static com.seregamorph.restapi.common.Constants.PARAM_SORT;

import com.seregamorph.restapi.exceptions.TypeMismatchExceptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class SortArgumentResolver {

    public static Sort resolveArgument(SortParam sortParam, String[] sortValues) {
        val orders = new ArrayList<SortField>();

        if (sortValues != null) {
            val sortFields = Stream.of(sortParam.value())
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            for (String value : sortValues) {
                if (StringUtils.isBlank(value)) {
                    continue;
                }

                // If parameter values are combined using semicolons
                // (handle similarly to Spring's SortHandlerMethodArgumentResolver)
                String[] chunks = value.split(";");

                for (String chunk : chunks) {
                    if (StringUtils.isBlank(chunk)) {
                        continue;
                    }

                    String[] strings = chunk.trim()
                            .split(":", 2);
                    val fieldName = strings[0];
                    if (!sortFields.contains(fieldName)) {
                        throw TypeMismatchExceptions.create(Sort.class, PARAM_SORT, value,
                                String.format("Field [%s] is not sortable, allowed fields: %s", fieldName, sortFields));
                    }
                    SortDirection direction = SortDirection.ASC;
                    if (strings.length > 1) {
                        try {
                            direction = SortDirection.fromString(strings[1]);
                        } catch (IllegalArgumentException e) {
                            throw TypeMismatchExceptions.create(SortDirection.class,
                                    PARAM_SORT, strings[1], "Possible values: "
                                            + Arrays.toString(SortDirection.values()));
                        }
                    }
                    orders.add(new SortField(fieldName, direction));
                }
            }
            val sortFieldUniqueNames = orders.stream()
                    .map(SortField::getFieldName)
                    .collect(Collectors.toSet());
            if (orders.size() != sortFieldUniqueNames.size()) {
                throw TypeMismatchExceptions.create(SortDirection.class,
                        PARAM_SORT, Arrays.toString(sortValues), "Duplicated field names");
            }
        } else {
            for (SortParam.DefaultField field : sortParam.defaultSort()) {
                orders.add(new SortField(field.value(), field.direction()));
            }
        }

        return new Sort(orders);
    }
}
