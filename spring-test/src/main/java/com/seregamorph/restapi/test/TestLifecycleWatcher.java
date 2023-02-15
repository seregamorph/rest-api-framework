package com.seregamorph.restapi.test;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 * Watch and store the test being executed to a thread-scoped store so that mock services can decide the correct json
 * responses.
 *
 * @see TestContext
 * @see AbstractSpringIT#watcher
 * @see AbstractSpringWebIT#watcher
 */
public class TestLifecycleWatcher extends TestWatcher {

    @Override
    protected void starting(Description description) {
        TestContext.setCurrentTest(new TestDescription(description));
    }

    @Override
    protected void finished(Description description) {
        TestContext.removeCurrentTest();
    }
}
