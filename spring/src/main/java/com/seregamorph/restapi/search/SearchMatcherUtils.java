package com.seregamorph.restapi.search;

import com.seregamorph.restapi.utils.ObjectUtils;
import com.seregamorph.restapi.utils.RelaxedObjects;
import java.util.Collection;
import java.util.function.IntPredicate;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class SearchMatcherUtils {

    public static boolean matches(Object value, SearchOperator searchOperator, Object searchValue) {
        switch (searchOperator) {
            case EQUAL:
                return RelaxedObjects.equals(value, searchValue);
            case NOT_EQUAL:
                return !RelaxedObjects.equals(value, searchValue);
            case LESS_THAN:
                return compare(value, searchValue, i -> i < 0);
            case LESS_THAN_OR_EQUAL:
                return compare(value, searchValue, i -> i <= 0);
            case GREATER_THAN:
                return compare(value, searchValue, i -> i > 0);
            case GREATER_THAN_OR_EQUAL:
                return compare(value, searchValue, i -> i >= 0);
            case CONTAINS:
                // Check value, not searchValue, and value can be either a collection or an array
                return containsSearchValue(value, searchValue);
            case NOT_CONTAINS:
                // Check value, not searchValue, and value can be either a collection or an array
                return !containsSearchValue(value, searchValue);
            case IS:
                if (searchValue == SearchValue.NULL) {
                    return value == null;
                }
                if (searchValue == SearchValue.EMPTY) {
                    return value == null || (value instanceof String && StringUtils.isEmpty((String) value));
                }
                if (searchValue == SearchValue.BLANK) {
                    return value == null || (value instanceof String && StringUtils.isBlank((String) value));
                }
                return false;
            case IS_NOT:
                if (searchValue == SearchValue.NULL) {
                    return value != null;
                }
                if (searchValue == SearchValue.EMPTY) {
                    return value != null && (!(value instanceof String) || StringUtils.isNotEmpty((String) value));
                }
                if (searchValue == SearchValue.BLANK) {
                    return value != null && (!(value instanceof String) || StringUtils.isNotBlank((String) value));
                }
                return false;
            case LIKE:
                if (searchValue instanceof String && value instanceof String) {
                    return containsInOrder((String) value, ((String) searchValue).split("%"));
                }
                return false;
            case NOT_LIKE:
                if (searchValue instanceof String && value instanceof String) {
                    return !containsInOrder((String) value, ((String) searchValue).split("%"));
                }
                return false;
            case IN:
                return containsValue(searchValue, value);
            case NOT_IN:
                return !containsValue(searchValue, value);
            default:
                return false;
        }
    }

    private static boolean containsSearchValue(Object multipleValues, Object searchValue) {
        Collection<?> collection = ObjectUtils.collection(multipleValues);

        for (Object element : collection) {
            if (RelaxedObjects.equals(element, searchValue)) {
                return true;
            }
        }

        return false;
    }

    private static boolean containsValue(Object multipleSearchValues, Object value) {
        // Notice that searchValue is either a single object or a collection, but can't be an array.
        // See VerifiableSearchField for details.
        if (multipleSearchValues instanceof Collection) {
            Collection<?> collection = (Collection<?>) multipleSearchValues;

            for (Object element : collection) {
                if (RelaxedObjects.equals(value, element)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean containsInOrder(String string, String... chunks) {
        int startingIndex = 0;

        for (String chunk : chunks) {
            if (StringUtils.isEmpty(chunk)) {
                continue;
            }

            int index = string.indexOf(chunk, startingIndex);

            if (index == -1) {
                return false;
            }

            startingIndex = index + chunk.length();
        }

        return true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean compare(Object value, Object searchValue, IntPredicate resultCheck) {
        if (value instanceof Comparable && searchValue instanceof Comparable) {
            return resultCheck.test(((Comparable) value).compareTo(searchValue));
        }
        return false;
    }
}
