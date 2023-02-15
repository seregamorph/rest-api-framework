package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.test.base.FrameworkRunner.FilterState.INIT_TEST_METHOD;
import static com.seregamorph.restapi.test.base.FrameworkRunner.FilterState.REGULAR_TEST_METHOD;
import static java.util.Comparator.comparing;
import static lombok.AccessLevel.PRIVATE;

import com.seregamorph.restapi.annotations.Compatibility;
import com.seregamorph.restapi.test.base.setup.BaseSetup;
import com.seregamorph.restapi.test.base.setup.BaseSetupSupport;
import com.seregamorph.restapi.test.utils.BaseTestUtils;
import com.seregamorph.restapi.utils.SpringVersions;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.springframework.core.KotlinDetector;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks;
import org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks;
import org.springframework.util.Assert;

@Slf4j
public class FrameworkRunner extends ParentRunner<Runner> {

    enum FilterState {
        INIT_TEST_METHOD,
        REGULAR_TEST_METHOD
    }

    private final ThreadLocal<FilterState> currentFilterState = new ThreadLocal<>();
    private final List<Runner> runners = new ArrayList<>();

    @Getter(PRIVATE)
    private final TestContextManager testContextManager;

    public FrameworkRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        testContextManager = new TestContextManager(testClass);
        handleRunners(testClass);
    }

    private void handleRunners(Class<?> testClass) throws InitializationError {
        // MockMvc based tests are always executed in a single thread, hence there can be few unsafe requests
        // (like PATCH) in a row that shares the same transaction (fullTransactionalSupport is true), the rollback
        // is handled via TransactionalTestExecutionListener.
        // TestRestTemplate based tests cannot have such contract (fullTransactionalSupport is false), single request
        // rollback is handled via TransactionRollbackFilter.
        val fullTransactionalSupport = BaseTestUtils.isMockWebEnvironment(testClass);

        val rootSetup = getRootSetup(testClass);
        applyAllInitTestBaseSetups(testClass, rootSetup);
        val executions = AbstractBaseSpringWebIT.getTestExecutions(rootSetup, fullTransactionalSupport);
        val map = new TreeMap<String, List<FrameworkMethod>>();
        for (List<FrameworkExecution> execs : executions.getChildren().values()) {
            for (FrameworkExecution exec : execs) {
                val httpMethod = exec.getHttpMethod();
                val name = getRunnerName(rootSetup.getEndpoint() + exec.getPathTemplate()
                        + (httpMethod == null ? "" : " " + httpMethod));
                map.computeIfAbsent(name, k -> new ArrayList<>())
                        .add(exec);
            }
        }

        map.computeIfAbsent(getRunnerName(rootSetup.getEndpoint()), k -> new ArrayList<>())
                .addAll(getTestFrameworkMethods(rootSetup));

        for (val entry : map.entrySet()) {
            val name = entry.getKey();
            // guarantee repeatable order, the ordering is case insensitive to match IDEA alphabetic ordering
            val children = entry.getValue();
            children.sort(comparing(frameworkMethod -> frameworkMethod.getName().toLowerCase()));
            runners.add(new NestedRunner(testClass, name, children));
        }
    }

    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        assert currentFilterState.get() == null;
        currentFilterState.set(REGULAR_TEST_METHOD);
        try {
            // allows to run tests by @Test and @ParameterizedTest methods
            super.filter(filter);
        } catch (NoTestsRemainException e) {
            resetFilteredChildren();
            // allows to run tests by @InitTest method
            currentFilterState.set(INIT_TEST_METHOD);
            super.filter(filter);
        } finally {
            assert currentFilterState.get() != null;
            currentFilterState.remove();
        }
    }

    /**
     * Weird workaround to allow run by method from the IDEA. This way we satisfy the Description.equals() condition
     * to match. In case if we use another arguments to create Description object, the test execution report will
     * lose the execution id.
     */
    @Nullable
    private FilterState getCurrentFilterState() {
        return currentFilterState.get();
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

    @Override
    protected Description describeChild(Runner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(Runner runner, RunNotifier notifier) {
        runner.run(notifier);
    }

    @Override
    protected Statement withBeforeClasses(Statement statement) {
        val junitBeforeClasses = super.withBeforeClasses(statement);
        return new RunBeforeTestClassCallbacks(junitBeforeClasses, getTestContextManager());
    }

    @Override
    protected Statement withAfterClasses(Statement statement) {
        val junitAfterClasses = super.withAfterClasses(statement);
        return new RunAfterTestClassCallbacks(junitAfterClasses, getTestContextManager());
    }

    /**
     * Reset state of ParentRunner.filteredChildren field. Leads to forcing re-evaluating filtering
     * on current {@link #getChildren()}.
     */
    private void resetFilteredChildren() {
        try {
            // Not too elegant, but it's instead of overriding fully several methods from ParentRunner.
            // In case of incompatibility with JUnit the failure will affect only a single scenario of
            // run by Setup method.
            val filteredChildrenField = ParentRunner.class.getDeclaredField("filteredChildren");
            filteredChildrenField.setAccessible(true);
            filteredChildrenField.set(this, null);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to reset ParentRunner.filteredChildren", e);
        }
    }

    static String getRunnerName(String endpoint) {
        if (endpoint.isEmpty()) {
            return "/";
        }

        return endpoint.replaceAll("\\{(\\w+)(:[^}]*)?}", "{$1}")
                .replace('.', '_');
    }

    private List<FrameworkMethod> getTestFrameworkMethods(TestSetup rootSetup) {
        return getTestClass().getAnnotatedMethods(Test.class).stream()
                .map(frameworkMethod -> new FrameworkExecution(frameworkMethod.getMethod(), rootSetup) {
                    @Override
                    void invoke(AbstractBaseSpringWebIT test) throws Throwable {
                        invokeExplosively(test);
                    }
                })
                .collect(Collectors.toList());
    }

    private class NestedRunner extends SpringJUnit4ClassRunner {

        private final ThreadLocal<MockMvcTestSetup> currentRootSetup = new ThreadLocal<>();
        private final ThreadLocal<BaseSetup<?, ?>> currentSetup = new ThreadLocal<>();

        private final String name;
        private final List<FrameworkMethod> children;

        private NestedRunner(Class<?> clazz, String name, List<FrameworkMethod> children) throws InitializationError {
            super(clazz);
            this.name = name;
            this.children = children;
        }

        @Override
        protected TestContextManager createTestContextManager(Class<?> clazz) {
            // reuse single testContextManager between all nested runners
            return FrameworkRunner.this.getTestContextManager();
        }

        @Override
        protected String getName() {
            return name;
        }

        @Override
        protected List<FrameworkMethod> getChildren() {
            return children;
        }

        @Override
        protected Statement methodInvoker(FrameworkMethod method, Object test) {
            if (method instanceof FrameworkExecution && test instanceof AbstractBaseSpringWebIT) {
                return new InvokeFrameworkMethod((FrameworkExecution) method, (AbstractBaseSpringWebIT) test);
            } else {
                return super.methodInvoker(method, test);
            }
        }

        @Override
        protected Statement classBlock(RunNotifier notifier) {
            return childrenInvoker(notifier);
        }

        @Override
        protected Description describeChild(FrameworkMethod method) {
            // we intentionally override the describeChild method, because super method caches description by
            // java.lang.Method (it is the same for different executions of the same test)
            return Description.createTestDescription(getTestClass().getJavaClass(),
                    testName(method), method.getAnnotations());
        }

        @Override
        protected String testName(FrameworkMethod method) {
            val filterState = getCurrentFilterState();
            if (filterState == REGULAR_TEST_METHOD) {
                return method.getMethod().getName();
            } else if (filterState == INIT_TEST_METHOD && method instanceof FrameworkExecution) {
                val initTestMethodName = ((FrameworkExecution) method).getInitTestMethodName();
                if (initTestMethodName != null) {
                    return initTestMethodName;
                }
            }
            return method.getName();
        }

        @Override
        protected void runChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
            if (frameworkMethod instanceof FrameworkExecution) {
                // we pass the MockMvcTestSetup implicit via ThreadLocal,
                // because cannot override methods to pass as argument
                assert currentRootSetup.get() == null;
                assert currentSetup.get() == null;
                val frameworkExecution = (FrameworkExecution) frameworkMethod;

                currentRootSetup.set(frameworkExecution.getRootSetup());
                currentSetup.set(frameworkExecution.getSetup());
                try {
                    super.runChild(frameworkMethod, notifier);
                } finally {
                    currentRootSetup.remove();
                    currentSetup.remove();
                }
            } else {
                super.runChild(frameworkMethod, notifier);
            }
        }

        @Override
        protected Object createTest() throws Exception {
            Object test = super.createTest();
            if (test instanceof AbstractBaseSpringWebIT) {
                val mvcIT = (AbstractBaseSpringWebIT) test;
                mvcIT.rootSetup = Objects.requireNonNull(currentRootSetup.get(), "current test setup not set");
                mvcIT.setup = currentSetup.get();
            }
            return test;
        }

        @Override
        protected void validateTestMethods(List<Throwable> errors) {
            // no op
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + "(" + name + ")";
        }
    }

    @Compatibility("Kotlin companion object is also supported")
    private static TestSetup getRootSetup(Class<?> testClass) {
        try {
            try {
                try {
                    val initTestMethod = getRootInitTestMethod(testClass, true);
                    Assert.state(testClass.getAnnotation(InitTest.class) == null, testClass + " should have either "
                            + "static @InitTest method returning TestSetup, or declare @InitTest(controllerClass) "
                            + "on the class level, but not both.");
                    BaseSetupSupport.setCurrentInitTestMethod(initTestMethod);
                    try {
                        return (TestSetup) initTestMethod.invoke(null);
                    } finally {
                        BaseSetupSupport.clearCurrentInitTestMethod();
                    }
                } catch (IllegalStateException e) {
                    val initTest = testClass.getAnnotation(InitTest.class);
                    if (initTest == null) {
                        throw e;
                    } else {
                        val controllerClass = initTest.value();
                        if (controllerClass == Object.class) {
                            throw new IllegalStateException("@InitTest on " + testClass + " should explicitly declare "
                                    + "controller type in value()", e);
                        }
                        return AbstractBaseSpringWebIT.forController(controllerClass);
                    }
                }
            } catch (IllegalStateException e) {
                if (SpringVersions.isAtLeast("5.0") && KotlinDetector.isKotlinType(testClass)) {
                    try {
                        Class<?> companionClass;
                        try {
                            companionClass = Class.forName(testClass.getName() + "$Companion");
                        } catch (ClassNotFoundException cnfe) {
                            log.trace("Missing Companion object for " + testClass, cnfe);
                            if (testClass == Object.class) {
                                throw new IllegalStateException("Failed to get @InitTest method");
                            } else {
                                return getRootSetup(testClass.getSuperclass());
                            }
                        }
                        val initTestMethod = getRootInitTestMethod(companionClass, false);
                        BaseSetupSupport.setCurrentInitTestMethod(initTestMethod);
                        try {
                            val constructor = companionClass.getDeclaredConstructor();
                            constructor.setAccessible(true);
                            return (TestSetup) initTestMethod.invoke(constructor.newInstance());
                        } finally {
                            BaseSetupSupport.clearCurrentInitTestMethod();
                        }
                    } catch (ReflectiveOperationException reflectiveOperationException) {
                        throw new IllegalStateException(reflectiveOperationException);
                    }
                } else {
                    throw e;
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Error while InitTest for " + testClass.getName(), e);
        }
    }

    private static void applyAllInitTestBaseSetups(Class<?> testClass, TestSetup rootSetup) {
        val methods = getMethodsWithAnnotation(testClass, InitTest.class, true, BaseSetup.class);
        for (val method : methods) {
            try {
                val initTest = method.getAnnotation(InitTest.class);
                if (initTest.value() != Object.class) {
                    throw new IllegalStateException(method + " should not define controller class in "
                            + "@InitTest.value()");
                }

                BaseSetupSupport.setCurrentInitTestMethod(method);
                try {
                    val setup = (BaseSetup<?, ?>) method.invoke(null);
                    rootSetup.add(setup);
                } finally {
                    BaseSetupSupport.clearCurrentInitTestMethod();
                }
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Error while InitTest for " + testClass.getName()
                        + " calling method " + method, e);
            }
        }
    }

    @Nonnull
    private static Method getRootInitTestMethod(Class<?> testClass, boolean isStatic) {
        val methods = getMethodsWithAnnotation(testClass, InitTest.class, isStatic, TestSetup.class);
        if (methods.isEmpty()) {
            throw new IllegalStateException(String.format(
                    "No @%s method can be found in %s.",
                    InitTest.class.getName(),
                    testClass.getName()));
        }
        if (methods.size() > 1) {
            throw new IllegalStateException(String.format("Expected 1 @%s method that returns %s, "
                            + "but found %d in %s: "
                            + "%s.",
                    InitTest.class.getName(), TestSetup.class,
                    methods.size(), testClass.getName(),
                    methods.stream().map(Method::getName).collect(Collectors.joining(", "))));
        }
        return methods.get(0);
    }

    private static List<Method> getMethodsWithAnnotation(
            Class<?> type, Class<? extends Annotation> annotation, boolean isStatic, Class<?> returnType) {
        return Arrays.stream(type.getMethods())
                .filter(method -> method.isAnnotationPresent(annotation)
                        && returnType.isAssignableFrom(method.getReturnType()))
                .peek(method -> {
                    if (Modifier.isStatic(method.getModifiers()) != isStatic) {
                        throw new IllegalStateException(method + (isStatic ? " should " : " should not ")
                                + "be static");
                    }
                })
                .collect(Collectors.toList());
    }

    @RequiredArgsConstructor
    private static class InvokeFrameworkMethod extends Statement {

        private final FrameworkExecution testMethod;
        private final AbstractBaseSpringWebIT test;

        @Override
        public void evaluate() throws Throwable {
            testMethod.invoke(test);
        }
    }

}
