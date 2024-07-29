package com.seregamorph.restapi.search;

import static org.hamcrest.Matchers.equalTo;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class SearchValueConverterTest extends AbstractUnitTest {

    @Test
    public void shouldConvertSpecialValue() throws Exception {
        String fieldName = "StringField";
        SearchParam.Field field = getSearchField(fieldName);

        collector.checkThat(SearchValueConverter.convertSingleObject(field.type(), true, "  null  "),
                equalTo(SearchValue.NULL));
        collector.checkThat(SearchValueConverter.convertSingleObject(field.type(), true, "  empty  "),
                equalTo(SearchValue.EMPTY));
        collector.checkThat(SearchValueConverter.convertSingleObject(field.type(), true, "  blank  "),
                equalTo(SearchValue.BLANK));
    }

    @Test
    public void shouldConvertSinglePrimitiveBooleanValue() throws Exception {
        String fieldName = "booleanField";
        shouldConvertSingleValue(fieldName, "  true  ", true);
        shouldConvertSingleValue(fieldName, "  TRUE  ", true);
        shouldConvertSingleValue(fieldName, "  false  ", false);
        shouldConvertSingleValue(fieldName, "  FALSE  ", false);
    }

    @Test
    public void shouldConvertSingleBooleanValue() throws Exception {
        String fieldName = "BooleanField";
        shouldConvertSingleValue(fieldName, "  true  ", Boolean.TRUE);
        shouldConvertSingleValue(fieldName, "  TRUE  ", Boolean.TRUE);
        shouldConvertSingleValue(fieldName, "  false  ", Boolean.FALSE);
        shouldConvertSingleValue(fieldName, "  FALSE  ", Boolean.FALSE);
    }

    @Test
    public void shouldConvertMultipleBooleanValues() throws Exception {
        String rawValue = "true,  false,  TRUE, FALSE";
        List<Object> expectedValue = Arrays.asList(Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);
        shouldConvertMultipleValues("booleanField", rawValue, expectedValue);
        shouldConvertMultipleValues("BooleanField", rawValue, expectedValue);
    }

    @Test
    public void shouldConvertSingleIntegerValue() throws Exception {
        shouldConvertSingleValue("intField", "  1  ", 1);
        shouldConvertSingleValue("IntegerField", "  -1  ", -1);
    }

    @Test
    public void shouldConvertMultipleIntegerValues() throws Exception {
        String rawValue = "-2147483648,  0,  2147483647";
        List<Object> expectedValue = Arrays.asList(Integer.MIN_VALUE, 0, Integer.MAX_VALUE);
        shouldConvertMultipleValues("intField", rawValue, expectedValue);
        shouldConvertMultipleValues("IntegerField", rawValue, expectedValue);
    }

    @Test
    public void shouldConvertSingleLongValue() throws Exception {
        shouldConvertSingleValue("longField", "  1  ", 1L);
        shouldConvertSingleValue("LongField", "  -1  ", -1L);
    }

    @Test
    public void shouldConvertMultipleLongValues() throws Exception {
        String rawValue = "-9223372036854775808,  0,   9223372036854775807";
        List<Object> expectedValue = Arrays.asList(Long.MIN_VALUE, 0L, Long.MAX_VALUE);
        shouldConvertMultipleValues("longField", rawValue, expectedValue);
        shouldConvertMultipleValues("LongField", rawValue, expectedValue);
    }

    @Test
    public void shouldConvertSingleDoubleValue() throws Exception {
        shouldConvertSingleValue("doubleField", "  1.5  ", 1.5);
        shouldConvertSingleValue("DoubleField", "  -1.5  ", -1.5);
    }

    @Test
    public void shouldConvertMultipleDoubleValues() throws Exception {
        String rawValue = "4.9E-324,  0,  1.7976931348623157E308";
        List<Object> expectedValue = Arrays.asList(Double.MIN_VALUE, 0D, Double.MAX_VALUE);
        shouldConvertMultipleValues("doubleField", rawValue, expectedValue);
        shouldConvertMultipleValues("DoubleField", rawValue, expectedValue);
    }

    @Test
    public void shouldConvertSingleStringValue() throws Exception {
        shouldConvertSingleValue("StringField", "  Foo  Bar  ", "Foo Bar");
    }

    @Test
    public void shouldConvertMultipleStringValues() throws Exception {
        shouldConvertMultipleValues("StringField",
                "\"foo bar\", \"baz\", \"whatever\"",
                Arrays.asList("foo bar", "baz", "whatever"));
    }

    @Test
    public void shouldConvertSingleLocalDateValue() throws Exception {
        shouldConvertSingleValue("LocalDateField", "  2020-02-02  ",
                LocalDate.of(2020, 2, 2));
    }

    @Test
    public void shouldConvertMultipleLocalDateValues() throws Exception {
        String rawValue = "\"2019-12-31\", \"2020-01-01\", \"2020-02-29\"";
        LocalDate localDate1 = LocalDate.of(2019, 12, 31);
        LocalDate localDate2 = LocalDate.of(2020, 1, 1);
        LocalDate localDate3 = LocalDate.of(2020, 2, 29);
        List<Object> expectedValue = Arrays.asList(localDate1, localDate2, localDate3);
        shouldConvertMultipleValues("LocalDateField", rawValue, expectedValue);
    }

    @Test
    public void shouldConvertSingleLocalDateTimeFieldValue() throws Exception {
        shouldConvertSingleValue("LocalDateTimeField", "  2020-02-02T14:25:45  ",
                LocalDateTime.of(2020, 2, 2, 14, 25, 45));
    }

    @Test
    public void shouldConvertMultipleLocalDateTimeValues() throws Exception {
        String rawValue = "\"2019-12-31T10:15:30\", \"2020-01-01T14:25:45\", \"2020-02-29T23:59:59\"";
        LocalDateTime localDateTime1 = LocalDateTime.of(2019, 12, 31, 10, 15, 30);
        LocalDateTime localDateTime2 = LocalDateTime.of(2020, 1, 1, 14, 25, 45);
        LocalDateTime localDateTime3 = LocalDateTime.of(2020, 2, 29, 23, 59, 59);
        List<Object> expectedValue = Arrays.asList(localDateTime1, localDateTime2, localDateTime3);
        shouldConvertMultipleValues("LocalDateTimeField", rawValue, expectedValue);
    }

    @Test
    public void shouldConvertSingleInstantValue() throws Exception {
        shouldConvertSingleValue("InstantField", "  2020-02-02T14:25:45Z  ",
                instant(2020, 2, 2, 14, 25, 45));
    }

    @Test
    public void shouldConvertMultipleInstantValues() throws Exception {
        String rawValue = "\"2019-12-31T10:15:30Z\", \"2020-01-01T14:25:45Z\", \"2020-02-29T23:59:59Z\"";
        Instant instant1 = instant(2019, 12, 31, 10, 15, 30);
        Instant instant2 = instant(2020, 1, 1, 14, 25, 45);
        Instant instant3 = instant(2020, 2, 29, 23, 59, 59);
        List<Object> expectedValue = Arrays.asList(instant1, instant2, instant3);
        shouldConvertMultipleValues("InstantField", rawValue, expectedValue);
    }

    @Test
    public void shouldConvertSingleOffsetDateTimeFieldValue() throws Exception {
        shouldConvertSingleValue("OffsetDateTimeField", "  2020-02-02T14:25:45+01:00  ",
                offsetDateTime(2020, 2, 2, 14, 25, 45));
    }

    @Test
    public void shouldConvertMultipleOffsetDateTimeValues() throws Exception {
        String rawValue = "\"2019-12-31T10:15:30+01:00\", \"2020-01-01T14:25:45+01:00\", \"2020-02-29T23:59:59+01:00\"";
        OffsetDateTime offsetDateTime1 = offsetDateTime(2019, 12, 31, 10, 15, 30);
        OffsetDateTime offsetDateTime2 = offsetDateTime(2020, 1, 1, 14, 25, 45);
        OffsetDateTime offsetDateTime3 = offsetDateTime(2020, 2, 29, 23, 59, 59);
        List<Object> expectedValue = Arrays.asList(offsetDateTime1, offsetDateTime2, offsetDateTime3);
        shouldConvertMultipleValues("OffsetDateTimeField", rawValue, expectedValue);
    }

    @Test
    public void shouldConvertSingleEnumValue() throws Exception {
        shouldConvertSingleValue("SampleEnumField", "  FIRST  ", SampleEnum.FIRST);
    }

    @Test
    public void shouldConvertMultipleEnumValues() throws Exception {
        String rawValue = "\"FIRST\", \"SECOND\", \"THIRD\"";
        List<Object> expectedValue = Arrays.asList(SampleEnum.FIRST, SampleEnum.SECOND, SampleEnum.THIRD);
        shouldConvertMultipleValues("SampleEnumField", rawValue, expectedValue);
    }

    private void shouldConvertMultipleValues(String fieldName, String rawValue, Object expectedValue) throws Exception {
        SearchParam.Field field = getSearchField(fieldName);

        Object result = SearchValueConverter.convertMultipleObjects(field.type(), false, roundBrackets(rawValue));
        collector.checkThat(result, equalTo(expectedValue));

        result = SearchValueConverter.convertMultipleObjects(field.type(), false, spaces(rawValue));
        collector.checkThat(result, equalTo(expectedValue));
    }

    private void shouldConvertSingleValue(String fieldName, String rawValue, Object expectedValue) throws Exception {
        SearchParam.Field field = getSearchField(fieldName);
        Object result = SearchValueConverter.convertSingleObject(field.type(), false, rawValue);
        collector.checkThat(result, equalTo(expectedValue));
    }

    private static Instant instant(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second).atZone(ZoneId.of("UTC")).toInstant();
    }

    private static OffsetDateTime offsetDateTime(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second).atZone(ZoneId.of("UTC+1")).toOffsetDateTime();
    }

    private SearchParam.Field getSearchField(String fieldName) throws Exception {
        SearchParam.Field[] fields = SearchValueConverterTest.class
                .getDeclaredMethod("doSomething", Search.class)
                .getParameters()[0]
                .getAnnotation(SearchParam.class)
                .value();

        for (SearchParam.Field field : fields) {
            if (StringUtils.equals(field.name(), fieldName)) {
                return field;
            }
        }

        throw new IllegalArgumentException("Invalid field: " + fieldName);
    }

    private static String spaces(String string) {
        return ' ' + string + ' ';
    }

    private static String roundBrackets(String string) {
        return " (" + string + ") ";
    }

    private enum SampleEnum {
        FIRST,
        SECOND,
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
