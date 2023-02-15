package com.seregamorph.restapi.test.listeners;

import lombok.SneakyThrows;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

/**
 * Listener handling {@link DatabaseStateReset} annotation.
 *
 * @see DatabaseStateReset
 */
@SuppressWarnings({"PMD.AvoidCatchingGenericException", "PMD.EmptyCatchBlock"})
public class DatabaseStateResetTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void afterTestClass(TestContext testContext) {
        DatabaseStateReset annotation = AnnotationUtils.findAnnotation(testContext.getTestClass(), DatabaseStateReset.class);
        if (annotation != null) {
            resetDatabaseState(testContext);
        }
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
        DatabaseStateReset annotation = AnnotationUtils.findAnnotation(testContext.getTestMethod(), DatabaseStateReset.class);
        if (annotation != null) {
            resetDatabaseState(testContext);
        }
    }

    @SneakyThrows
    private static void resetDatabaseState(TestContext testContext) {
        DatabaseStateManagementUtils.resetDatabaseState(
                testContext.getApplicationContext().getBean(DatabaseStateResettable.class));
    }
}
