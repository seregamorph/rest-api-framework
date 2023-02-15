package com.seregamorph.restapi.test.base;

import static org.hamcrest.Matchers.is;

import com.seregamorph.restapi.base.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class BaseMapperTest extends AbstractBaseMapperTest {

    // We need the exact interface of the mapper (e.g. MyResourceMapper), not the generated class
    // (e.g. MyResourceMapperImpl or MyResourceMapperImpl_).
    private final Class<? extends BaseMapper> mapperClass;

    protected BaseMapperTest(Class<? extends BaseMapper> mapperClass) {
        this.mapperClass = mapperClass;
    }

    @Override
    protected <T extends BaseMapper> T getMapper(Class<T> mapperClass) {
        return Mappers.getMapper(mapperClass);
    }

    @Test
    public void testClassNameShouldContainMapperDeclarationClassName() {
        collector.checkThat(this.getClass().getName(), is(mapperClass.getName() + "Test"));
    }

    @Test
    public void fieldMappersMustHaveTheSameMappingInterfaces() throws Exception {
        fieldMappersMustHaveTheSameMappingInterfaces(mapperClass);
    }

    @Test
    public void allMappersForNestedResourcesMustBeUsedInGeneratedMappingLogic() throws Exception {
        allMappersForNestedResourcesMustBeUsedInGeneratedMappingLogic(mapperClass);
    }
}
