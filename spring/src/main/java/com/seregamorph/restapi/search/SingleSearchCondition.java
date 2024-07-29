package com.seregamorph.restapi.search;

import static com.seregamorph.restapi.search.ArgumentWrappingHelper.wrapGroup;

import com.seregamorph.restapi.utils.ObjectUtils;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
@NoArgsConstructor
public class SingleSearchCondition implements SearchCondition {

    private String field;
    private SearchOperator operator;
    /**
     * Either a single object with precise data type (e.g. Integer), or a collection of precise data type objects.
     */
    private Object value;

    public SingleSearchCondition setValue(Object value) {
        this.value = ObjectUtils.singleOrCollection(value);
        return this;
    }

    public SingleSearchCondition(String field, SearchOperator operator, Object value) {
        this.field = field;
        this.operator = operator;
        this.value = ObjectUtils.singleOrCollection(value);
    }

    @SuppressWarnings({"unchecked", "unused"})
    public <T> T getValue() {
        return (T) value;
    }

    @Override
    public String toString() {
        return String.format("%s%s%s", field, getSearchOperator(), getSearchValue());
    }

    private String getSearchOperator() {
        String result = operator.getOperator();

        if (Character.isAlphabetic(result.charAt(0))) {
            result = ' ' + result;
        }

        if (Character.isAlphabetic(result.charAt(result.length() - 1))) {
            result = result + ' ';
        }

        return result;
    }

    private String getSearchValue() {
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            String result = collection.stream().map(String::valueOf).collect(Collectors.joining(","));
            return wrapGroup(result);
        }

        if (value instanceof SearchValue) {
            return ((SearchValue) value).name().toLowerCase();
        }

        if (value instanceof String) {
            return "\"" + value + "\"";
        }

        return String.valueOf(value);
    }
}
