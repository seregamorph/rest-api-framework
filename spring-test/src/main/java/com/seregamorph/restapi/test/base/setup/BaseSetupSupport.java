package com.seregamorph.restapi.test.base.setup;

import java.lang.reflect.Method;
import javax.annotation.Nonnull;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;

@UtilityClass
public class BaseSetupSupport {

    private static final ThreadLocal<Method> currentInitTestMethod = new ThreadLocal<>();

    public static void setCurrentInitTestMethod(Method method) {
        assert currentInitTestMethod.get() == null;
        currentInitTestMethod.set(method);
    }

    public static void clearCurrentInitTestMethod() {
        assert currentInitTestMethod.get() != null;
        currentInitTestMethod.remove();
    }

    @Nonnull
    static Method getCurrentInitTestMethod() {
        Method current = currentInitTestMethod.get();
        Assert.state(current != null, "Current init test method is not set. You should create BaseSetup instances "
                + "only inside of @InitTest methods");
        return current;
    }

}
