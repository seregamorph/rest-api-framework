package com.seregamorph.restapi.search;

import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@RequiredArgsConstructor
public enum LogicalOperator {

    AND("and", ";"),
    OR("or", "|");

    private final String primaryOperator;
    private final String secondaryOperator;

    public String[] getOperators() {
        return new String[] {primaryOperator, secondaryOperator};
    }

    @Nullable
    public static LogicalOperator of(String value) {
        String standardValue = StringUtils.trimToEmpty(value).toLowerCase();

        for (LogicalOperator logicalOperator : LogicalOperator.values()) {
            for (String operator : logicalOperator.getOperators()) {
                if (StringUtils.equals(operator, standardValue)) {
                    return logicalOperator;
                }
            }
        }

        return null;
    }

    public static boolean isSpecialChar(char ch) {
        return ch == ';' || ch == '|';
    }
}
