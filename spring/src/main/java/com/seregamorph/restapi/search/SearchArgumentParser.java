package com.seregamorph.restapi.search;

import static com.seregamorph.restapi.common.Constants.PARAM_SEARCH;
import static com.seregamorph.restapi.search.ArgumentWrappingHelper.unwrapGroup;

import com.seregamorph.restapi.exceptions.TypeMismatchExceptions;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

class SearchArgumentParser {

    static final String BAD_SEARCH_ARGUMENT_FORMAT = "Format: <fieldName><operator><value>. "
            + "Use logical operators and round brackets to build more complex search argument.";
    static final String MATCHING_CLOSING_CHAR_NOT_FOUND_FROM_STRING = "Unable to find matching group closing char from string: [%s].";
    static final String LOGICAL_OPERATOR_NOT_FOUND_FROM_STRING = "Unable to find logical operator from string: [%s].";
    static final String FIELD_NOT_FOUND_FROM_STRING = "Unable to find search field from string: [%s].";
    static final String OPERATOR_NOT_FOUND = "Unable to find search operator.";
    static final String OPERATOR_NOT_FOUND_FROM_STRING = "Unable to find search operator from string: [%s].";
    static final String VALUE_NOT_FOUND = "Unable to find search value.";
    static final String VALUE_NOT_FOUND_FROM_STRING = "Unable to find search value from string: [%s].";

    private final String originalString;
    private final String unwrappedString;
    private final char[] chars;

    private SearchArgumentParser(String originalString, String stringToParse) {
        this.originalString = originalString;
        this.unwrappedString = unwrapGroup(stringToParse);
        this.chars = unwrappedString.toCharArray();
    }

    private int skipWhitespaces(int currentIndex) {
        int index = currentIndex;
        while (index < chars.length && Character.isWhitespace(chars[index])) {
            ++index;
        }
        return index;
    }

    private int nextOrError(int index, String errorTemplate) {
        int nextIndex = index + 1;
        if (nextIndex < chars.length) {
            return nextIndex;
        }
        throw TypeMismatchExceptions.create(Search.class,
                PARAM_SEARCH, originalString, String.format(errorTemplate, ""));
    }

    private static boolean isFrameworkSpecialChar(char ch) {
        return SearchOperator.isSpecialChar(ch)
                || LogicalOperator.isSpecialChar(ch)
                || ArgumentWrappingHelper.isSpecialChar(ch);
    }

    private static int getCharType(char ch) {
        // The 'type' value we assign to the char is not important as long as they are different from each other
        if (Character.isWhitespace(ch)) {
            return -1;
        }
        if (SearchOperator.isSpecialChar(ch)) {
            return -2;
        }
        if (LogicalOperator.isSpecialChar(ch)) {
            return -3;
        }
        if (ArgumentWrappingHelper.isSpecialChar(ch)) {
            return -4;
        }
        return -5;
    }

    private static int moveBackward(char[] chars, int startingFromIndex) {
        // Move words by words
        int beginIndex = startingFromIndex;

        while (beginIndex > 0
                && getCharType(chars[beginIndex]) == getCharType(chars[beginIndex - 1])) {
            --beginIndex;
        }

        return beginIndex;
    }

    private boolean isStartOfLogicalOperator(int currentIndex) {
        int index = skipWhitespaces(currentIndex);

        if (index >= chars.length) {
            return false;
        }

        int nextIndex = index;

        if (LogicalOperator.isSpecialChar(chars[nextIndex])) {
            // Move until we hit a non special char
            while (nextIndex < chars.length - 1 && LogicalOperator.isSpecialChar(chars[nextIndex + 1])) {
                ++nextIndex;
            }
        } else {
            // Move until we hit a whitespace or a clause wrapping char
            while (nextIndex < chars.length - 1
                    && !Character.isWhitespace(chars[nextIndex + 1])
                    && !ArgumentWrappingHelper.isGroupOpeningChar(chars[nextIndex + 1])) {
                ++nextIndex;
            }
        }

        // Note: This potential logical operator may or may not contain special characters
        String potentialLogicalOperator = unwrappedString.substring(index, nextIndex + 1);

        // The character before the logical operator, if any, should have a different type
        if (index > 0 && getCharType(chars[index - 1]) == getCharType(chars[index])) {
            return false;
        }

        // The character after the logical operator, if any, should have a different type
        if (nextIndex < chars.length - 1 && getCharType(chars[nextIndex + 1]) == getCharType(chars[nextIndex])) {
            return false;
        }

        return LogicalOperator.of(potentialLogicalOperator) != null;
    }

    private int findLogicalOperatorEndingBorderIndex(int currentIndex) {
        int index = skipWhitespaces(currentIndex);
        int nextIndex = index;

        while (index <= nextIndex && nextIndex <= chars.length && LogicalOperator.of(unwrappedString.substring(index, nextIndex)) == null) {
            ++nextIndex;
        }

        if (nextIndex >= chars.length) {
            return chars.length - 1;
        }

        return nextIndex - 1;
    }

    private int findFieldEndingBorderIndex(int currentIndex) {
        int index = skipWhitespaces(currentIndex);
        if (index == chars.length || isFrameworkSpecialChar(chars[index])) {
            return -1;
        }
        while (index < chars.length - 1 && !isFrameworkSpecialChar(chars[index + 1]) && !Character.isWhitespace(chars[index + 1])) {
            ++index;
        }
        return index;
    }

    private int findOperatorEndingIndex(int currentIndex) {
        int startingIndex = skipWhitespaces(currentIndex);
        int operatorEndingIndex = chars.length - 1;
        int index = chars.length - 1;
        int nextIndex = -1;

        while (true) {
            if (nextIndex == -1) {
                nextIndex = index + 1;
            } else {
                nextIndex = moveBackward(chars, index);
            }

            if (nextIndex < startingIndex) {
                break;
            }

            String operatorCandidate = unwrappedString.substring(startingIndex, nextIndex)
                    .trim()
                    .replaceAll("[\\s]+", " ")
                    .toLowerCase();

            if (SearchOperator.of(operatorCandidate) != null) {
                operatorEndingIndex = nextIndex - 1;
                break;
            }

            index = nextIndex - 1;
        }

        return operatorEndingIndex;
    }

    private int findValueEndingIndex(int currentIndex) {
        int index = skipWhitespaces(currentIndex);

        if (index < chars.length && ArgumentWrappingHelper.isQuoteChar(chars[index])) {
            return findClosingStringWrappingCharIndex(index);
        }

        if (index < chars.length && ArgumentWrappingHelper.isGroupOpeningChar(chars[index])) {
            return findGroupClosingCharIndex(index);
        }

        if (index == chars.length || isStartOfLogicalOperator(index) || chars[index] == ',') {
            return -1;
        }

        while (index < chars.length - 1 && !isStartOfLogicalOperator(index + 1) && chars[index + 1] != ',') {
            ++index;
        }

        return index;
    }

    private int findClosingStringWrappingCharIndex(int currentIndex) {
        int index = skipWhitespaces(currentIndex);

        if (index < chars.length && ArgumentWrappingHelper.isQuoteChar(chars[index])) {
            int openingCharIndex = index;

            while (index < chars.length - 1 && !ArgumentWrappingHelper.isMatchingChar(chars[openingCharIndex], chars[index + 1])) {
                ++index;
            }

            return index + 1;
        }

        while (index < chars.length - 1 && !isStartOfLogicalOperator(index)) {
            ++index;
        }

        return index;
    }

    private int findGroupClosingCharIndex(int currentIndex) {
        int index = skipWhitespaces(currentIndex);
        int openingChars = 0;

        while (index < chars.length) {
            if (ArgumentWrappingHelper.isGroupOpeningChar(chars[index])) {
                ++openingChars;
            } else if (ArgumentWrappingHelper.isGroupClosingChar(chars[index])) {
                --openingChars;
                if (openingChars == 0) {
                    return index;
                }
            }

            ++index;
        }

        throw TypeMismatchExceptions.create(Search.class,
                PARAM_SEARCH, originalString,
                String.format(MATCHING_CLOSING_CHAR_NOT_FOUND_FROM_STRING, unwrappedString.substring(currentIndex)));
    }

    /**
     * Parses atomic clauses and logical operators. Notice: As the name implies, composite clauses are NOT parsed!
     *
     * @return a list of either string (unparsed composite clauses), {@link AtomicSearchClause}
     * or {@link LogicalOperator}.
     */
    private List<Object> parseAtomicClausesAndLogicalOperators() {
        List<Object> results = new ArrayList<>();
        int startingIndex = 0;
        int endingIndex;

        while (startingIndex < chars.length - 1) {
            int firstNonWhitespaceCharIndex = skipWhitespaces(startingIndex);

            if (ArgumentWrappingHelper.isGroupOpeningChar(chars[firstNonWhitespaceCharIndex])) {
                endingIndex = findGroupClosingCharIndex(firstNonWhitespaceCharIndex);
                results.add(unwrappedString.substring(startingIndex, endingIndex + 1));
            } else {
                endingIndex = findFieldEndingBorderIndex(startingIndex);
                String field = endingIndex == -1
                        ? ""
                        : unwrappedString.substring(startingIndex, endingIndex + 1);
                if (StringUtils.isBlank(field)) {
                    throw TypeMismatchExceptions.create(Search.class,
                            PARAM_SEARCH, originalString,
                            String.format(FIELD_NOT_FOUND_FROM_STRING, unwrappedString.substring(startingIndex)));
                }

                startingIndex = nextOrError(endingIndex, OPERATOR_NOT_FOUND);
                endingIndex = findOperatorEndingIndex(startingIndex);
                SearchOperator operator = SearchOperator.of(unwrappedString.substring(startingIndex, endingIndex + 1));
                if (operator == null) {
                    throw TypeMismatchExceptions.create(Search.class,
                            PARAM_SEARCH, originalString,
                            String.format(OPERATOR_NOT_FOUND_FROM_STRING, unwrappedString.substring(startingIndex)));
                }

                startingIndex = nextOrError(endingIndex, VALUE_NOT_FOUND);
                endingIndex = findValueEndingIndex(startingIndex);
                String value = endingIndex == -1
                        ? ""
                        : unwrappedString.substring(startingIndex, endingIndex + 1);
                if (StringUtils.isBlank(value)) {
                    throw TypeMismatchExceptions.create(Search.class,
                            PARAM_SEARCH, originalString,
                            String.format(VALUE_NOT_FOUND_FROM_STRING, unwrappedString.substring(startingIndex)));
                }

                results.add(new AtomicSearchClause(field.trim(), operator, value.trim()));
            }

            startingIndex = endingIndex + 1;

            if (endingIndex < chars.length - 1) {
                endingIndex = findLogicalOperatorEndingBorderIndex(startingIndex);
                String logicalOperatorString = unwrappedString.substring(startingIndex, endingIndex + 1);
                LogicalOperator logicalOperator = LogicalOperator.of(logicalOperatorString);
                if (logicalOperator == null) {
                    throw TypeMismatchExceptions.create(Search.class,
                            PARAM_SEARCH, originalString,
                            String.format(LOGICAL_OPERATOR_NOT_FOUND_FROM_STRING, unwrappedString.substring(startingIndex)));
                }
                results.add(logicalOperator);
                startingIndex = endingIndex + 1;
            }
        }

        return results;
    }

    private SearchClause toSearchClause(Object searchClause) {
        if (searchClause instanceof String) {
            return new SearchArgumentParser(originalString, (String) searchClause).parseInternally();
        }
        if (searchClause instanceof AtomicSearchClause) {
            return new SearchClause((AtomicSearchClause) searchClause);
        }
        throw TypeMismatchExceptions.create(Search.class,
                PARAM_SEARCH, originalString, BAD_SEARCH_ARGUMENT_FORMAT);
    }

    private SearchClause parseInternally() {
        List<Object> elements = parseAtomicClausesAndLogicalOperators();

        if (elements.size() % 2 == 0) {
            // The number of elements must be an odd number
            throw TypeMismatchExceptions.create(Search.class,
                    PARAM_SEARCH, originalString, BAD_SEARCH_ARGUMENT_FORMAT);
        }

        if (elements.size() == 1) {
            return toSearchClause(elements.get(0));
        }

        // The elements are at the same level; and if we have both 'and' and 'or', then 'and' should take higher priority.
        // Therefore, the result should have root logical operator OR.
        SearchClause compositeOrClause = new SearchClause(LogicalOperator.OR);
        SearchClause compositeAndClause = null;

        // Notice that the expected format is <field><operator><value><logicalOperator><field><operator><value>...
        // Therefore, logical operators are supposed to be at odd positions. The others are clauses.
        for (int i = 1; i < elements.size(); i += 2) {
            LogicalOperator logicalOperator = (LogicalOperator) elements.get(i);

            if (logicalOperator == LogicalOperator.AND) {
                if (compositeAndClause == null) {
                    compositeAndClause = new SearchClause(LogicalOperator.AND);
                    compositeAndClause.add(toSearchClause(elements.get(i - 1)));
                }
                compositeAndClause.add(toSearchClause(elements.get(i + 1)));
            } else {
                if (compositeAndClause != null) {
                    compositeOrClause.add(compositeAndClause);
                    compositeAndClause = null;
                } else {
                    compositeOrClause.add(toSearchClause(elements.get(i - 1)));
                }

                if (i == elements.size() - 2) {
                    compositeOrClause.add(toSearchClause(elements.get(i + 1)));
                }
            }
        }

        if (compositeAndClause != null) {
            compositeOrClause.add(compositeAndClause);
        }

        return compositeOrClause.cleanup();
    }

    static SearchClause parse(String originalString) {
        return new SearchArgumentParser(originalString, originalString).parseInternally();
    }
}
