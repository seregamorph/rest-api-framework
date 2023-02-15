package com.seregamorph.restapi.search;

import static com.seregamorph.restapi.search.ArgumentWrappingHelper.DOUBLE_QUOTE_CHAR;
import static com.seregamorph.restapi.search.ArgumentWrappingHelper.GROUP_CLOSING_CHAR;
import static com.seregamorph.restapi.search.ArgumentWrappingHelper.GROUP_OPENING_CHAR;
import static com.seregamorph.restapi.search.ArgumentWrappingHelper.SINGLE_QUOTE_CHAR;
import static com.seregamorph.restapi.search.SearchOperator.EQUAL;
import static com.seregamorph.restapi.search.SearchOperator.GREATER_THAN;
import static com.seregamorph.restapi.search.SearchOperator.GREATER_THAN_OR_EQUAL;
import static com.seregamorph.restapi.search.SearchOperator.IN;
import static com.seregamorph.restapi.search.SearchOperator.IS;
import static com.seregamorph.restapi.search.SearchOperator.LESS_THAN;
import static com.seregamorph.restapi.search.SearchOperator.LESS_THAN_OR_EQUAL;
import static com.seregamorph.restapi.search.SearchOperator.LIKE;
import static com.seregamorph.restapi.search.SearchOperator.NOT_EQUAL;
import static com.seregamorph.restapi.search.SearchOperator.NOT_IN;
import static com.seregamorph.restapi.search.SearchOperator.NOT_LIKE;
import static org.hamcrest.Matchers.equalTo;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.TypeMismatchException;

@Slf4j
public class SearchConditionConverterTest extends AbstractUnitTest {

    private static final LocalDate LOCAL_DATE = LocalDate.now();
    private static final LocalDate LOCAL_DATE_2 = LocalDate.now().plusDays(1);

    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.now();
    private static final LocalDateTime LOCAL_DATE_TIME_2 = LocalDateTime.now().plusDays(1);

    private static final Instant INSTANT = Instant.now();
    private static final Instant INSTANT_2 = Instant.now().plusSeconds(1);

    private static final OffsetDateTime OFFSET_DATE_TIME = OffsetDateTime.now();
    private static final OffsetDateTime OFFSET_DATE_TIME_2 = OffsetDateTime.now().plusSeconds(1);

    // Tests with logical operators

    @Test
    public void shouldConvertSimpleOr() throws Exception {
        shouldConvertSearchConditionGroup(new SearchConditionGroup(LogicalOperator.OR)
                .addSearchCondition("intField", EQUAL, 1)
                .addSearchCondition("longField", GREATER_THAN_OR_EQUAL, 2L));
    }

    @Test
    public void shouldConvertSimpleAnd() throws Exception {
        shouldConvertSearchConditionGroup(new SearchConditionGroup(LogicalOperator.AND)
                .addSearchCondition("intField", EQUAL, 1)
                .addSearchCondition("longField", GREATER_THAN_OR_EQUAL, 2L));
    }

    @Test
    public void shouldConvertOrWith2Ands() throws Exception {
        shouldConvertSearchConditionGroup(new SearchConditionGroup(LogicalOperator.OR)
                .addSearchCondition(new SearchConditionGroup(LogicalOperator.AND)
                        .addSearchCondition("intField", EQUAL, 1)
                        .addSearchCondition("longField", GREATER_THAN_OR_EQUAL, 2L))
                .addSearchCondition(new SearchConditionGroup(LogicalOperator.AND)
                        .addSearchCondition("doubleField", LESS_THAN_OR_EQUAL, 3.0D)
                        .addSearchCondition("StringField", IS, SearchValue.NULL)));
    }

    @Test
    public void shouldConvertAndWith2Ors() throws Exception {
        shouldConvertSearchConditionGroup(new SearchConditionGroup(LogicalOperator.AND)
                .addSearchCondition(new SearchConditionGroup(LogicalOperator.OR)
                        .addSearchCondition("intField", EQUAL, 1)
                        .addSearchCondition("longField", GREATER_THAN_OR_EQUAL, 2L))
                .addSearchCondition(new SearchConditionGroup(LogicalOperator.OR)
                        .addSearchCondition("doubleField", LESS_THAN_OR_EQUAL, 3.0D)
                        .addSearchCondition("StringField", IS, SearchValue.NULL)));
    }

    @Test
    public void shouldConvertAndBetweenSingleAndOr() throws Exception {
        shouldConvertSearchConditionGroup(new SearchConditionGroup(LogicalOperator.AND)
                .addSearchCondition("intField", EQUAL, 1)
                .addSearchCondition(new SearchConditionGroup(LogicalOperator.OR)
                        .addSearchCondition("longField", GREATER_THAN_OR_EQUAL, 2L)
                        .addSearchCondition("doubleField", LESS_THAN_OR_EQUAL, 3.0D)));
    }

    @Test
    public void shouldConvertOrBetweenSingleAndAnd() throws Exception {
        shouldConvertSearchConditionGroup(new SearchConditionGroup(LogicalOperator.OR)
                .addSearchCondition("intField", EQUAL, 1)
                .addSearchCondition(new SearchConditionGroup(LogicalOperator.AND)
                        .addSearchCondition("longField", GREATER_THAN_OR_EQUAL, 2L)
                        .addSearchCondition("doubleField", LESS_THAN_OR_EQUAL, 3.0D)));
    }

    @Test
    public void shouldConvertComplexAnd() throws Exception {
        shouldConvertSearchConditionGroup(new SearchConditionGroup(LogicalOperator.AND)
                .addSearchCondition("intField", EQUAL, 1)
                .addSearchCondition(new SearchConditionGroup(LogicalOperator.OR)
                        .addSearchCondition("longField", GREATER_THAN_OR_EQUAL, 2L)
                        .addSearchCondition(new SearchConditionGroup(LogicalOperator.AND)
                                .addSearchCondition("doubleField", LESS_THAN_OR_EQUAL, 3.0D)
                                .addSearchCondition("StringField", IS, SearchValue.NULL))));
    }

    @Test
    public void shouldConvertComplexOr() throws Exception {
        shouldConvertSearchConditionGroup(new SearchConditionGroup(LogicalOperator.OR)
                .addSearchCondition("intField", EQUAL, 1)
                .addSearchCondition(new SearchConditionGroup(LogicalOperator.AND)
                        .addSearchCondition("longField", GREATER_THAN_OR_EQUAL, 2L)
                        .addSearchCondition(new SearchConditionGroup(LogicalOperator.OR)
                                .addSearchCondition("doubleField", LESS_THAN_OR_EQUAL, 3.0D)
                                .addSearchCondition("StringField", IS, SearchValue.NULL))));
    }

    @Test
    public void shouldTakeAndWithHigherPriority() throws Exception {
        SearchClause searchClause = SearchArgumentParser.parse("intField=1 and longField=2 or StringField is null");
        collector.checkThat(SearchConditionConverter.convertToSearchCondition(searchParam(), "whatever", searchClause),
                equalTo(new SearchConditionGroup(LogicalOperator.OR)
                        .addSearchCondition(new SearchConditionGroup(LogicalOperator.AND)
                                .addSearchCondition("intField", EQUAL, 1)
                                .addSearchCondition("longField", EQUAL, 2L))
                        .addSearchCondition("StringField", IS, SearchValue.NULL)));
    }

    @Test
    public void shouldTakeAndWithHigherPriority2() throws Exception {
        SearchClause searchClause = SearchArgumentParser.parse("intField=1 or longField=2 and StringField is null");
        collector.checkThat(SearchConditionConverter.convertToSearchCondition(searchParam(), "whatever", searchClause),
                equalTo(new SearchConditionGroup(LogicalOperator.OR)
                        .addSearchCondition("intField", EQUAL, 1)
                        .addSearchCondition(new SearchConditionGroup(LogicalOperator.AND)
                                .addSearchCondition("longField", EQUAL, 2L)
                                .addSearchCondition("StringField", IS, SearchValue.NULL))));
    }

    private void shouldConvertSearchConditionGroup(SearchConditionGroup group) throws Exception {
        SearchParam searchParam = searchParam();
        List<String> arguments = SearchArgumentGenerator.generate(group);
        log.debug("Generated {} search arguments", arguments.size());

        for (String argument : arguments) {
            log.debug("Argument: {}", argument);
            // We want immediate feedback. No ErrorCollector here.
            Assert.assertEquals(group, SearchConditionConverter.convertToSearchCondition(searchParam, "whatever", SearchArgumentParser.parse(argument)));
        }
    }

    @Test
    public void shouldThrowExceptionOnNonSearchableField() throws Exception {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage("Argument [foo=bar] for parameter [search] is invalid: [Field [foo] is not searchable.]");

        SearchConditionConverter.convertToSearchCondition(searchParam(), "foo=bar", SearchArgumentParser.parse("foo=bar"));
    }

    @Test
    public void shouldThrowExceptionOnIllegalOperator() throws Exception {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage("Argument [DoubleField=>bar] for parameter [search] is invalid: [Unable to find search operator from string: [=>bar].]");

        SearchConditionConverter.convertToSearchCondition(searchParam(), "DoubleField=>bar", SearchArgumentParser.parse("DoubleField=>bar"));
    }

    @Test
    public void shouldThrowExceptionOnConversionFailure() throws Exception {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage("Argument [intField>=foo] for parameter [search] is invalid: [Value conversion hit an error: [For input string: \"foo\"].]");

        SearchConditionConverter.convertToSearchCondition(searchParam(), "intField>=foo", SearchArgumentParser.parse("intField>=foo"));
    }

    @Test
    public void shouldConvertArgument() throws Exception {
        shouldConvertExactValueComparisonArgument("booleanField", true);
        shouldConvertExactValueComparisonArgument("booleanField", false);
        shouldConvertUnquotedCollectionComparisonArgument("booleanField", true, false);

        shouldConvertExactValueComparisonArgument("BooleanField", true);
        shouldConvertExactValueComparisonArgument("BooleanField", false);
        shouldConvertUnquotedCollectionComparisonArgument("BooleanField", true, false);

        shouldConvertNumericComparisonArgument("intField", 1);
        shouldConvertUnquotedCollectionComparisonArgument("intField", 1, -1);

        shouldConvertNumericComparisonArgument("IntegerField", 1);
        shouldConvertUnquotedCollectionComparisonArgument("IntegerField", 1, -1);

        shouldConvertNumericComparisonArgument("longField", 1L);
        shouldConvertUnquotedCollectionComparisonArgument("longField", 1L, -1L);

        shouldConvertNumericComparisonArgument("LongField", 1L);
        shouldConvertUnquotedCollectionComparisonArgument("LongField", 1L, -1L);

        shouldConvertNumericComparisonArgument("doubleField", 1.5D);
        shouldConvertUnquotedCollectionComparisonArgument("doubleField", 1.5D, -1.5D);

        shouldConvertNumericComparisonArgument("DoubleField", 1.5D);
        shouldConvertUnquotedCollectionComparisonArgument("DoubleField", 1.5D, -1.5D);

        shouldConvertUnquotedStringComparisonArgument("StringField", "foo");
        shouldConvertUnquotedCollectionComparisonArgument("StringField", "foo", "bar");

        shouldConvertQuotedStringComparisonArgument("StringField", "foo");
        shouldConvertQuotedCollectionComparisonArgument("StringField", "foo", "bar");

        shouldConvertNumericComparisonArgument("LocalDateField", LOCAL_DATE);
        shouldConvertQuotedCollectionComparisonArgument("LocalDateField", LOCAL_DATE, LOCAL_DATE_2);

        shouldConvertNumericComparisonArgument("LocalDateTimeField", LOCAL_DATE_TIME);
        shouldConvertQuotedCollectionComparisonArgument("LocalDateTimeField", LOCAL_DATE_TIME, LOCAL_DATE_TIME_2);

        shouldConvertNumericComparisonArgument("InstantField", INSTANT);
        shouldConvertQuotedCollectionComparisonArgument("InstantField", INSTANT, INSTANT_2);

        shouldConvertNumericComparisonArgument("OffsetDateTimeField", OFFSET_DATE_TIME);
        shouldConvertQuotedCollectionComparisonArgument("OffsetDateTimeField", OFFSET_DATE_TIME, OFFSET_DATE_TIME_2);

        shouldConvertExactValueComparisonArgument("SampleEnumField", SampleEnum.FIRST);
        shouldConvertUnquotedCollectionComparisonArgument("SampleEnumField", SampleEnum.FIRST, SampleEnum.SECOND);
        shouldConvertQuotedCollectionComparisonArgument("SampleEnumField", SampleEnum.FIRST, SampleEnum.SECOND);
    }

    // Boolean, Enum
    private void shouldConvertExactValueComparisonArgument(String field, Object value) throws Exception {
        shouldConvertArgumentCorrectly(argument(field, EQUAL, value),
                field, EQUAL, value);
        shouldConvertArgumentCorrectly(argument(field, NOT_EQUAL, value),
                field, NOT_EQUAL, value);
    }

    // Integer, Long, Double, Date-types, ...
    private void shouldConvertNumericComparisonArgument(String field, Object value) throws Exception {
        shouldConvertArgumentCorrectly(argument(field, EQUAL, value),
                field, EQUAL, value);
        shouldConvertArgumentCorrectly(argument(field, NOT_EQUAL, value),
                field, NOT_EQUAL, value);
        shouldConvertArgumentCorrectly(argument(field, GREATER_THAN_OR_EQUAL, value),
                field, GREATER_THAN_OR_EQUAL, value);
        shouldConvertArgumentCorrectly(argument(field, GREATER_THAN, value),
                field, GREATER_THAN, value);
        shouldConvertArgumentCorrectly(argument(field, LESS_THAN_OR_EQUAL, value),
                field, LESS_THAN_OR_EQUAL, value);
        shouldConvertArgumentCorrectly(argument(field, LESS_THAN, value),
                field, LESS_THAN, value);
    }

    // String
    @SuppressWarnings("SameParameterValue")
    private void shouldConvertUnquotedStringComparisonArgument(String field, Object value) throws Exception {
        shouldConvertArgumentCorrectly(argument(field, EQUAL, value),
                field, EQUAL, value);
        shouldConvertArgumentCorrectly(argument(field, NOT_EQUAL, value),
                field, NOT_EQUAL, value);
        shouldConvertArgumentCorrectly(argument(field, LIKE, value),
                field, LIKE, value);
        shouldConvertArgumentCorrectly(argument(field, NOT_LIKE, value),
                field, NOT_LIKE, value);
    }

    // String, Date-types, ...
    @SuppressWarnings("SameParameterValue")
    private void shouldConvertQuotedStringComparisonArgument(String field, Object value) throws Exception {
        for (char quoteChar : new char[] {SINGLE_QUOTE_CHAR, DOUBLE_QUOTE_CHAR}) {
            shouldConvertArgumentCorrectly(argument(field, EQUAL, wrap(value, quoteChar)),
                    field, EQUAL, value);
            shouldConvertArgumentCorrectly(argument(field, NOT_EQUAL, wrap(value, quoteChar)),
                    field, NOT_EQUAL, value);
            shouldConvertArgumentCorrectly(argument(field, LIKE, wrap(value, quoteChar)),
                    field, LIKE, value);
            shouldConvertArgumentCorrectly(argument(field, NOT_LIKE, wrap(value, quoteChar)),
                    field, NOT_LIKE, value);
        }
    }

    // Integer, Long, Double, String, ...
    private void shouldConvertUnquotedCollectionComparisonArgument(String field, Object value1, Object value2) throws Exception {
        shouldConvertArgumentCorrectly(argument(field, IN, wrap(value1, value2, GROUP_OPENING_CHAR, GROUP_CLOSING_CHAR)),
                field, IN, Arrays.asList(value1, value2));
        shouldConvertArgumentCorrectly(argument(field, NOT_IN, wrap(value1, value2, GROUP_OPENING_CHAR, GROUP_CLOSING_CHAR)),
                field, NOT_IN, Arrays.asList(value1, value2));
    }

    // String, Date-types, ...
    private void shouldConvertQuotedCollectionComparisonArgument(String field, Object value1, Object value2) throws Exception {
        for (char quoteChar : new char[] {SINGLE_QUOTE_CHAR, DOUBLE_QUOTE_CHAR}) {
            shouldConvertArgumentCorrectly(argument(field, IN, wrap(wrap(value1, quoteChar), wrap(value2, quoteChar), GROUP_OPENING_CHAR, GROUP_CLOSING_CHAR)),
                    field, IN, Arrays.asList(value1, value2));
            shouldConvertArgumentCorrectly(argument(field, NOT_IN, wrap(wrap(value1, quoteChar), wrap(value2, quoteChar), GROUP_OPENING_CHAR, GROUP_CLOSING_CHAR)),
                    field, NOT_IN, Arrays.asList(value1, value2));
        }
    }

    private void shouldConvertArgumentCorrectly(String argument,
                                                String expectedField, SearchOperator expectedOperator, Object expectedValue)
            throws Exception {
        log.debug("Argument: {}", argument);
        SearchCondition condition = SearchConditionConverter.convertToSearchCondition(searchParam(), "whatever", SearchArgumentParser.parse(argument));
        SearchCondition expectedCondition = new SingleSearchCondition(expectedField, expectedOperator, expectedValue);
        // We want immediate feedback. No ErrorCollector here.
        Assert.assertEquals(expectedCondition, condition);
    }

    private static SearchParam searchParam() throws Exception {
        return SearchConditionConverterTest.class
                .getDeclaredMethod("doSomething", Search.class)
                .getParameters()[0]
                .getAnnotation(SearchParam.class);
    }

    private static String argument(String field, SearchOperator operator, Object value) {
        return String.format("%s %s %s", field, operator.getOperator(), value);
    }

    private static String wrap(Object value1, Object value2, char... chars) {
        return String.format("%s%s, %s%s", chars[0], value1, value2, chars[1]);
    }

    private static String wrap(Object value, char ch) {
        return String.format("%s%s%s", ch, value, ch);
    }

    private enum SampleEnum {
        FIRST,
        SECOND,
        @SuppressWarnings("unused")
        THIRD
    }

    @SuppressWarnings("unused")
    private static void doSomething(
            @SearchParam({
                    @SearchParam.Field(name = "booleanField", type = boolean.class),
                    @SearchParam.Field(name = "BooleanField", type = Boolean.class),
                    @SearchParam.Field(name = "intField", type = int.class),
                    @SearchParam.Field(name = "IntegerField", type = Integer.class),
                    @SearchParam.Field(name = "longField", type = long.class),
                    @SearchParam.Field(name = "LongField", type = Long.class),
                    @SearchParam.Field(name = "doubleField", type = double.class),
                    @SearchParam.Field(name = "DoubleField", type = Double.class),
                    @SearchParam.Field(name = "StringField"),
                    @SearchParam.Field(name = "LocalDateField", type = LocalDate.class),
                    @SearchParam.Field(name = "LocalDateTimeField", type = LocalDateTime.class),
                    @SearchParam.Field(name = "InstantField", type = Instant.class),
                    @SearchParam.Field(name = "OffsetDateTimeField", type = OffsetDateTime.class),
                    @SearchParam.Field(name = "SampleEnumField", type = SampleEnum.class)
            })
            final Search search
    ) {
        // Intentionally left blank
    }
}
