package com.seregamorph.restapi.test;

import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.springframework.context.ApplicationContext;

@UtilityClass
public class TestApplicationContextHolder {

    // Implementation note: This is a test-scoped utility class allowing to access the current applicationContext
    // from anywhere, including static methods. The applicationContext is supposed to be set upon construction of a new
    // Spring bean, and removed upon its destruction.
    // There are other ways to retrieve applicationContext from a static context, but they are imperfect. E.g. By using
    // an ApplicationContextAware component, we can let Spring inject the applicationContext for us. The disadvantage,
    // however, is that we might end up having only the first applicationContext while in reality, there could be a few
    // of them when a test suite is executed.

    private static final ThreadLocal<ApplicationContext> currentContext = new InheritableThreadLocal<>();

    public static Optional<ApplicationContext> getApplicationContext() {
        return Optional.ofNullable(currentContext.get());
    }

    public static void setApplicationContext(ApplicationContext context) {
        currentContext.set(context);
    }
}
