package com.seregamorph.restapi.search;

import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
public enum SearchOperator {

    LESS_THAN("<", false, false, "Less than"),
    LESS_THAN_OR_EQUAL("<=", false, false, "Less than or equal"),
    EQUAL("=", false, false, "Equal"),
    NOT_EQUAL("!=", false, false, "Not equal"),
    GREATER_THAN(">", false, false, "Greater than"),
    GREATER_THAN_OR_EQUAL(">=", false, false, "Greater than or equal"),
    CONTAINS("contains", false, false, "Contains"),
    NOT_CONTAINS("not contains", false, false, "Not contains"),
    IS("is", false, true, "Is"),
    IS_NOT("is not", false, true, "Is not"),
    LIKE("like", false, false, "Like"),
    NOT_LIKE("not like", false, false, "Not like"),
    IN("in", true, false, "In"),
    NOT_IN("not in", true, false, "Not in");

    private final String operator;
    private final boolean multipleValueSupported;
    private final boolean specialValueSupported;
    private final String desc;

    @Nullable
    public static SearchOperator of(String value) {
        String standardValue = StringUtils.trimToEmpty(value).replaceAll("[\\s]+", " ").toLowerCase();

        for (SearchOperator operator : SearchOperator.values()) {
            if (StringUtils.equals(operator.operator, standardValue)) {
                return operator;
            }
        }

        return null;
    }

    public static boolean isSpecialChar(char ch) {
        return ch == '<' || ch == '>' || ch == '=' || ch == '!';
    }
}
