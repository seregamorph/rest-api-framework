package com.seregamorph.restapi.test.config.spi;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.ServiceLoader;

@UtilityClass
public class TestFrameworkConfigHolder {

    @Getter
    private static final TestFrameworkConfig testFrameworkConfig = loadTestFrameworkConfig();

    private static TestFrameworkConfig loadTestFrameworkConfig() {
        val loader = ServiceLoader.load(TestFrameworkConfig.class, TestFrameworkConfigHolder.class.getClassLoader());

        if (loader.iterator().hasNext()) {
            return loader.iterator().next();
        } else {
            return new TestFrameworkConfig() {};
        }
    }
}
