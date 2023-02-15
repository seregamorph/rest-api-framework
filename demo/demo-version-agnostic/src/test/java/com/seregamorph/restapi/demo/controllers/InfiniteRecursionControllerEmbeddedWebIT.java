package com.seregamorph.restapi.demo.controllers;

import static org.junit.rules.ExpectedException.none;

import com.seregamorph.restapi.test.base.EmbeddedWebIT;
import com.seregamorph.restapi.test.base.InitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.web.client.ResourceAccessException;

@EmbeddedWebIT
@InitTest(InfiniteRecursionController.class)
public class InfiniteRecursionControllerEmbeddedWebIT extends AbstractBaseWebIT {

    @Rule
    public final ExpectedException expectedException = none();

    @Test
    public void getInfiniteJsonTestShouldFail() throws Exception {
        prepareExpectedExceptionForRecursiveJson();

        rest.post()
                .andReturn();
    }

    void prepareExpectedExceptionForRecursiveJson() {
        expectedException.expect(ResourceAccessException.class);
        expectedException.expectMessage("I/O error on POST request for "
                + "\"" + localhostUri() + InfiniteRecursionController.ENDPOINT + "\": "
                + "Unexpected content at the end of chunk");
    }
}
