package com.seregamorph.restapi.test.utils;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import lombok.Data;
import lombok.val;
import org.junit.Test;

import java.util.Arrays;

import static com.seregamorph.restapi.test.utils.MoreMatchers.*;
import static java.util.Collections.singleton;
import static java.util.Comparator.naturalOrder;
import static org.hamcrest.Matchers.*;

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

    @Data
    private static class SampleResource {

        private String name;
    }
}
