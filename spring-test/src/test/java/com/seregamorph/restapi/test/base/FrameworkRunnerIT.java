package com.seregamorph.restapi.test.base;

import static org.junit.Assert.assertTrue;

import com.seregamorph.restapi.test.base.setup.GetAllSetup;
import org.junit.Test;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Note: actually it's not a test, but a support class from {@link FrameworkRunnerTest} (cannot be nested because of
 * static @{@link InitTest} method).
 */
@TestExecutionListeners(inheritListeners = false)
@InitTest(FrameworkRunnerIT.TestController.class)
public class FrameworkRunnerIT {

    @InitTest
    public static GetAllSetup getAllSetup() {
        return new GetAllSetup("/all");
    }

    @Test
    public void simpleTest() {
        // no assertions in this test to validate call from FrameworkRunnerTest.shouldFilterTestExecutions
        FrameworkRunnerTest.simpleTestExecuted.set(true);

        // CR
        assertTrue(FrameworkRunnerTest.simpleTestExecuted.get());
    }

    @RequestMapping("/api/test")
    static class TestController {

    }

}
