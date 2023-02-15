package com.seregamorph.restapi.test.base;

import com.seregamorph.restapi.test.base.setup.BaseSetup;
import java.lang.reflect.Method;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import org.junit.runners.model.FrameworkMethod;
import org.springframework.http.HttpMethod;

@Getter
abstract class FrameworkExecution extends FrameworkMethod {

    private final MockMvcTestSetup rootSetup;
    @Nullable
    private final BaseSetup<?, ?> setup;
    @Nullable
    private final String executionName;

    /**
     * Zero if executionName is unique, incremented otherwise (1-based).
     */
    @Setter
    private int executionSuffix;

    /**
     * Execution of @ParameterizedTest
     */
    FrameworkExecution(Method method, MockMvcTestSetup rootSetup,
                       BaseSetup<?, ?> setup, String executionName) {
        super(method);
        this.rootSetup = rootSetup;
        this.setup = setup;
        this.executionName = executionName;
    }

    /**
     * Execution of regular @Test
     */
    FrameworkExecution(Method method, MockMvcTestSetup rootSetup) {
        this(method, rootSetup, null, null);
    }

    abstract void invoke(AbstractBaseSpringWebIT test) throws Throwable;

    @Nullable
    HttpMethod getHttpMethod() {
        return setup == null ? null : setup.getHttpMethod();
    }

    String getPathTemplate() {
        return setup == null ? "" : setup.getPathTemplate();
    }

    @Nullable
    String getInitTestMethodName() {
        return setup == null ? null : setup.getInitTestMethodName();
    }

    @Override
    public String getName() {
        return super.getName() + (executionName == null ? ""
                : "[" + executionName + (executionSuffix == 0 ? "" : "~" + executionSuffix) + "]");
    }

    @Override
    public String toString() {
        return getName();
    }

}
