package com.seregamorph.restapi.demo.controllers;

import static com.seregamorph.restapi.common.Constants.ENDPOINT_ID;
import static com.seregamorph.restapi.demo.controllers.FrameworkControllerWebIT.TestEventType.JUNIT_AFTER_CLASS;
import static com.seregamorph.restapi.demo.controllers.FrameworkControllerWebIT.TestEventType.JUNIT_BEFORE_CLASS;
import static com.seregamorph.restapi.demo.controllers.FrameworkControllerWebIT.TestEventType.LISTENER_AFTER_TEST_CLASS;
import static com.seregamorph.restapi.demo.controllers.FrameworkControllerWebIT.TestEventType.LISTENER_AFTER_TEST_EXECUTION;
import static com.seregamorph.restapi.demo.controllers.FrameworkControllerWebIT.TestEventType.LISTENER_AFTER_TEST_METHOD;
import static com.seregamorph.restapi.demo.controllers.FrameworkControllerWebIT.TestEventType.LISTENER_BEFORE_TEST_CLASS;
import static com.seregamorph.restapi.demo.controllers.FrameworkControllerWebIT.TestEventType.LISTENER_BEFORE_TEST_EXECUTION;
import static com.seregamorph.restapi.demo.controllers.FrameworkControllerWebIT.TestEventType.LISTENER_BEFORE_TEST_METHOD;
import static com.seregamorph.restapi.demo.controllers.FrameworkControllerWebIT.TestEventType.LISTENER_PREPARE_TEST_INSTANCE;
import static com.seregamorph.restapi.demo.controllers.FrameworkControllerWebIT.TestEventType.RULE_CLASS_FINISHED;
import static com.seregamorph.restapi.demo.controllers.FrameworkControllerWebIT.TestEventType.RULE_CLASS_STARTING;
import static com.seregamorph.restapi.demo.controllers.FrameworkControllerWebIT.TestEventType.RULE_INSTANCE_FINISHED;
import static com.seregamorph.restapi.demo.controllers.FrameworkControllerWebIT.TestEventType.RULE_INSTANCE_STARTING;
import static com.seregamorph.restapi.partial.PartialPayloadFactory.partial;
import static com.seregamorph.restapi.test.base.support.RequestType.RETRIEVAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.seregamorph.restapi.demo.resources.PersonResource;
import com.seregamorph.restapi.demo.resources.TeamResource;
import com.seregamorph.restapi.test.base.InitTest;
import com.seregamorph.restapi.test.base.setup.PatchSetup;
import com.seregamorph.restapi.test.base.setup.PostSetup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;

@TestExecutionListeners(FrameworkControllerWebIT.EventLoggingTestExecutionListener.class)
@InitTest(FrameworkController.class)
public abstract class FrameworkControllerWebIT extends AbstractBaseWebIT {

    enum TestEventType {
        /**
         * @see org.junit.BeforeClass
         */
        JUNIT_BEFORE_CLASS,
        /**
         * @see org.junit.AfterClass
         */
        JUNIT_AFTER_CLASS,
        /**
         * @see TestExecutionListener#beforeTestClass(TestContext)
         */
        LISTENER_BEFORE_TEST_CLASS,
        /**
         * @see TestExecutionListener#prepareTestInstance(TestContext)
         */
        LISTENER_PREPARE_TEST_INSTANCE,
        /**
         * @see TestExecutionListener#beforeTestMethod(TestContext)
         */
        LISTENER_BEFORE_TEST_METHOD,
        /**
         * @see TestExecutionListener#beforeTestExecution(TestContext)
         */
        LISTENER_BEFORE_TEST_EXECUTION,
        /**
         * @see TestExecutionListener#afterTestExecution(TestContext)
         */
        LISTENER_AFTER_TEST_EXECUTION,
        /**
         * @see TestExecutionListener#afterTestMethod(TestContext)
         */
        LISTENER_AFTER_TEST_METHOD,
        /**
         * @see TestExecutionListener#afterTestClass(TestContext)
         */
        LISTENER_AFTER_TEST_CLASS,
        /**
         * @see Rule
         * @see TestWatcher#starting(Description)
         */
        RULE_INSTANCE_STARTING,
        /**
         * @see Rule
         * @see TestWatcher#finished(Description)
         */
        RULE_INSTANCE_FINISHED,
        /**
         * @see ClassRule
         * @see TestWatcher#starting(Description)
         */
        RULE_CLASS_STARTING,
        /**
         * @see ClassRule
         * @see TestWatcher#finished(Description)
         */
        RULE_CLASS_FINISHED
    }

    @RequiredArgsConstructor
    static class TestEvent {

        private final TestEventType eventType;
        private final String displayName;

        @Override
        public String toString() {
            return "\n" + eventType + ", " + displayName;
        }
    }

    private static final ThreadLocal<List<TestEvent>> currentEvents = new ThreadLocal<>();

    private static final ThreadLocal<String> currentInstanceTestDisplayName = new ThreadLocal<>();
    private static final ThreadLocal<String> currentStaticTestDisplayName = new ThreadLocal<>();

    @Rule
    public final TestRule instanceTestRule = new TestWatcher() {

        @Override
        protected void starting(Description description) {
            currentInstanceTestDisplayName.set(description.getDisplayName());
            log(RULE_INSTANCE_STARTING);
        }

        @Override
        protected void finished(Description description) {
            log(RULE_INSTANCE_FINISHED);
            currentInstanceTestDisplayName.remove();
        }
    };

    @ClassRule
    public static final TestRule classTestRule = new TestWatcher() {

        @Override
        protected void starting(Description description) {
            assertNull("Current thread events should be null", currentEvents.get());
            currentEvents.set(new ArrayList<>());

            currentStaticTestDisplayName.set(description.getDisplayName());
            log(RULE_CLASS_STARTING);
        }

        @Override
        protected void finished(Description description) {
            log(RULE_CLASS_FINISHED);
            currentStaticTestDisplayName.remove();

            val events = currentEvents.get();
            try {
                assertEquals("Note: expected list of test events is not filtered (run all tests)",
                        expectedTestEvents(description.getDisplayName()).toString(), events.toString());
            } finally {
                currentEvents.remove();
            }
        }
    };

    @InitTest
    public static PostSetup postSetup() {
        return new PostSetup()
                .setRequestPayload(partial(PersonResource.class)
                        .setName("name")
                        .setEmailAddress("user@example.com")
                        .setTeam(partial(TeamResource.class)
                                .setId(1L)))
                .setDefaultResultMatchers();
    }

    @InitTest
    public static PatchSetup patchSetup() {
        return new PatchSetup(ENDPOINT_ID, 1L)
                .setRequestType(RETRIEVAL)
                .setRequestPayload(partial(PersonResource.class))
                .setDefaultResultMatchers();
    }

    @BeforeClass
    public static void beforeClassEvent() {
        log(JUNIT_BEFORE_CLASS);
    }

    @AfterClass
    public static void afterClassEvent() {
        log(JUNIT_AFTER_CLASS);
    }

    @Test
    public void emptyPatchShouldReturn200() throws Exception {
        rest.patch(ENDPOINT_ID, 1)
                .content(object())
                .andExpect(status().isOk());
    }

    private static void log(TestEventType eventType) {
        String displayName = currentInstanceTestDisplayName.get();
        if (displayName == null) {
            displayName = currentStaticTestDisplayName.get();
        }
        val event = new TestEvent(eventType, displayName);
        currentEvents.get()
                .add(event);
    }

    static class EventLoggingTestExecutionListener implements TestExecutionListener {

        @Override
        public void beforeTestClass(TestContext testContext) {
            log(LISTENER_BEFORE_TEST_CLASS);
        }

        @Override
        public void prepareTestInstance(TestContext testContext) {
            log(LISTENER_PREPARE_TEST_INSTANCE);
        }

        @Override
        public void beforeTestMethod(TestContext testContext) {
            log(LISTENER_BEFORE_TEST_METHOD);
        }

        @Override
        public void beforeTestExecution(TestContext testContext) {
            log(LISTENER_BEFORE_TEST_EXECUTION);
        }

        @Override
        public void afterTestExecution(TestContext testContext) {
            log(LISTENER_AFTER_TEST_EXECUTION);
        }

        @Override
        public void afterTestMethod(TestContext testContext) {
            log(LISTENER_AFTER_TEST_METHOD);
        }

        @Override
        public void afterTestClass(TestContext testContext) {
            log(LISTENER_AFTER_TEST_CLASS);
        }
    }

    @RequiredArgsConstructor
    private static class NestedTestEvent {
        private final TestEventType beforeType;
        private final TestEventType afterType;
        @Nullable
        private final String displayName;

        private final List<NestedTestEvent> nested = new ArrayList<>();
    }

    private static NestedTestEvent nested(TestEventType beforeType, TestEventType afterType, String displayName,
                                          NestedTestEvent... nested) {
        val event = new NestedTestEvent(beforeType, afterType, displayName);
        Collections.addAll(event.nested, nested);
        return event;
    }

    private static NestedTestEvent nested(TestEventType beforeType, TestEventType afterType, NestedTestEvent... nested) {
        return nested(beforeType, afterType, null, nested);
    }

    private static NestedTestEvent nestedTest(String testDisplayName) {
        return nested(LISTENER_PREPARE_TEST_INSTANCE, null,
                nested(RULE_INSTANCE_STARTING, RULE_INSTANCE_FINISHED, testDisplayName,
                        nested(LISTENER_BEFORE_TEST_METHOD, LISTENER_AFTER_TEST_METHOD,
                                nested(LISTENER_BEFORE_TEST_EXECUTION, LISTENER_AFTER_TEST_EXECUTION))));
    }

    private static List<TestEvent> expectedTestEvents(String testClassName) {
        val fullTransactionalSupport = testClassName.endsWith("MockWebIT");

        val tests = new ArrayList<NestedTestEvent>();
//        tests.add(nestedTest("controllerClassShouldHaveSwaggerAnnotations(" + testClassName + ")"));
        tests.add(nestedTest("controllerTestClassShouldHaveCorrectName(" + testClassName + ")"));
        tests.add(nestedTest("emptyPatchShouldReturn200(" + testClassName + ")"));
        tests.add(nestedTest("mappedMethodsShouldBePublicAndHaveCorrectAnnotations("
                + testClassName + ")"));
        if (fullTransactionalSupport) {
            tests.add(nestedTest("postShouldCreateResource[](" + testClassName + ")"));
        }
        tests.add(nestedTest("postShouldManageDefaultPayload[](" + testClassName + ")"));
        tests.add(nestedTest("postShouldReturn400WhenEmptyPayload[](" + testClassName + ")"));
        tests.add(nestedTest("patchShouldManageDefaultPayload[/1](" + testClassName + ")"));
        tests.add(nestedTest("patchShouldReturn400WhenEmptyPayload[/1](" + testClassName + ")"));

        return extract(nested(RULE_CLASS_STARTING, RULE_CLASS_FINISHED, testClassName,
                nested(LISTENER_BEFORE_TEST_CLASS, LISTENER_AFTER_TEST_CLASS,
                        nested(JUNIT_BEFORE_CLASS, JUNIT_AFTER_CLASS,
                                tests.toArray(new NestedTestEvent[0])
                        ))));
    }

    private static List<TestEvent> extract(NestedTestEvent testEvent) {
        val events = new ArrayList<TestEvent>();
        extract(testEvent, events, null);
        return events;
    }

    private static void extract(NestedTestEvent testEvent, List<TestEvent> events, String inheritedDisplayName) {
        String displayName = testEvent.displayName == null ? inheritedDisplayName : testEvent.displayName;
        events.add(new TestEvent(testEvent.beforeType, displayName));

        for (val nestedTestEvent : testEvent.nested) {
            extract(nestedTestEvent, events, displayName);
        }

        if (testEvent.afterType != null) {
            events.add(new TestEvent(testEvent.afterType, displayName));
        }
    }

}
