package com.seregamorph.restapi.test.mocks;

import com.seregamorph.restapi.test.base.ParameterizedTest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;

@Slf4j
@UtilityClass
class TestClassUtils {

    static List<String> getTestMethodNames(Class<?> testClass, boolean includeParameterizedTest) {
        // We use java.lang.Class.getMethods because:
        // - @Test methods must be public as per JUnit requirements
        // - We need to collect methods from ancestors as well.
        val methods = testClass.getMethods();
        List<String> methodNames = new ArrayList<>();

        for (Method method : methods) {
            if (method.getAnnotation(Test.class) != null
                    || includeParameterizedTest && method.getAnnotation(ParameterizedTest.class) != null) {
                methodNames.add(method.getName());
            }
        }

        return methodNames;
    }
}
