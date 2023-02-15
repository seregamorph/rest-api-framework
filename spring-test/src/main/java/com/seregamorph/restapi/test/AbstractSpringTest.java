package com.seregamorph.restapi.test;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.web.context.request.RequestContextHolder;

public abstract class AbstractSpringTest extends AbstractUnitTest {

    @BeforeClass
    public static void cleanupContextBeforeTestClass() {
        // HATEOAS links are generated based on the current request context.
        // In case a test mocks this, other tests might share the context state and fail
        // due to unexpected URLs in links, hence it should be reset.
        RequestContextHolder.resetRequestAttributes();
    }

    @AfterClass
    public static void cleanupContextAfterTestClass() {
        RequestContextHolder.resetRequestAttributes();
    }
}
