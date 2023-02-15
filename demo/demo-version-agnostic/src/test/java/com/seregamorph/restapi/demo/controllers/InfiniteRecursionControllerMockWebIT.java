package com.seregamorph.restapi.demo.controllers;

import static org.hamcrest.Matchers.oneOf;
import static org.junit.rules.ExpectedException.none;

import com.seregamorph.restapi.test.base.InitTest;
import com.seregamorph.restapi.test.base.MockWebIT;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@MockWebIT
@InitTest(InfiniteRecursionController.class)
public class InfiniteRecursionControllerMockWebIT extends AbstractBaseWebIT {

    @Rule
    public final ExpectedException expectedException = none();

    @Test
    public void getInfiniteJsonTestShouldFail() throws Exception {
        expectedException.expect(AssertionError.class);
        // Please note that catching bad json serialization issue is unreliable (even if you run the same test few times
        // in a row), but it gives more detailed exception message.
        // In any case, it has fallback validation of HttpMessageNotWritableException
        // see MockMvcResponseContentResultHandler
        expectedException.expectMessage(oneOf(
                "Content-Type is \"application/json\", but the content is not parsable",
                "Failed to serialize response"
        ));

        rest.post()
                .andReturn();
    }

}
