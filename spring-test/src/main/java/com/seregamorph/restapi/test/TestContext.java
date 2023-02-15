package com.seregamorph.restapi.test;

import com.seregamorph.restapi.test.mocks.MockJsonDataStore;
import lombok.experimental.UtilityClass;

/**
 * A thread - scoped store of {@link TestDescription}. Will be used in mock services to decide mock json responses
 * based on the test being executed.
 *
 * @see MockJsonDataStore#findJsonResponse()
 * @see AbstractSpringIT#watcher
 * @see AbstractSpringWebIT#watcher
 */
@UtilityClass
@SuppressWarnings("WeakerAccess")
public class TestContext {

    /**
     * A thread local store of current test being executed. It is mandatory for the store to be inheritable due to the
     * frequent use of {@link java.util.concurrent.ExecutorService} in our system.
     */
    private static final ThreadLocal<TestDescription> currentTest = new InheritableThreadLocal<>();

    public static void setCurrentTest(TestDescription description) {
        currentTest.set(description);
    }

    public static TestDescription getCurrentTest() {
        return currentTest.get();
    }

    public static void removeCurrentTest() {
        currentTest.remove();
    }
}
