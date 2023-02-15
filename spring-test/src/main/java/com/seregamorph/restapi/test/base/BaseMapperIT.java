package com.seregamorph.restapi.test.base;

import static org.hamcrest.Matchers.is;

import com.seregamorph.restapi.base.BaseMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public abstract class BaseMapperIT extends AbstractBaseMapperIT {

    // This IT is created to deal with the case where mappers use non-default component model.
    // Extracting mappers by name won't inject fields.

    @Autowired
    protected ApplicationContext applicationContext;

    // We need the exact interface of the mapper (e.g. MyResourceMapper), not the generated class
    // (e.g. MyResourceMapperImpl or MyResourceMapperImpl_).
    private final Class<? extends BaseMapper> mapperClass;

    protected BaseMapperIT(Class<? extends BaseMapper> mapperClass) {
        this.mapperClass = mapperClass;
    }

    protected BaseMapperIT(Class<? extends BaseMapper> mapperClass, boolean infiniteRecursionDetectionEnabled) {
        super(infiniteRecursionDetectionEnabled);
        this.mapperClass = mapperClass;
    }

    @Override
    protected <T extends BaseMapper> T getMapper(Class<T> mapperClass) {
        return applicationContext.getBean(mapperClass);
    }

    @Test
    public void testClassNameShouldContainMapperDeclarationClassName() {
        collector.checkThat(this.getClass().getName(), is(mapperClass.getName() + "IT"));
    }

    @Test
    public void fieldMappersMustHaveTheSameMappingInterfaces() throws Exception {
        fieldMappersMustHaveTheSameMappingInterfaces(mapperClass);
    }

    @Test
    public void allMappersForNestedResourcesMustBeUsedInGeneratedMappingLogic() throws Exception {
        allMappersForNestedResourcesMustBeUsedInGeneratedMappingLogic(mapperClass);
    }

    @Test
    public void shouldHandleInfiniteRecursion() throws Exception {
        shouldHandleInfiniteRecursion(mapperClass);
    }
}
