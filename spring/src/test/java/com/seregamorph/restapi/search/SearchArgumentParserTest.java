package com.seregamorph.restapi.search;

import static com.seregamorph.restapi.search.SearchArgumentParser.BAD_SEARCH_ARGUMENT_FORMAT;
import static com.seregamorph.restapi.search.SearchArgumentParser.FIELD_NOT_FOUND_FROM_STRING;
import static com.seregamorph.restapi.search.SearchArgumentParser.LOGICAL_OPERATOR_NOT_FOUND_FROM_STRING;
import static com.seregamorph.restapi.search.SearchArgumentParser.MATCHING_CLOSING_CHAR_NOT_FOUND_FROM_STRING;
import static com.seregamorph.restapi.search.SearchArgumentParser.OPERATOR_NOT_FOUND;
import static com.seregamorph.restapi.search.SearchArgumentParser.OPERATOR_NOT_FOUND_FROM_STRING;
import static com.seregamorph.restapi.search.SearchArgumentParser.VALUE_NOT_FOUND;
import static com.seregamorph.restapi.search.SearchArgumentParser.VALUE_NOT_FOUND_FROM_STRING;
import static com.seregamorph.restapi.search.SearchOperator.EQUAL;
import static com.seregamorph.restapi.search.SearchOperator.GREATER_THAN_OR_EQUAL;
import static com.seregamorph.restapi.search.SearchOperator.IS;
import static com.seregamorph.restapi.search.SearchOperator.LESS_THAN_OR_EQUAL;
import static com.seregamorph.restapi.search.SearchOperator.NOT_EQUAL;
import static org.hamcrest.Matchers.equalTo;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import org.junit.Test;
import org.springframework.beans.TypeMismatchException;

public class SearchArgumentParserTest extends AbstractUnitTest {

    @Test
    public void shouldParseAtomicClause() {
        shouldParseCorrectly("age=1", new SearchClause("age", EQUAL, "1"));
        shouldParseCorrectly("(age=1)", new SearchClause("age", EQUAL, "1"));
    }

    @Test
    public void shouldParseArgumentsContainingMixedLogicalOperators() {
        shouldParseCorrectly("age=1 and age=2 and age=3 and age=4 and age=5",
                new SearchClause(LogicalOperator.AND)
                        .add("age", EQUAL, "1")
                        .add("age", EQUAL, "2")
                        .add("age", EQUAL, "3")
                        .add("age", EQUAL, "4")
                        .add("age", EQUAL, "5"));
        shouldParseCorrectly("age=1 or age=2 or age=3 or age=4 or age=5",
                new SearchClause(LogicalOperator.OR)
                        .add("age", EQUAL, "1")
                        .add("age", EQUAL, "2")
                        .add("age", EQUAL, "3")
                        .add("age", EQUAL, "4")
                        .add("age", EQUAL, "5"));
        shouldParseCorrectly("age=1 and age=2 or age=3 and age=4 or age=5",
                new SearchClause(LogicalOperator.OR)
                        .add(new SearchClause(LogicalOperator.AND)
                                .add("age", EQUAL, "1")
                                .add("age", EQUAL, "2"))
                        .add(new SearchClause(LogicalOperator.AND)
                                .add("age", EQUAL, "3")
                                .add("age", EQUAL, "4"))
                        .add("age", EQUAL, "5"));
        shouldParseCorrectly("age=1 or age=2 and age=3 or age=4 and age=5",
                new SearchClause(LogicalOperator.OR)
                        .add("age", EQUAL, "1")
                        .add(new SearchClause(LogicalOperator.AND)
                                .add("age", EQUAL, "2")
                                .add("age", EQUAL, "3"))
                        .add(new SearchClause(LogicalOperator.AND)
                                .add("age", EQUAL, "4")
                                .add("age", EQUAL, "5")));
        shouldParseCorrectly("age=1 and (age=2 or age=3) and (age=4 or age=5)",
                new SearchClause(LogicalOperator.AND)
                        .add("age", EQUAL, "1")
                        .add(new SearchClause(LogicalOperator.OR)
                                .add("age", EQUAL, "2")
                                .add("age", EQUAL, "3"))
                        .add(new SearchClause(LogicalOperator.OR)
                                .add("age", EQUAL, "4")
                                .add("age", EQUAL, "5")));
        shouldParseCorrectly("(age=1 or age=2) and (age=3 or age=4) and age=5",
                new SearchClause(LogicalOperator.AND)
                        .add(new SearchClause(LogicalOperator.OR)
                                .add("age", EQUAL, "1")
                                .add("age", EQUAL, "2"))
                        .add(new SearchClause(LogicalOperator.OR)
                                .add("age", EQUAL, "3")
                                .add("age", EQUAL, "4"))
                        .add("age", EQUAL, "5"));
        shouldParseCorrectly("age=1 and (age=2 or age=3 and age=4) or age=5",
                new SearchClause(LogicalOperator.OR)
                        .add(new SearchClause(LogicalOperator.AND)
                                .add("age", EQUAL, "1")
                                .add(new SearchClause(LogicalOperator.OR)
                                        .add("age", EQUAL, "2")
                                        .add(new SearchClause(LogicalOperator.AND)
                                                .add("age", EQUAL, "3")
                                                .add("age", EQUAL, "4"))))
                        .add("age", EQUAL, "5"));
        shouldParseCorrectly("(age=1 or age=2 and age=3 or age=4) and age=5",
                new SearchClause(LogicalOperator.AND)
                        .add(new SearchClause(LogicalOperator.OR)
                                .add("age", EQUAL, "1")
                                .add(new SearchClause(LogicalOperator.AND)
                                        .add("age", EQUAL, "2")
                                        .add("age", EQUAL, "3"))
                                .add("age", EQUAL, "4"))
                        .add("age", EQUAL, "5"));
    }

    @Test
    public void shouldParseAndActualArgument() {
        shouldParseCorrectly("startDate<=2020-02-11T08:48:55+10:30 and (endDate>=2020-02-11T08:48:55+10:30 or endDate is null)",
                new SearchClause(LogicalOperator.AND)
                        .add("startDate", LESS_THAN_OR_EQUAL, "2020-02-11T08:48:55+10:30")
                        .add(new SearchClause(LogicalOperator.OR)
                                .add("endDate", GREATER_THAN_OR_EQUAL, "2020-02-11T08:48:55+10:30")
                                .add("endDate", IS, "null")));
    }

    @Test
    public void shouldParseArgumentsContainingAndLogicalOperator() {
        shouldParseCorrectly("name=\"A name; quoted value containing a logical operator\"",
                new SearchClause("name", EQUAL, "\"A name; quoted value containing a logical operator\""));
        shouldParseCorrectly("name=\"A name and quoted value containing a logical operator\"",
                new SearchClause("name", EQUAL, "\"A name and quoted value containing a logical operator\""));
        shouldParseCorrectly("name=A name; name != whatever",
                new SearchClause(LogicalOperator.AND)
                        .add("name", EQUAL, "A name")
                        .add("name", NOT_EQUAL, "whatever"));
        shouldParseCorrectly("name=A name and name != whatever",
                new SearchClause(LogicalOperator.AND)
                        .add("name", EQUAL, "A name")
                        .add("name", NOT_EQUAL, "whatever"));
    }

    @Test
    public void shouldRejectEmptyString() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(BAD_SEARCH_ARGUMENT_FORMAT);

        SearchArgumentParser.parse("");
    }

    @Test
    public void shouldRejectBlankString() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(BAD_SEARCH_ARGUMENT_FORMAT);

        SearchArgumentParser.parse("");
    }

    @Test
    public void shouldRejectSingleCharString() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(BAD_SEARCH_ARGUMENT_FORMAT);

        SearchArgumentParser.parse(";");
    }

    @Test
    public void shouldRejectWhenNoField() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(String.format(FIELD_NOT_FOUND_FROM_STRING, "=18"));

        SearchArgumentParser.parse("=18");
    }

    @Test
    public void shouldRejectWhenNoOperator() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(OPERATOR_NOT_FOUND);

        SearchArgumentParser.parse("age");
    }

    @Test
    public void shouldRejectWhenNoValue() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(VALUE_NOT_FOUND);

        SearchArgumentParser.parse("age=");
    }

    @Test
    public void shouldRejectWhenBlankValue() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(String.format(VALUE_NOT_FOUND_FROM_STRING, ";"));

        SearchArgumentParser.parse("age= ;");
    }

    @Test
    public void shouldRejectRedundantAnd1() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(BAD_SEARCH_ARGUMENT_FORMAT);

        SearchArgumentParser.parse("age=1;");
    }

    @Test
    public void shouldRejectRedundantOr1() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(BAD_SEARCH_ARGUMENT_FORMAT);

        SearchArgumentParser.parse("age=1|");
    }

    @Test
    public void shouldRejectRedundantAnd2() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(BAD_SEARCH_ARGUMENT_FORMAT);

        SearchArgumentParser.parse("age=1 and");
    }

    @Test
    public void shouldRejectRedundantOr2() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(BAD_SEARCH_ARGUMENT_FORMAT);

        SearchArgumentParser.parse("age=1 or");
    }

    @Test
    public void shouldRejectWhenNoOperatorAfterAndAndField1() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(String.format(OPERATOR_NOT_FOUND_FROM_STRING, " value containing a logical operator"));

        // Logical operator is NOT wrapped in quotes -> logical operator is recognized
        // even though the 2nd clause is invalid
        SearchArgumentParser.parse("name=aname;unquoted value containing a logical operator");
    }

    @Test
    public void shouldRejectWhenNoOperatorAfterOrAndField1() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(String.format(OPERATOR_NOT_FOUND_FROM_STRING, " value containing a logical operator"));

        // Logical operator is NOT wrapped in quotes -> logical operator is recognized
        // even though the 2nd clause is invalid
        SearchArgumentParser.parse("name=aname|unquoted value containing a logical operator");
    }

    @Test
    public void shouldRejectWhenNoOperatorAfterAndAndField2() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(String.format(OPERATOR_NOT_FOUND_FROM_STRING, " value containing a logical operator"));

        // Logical operator is NOT wrapped in quotes -> logical operator is recognized
        // even though the 2nd clause is invalid
        SearchArgumentParser.parse("name=aname and unquoted value containing a logical operator");
    }

    @Test
    public void shouldRejectWhenNoOperatorAfterOrAndField2() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(String.format(OPERATOR_NOT_FOUND_FROM_STRING, " value containing a logical operator"));

        // Logical operator is NOT wrapped in quotes -> logical operator is recognized
        // even though the 2nd clause is invalid
        SearchArgumentParser.parse("name=aname or unquoted value containing a logical operator");
    }

    @Test
    public void shouldRejectWhenNoMatchingClosingCharForClause() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(String.format(MATCHING_CLOSING_CHAR_NOT_FOUND_FROM_STRING, "(age >= 18 or group.id = 5"));

        // Logical operator is NOT wrapped in quotes -> logical operator is recognized
        // even though the 2nd clause is invalid
        SearchArgumentParser.parse("name=aname and (age >= 18 or group.id = 5");
    }

    @Test
    public void shouldRejectWhenNoMatchingClosingCharForMultipleValue() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(String.format(MATCHING_CLOSING_CHAR_NOT_FOUND_FROM_STRING, "(ACTIVE, PENDING"));

        // Logical operator is NOT wrapped in quotes -> logical operator is recognized
        // even though the 2nd clause is invalid
        SearchArgumentParser.parse("status in (ACTIVE, PENDING");
    }

    @Test
    public void shouldRejectWhenMultipleValuesNotWrapped() {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage(String.format(LOGICAL_OPERATOR_NOT_FOUND_FROM_STRING, ", PENDING"));

        // Logical operator is NOT wrapped in quotes -> logical operator is recognized
        // even though the 2nd clause is invalid
        SearchArgumentParser.parse("status in ACTIVE, PENDING");
    }

    @Test
    public void shouldParseArgumentsContainingOrLogicalOperators() {
        shouldParseCorrectly("name=\"A name| quoted value containing a logical operator\"",
                new SearchClause("name", EQUAL, "\"A name| quoted value containing a logical operator\""));
        shouldParseCorrectly("name=\"A name or quoted value containing a logical operator\"",
                new SearchClause("name", EQUAL, "\"A name or quoted value containing a logical operator\""));
        shouldParseCorrectly("name=A name| name != whatever",
                new SearchClause(LogicalOperator.OR)
                        .add("name", EQUAL, "A name")
                        .add("name", NOT_EQUAL, "whatever"));
        shouldParseCorrectly("name=A name or name != whatever",
                new SearchClause(LogicalOperator.OR)
                        .add("name", EQUAL, "A name")
                        .add("name", NOT_EQUAL, "whatever"));
    }

    @Test
    public void shouldParseArgumentsContainingOperators() {
        shouldParseCorrectly("name=\"A name > another name\"",
                new SearchClause("name", EQUAL, "\"A name > another name\""));
        shouldParseCorrectly("name=\"A name >= another name\"",
                new SearchClause("name", EQUAL, "\"A name >= another name\""));
        shouldParseCorrectly("name=aname another = name",
                new SearchClause("name", EQUAL, "aname another = name"));
        shouldParseCorrectly("name=aname another not contains name",
                new SearchClause("name", EQUAL, "aname another not contains name"));
    }

    @Test
    public void shouldParseArgumentsContainingWrappingChars() {
        shouldParseCorrectly("name=\"A name () [] {}\"",
                new SearchClause("name", EQUAL, "\"A name () [] {}\""));
        shouldParseCorrectly("name=\"A name () [] {}\"  ",
                new SearchClause("name", EQUAL, "\"A name () [] {}\""));
        shouldParseCorrectly("name=aname () [] {}",
                new SearchClause("name", EQUAL, "aname () [] {}"));
    }

    @Test
    public void shouldParseArguments() {
        shouldParseCorrectly("field1=1|field2>=2",
                new SearchClause(LogicalOperator.OR)
                        .add("field1", EQUAL, "1")
                        .add("field2", GREATER_THAN_OR_EQUAL, "2"));
        shouldParseCorrectly("field1=1 or field2>=2",
                new SearchClause(LogicalOperator.OR)
                        .add("field1", EQUAL, "1")
                        .add("field2", GREATER_THAN_OR_EQUAL, "2"));
        shouldParseCorrectly("(field1=1)or(field2>=2)",
                new SearchClause(LogicalOperator.OR)
                        .add("field1", EQUAL, "1")
                        .add("field2", GREATER_THAN_OR_EQUAL, "2"));

        shouldParseCorrectly("field1=1;field2>=2",
                new SearchClause(LogicalOperator.AND)
                        .add("field1", EQUAL, "1")
                        .add("field2", GREATER_THAN_OR_EQUAL, "2"));
        shouldParseCorrectly("field1=1 and field2>=2",
                new SearchClause(LogicalOperator.AND)
                        .add("field1", EQUAL, "1")
                        .add("field2", GREATER_THAN_OR_EQUAL, "2"));
        shouldParseCorrectly("(field1=1)and(field2>=2)",
                new SearchClause(LogicalOperator.AND)
                        .add("field1", EQUAL, "1")
                        .add("field2", GREATER_THAN_OR_EQUAL, "2"));

        shouldParseCorrectly("(field1=1;(field2>=2|(field3<=3;field4 is null)))",
                new SearchClause(LogicalOperator.AND)
                        .add("field1", EQUAL, "1")
                        .add(new SearchClause(LogicalOperator.OR)
                                .add("field2", GREATER_THAN_OR_EQUAL, "2")
                                .add(new SearchClause(LogicalOperator.AND)
                                        .add("field3", LESS_THAN_OR_EQUAL, "3")
                                        .add("field4", IS, "null"))));
        shouldParseCorrectly("(field1=1;field2>=2|(field3<=3;field4 is null))",
                new SearchClause(LogicalOperator.OR)
                        .add(new SearchClause(LogicalOperator.AND)
                                .add("field1", EQUAL, "1")
                                .add("field2", GREATER_THAN_OR_EQUAL, "2"))
                        .add(new SearchClause(LogicalOperator.AND)
                                .add("field3", LESS_THAN_OR_EQUAL, "3")
                                .add("field4", IS, "null")));
        shouldParseCorrectly("(field1=1|((field2>=2|(field3<=3|field4 is null))))",
                new SearchClause(LogicalOperator.OR)
                        .add("field1", EQUAL, "1")
                        .add("field2", GREATER_THAN_OR_EQUAL, "2")
                        .add("field3", LESS_THAN_OR_EQUAL, "3")
                        .add("field4", IS, "null"));
    }

    private void shouldParseCorrectly(String input, SearchClause expectedSearchClause) {
        collector.checkThat(SearchArgumentParser.parse(input), equalTo(expectedSearchClause));
    }
}
