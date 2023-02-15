package com.seregamorph.restapi.test.utils;

import static com.seregamorph.restapi.test.utils.MoreMatchers.hasRelaxedValue;
import static com.seregamorph.restapi.test.utils.MoreMatchers.matches;
import static com.seregamorph.restapi.test.utils.MoreMatchers.predicate;
import static com.seregamorph.restapi.test.utils.MoreMatchers.softOrdered;
import static com.seregamorph.restapi.test.utils.MoreMatchers.strictOrdered;
import static com.seregamorph.restapi.test.utils.MoreMatchers.where;
import static java.util.Collections.singleton;
import static java.util.Comparator.naturalOrder;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import lombok.Data;
import lombok.val;
import org.junit.Test;

@SuppressWarnings("UnpredictableBigDecimalConstructorCall")
public class MoreMatchersTest extends AbstractUnitTest {

    private static final String ARGUMENT = "str";
    private static final String SUCCESS_MESSAGE = "Should have valid length";
    private static final String FAIL_MESSAGE = "Should have invalid length";

    @Test
    public void whereShouldGiveReferenceMethodDiagnosticsNotNull() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: Object that matches not null after call SampleResource.getName\n"
                + "     but: was null");

        val user = new SampleResource();
        collector.checkThat(user, where(SampleResource::getName, notNullValue()));
    }

    @SuppressWarnings({"Convert2MethodRef", "CodeBlock2Expr"})
    @Test
    public void whereShouldGiveReferenceLambdaMethodDiagnostics() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage(allOf(
                startsWith("\nExpected: Object that matches not null after call MoreMatchersTest.java:"),
                endsWith("\n     but: was null")
        ));

        val user = new SampleResource();
        collector.checkThat(user, where(sampleResource -> {
            return sampleResource.getName();
        }, notNullValue()));
    }

    @Test
    public void whereShouldGiveReferenceMethodDiagnosticsNull() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Expected: Object that matches null after call SampleResource.getName\n"
                + "     but: was \"value\"");

        val user = new SampleResource()
                .setName("value");
        collector.checkThat(user, where(SampleResource::getName, nullValue()));
    }

    @Test
    public void predicateShouldSuccess() {
        collector.checkThat(ARGUMENT, predicate(str -> str.length() == ARGUMENT.length(), SUCCESS_MESSAGE));
    }

    @Test
    public void predicateShouldFail() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage(FAIL_MESSAGE);

        collector.checkThat(ARGUMENT, predicate(str -> str.length() != ARGUMENT.length(), FAIL_MESSAGE));
    }

    @Test
    public void strictOrderedSingleShouldSuccess() {
        collector.checkThat(singleton(1), strictOrdered(naturalOrder()));
    }

    @Test
    public void strictOrderedEqualShouldFail() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Found equal elements 1 and 1");

        collector.checkThat(Arrays.asList(1, 1), strictOrdered(naturalOrder()));
    }

    @Test
    public void softOrderedEqualShouldSuccess() {
        collector.checkThat(Arrays.asList(1, 1, 2), softOrdered(naturalOrder()));
    }

    @Test
    public void orderedEqualShouldFailIfIncorrectOrdered() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("Found unordered elements 2 and 0");

        collector.checkThat(Arrays.asList(1, 2, 0), strictOrdered(naturalOrder()));
    }

    @Test
    public void orderedEqualShouldSuccessIfCorrectOrdered() {
        collector.checkThat(Arrays.asList(1, 2), strictOrdered(naturalOrder()));
    }

    @Test
    public void testMatchesRegex() {
        collector.checkThat("123", matches("^\\d+$"));
        collector.checkThat("ddd", not(matches("^\\d+$")));
    }

    @Test
    public void intValueShouldNotHaveRelaxedLongValue() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <20>\n"
                + "     but: was <10>");

        collector.checkThat(10, hasRelaxedValue(20L));
    }

    @Test
    public void doubleValueShouldNotHaveRelaxedLongValue() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <1>\n"
                + "     but: was <1.0>");

        collector.checkThat(1.0d, hasRelaxedValue(1L));
    }

    @Test
    public void nullValueShouldNotHaveRelaxedLongValue() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <10>\n"
                + "     but: was null");

        collector.checkThat(null, hasRelaxedValue(10L));
    }

    @Test
    public void stringValueShouldNotHaveRelaxedLongValue() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <10>\n"
                + "     but: was \"10\"");

        collector.checkThat("10", hasRelaxedValue(10L));
    }

    @Test
    public void intValueShouldHaveRelaxedIntValue() {
        collector.checkThat(10, hasRelaxedValue(10));
    }

    @Test
    public void intValueShouldHaveRelaxedLongValue() {
        collector.checkThat(10, hasRelaxedValue(10L));
    }

    @Test
    public void longValueShouldHaveRelaxedLongValue() {
        collector.checkThat(10L, hasRelaxedValue(10L));
    }

    @Test
    public void longValueShouldHaveRelaxedIntValue() {
        collector.checkThat(10L, hasRelaxedValue(10));
    }

    @Test
    public void offsetDateTimeMatcherShouldRecognizeSameTimeZone() {
        collector.checkThat("2020-01-02T03:04:05.678+07:00",
                hasRelaxedValue(OffsetDateTime.parse("2020-01-02T03:04:05.678+07:00")));
    }

    @Test
    public void offsetDateTimeMatcherShouldIgnoreTimeZone() {
        collector.checkThat("2020-01-02T03:04:05.678+07:00",
                hasRelaxedValue(OffsetDateTime.parse("2020-01-02T04:04:05.678+08:00")));
    }

    @Test
    public void floatShouldHaveRelaxedBigDecimal() {
        collector.checkThat(1.23f, hasRelaxedValue(new BigDecimal("1.23")));
        collector.checkThat(1.23f, hasRelaxedValue(new BigDecimal("1.230")));
        collector.checkThat(1.23f, hasRelaxedValue(new BigDecimal(1.23f)));
    }

    @Test
    public void doubleShouldHaveRelaxedBigDecimal() {
        collector.checkThat(1.23d, hasRelaxedValue(new BigDecimal("1.23")));
        collector.checkThat(1.23d, hasRelaxedValue(new BigDecimal("1.230")));
        collector.checkThat(1.23d, hasRelaxedValue(new BigDecimal(1.23d)));
    }

    @Test
    public void bigDecimalShouldHaveRelaxedFloat() {
        collector.checkThat(new BigDecimal("1.23"), hasRelaxedValue(1.23f));
        collector.checkThat(new BigDecimal("1.230"), hasRelaxedValue(1.23f));
        collector.checkThat(new BigDecimal(1.23f), hasRelaxedValue(1.23f));
    }

    @Test
    public void bigDecimalShouldHaveRelaxedDouble() {
        collector.checkThat(new BigDecimal("1.23"), hasRelaxedValue(1.23d));
        collector.checkThat(new BigDecimal("1.230"), hasRelaxedValue(1.23d));
        collector.checkThat(new BigDecimal(1.23d), hasRelaxedValue(1.23d));
    }

    @Test
    public void floatShouldHaveRelaxedDouble() {
        collector.checkThat(1.23f, hasRelaxedValue(1.23d));
    }

    @Test
    public void doubleShouldHaveRelaxedFloat() {
        collector.checkThat(1.23d, hasRelaxedValue(1.23f));
    }

    @Test
    public void floatShouldHaveRelaxedFloat() {
        collector.checkThat(1.23f, hasRelaxedValue(1.23f));
    }

    @Test
    public void doubleShouldHaveRelaxedDouble() {
        collector.checkThat(1.23d, hasRelaxedValue(1.23d));
    }

    @Test
    public void floatShouldNotMatchWrongFloatValueLess() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <1.1>\n"
                + "     but: was <1.2F>");

        collector.checkThat(1.2f, hasRelaxedValue(1.1f));
    }

    @Test
    public void floatShouldNotMatchWrongFloatValueGreater() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <1.2>\n"
                + "     but: was <1.1F>");

        collector.checkThat(1.1f, hasRelaxedValue(1.2f));
    }

    @Test
    public void doubleShouldNotMatchWrongDoubleValueLess() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <1.1>\n"
                + "     but: was <1.2>");

        collector.checkThat(1.2d, hasRelaxedValue(1.1d));
    }

    @Test
    public void doubleShouldNotMatchWrongDoubleValueGreater() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <1.2>\n"
                + "     but: was <1.1>");

        collector.checkThat(1.1d, hasRelaxedValue(1.2d));
    }

    @Test
    public void doubleShouldNotMatchWrongFloatValueLess() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <1.1>\n"
                + "     but: was <1.2>");

        collector.checkThat(1.2d, hasRelaxedValue(1.1f));
    }

    @Test
    public void doubleShouldNotMatchWrongFloatValueGreater() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <1.2>\n"
                + "     but: was <1.1>");

        collector.checkThat(1.1d, hasRelaxedValue(1.2f));
    }

    @Test
    public void floatShouldNotMatchWrongDoubleValueLess() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <1.1>\n"
                + "     but: was <1.2F>");

        collector.checkThat(1.2f, hasRelaxedValue(1.1d));
    }

    @Test
    public void floatShouldNotMatchWrongDoubleValueGreater() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <1.2>\n"
                + "     but: was <1.1F>");

        collector.checkThat(1.1f, hasRelaxedValue(1.2d));
    }

    @Test
    public void floatShouldNotMatchWrongBigDecimalValueLess() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <1.1>\n"
                + "     but: was <1.2F>");

        collector.checkThat(1.2f, hasRelaxedValue(new BigDecimal("1.1")));
    }

    @Test
    public void floatShouldNotMatchWrongBigDecimalValueGreater() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <1.2>\n"
                + "     but: was <1.1F>");

        collector.checkThat(1.1f, hasRelaxedValue(new BigDecimal("1.2")));
    }

    @Test
    public void doubleShouldNotMatchWrongBigDecimalValueLess() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <1.1>\n"
                + "     but: was <1.2>");

        collector.checkThat(1.2d, hasRelaxedValue(new BigDecimal("1.1")));
    }

    @Test
    public void doubleShouldNotMatchWrongBigDecimalValueGreater() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <1.2>\n"
                + "     but: was <1.1>");

        collector.checkThat(1.1d, hasRelaxedValue(new BigDecimal("1.2")));
    }

    @Test
    public void bigDecimalShouldNotMatchWrongFloatValueLess() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <1.1>\n"
                + "     but: was <1.2>");

        collector.checkThat(new BigDecimal("1.2"), hasRelaxedValue(1.1f));
    }

    @Test
    public void bigDecimalShouldNotMatchWrongFloatValueGreater() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <1.2>\n"
                + "     but: was <1.1>");

        collector.checkThat(new BigDecimal("1.1"), hasRelaxedValue(1.2f));
    }

    @Test
    public void bigDecimalShouldNotMatchWrongDoubleValueLess() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <1.1>\n"
                + "     but: was <1.2>");

        collector.checkThat(new BigDecimal("1.2"), hasRelaxedValue(1.1d));
    }

    @Test
    public void bigDecimalShouldNotMatchWrongDoubleValueGreater() {
        expectedException.expect(AssertionError.class);
        expectedException.expectMessage("\n"
                + "Expected: <1.2>\n"
                + "     but: was <1.1>");

        collector.checkThat(new BigDecimal("1.1"), hasRelaxedValue(1.2d));
    }

    @Data
    private static class SampleResource {

        private String name;
    }
}
