package com.seregamorph.restapi.search;

import static com.seregamorph.restapi.search.ArgumentWrappingHelper.isWrappedGroup;
import static com.seregamorph.restapi.search.ArgumentWrappingHelper.isWrappedString;
import static com.seregamorph.restapi.search.ArgumentWrappingHelper.unwrapString;
import static com.seregamorph.restapi.search.SearchParamUtils.isDataTypeSupported;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
class SearchValueConverter {

    /**
     * See the list of data types in {@link SearchParamConstants}.
     *
     * @param dataType the data type.
     * @param rawValue the raw value.
     * @return the object converted from the raw value.
     */
    static Object convertSingleObject(Class<?> dataType, boolean checkSpecialValue, String rawValue) {
        if (!isDataTypeSupported(dataType)) {
            throw new IllegalArgumentException(
                    String.format("Data type [%s] is not supported", dataType.getSimpleName()));
        }

        String trimmedRawValue = rawValue.trim();

        if (checkSpecialValue) {
            SearchValue searchValue = SearchValue.of(trimmedRawValue);

            if (searchValue != null) {
                return searchValue;
            }
        }

        if (dataType == String.class) {
            if (isWrappedString(trimmedRawValue)) {
                return unwrapString(trimmedRawValue);
            }

            // Strings not wrapped in quotes should be standardized
            return trimmedRawValue.replaceAll("[\\s]+", " ");
        }

        trimmedRawValue = unwrapString(trimmedRawValue);

        if (dataType == boolean.class || dataType == Boolean.class) {
            return Boolean.parseBoolean(trimmedRawValue);
        }

        if (dataType == int.class || dataType == Integer.class) {
            return Integer.parseInt(trimmedRawValue);
        }

        if (dataType == long.class || dataType == Long.class) {
            return Long.parseLong(trimmedRawValue);
        }

        if (dataType == double.class || dataType == Double.class) {
            return Double.parseDouble(trimmedRawValue);
        }

        if (dataType == LocalDate.class) {
            return LocalDate.parse(trimmedRawValue);
        }

        if (dataType == LocalDateTime.class) {
            return LocalDateTime.parse(trimmedRawValue);
        }

        if (dataType == Instant.class) {
            return Instant.parse(trimmedRawValue);
        }

        if (dataType == OffsetDateTime.class) {
            return OffsetDateTime.parse(trimmedRawValue);
        }

        if (Enum.class.isAssignableFrom(dataType)) {
            Object[] enumConstants = dataType.getEnumConstants();

            for (Object enumConstant : enumConstants) {
                if (StringUtils.equalsIgnoreCase(((Enum<?>) enumConstant).name(), trimmedRawValue)) {
                    return enumConstant;
                }
            }

            throw new IllegalArgumentException(String.format("Invalid [%s] value: [%s]", dataType, trimmedRawValue));
        }

        throw new IllegalStateException(String.format("Unsupported data type: [%s]", dataType));
    }

    static List<Object> convertMultipleObjects(
            Class<?> dataType, boolean checkSpecialValue, String... rawValues) {
        if (!isDataTypeSupported(dataType)) {
            throw new IllegalArgumentException(
                    String.format("Data type [%s] is not supported", dataType.getSimpleName()));
        }

        List<Object> results = new ArrayList<>();

        for (String rawValue : rawValues) {
            if (StringUtils.isNotBlank(rawValue)) {
                results.add(convertSingleObject(dataType, checkSpecialValue, rawValue.trim()));
            }
        }

        return results;
    }

    static List<Object> convertMultipleObjects(
            Class<?> dataType, boolean checkSpecialValue, String rawValue) {
        if (!isDataTypeSupported(dataType)) {
            throw new IllegalArgumentException(
                    String.format("Data type [%s] is not supported", dataType.getSimpleName()));
        }

        String value = rawValue.trim();

        if (isWrappedGroup(value)) {
            value = value.substring(1, value.length() - 1);
        }

        return convertMultipleObjects(dataType, checkSpecialValue, value.split(","));
    }
}
