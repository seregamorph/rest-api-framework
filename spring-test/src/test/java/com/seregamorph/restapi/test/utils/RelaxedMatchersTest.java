package com.seregamorph.restapi.test.utils;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static com.seregamorph.restapi.test.utils.RelaxedMatchers.hasRelaxedValue;

public class RelaxedMatchersTest extends AbstractUnitTest {

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
}