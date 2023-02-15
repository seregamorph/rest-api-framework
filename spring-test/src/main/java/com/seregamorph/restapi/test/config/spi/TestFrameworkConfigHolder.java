package com.seregamorph.restapi.test.config.spi;

import java.util.ServiceLoader;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.val;

@UtilityClass
public class TestFrameworkConfigHolder {

    @Getter
    private static final TestFrameworkConfig testFrameworkConfig = loadTestFrameworkConfig();

    private static TestFrameworkConfig loadTestFrameworkConfig() {
        val loader = ServiceLoader.load(TestFrameworkConfig.class);

        if (loader.iterator().hasNext()) {
            return loader.iterator().next();
        } else {
            return new TestFrameworkConfig() {};
        }
    }
}
