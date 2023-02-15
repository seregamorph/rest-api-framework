package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.common.Constants.MAPPER_QUALIFIER;
import static org.junit.Assume.assumeFalse;

import com.seregamorph.restapi.base.BaseMapper;
import com.seregamorph.restapi.base.BaseResource;
import com.seregamorph.restapi.base.PruningMapper;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public abstract class BaseMappersIT extends AbstractBaseMapperIT {

    // This IT is created to deal with the case where mappers use non-default component model.
    // Extracting mappers by name won't inject fields.

    @Autowired
    protected ApplicationContext applicationContext;

    private final Set<Class<? extends BaseMapper>> mapperClasses = new HashSet<>();

    protected BaseMappersIT() {
    }

    protected BaseMappersIT(boolean infiniteRecursionDetectionEnabled) {
        super(infiniteRecursionDetectionEnabled);
    }

    @Before
    public void initMapperClasses() {
        val mappers = applicationContext.getBeansOfType(BaseMapper.class);
        if (mappers.isEmpty()) {
            throw new IllegalStateException("No beans of type `BaseMapper` found in context");
        }
        mappers.values().forEach(bean -> {
            for (val iface : bean.getClass().getInterfaces()) {
                if (BaseMapper.class.isAssignableFrom(iface)) {
                    mapperClasses.add(iface.asSubclass(BaseMapper.class));
                    log.info("Found mapper: {}", iface.getName());
                }
            }
        });
    }

    @Override
    protected <T extends BaseMapper> T getMapper(Class<T> mapperClass) {
        return applicationContext.getBean(mapperClass);
    }

    @Test
    public void fieldMappersMustHaveTheSameMappingInterfaces() throws Exception {
        for (val mapperClass : mapperClasses) {
            log.info("Testing {}...", mapperClass);
            fieldMappersMustHaveTheSameMappingInterfaces(mapperClass);
        }
    }

    @Test
    public void allMappersForNestedResourcesMustBeUsedInGeneratedMappingLogic() throws Exception {
        for (val mapperClass : mapperClasses) {
            log.info("Testing {}...", mapperClass);
            allMappersForNestedResourcesMustBeUsedInGeneratedMappingLogic(mapperClass);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void allMappersShouldHandleInfiniteRecursion() throws Exception {
        // We cannot have fair type safety here
        val mappers = applicationContext.getBeansOfType(PruningMapper.class)
                .values().stream()
                .map(mapper -> (PruningMapper<Object, ? extends BaseResource>) mapper)
                .collect(Collectors.toList());

        assumeFalse("No beans of type `EntityToResourceMapper` found in context", mappers.isEmpty());

        for (val mapper : mappers) {
            Qualifier qualifier = mapper.getClass().getAnnotation(Qualifier.class);

            // If a mapper is not annotated with @DecoratedWith, there will be only 1 implementor of the mapper,
            // e.g. UserMapperImpl.
            // If a mapper is annotated with @DecoratedWith, there will be 2 implementors of the same mapper,
            // one with @Qualifier(MAPPER_QUALIFIER) (e.g. UserMapperImpl_) and one without but extending
            // from the decorator (e.g. UserMapperImpl).
            // Because decorators are where we put our custom logic, they should handle infinite recursion. We therefore
            // do not check the one being annotated with @Qualifier(MAPPER_QUALIFIER) here.
            if (qualifier != null && MAPPER_QUALIFIER.equals(qualifier.value())) {
                continue;
            }

            Class<? extends BaseMapper> mapperClass = mapper.getClass();

            for (Class<?> iface : mapperClass.getInterfaces()) {
                if (PruningMapper.class.isAssignableFrom(iface)) {
                    mapperClass = iface.asSubclass(PruningMapper.class);
                    break;
                }
            }

            shouldHandleInfiniteRecursion(mapperClass);
        }
    }
}
