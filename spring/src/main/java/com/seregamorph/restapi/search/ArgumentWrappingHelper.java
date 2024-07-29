package com.seregamorph.restapi.search;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
class ArgumentWrappingHelper {

    static final char GROUP_OPENING_CHAR = '(';
    static final char GROUP_CLOSING_CHAR = ')';
    static final char SINGLE_QUOTE_CHAR = '\'';
    static final char DOUBLE_QUOTE_CHAR = '"';

    static boolean isSpecialChar(char ch) {
        return ch == GROUP_OPENING_CHAR
                || ch == GROUP_CLOSING_CHAR
                || ch == SINGLE_QUOTE_CHAR
                || ch == DOUBLE_QUOTE_CHAR;
    }

    static boolean isMatchingChar(char first, char second) {
        return (first == GROUP_OPENING_CHAR && second == GROUP_CLOSING_CHAR)
                || (first == SINGLE_QUOTE_CHAR && second == SINGLE_QUOTE_CHAR)
                || (first == DOUBLE_QUOTE_CHAR && second == DOUBLE_QUOTE_CHAR);
    }

    static boolean isGroupOpeningChar(char ch) {
        return ch == GROUP_OPENING_CHAR;
    }

    static boolean isGroupClosingChar(char ch) {
        return ch == GROUP_CLOSING_CHAR;
    }

    static boolean isQuoteChar(char ch) {
        return ch == SINGLE_QUOTE_CHAR || ch == DOUBLE_QUOTE_CHAR;
    }

    static String wrapGroup(String group) {
        return GROUP_OPENING_CHAR + group + GROUP_CLOSING_CHAR;
    }

    static String unwrapGroup(String rawGroup) {
        String result = rawGroup.trim();

        while (isWrappedGroup(result)) {
            result = result.substring(1, result.length() - 1).trim();
        }

        return result;
    }

    static String unwrapString(String rawValue) {
        String result = rawValue.trim();

        if (isWrappedString(result)) {
            // No trim, no recursive calls. Quoted values are preserved.
            return result.substring(1, result.length() - 1);
        }

        return result;
    }

    static boolean isWrappedGroup(String rawGroup) {
        // Wrapping chars are ( and )
        // () is wrapped
        // (()) is wrapped
        // foo is NOT wrapped
        // foo bar is NOT wrapped
        // () | () is NOT wrapped
        // (() | ()) is wrapped
        // (")") is wrapped
        if (StringUtils.isEmpty(rawGroup)) {
            // Special case
            return false;
        }

        char[] chars = rawGroup.trim().toCharArray();
        int openingCharCount = 0;

        for (char ch : chars) {
            if (openingCharCount > 0 && isGroupClosingChar(ch)) {
                --openingCharCount;
            } else if (isGroupOpeningChar(ch)) {
                ++openingCharCount;
            } else if (openingCharCount == 0) {
                // No opening char yet, and we already hit a normal char. We are sure that the string is not wrapped.
                return false;
            }
        }

        return openingCharCount == 0;
    }

    static boolean isWrappedString(String rawValue) {
        String trimmedRawValue = StringUtils.trimToEmpty(rawValue);

        if (trimmedRawValue.length() < 2) {
            return false;
        }

        char firstChar = trimmedRawValue.charAt(0);
        char lastChar = trimmedRawValue.charAt(trimmedRawValue.length() - 1);

        return isQuoteChar(firstChar) && isMatchingChar(firstChar, lastChar);
    }
}
