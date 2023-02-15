package com.seregamorph.restapi.test.utils;

import static com.seregamorph.restapi.common.Constants.ENDPOINT_ID;
import static com.seregamorph.restapi.test.utils.MoreMatchers.where;
import static com.seregamorph.restapi.test.utils.WebTestUtils.buildEndpoint;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import lombok.val;
import org.junit.Test;

public class WebTestUtilsTest extends AbstractUnitTest {

    @Test
    public void shouldFormatTwoParams() {
        val uri = buildEndpoint("/{parentId}/details/{childId}", 12, 34L);

        collector.checkThat(uri, hasToString("/12/details/34"));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenEmptyAndParam() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Redundant path variable to build template [] variables=[1]");

        buildEndpoint("", 1);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenMissedRequired() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Missing required path variables to build template [/{id}] variables=[]");
        expectedException.expectCause(
                where(Throwable::getMessage, equalTo("Not enough variable values available to expand 'id'")));

        buildEndpoint(ENDPOINT_ID);
    }

}
